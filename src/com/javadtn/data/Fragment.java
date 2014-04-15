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

import java.io.Serializable;

import com.javadtn.information.FragmentBodyInformation;

public class Fragment implements Serializable {
	private static final long serialVersionUID = 7815246066310550513L;
	private String sourceId;
	private String destinationId;
	private String fromId;
	private String messageId;
	private long ttl;
	private FragmentBodyInformation body;
	private int fragmentId;
	private int numberOfFragments;

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public int getNumberOfFragments() {
		return numberOfFragments;
	}

	public void setNumberOfFragments(int numberOfFragments) {
		this.numberOfFragments = numberOfFragments;
	}

	public int getFragmentId() {
		return fragmentId;
	}

	public void setFragmentId(int fragmentId) {
		this.fragmentId = fragmentId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String iD) {
		messageId = iD;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public FragmentBodyInformation getBody() {
		return body;
	}

	public void setBody(FragmentBodyInformation body) {
		this.body = body;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

}
