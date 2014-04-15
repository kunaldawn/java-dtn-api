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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import com.javadtn.information.AdvertisementInformation;
import com.javadtn.information.NeighbourInformation;

public class NeighbourManager {
	private Thread expiryThread;
	private DataManager dataManager;
	private EventManager eventManager;
	private Thread serverThread;
	private Thread clientThread;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket = null;

	public NeighbourManager(final DataManager dataManager,
			EventManager eventManager) {
		this.dataManager = dataManager;
		this.eventManager = eventManager;

		expiryThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						dataManager.expireNeighbours();
					} catch (Exception ex) {
					} finally {
						try {
							Thread.sleep(SettingsManager.NEIGHBOUR_VALIDATION_INTERVAL);
						} catch (Exception ex) {

						}
					}
				}
			}
		});
		expiryThread.start();
		serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					serverSocket = new DatagramSocket();
					serverSocket.setReuseAddress(true);
					serverSocket.setBroadcast(true);
					AdvertisementInformation message = new AdvertisementInformation();

					message.setIpAddress(SettingsManager.NETWORKING_HOST_IP);
					message.setNodeId(SettingsManager.NETWORKING_NODEID);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(message);
					oos.flush();
					oos.close();
					InetAddress broadcastAddr = InetAddress
							.getByName(SettingsManager.NETWORKING_BROADCAST_IP);

					DatagramPacket packet = new DatagramPacket(bos
							.toByteArray(), bos.toByteArray().length,
							broadcastAddr,
							SettingsManager.NETWORKING_ADVERTISEMENT_PORT);
					while (true) {
						try {
							serverSocket.send(packet);
							Thread.sleep(SettingsManager.NETWORKING_ADVERTISEMENT_INTERVAL);
						} catch (Exception ex) {

						}
					}
				} catch (Exception ex) {

				}

			}
		});
		serverThread.start();
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					clientSocket = new DatagramSocket(
							SettingsManager.NETWORKING_ADVERTISEMENT_PORT);
					clientSocket.setBroadcast(true);
					clientSocket.setReuseAddress(true);
					clientSocket.setSoTimeout(1000);
					byte[] buffer = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buffer,
							buffer.length);
					while (true) {
						try {
							clientSocket.receive(packet);
							ObjectInputStream os = new ObjectInputStream(
									new ByteArrayInputStream(buffer));
							AdvertisementInformation msg = (AdvertisementInformation) os
									.readObject();
							onReceiveAdvertisement(msg.getNodeId(),
									msg.getIpAddress());
							System.out.println(msg.getNodeId());
						} catch (Exception ex) {
						}
					}

				} catch (Exception ex) {
				} finally {
					if (clientSocket != null)
						clientSocket.close();
				}

			}
		});
		clientThread.start();
	}

	public NeighbourInformation getNeighbourById(String id) {
		try {
			return dataManager.getNeighbourInfoById(id);
		} catch (Exception ex) {

		}
		return null;
	}

	public void updateListSyncTime(String id) {
		try {
			dataManager.updateLastSyncTime(id);
		} catch (Exception ex) {

		}
	}

	public NeighbourInformation getOldestSyncNeighbour() {
		try {
			return dataManager.getOldestSyncNeighbour();
		} catch (Exception ex) {

		}
		return null;
	}

	public void onReceiveAdvertisement(String nodeId, String nodeIp) {
		try {
			if (!nodeId.matches(SettingsManager.NETWORKING_NODEID)) {
				if (dataManager.isAlreadyDiscoveredNeighbour(nodeId)) {
					dataManager.updateNeighbourLastSeen(nodeId, nodeIp);
					eventManager.publishEvent(EventManager.NEIGHBOUR_SEEN, "NODEID="
							+ nodeId, "NODEIP=" + nodeIp);
				} else {
					NeighbourInformation node = new NeighbourInformation();
					node.setLastSeen(new Date().getTime());
					node.setNodeId(nodeId);
					node.setNodeIp(nodeIp);
					node.setLastSync(0);
					dataManager.addNeighbourInfo(node);
					eventManager.publishEvent(EventManager.NEIGHBOUR_DISCOVERED,
							"NODEID=" + nodeId, "NODEIP=" + nodeIp);
				}
			}
		} catch (Exception ex) {

		}

	}

}
