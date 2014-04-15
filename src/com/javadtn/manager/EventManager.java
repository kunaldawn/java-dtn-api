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

import java.util.ArrayList;

import com.javadtn.listner.EventListener;
import com.javadtn.listner.MessageListener;

public class EventManager {

	public static int NEIGHBOUR_DISCOVERED = 101;
	public static int NEIGHBOUR_SEEN = 102;

	public static int INVESTIGATION_LOCK_DYNAMIC_TIMEOUT = 201;
	public static int INVESTIGATION_LOCK_GET = 202;
	public static int INVESTIGATION_LOCK_REMOVE = 203;

	public static int INVESTIGATION_REQUEST_SEND = 301;
	public static int INVESTIGATION_REQUEST_SEND_FAIL = 302;
	public static int INVESTIGATION_REQUEST_SEND_WAIT = 303;
	public static int INVESTIGATION_REQUEST_RECEIVE = 304;

	public static int INVESTIGATION_RESPONSE_SEND = 401;
	public static int INVESTIGATION_RESPONSE_SEND_FAIL = 402;
	public static int INVESTIGATION_RESPONSE_RECEIVE = 403;

	public static int MESSAGE_SEND = 501;
	public static int MESSAGE_SEND_FAIL = 502;
	public static int MESSAGE_RECEIVE = 503;

	public static int FRAGMENT_FRAGMENTATION = 601;
	public static int FRAGMENT_REASSEMBLE = 602;

	private ArrayList<MessageListener> messageListeners;
	private ArrayList<EventListener> eventListeners;

	public EventManager() {
		messageListeners = new ArrayList<MessageListener>();
		eventListeners = new ArrayList<EventListener>();
	}

	public void registerMessageListeners(MessageListener... listenerList) {
		synchronized (messageListeners) {
			for (MessageListener listener : listenerList)
				messageListeners.add(listener);
		}
	}

	public void removeMessageListener(MessageListener listener) {
		synchronized (messageListeners) {
			if (messageListeners.contains(listener))
				messageListeners.remove(listener);
		}
	}

	public void registerEventListerers(EventListener... listenersList) {
		synchronized (eventListeners) {
			for (EventListener listener : listenersList)
				eventListeners.add(listener);
		}
	}

	public void removeEventListener(EventListener listener) {
		synchronized (eventListeners) {
			if (eventListeners.contains(listener))
				eventListeners.remove(listener);
		}
	}

	public void publishEvent(Integer eventType, String... eventMessage) {
		synchronized (eventListeners) {
			for (EventListener listener : eventListeners)
				listener.onEvent(eventType, eventMessage);
		}
	}

	public ArrayList<MessageListener> getMessageListeners() {
		return messageListeners;
	}

	public ArrayList<EventListener> getEventListeners() {
		return eventListeners;
	}
}
