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
package com.javadtn.data;

import java.util.ArrayList;

import com.javadtn.information.FragmentHopInformation;

public class Message {
	private String messageId;
	private long messageTtl;
	private byte[] messageData;
	private ArrayList<FragmentHopInformation> hops;
	private String destinationId;

	public String getMessageId() {
		return messageId;
	}

	public Message setMessageId(String messageId) {
		this.messageId = messageId;
		return this;
	}

	public long getMessageTtl() {
		return messageTtl;
	}

	public Message setMessageTtl(long messageTtl) {
		this.messageTtl = messageTtl;
		return this;
	}

	public byte[] getMessageData() {
		return messageData;
	}

	public Message setMessageData(byte[] messageData) {
		this.messageData = messageData;
		return this;
	}

	public ArrayList<FragmentHopInformation> getHops() {
		return hops;
	}

	public Message setHops(ArrayList<FragmentHopInformation> hops) {
		this.hops = hops;
		return this;
	}

	public void addHops(ArrayList<FragmentHopInformation> hop) {
		if (hops == null)
			hops = new ArrayList<FragmentHopInformation>();
		hops.addAll(hop);
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

}
