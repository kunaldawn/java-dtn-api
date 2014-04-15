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

public class SettingsManager {

	public static int NETWORKING_ADVERTISEMENT_PORT = 9899;
	public static int NETWORKING_INVESTIGATION_REQUEST_PORT = 9898;
	public static int NETWORKING_INVESTIGATION_RESPONSE_PORT = 9897;
	public static int NETWORKING_MESSAGE_TRANSFER_PORT = 9896;
	public static long NETWORKING_ADVERTISEMENT_INTERVAL = 3000;
	public static int NETWORKING_COMMUNICATION_TIMEOUT = 10000;
	public static String NETWORKING_HOST_IP = "";
	public static String NETWORKING_BROADCAST_IP = "";
	public static String NETWORKING_NODEID = "";
	public static String NETWORKING_BROADCAST_TAG = "broadcast";

	public static long NEIGHBOUR_VALIDATION_INTERVAL = 1000;
	public static long NEIGHBOUR_EXPIRE_INTERVAL = 15000;

	public static long MESSAGING_VALIDATION_INTERVAL = 1000;
	public static boolean MESSAGING_USE_TTL_EXPIRY = true;

	public static boolean FRAGMENTATION_ENABLED = true;
	public static int FRAGMENTATION_SIZE_LIMIT = 1024 * 100;

	public static boolean DISK_DB = false;
	public static String DISK_DB_NAME = "javadtn.db";

	public static int INVESTIGATION_DYNAMIC_TIMEOUT = 30000;
	public static long INVESTIGATION_MIN_INTERVAL = 5000;
	public static long INVESTIGATION_MAX_INTERVAL = 100000;
	public static long INVESTIGATION_QUERY_INTERVAL = 1000;
	public static int INVESTIGATION_DYNAMIC_TIMEOUT_VALIDATION_INTERVAL = 1;
	public static int INVESTIGATION_PARALLEL_REQUEST_COUNT = 3;
	public static long INVESTIGATION_PARALLEL_DELAY_MIN = 1000;
	public static long INVESTIGATION_PARALLEL_DELAY_MAX = 5000;
	public static long INVESTIGATION_MESSAGE_MIN_TTL = 10000;
	public static int INVESTIGATION_MESSAGE_SEND_LIMIT_PER_SYNC = 3;
	public static boolean INVESTIGATION_MESSAGE_SEND_LIMITED = false;
}
