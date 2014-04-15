//-----------------------------------------------------------------------------
//	JavaDTN v0.2
//	Copyright (C) 2014  Kunal Dawn <kunal.dawn@gmail.com>
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.
//-----------------------------------------------------------------------------
package com.javadtn.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Semaphore;

import com.javadtn.data.Fragment;
import com.javadtn.information.FragmentHopInformation;
import com.javadtn.information.FragmentInformation;
import com.javadtn.information.InvestigationInformation;
import com.javadtn.information.InvestigationLockInformation;
import com.javadtn.information.NeighbourInformation;

public class InvestigationManager {

	private Semaphore lock;
	private HashMap<String, InvestigationLockInformation> nodeLocks;
	private NeighbourManager neighbourManager;
	private Random randomGenerator;
	private EventManager eventManager;
	private DataManager dataManager;
	private String[] selfParts;
	private FragmentManager fragmentManager;

	private Thread investigationResponseReceiveServer;
	private Thread investigationRequestReceiveServer;
	private Thread investigationRequestSendServer;
	private Thread investigationDynamicTimeoutServer;

	private Thread messageExpiryServer;
	private Thread messageReceiveServer;

	public InvestigationManager(final NeighbourManager neighbourManager,
			final EventManager eventManager, final DataManager dataManager,
			FragmentManager fragmentManager) {

		this.fragmentManager = fragmentManager;
		this.dataManager = dataManager;
		this.eventManager = eventManager;
		this.neighbourManager = neighbourManager;
		this.lock = new Semaphore(
				SettingsManager.INVESTIGATION_PARALLEL_REQUEST_COUNT, true);
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.nodeLocks = new HashMap<String, InvestigationLockInformation>();

		initMessageExpiryServer();
		initMessageReceiveServer();
		initInvestigationDynamicTimeoutServer();
		initInvestigationResponseReceiveServer();
		initInvestigationRequestReceiveServer();
		initInvestigationRequestSendServer();
	}

	private void initInvestigationDynamicTimeoutServer() {
		investigationDynamicTimeoutServer = new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> unlockList = new ArrayList<>();
				while (true) {
					synchronized (nodeLocks) {
						unlockList.clear();
						Iterator<Entry<String, InvestigationLockInformation>> iterator = nodeLocks
								.entrySet().iterator();
						while (iterator.hasNext()) {
							InvestigationLockInformation data = iterator.next()
									.getValue();

							if (data.isDynamicMode()) {
								if (data.getTimer() > 0)
									data.setTimer(data.getTimer()
											- SettingsManager.INVESTIGATION_DYNAMIC_TIMEOUT_VALIDATION_INTERVAL);
								else if (data.getTimer() == 0)
									unlockList.add(data.getNodeId());
							}
						}
					}
					synchronized (nodeLocks) {
						for (String key : unlockList) {
							unLock(key);
							eventManager
									.publishEvent(
											EventManager.INVESTIGATION_LOCK_DYNAMIC_TIMEOUT,
											"NODEID=" + key);
						}

					}
					try {
						Thread.sleep(SettingsManager.INVESTIGATION_DYNAMIC_TIMEOUT_VALIDATION_INTERVAL);
					} catch (Exception ex) {
					}
				}
			}
		});
		investigationDynamicTimeoutServer.start();
	}

	private void initInvestigationRequestSendServer() {
		investigationRequestSendServer = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						NeighbourInformation neighbour = neighbourManager
								.getOldestSyncNeighbour();
						if (neighbour != null) {
							long checkTime = SettingsManager.INVESTIGATION_MIN_INTERVAL
									+ (long) (randomGenerator.nextDouble() * (SettingsManager.INVESTIGATION_MAX_INTERVAL - SettingsManager.INVESTIGATION_MIN_INTERVAL));
							if ((neighbour.getLastSync() < (System
									.currentTimeMillis() - checkTime))
									&& !isLockedFor(neighbour.getNodeId())) {
								eventManager
										.publishEvent(
												EventManager.INVESTIGATION_REQUEST_SEND_WAIT,
												"NODEID="
														+ neighbour.getNodeId(),
												"LASTSYNC="
														+ neighbour
																.getLastSync(),
												"LASTSEEN="
														+ neighbour
																.getLastSeen());
								getLock(neighbour.getNodeId());
								neighbourManager.updateListSyncTime(neighbour
										.getNodeId());

								sendInvestigationRequest(neighbour,
										getFragmentInfos());

							}
						}
					} catch (Exception ex) {
					}
					try {
						Thread.sleep(SettingsManager.INVESTIGATION_QUERY_INTERVAL);
					} catch (Exception ex) {
					}

				}
			}
		});
		investigationRequestSendServer.start();
	}

	private void initInvestigationResponseReceiveServer() {
		investigationResponseReceiveServer = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerSocket server = null;
				try {
					server = new ServerSocket(
							SettingsManager.NETWORKING_INVESTIGATION_RESPONSE_PORT);
					while (true) {
						Socket client = server.accept();
						handleInvestigationResponseClient(client);
					}
				} catch (IOException e) {
				} finally {
					try {
						if (server != null)
							server.close();
					} catch (IOException e) {
					}
				}
			}
		});
		investigationResponseReceiveServer.start();
	}

	private void initInvestigationRequestReceiveServer() {
		investigationRequestReceiveServer = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerSocket server = null;
				try {
					server = new ServerSocket(
							SettingsManager.NETWORKING_INVESTIGATION_REQUEST_PORT);
					while (true) {
						Socket client = server.accept();
						handleInvestigationRequestClient(client);
					}
				} catch (Exception e) {
				} finally {
					try {
						if (server != null)
							server.close();
					} catch (Exception e) {
					}
				}

			}
		});
		investigationRequestReceiveServer.start();
	}

	private void initMessageExpiryServer() {
		if (SettingsManager.MESSAGING_USE_TTL_EXPIRY) {
			messageExpiryServer = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						dataManager
								.reduceTTLBy(SettingsManager.MESSAGING_VALIDATION_INTERVAL);
						dataManager.deleteExpiredFragments();

					} catch (Exception ex) {
					} finally {
						try {
							Thread.sleep(SettingsManager.MESSAGING_VALIDATION_INTERVAL);
						} catch (Exception ex) {

						}
					}
				}
			});
			messageExpiryServer.start();
		}
	}

	private void initMessageReceiveServer() {
		messageReceiveServer = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerSocket server = null;
				try {
					server = new ServerSocket(
							SettingsManager.NETWORKING_MESSAGE_TRANSFER_PORT);
					while (true) {
						Socket client = server.accept();
						handleMessageReceiveClient(client);
					}
				} catch (Exception e) {
				} finally {
					try {
						if (server != null)
							server.close();
					} catch (IOException e) {
					}
				}
			}
		});
		messageReceiveServer.start();
	}

	private void handleMessageReceiveClient(final Socket client) {
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(client.getInputStream()));
			Object obj = ois.readObject();
			if (obj instanceof Fragment) {
				Fragment msg = (Fragment) obj;
				eventManager.publishEvent(EventManager.MESSAGE_RECEIVE,
						"NODEID=" + msg.getFromId(),
						"MESSAGEID=" + msg.getMessageId(),
						"FRAGMENTID=" + msg.getFragmentId());
				addFragment(msg);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
		}
	}

	public void handleInvestigationResponseClient(final Socket client) {
		Thread clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ObjectInputStream ois = new ObjectInputStream(client
							.getInputStream());
					Object obj = ois.readObject();
					if (obj instanceof InvestigationInformation) {
						InvestigationInformation invMessage = (InvestigationInformation) obj;
						ArrayList<FragmentInformation> tosend = invMessage
								.getFragmentInfos();
						NeighbourInformation neighbour = neighbourManager
								.getNeighbourById(invMessage.getNodeId());
						if (neighbour != null) {
							eventManager
									.publishEvent(
											EventManager.INVESTIGATION_RESPONSE_RECEIVE,
											"NODEID=" + neighbour.getNodeId(),
											"RESPONSE_COUNT=" + tosend.size());
							sendMessageFragments(tosend, neighbour);
						}
					}
				} catch (Exception e) {
				} finally {
					try {
						if (client != null)
							client.close();
					} catch (IOException e) {
					}
				}

			}
		});
		clientThread.start();
	}

	private void handleInvestigationRequestClient(final Socket client) {
		Thread clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ObjectInputStream ois = new ObjectInputStream(client
							.getInputStream());
					Object obj = ois.readObject();
					if (obj instanceof InvestigationInformation) {
						InvestigationInformation invMessage = (InvestigationInformation) obj;
						ArrayList<FragmentInformation> neighbourInfo = invMessage
								.getFragmentInfos();
						ArrayList<FragmentInformation> selfInfo = getFragmentInfos();
						ArrayList<FragmentInformation> toSend = getToSendList(
								selfInfo, neighbourInfo);
						eventManager.publishEvent(
								EventManager.INVESTIGATION_REQUEST_RECEIVE,
								"NODEID=" + invMessage.getNodeId(),
								"REQUEST_COUNT=" + neighbourInfo.size());
						NeighbourInformation neighbour = neighbourManager
								.getNeighbourById(invMessage.getNodeId());
						if (neighbour != null)
							sendInvestigationResponse(neighbour, toSend);
					}
				} catch (Exception e) {
				} finally {
					try {
						if (client != null)
							client.close();
					} catch (IOException e) {
					}
				}

			}
		});
		clientThread.start();
	}

	private void sendMessageFragments(
			final ArrayList<FragmentInformation> fragmentInfos,
			final NeighbourInformation neighbour) {
		Socket client = null;
		int sendLimit = 0;
		try {
			cancelDynamicTimeOut(neighbour.getNodeId());
			for (FragmentInformation info : fragmentInfos) {

				if (isLockedFor(neighbour.getNodeId())) {
					Fragment msg = getFragmentByInfo(info);
					msg.setFromId(SettingsManager.NETWORKING_NODEID);
					if (msg != null) {
						client = new Socket(
								neighbour.getNodeIp(),
								SettingsManager.NETWORKING_MESSAGE_TRANSFER_PORT);
						client.setTcpNoDelay(true);
						client.setSoTimeout(SettingsManager.NETWORKING_COMMUNICATION_TIMEOUT);
						ObjectOutputStream oos = new ObjectOutputStream(
								new BufferedOutputStream(
										client.getOutputStream()));
						oos.writeObject(msg);
						oos.flush();
						eventManager.publishEvent(EventManager.MESSAGE_SEND,
								"NODEID=" + neighbour.getNodeId(), "MESSAGEID="
										+ msg.getMessageId(), "FRAGMENTID="
										+ msg.getFragmentId());
						if (SettingsManager.INVESTIGATION_MESSAGE_SEND_LIMITED) {
							sendLimit++;
							if (sendLimit >= SettingsManager.INVESTIGATION_MESSAGE_SEND_LIMIT_PER_SYNC)
								break;
						}
					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			eventManager.publishEvent(EventManager.MESSAGE_SEND_FAIL, "NODEID="
					+ neighbour.getNodeId());
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
			unLock(neighbour.getNodeId());
		}
	}

	private void sendInvestigationRequest(final NeighbourInformation neighbour,
			ArrayList<FragmentInformation> infos) {
		Socket client = null;
		try {
			client = new Socket(neighbour.getNodeIp(),
					SettingsManager.NETWORKING_INVESTIGATION_REQUEST_PORT);
			client.setSoTimeout(SettingsManager.NETWORKING_COMMUNICATION_TIMEOUT);
			InvestigationInformation message = new InvestigationInformation();
			message.setFragmentInfos(infos);
			message.setNodeId(SettingsManager.NETWORKING_NODEID);
			ObjectOutputStream oos = new ObjectOutputStream(
					client.getOutputStream());
			oos.writeObject(message);
			oos.flush();
			oos.close();
			eventManager.publishEvent(EventManager.INVESTIGATION_REQUEST_SEND,
					"NODEID=" + neighbour.getNodeId(),
					"LASTSYNC=" + neighbour.getLastSync(), "LASTSEEN="
							+ neighbour.getLastSeen());
		} catch (Exception e) {
			eventManager.publishEvent(
					EventManager.INVESTIGATION_REQUEST_SEND_FAIL, "NODEID="
							+ neighbour.getNodeId(),
					"LASTSYNC=" + neighbour.getLastSync(), "LASTSEEN="
							+ neighbour.getLastSeen());

			unLock(neighbour.getNodeId());
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
		}

	}

	private void sendInvestigationResponse(NeighbourInformation neighbour,
			ArrayList<FragmentInformation> infos) {
		Socket client = null;
		try {
			client = new Socket(neighbour.getNodeIp(),
					SettingsManager.NETWORKING_INVESTIGATION_RESPONSE_PORT);
			client.setSoTimeout(SettingsManager.NETWORKING_COMMUNICATION_TIMEOUT);
			InvestigationInformation message = new InvestigationInformation();
			message.setFragmentInfos(infos);
			message.setNodeId(SettingsManager.NETWORKING_NODEID);
			ObjectOutputStream oos = new ObjectOutputStream(
					client.getOutputStream());
			oos.writeObject(message);
			oos.flush();
			oos.close();
			eventManager.publishEvent(EventManager.INVESTIGATION_RESPONSE_SEND,
					"NODEID=" + neighbour.getNodeId(), "RESPONSE_COUNT="
							+ infos.size());
		} catch (Exception e) {
			eventManager.publishEvent(
					EventManager.INVESTIGATION_RESPONSE_SEND_FAIL, "NODEID="
							+ neighbour.getNodeId(),
					"RESPONSE_COUNT=" + infos.size());
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
		}

	}

	private boolean isForwardableToAPI(String nodeId) {
		if (nodeId.matches(SettingsManager.NETWORKING_BROADCAST_TAG)) {
			return true;
		}
		if (selfParts == null)
			selfParts = breakIdIntoParts(SettingsManager.NETWORKING_NODEID);
		String[] recvParts = breakIdIntoParts(nodeId);
		int minParts = Math.min(selfParts.length, recvParts.length);
		for (int i = 0; i < minParts; i++) {
			if (i < selfParts.length && i < recvParts.length) {
				if (recvParts[i]
						.matches(SettingsManager.NETWORKING_BROADCAST_TAG))
					return true;
				if (!recvParts[i].matches(selfParts[i]))
					return false;
			}
		}
		return false;
	}

	private void addFragment(Fragment fragment) {
		addHopTag(fragment);
		dataManager.addMessageFragment(fragment);
		if (isForwardableToAPI(fragment.getDestinationId())
				&& !fragment.getSourceId().matches(
						SettingsManager.NETWORKING_NODEID)) {
			int count = dataManager.isReceivedAllFragments(fragment
					.getMessageId());
			if (count != -1) {
				if (!dataManager.isDeliveredMessage(fragment.getMessageId())) {
					dataManager.setMessageAsDelivered(fragment.getMessageId());
					fragmentManager.doReAssembleAndSignal(dataManager
							.getAllMessageFragment(fragment.getMessageId(),
									count));
				}
			}
		}
	}

	private boolean getLock(String nodeid) throws InterruptedException {
		lock.acquire();
		synchronized (nodeLocks) {
			if (nodeLocks.containsKey(nodeid)) {
				return false;
			} else {
				nodeLocks
						.put(nodeid,
								new InvestigationLockInformation()
										.setNodeId(nodeid)
										.setDynamicMode(true)
										.setTimer(
												SettingsManager.INVESTIGATION_DYNAMIC_TIMEOUT));
				eventManager.publishEvent(EventManager.INVESTIGATION_LOCK_GET,
						"NODEID=" + nodeid);

				return true;
			}
		}

	}

	private void unLock(String nodeid) {
		synchronized (nodeLocks) {
			if (nodeLocks.containsKey(nodeid)) {
				nodeLocks.remove(nodeid);
				lock.release();
				eventManager.publishEvent(
						EventManager.INVESTIGATION_LOCK_REMOVE, "NODEID="
								+ nodeid);

			}
		}
	}

	private void cancelDynamicTimeOut(String nodeId) {
		synchronized (nodeLocks) {
			if (nodeLocks.containsKey(nodeId)) {
				nodeLocks.get(nodeId).setDynamicMode(false);
			}
		}
	}

	private boolean isLockedFor(String nodeid) {
		synchronized (nodeLocks) {
			return nodeLocks.containsKey(nodeid);
		}

	}

	private String[] breakIdIntoParts(String nodeId) {
		return nodeId.split("\\.");
	}

	private ArrayList<FragmentInformation> getFragmentInfos() {
		return dataManager
				.getAllFragmentsInfoLimitingMinTTL(SettingsManager.INVESTIGATION_MESSAGE_MIN_TTL);
	}

	private Fragment getFragmentByInfo(FragmentInformation info) {
		return dataManager.getMessageFragment(info);
	}

	private void addHopTag(Fragment fragment) {
		FragmentHopInformation hop = new FragmentHopInformation();
		hop.setFromNode(fragment.getFromId())
				.setToNode(SettingsManager.NETWORKING_NODEID)
				.setTimeStamp(new Date());
		fragment.getBody().addHop(hop);
	}

	private ArrayList<FragmentInformation> getToSendList(
			ArrayList<FragmentInformation> selfInfo,
			ArrayList<FragmentInformation> neighbourInfo) {
		ArrayList<FragmentInformation> toSend = new ArrayList<FragmentInformation>();
		boolean flag = false;
		for (FragmentInformation ninfo : neighbourInfo) {

			flag = false;
			for (FragmentInformation sinfo : selfInfo) {
				if (sinfo.equals(ninfo)) {
					flag = true;
					break;
				}
			}
			if (flag == false)
				toSend.add(ninfo);
		}
		return toSend;
	}

}
