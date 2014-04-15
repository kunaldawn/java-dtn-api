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
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;
import com.javadtn.data.Fragment;
import com.javadtn.information.FragmentBodyInformation;
import com.javadtn.information.FragmentInformation;
import com.javadtn.information.MessageInformation;
import com.javadtn.information.NeighbourInformation;

public class DataManager {
	private SQLiteQueue jobQueue = null;
	private int NEIGHBOUR_MODE_ACTIVATED = 1;
	private int NEIGHBOUR_MODE_DEACTIVATED = 0;

	public DataManager() {
		if (SettingsManager.DISK_DB) {
			File dbfile = new File(SettingsManager.DISK_DB_NAME);
			jobQueue = new SQLiteQueue(dbfile);
		} else {
			jobQueue = new SQLiteQueue();
		}
		jobQueue.start();
		initDB();
	}

	private void initDB() {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("CREATE TABLE IF NOT EXISTS fragment "
								+ "(sourceId TEXT, destinationId TEXT, messageId TEXT, "
								+ "fragmentId INTEGER, fragmentCount INTEGER, "
								+ "fragmentTTL BIGINT, fragmentData BLOB, "
								+ "isDeliveredAPI INTEGER DEFAULT 0);");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}

				statement = con
						.prepare("CREATE TABLE IF NOT EXISTS nodes (nodeId TEXT,"
								+ " nodeIp TEXT, lastSeen INTEGER, lastSync INTEGER,"
								+ " modeF INTEGER);");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		});
	}

	public Fragment getMessageFragment(
			final FragmentInformation info) {
		return jobQueue.execute(new SQLiteJob<Fragment>() {

			@Override
			protected Fragment job(SQLiteConnection con)
					throws Throwable {
				Fragment fragment = null;
				SQLiteStatement statement = con
						.prepare("SELECT * FROM fragment WHERE messageId = '"
								+ info.getMessageId() + "' and fragmentId = "
								+ info.getFragmentId());
				try {
					if (statement.step()) {
						fragment = new Fragment();
						fragment.setSourceId(statement.columnString(0));
						fragment.setDestinationId(statement.columnString(1));
						fragment.setMessageId(statement.columnString(2));
						fragment.setFragmentId(statement.columnInt(3));
						fragment.setNumberOfFragments(statement.columnInt(4));
						fragment.setTtl(statement.columnLong(5));
						fragment.setBody(decodeBody(statement.columnBlob(6)));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}

				return fragment;
			}
		}).complete();
	}

	public MessageInformation getAllMessageFragment(
			final String messageId, final int count) {
		return jobQueue.execute(new SQLiteJob<MessageInformation>() {

			@Override
			protected MessageInformation job(SQLiteConnection con)
					throws Throwable {
				ArrayList<Fragment> fragments = new ArrayList<Fragment>();
				SQLiteStatement statement = con
						.prepare("SELECT * FROM fragment WHERE messageId = '"
								+ messageId + "' ORDER BY fragmentId;");
				try {
					while (statement.step()) {
						Fragment frag = new Fragment();
						frag.setSourceId(statement.columnString(0));
						frag.setDestinationId(statement.columnString(1));
						frag.setMessageId(statement.columnString(2));
						frag.setFragmentId(statement.columnInt(3));
						frag.setNumberOfFragments(statement.columnInt(4));
						frag.setTtl(statement.columnLong(5));
						frag.setBody(decodeBody(statement.columnBlob(6)));
						fragments.add(frag);
					}
				} catch (Exception ex) {
					fragments.clear();
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				MessageInformation container = new MessageInformation();
				container.setFragments(fragments);
				container.setFragmentCount(count);
				return container;
			}
		}).complete();
	}

	public void addMessageFragment(final Fragment message) {
		if (!isDuplicateFragment(message.getMessageId(),
				message.getFragmentId()))
			jobQueue.execute(new SQLiteJob<Object>() {

				@Override
				protected Object job(SQLiteConnection con) throws Throwable {
					SQLiteStatement statement = con
							.prepare("INSERT INTO fragment VALUES(?,?,?,?,?,?,?,null)");
					try {
						statement.bind(1, message.getSourceId());
						statement.bind(2, message.getDestinationId());
						statement.bind(3, message.getMessageId());
						statement.bind(4, message.getFragmentId());
						statement.bind(5, message.getNumberOfFragments());
						statement.bind(6, message.getTtl());
						statement.bind(7, encodeBody(message.getBody()));
						statement.step();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						statement.dispose();
					}
					return null;
				}
			});
	}

	public boolean isDuplicateFragment(final String messageId,
			final int fragmmentId) {
		return jobQueue.execute(new SQLiteJob<Boolean>() {

			@Override
			protected Boolean job(SQLiteConnection con) throws Throwable {
				boolean result = false;
				SQLiteStatement statement = con
						.prepare("SELECT messageId FROM fragment WHERE messageId = '"
								+ messageId
								+ "' AND fragmentId = "
								+ fragmmentId + ";");

				try {
					result = statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return result;
			}
		}).complete();
	}

	public void reduceTTLBy(final long milliseconds) {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("UPDATE fragment SET fragmentTTL = fragmentTTL - "
								+ milliseconds + ";");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		});
	}

	public void deleteExpiredFragments() {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("DELETE FROM fragment WHERE fragmentTTL <= 0;");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		});
	}

	public ArrayList<FragmentInformation> getAllFragmentsInfoLimitingMinTTL(
			final long minTTL) {
		return jobQueue.execute(
				new SQLiteJob<ArrayList<FragmentInformation>>() {

					@Override
					protected ArrayList<FragmentInformation> job(
							SQLiteConnection con) throws Throwable {
						ArrayList<FragmentInformation> infos = new ArrayList<FragmentInformation>();

						SQLiteStatement statement = con
								.prepare("SELECT messageId, fragmentId FROM fragment WHERE fragmentTTL >= "
										+ minTTL + " ORDER BY fragmentTTL;");
						try {
							while (statement.step()) {
								FragmentInformation info = new FragmentInformation();
								info.setMessageId(statement.columnString(0));
								info.setFragmentId(statement.columnInt(1));
								infos.add(info);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							statement.dispose();
						}
						return infos;
					}
				}).complete();
	}

	public byte[] encodeBody(FragmentBodyInformation body) {
		byte[] data = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(body);
			oos.flush();
			data = baos.toByteArray();
			baos.close();
		} catch (Exception ex) {
			System.out.println("UNABLE TO ENCODE MESSAGE BODY : "
					+ ex.getMessage());
		}
		return data;
	}

	public boolean isDeliveredMessage(final String messageId) {
		return jobQueue.execute(new SQLiteJob<Boolean>() {

			@Override
			protected Boolean job(SQLiteConnection con) throws Throwable {
				boolean result = false;
				SQLiteStatement statement = con
						.prepare("SELECT messageId FROM fragment where messageId = '"
								+ messageId + "' AND isDeliveredAPI = 1");
				try {
					if (statement.step())
						result = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return result;
			}
		}).complete();
	}

	public void setMessageAsDelivered(final String messageId) {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("UPDATE fragment SET isDeliveredAPI = 1 WHERE messageId = '"
								+ messageId + "';");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		}).complete();
	}

	public int isReceivedAllFragments(final String messageId) {
		final int fragCount = getFragmentCountFor(messageId);
		if (fragCount == -1)
			return -1;
		return jobQueue.execute(new SQLiteJob<Integer>() {

			@Override
			protected Integer job(SQLiteConnection con) throws Throwable {
				int result = -1;
				SQLiteStatement statement = con
						.prepare("SELECT COUNT(messageId) FROM fragment WHERE messageId = '"
								+ messageId + "';");
				try {
					if (statement.step()) {
						int count = statement.columnInt(0);
						if (count == fragCount)
							result = count;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return result;
			}
		}).complete();
	}

	public int getFragmentCountFor(final String messageId) {
		return jobQueue.execute(new SQLiteJob<Integer>() {

			@Override
			protected Integer job(SQLiteConnection con) throws Throwable {
				int count = -1;
				SQLiteStatement statement = con
						.prepare("SELECT fragmentCount FROM fragment WHERE messageId = '"
								+ messageId + "' LIMIT 1;");
				try {
					if (statement.step())
						count = statement.columnInt(0);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return count;
			}
		}).complete();
	}

	public ArrayList<String> getMessagePool() {
		return jobQueue.execute(new SQLiteJob<ArrayList<String>>() {

			@Override
			protected ArrayList<String> job(SQLiteConnection con)
					throws Throwable {
				ArrayList<String> pool = new ArrayList<String>();
				SQLiteStatement statement = con
						.prepare("SELECT DISTINCT messageId FROM fragment;");
				try {
					while (statement.step())
						pool.add(statement.columnString(0));
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return pool;
			}
		}).complete();
	}

	public void addNeighbourInfo(final NeighbourInformation neighbour) {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("INSERT INTO nodes VALUES(?,?,?,?,?);");
				try {
					statement.bind(1, neighbour.getNodeId());
					statement.bind(2, neighbour.getNodeIp());
					statement.bind(3, neighbour.getLastSeen());
					statement.bind(4, neighbour.getLastSync());
					statement.bind(5, NEIGHBOUR_MODE_ACTIVATED);
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		}).complete();
	}

	public NeighbourInformation getOldestSyncNeighbour() {
		return jobQueue.execute(new SQLiteJob<NeighbourInformation>() {

			@Override
			protected NeighbourInformation job(SQLiteConnection con)
					throws Throwable {
				NeighbourInformation info = null;
				SQLiteStatement statement = con
						.prepare("SELECT * FROM nodes WHERE modeF = "
								+ NEIGHBOUR_MODE_ACTIVATED
								+ " ORDER BY lastSync LIMIT 1;");
				try {
					if (statement.step()) {
						info = new NeighbourInformation();
						info.setNodeId(statement.columnString(0));
						info.setNodeIp(statement.columnString(1));
						info.setLastSeen(statement.columnLong(2));
						info.setLastSync(statement.columnLong(3));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return info;
			}
		}).complete();
	}

	public ArrayList<NeighbourInformation> getAllNeighbours() {
		return jobQueue.execute(
				new SQLiteJob<ArrayList<NeighbourInformation>>() {

					@Override
					protected ArrayList<NeighbourInformation> job(
							SQLiteConnection con) throws Throwable {
						ArrayList<NeighbourInformation> infos = new ArrayList<NeighbourInformation>();
						SQLiteStatement statement = con
								.prepare("SELECT * FROM nodes;");
						try {
							while (statement.step()) {
								NeighbourInformation info = new NeighbourInformation();
								info.setNodeId(statement.columnString(0));
								info.setNodeIp(statement.columnString(1));
								info.setLastSeen(statement.columnLong(2));
								info.setLastSync(statement.columnLong(3));
								info.setModeF(statement.columnInt(4));
								infos.add(info);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							statement.dispose();
						}
						return infos;
					}
				}).complete();
	}

	public NeighbourInformation getNeighbourInfoById(final String id) {
		return jobQueue.execute(new SQLiteJob<NeighbourInformation>() {

			@Override
			protected NeighbourInformation job(SQLiteConnection con)
					throws Throwable {
				NeighbourInformation info = null;
				SQLiteStatement statement = con
						.prepare("SELECT * FROM nodes WHERE nodeId = '" + id
								+ "';");
				try {
					if (statement.step()) {
						info = new NeighbourInformation();
						info.setNodeId(statement.columnString(0));
						info.setNodeIp(statement.columnString(1));
						info.setLastSeen(statement.columnLong(2));
						info.setLastSync(statement.columnLong(3));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return info;
			}
		}).complete();
	}

	public void updateLastSyncTime(final String id) {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("UPDATE nodes SET lastSync = "
								+ new Date().getTime() + " WHERE nodeId = '"
								+ id + "';");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		}).complete();
	}

	public boolean isAlreadyDiscoveredNeighbour(final String id) {
		return jobQueue.execute(new SQLiteJob<Boolean>() {

			@Override
			protected Boolean job(SQLiteConnection con) throws Throwable {
				Boolean result = false;
				SQLiteStatement statement = con
						.prepare("SELECT nodeId FROM nodes WHERE nodeId = '"
								+ id + "';");
				try {
					if (statement.step())
						result = true;
				} catch (Exception ex) {
				} finally {
					statement.dispose();
				}
				return result;
			}
		}).complete();
	}

	public void updateNeighbourLastSeen(final String id, final String nodeIp) {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {

				SQLiteStatement statement = con
						.prepare("UPDATE nodes SET lastSeen = "
								+ new Date().getTime() + ", nodeIp = '"
								+ nodeIp + "', modeF = "
								+ NEIGHBOUR_MODE_ACTIVATED
								+ " WHERE nodeId = '" + id + "';");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		}).complete();
	}

	public void expireNeighbours() {
		jobQueue.execute(new SQLiteJob<Object>() {

			@Override
			protected Object job(SQLiteConnection con) throws Throwable {
				SQLiteStatement statement = con
						.prepare("UPDATE nodes SET modeF = "
								+ NEIGHBOUR_MODE_DEACTIVATED
								+ " WHERE lastSeen < "
								+ (System.currentTimeMillis() - SettingsManager.NEIGHBOUR_EXPIRE_INTERVAL)
								+ ";");
				try {
					statement.step();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					statement.dispose();
				}
				return null;
			}
		}).complete();
	}

	public FragmentBodyInformation decodeBody(byte[] data) {
		FragmentBodyInformation body = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			body = (FragmentBodyInformation) ois.readObject();
			ois.close();
			bais.close();
		} catch (Exception ex) {
			System.out.println("UNABLE TO DECODE MESSAGE BODY : "
					+ ex.getMessage());
		}
		return body;
	}

}
