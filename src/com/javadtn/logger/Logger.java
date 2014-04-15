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
package com.javadtn.logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

import com.javadtn.listner.EventListener;
import com.javadtn.manager.EventManager;

public class Logger implements EventListener {
	private File logfile;
	private PrintWriter writer;

	public Logger(String fileURI) {
		try {
			logfile = new File(fileURI);
			writer = new PrintWriter(logfile);
			writer.println("###################NINJADTN#####################");
			writer.println("#CREATED ON : " + new Date());
			writer.println("#LOG ID : " + UUID.randomUUID());
			writer.println("################################################");
			writer.flush();
		} catch (Exception ex) {

		}
	}

	@Override
	public void onEvent(Integer eventType, String... eventMessage) {
		try {
			if (writer != null) {
				long ts = System.currentTimeMillis();
				writer.print(ts);
				System.out.print(ts);
				if (EventManager.NEIGHBOUR_DISCOVERED == eventType) {
					writer.print(":NEIGHBOUR_DISCOVERED");
					System.out.print(":NEIGHBOUR_DISCOVERED");
				} else if (EventManager.NEIGHBOUR_SEEN == eventType) {
					writer.print(":NEIGHBOUR_SEEN");
					System.out.print(":NEIGHBOUR_SEEN");
				} else if (EventManager.INVESTIGATION_LOCK_DYNAMIC_TIMEOUT == eventType) {
					writer.print(":INVESTIGATION_LOCK_DYNAMIC_TIMEOUT");
					System.out.print(":INVESTIGATION_LOCK_DYNAMIC_TIMEOUT");
				} else if (EventManager.INVESTIGATION_LOCK_GET == eventType) {
					writer.print(":INVESTIGATION_LOCK_GET");
					System.out.print(":INVESTIGATION_LOCK_GET");
				} else if (EventManager.INVESTIGATION_LOCK_REMOVE == eventType) {
					writer.print(":INVESTIGATION_LOCK_REMOVE");
					System.out.print(":INVESTIGATION_LOCK_REMOVE");
				} else if (EventManager.INVESTIGATION_REQUEST_SEND == eventType) {
					writer.print(":INVESTIGATION_REQUEST_SEND");
					System.out.print(":INVESTIGATION_REQUEST_SEND");
				} else if (EventManager.INVESTIGATION_REQUEST_SEND_FAIL == eventType) {
					writer.print(":INVESTIGATION_REQUEST_SEND_FAIL");
					System.out.print(":INVESTIGATION_REQUEST_SEND_FAIL");
				} else if (EventManager.INVESTIGATION_REQUEST_SEND_WAIT == eventType) {
					writer.print(":INVESTIGATION_REQUEST_SEND_WAIT");
					System.out.print(":INVESTIGATION_REQUEST_SEND_WAIT");
				} else if (EventManager.INVESTIGATION_REQUEST_RECEIVE == eventType) {
					writer.print(":INVESTIGATION_REQUEST_RECEIVE");
					System.out.print(":INVESTIGATION_REQUEST_RECEIVE");
				} else if (EventManager.INVESTIGATION_RESPONSE_SEND == eventType) {
					writer.print(":INVESTIGATION_RESPONSE_SEND");
					System.out.print(":INVESTIGATION_RESPONSE_SEND");
				} else if (EventManager.INVESTIGATION_RESPONSE_SEND_FAIL == eventType) {
					writer.print(":INVESTIGATION_RESPONSE_SEND_FAIL");
					System.out.print(":INVESTIGATION_RESPONSE_SEND_FAIL");
				} else if (EventManager.INVESTIGATION_RESPONSE_RECEIVE == eventType) {
					writer.print(":INVESTIGATION_RESPONSE_RECEIVE");
					System.out.print(":INVESTIGATION_RESPONSE_RECEIVE");
				} else if (EventManager.MESSAGE_SEND == eventType) {
					writer.print(":MESSAGE_SEND");
					System.out.print(":MESSAGE_SEND");
				} else if (EventManager.MESSAGE_SEND_FAIL == eventType) {
					writer.print(":MESSAGE_SEND_FAIL");
					System.out.print(":MESSAGE_SEND_FAIL");
				} else if (EventManager.MESSAGE_RECEIVE == eventType) {
					writer.print(":MESSAGE_RECEIVE");
					System.out.print(":MESSAGE_RECEIVE");
				} else if (EventManager.FRAGMENT_FRAGMENTATION == eventType) {
					writer.print(":FRAGMENT_FRAGMENTATION");
					System.out.print(":FRAGMENT_FRAGMENTATION");
				} else if (EventManager.FRAGMENT_REASSEMBLE == eventType) {
					writer.print(":FRAGMENT_REASSEMBLE");
					System.out.print(":FRAGMENT_REASSEMBLE");
				}

				for (String msg : eventMessage) {
					writer.print(":" + msg);
					System.out.print(":" + msg);
				}
				writer.println();
				System.out.println();
				writer.flush();

			}
		} catch (Exception ex) {

		}
	}
}
