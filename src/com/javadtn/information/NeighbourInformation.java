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
package com.javadtn.information;

import java.io.Serializable;

public class NeighbourInformation implements Serializable {
	private static final long serialVersionUID = -9068513159847955604L;
	private String nodeId;
	private String nodeIp;
	private long lastSeen;
	private long lastSync;
	private int modeF;

	public String getNodeId() {
		return nodeId;
	}

	public NeighbourInformation setNodeId(String nodeid) {
		nodeId = nodeid;
		return this;
	}

	public String getNodeIp() {
		return nodeIp;
	}

	public NeighbourInformation setNodeIp(String nodeip) {
		nodeIp = nodeip;
		return this;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public NeighbourInformation setLastSeen(long lastseen) {
		lastSeen = lastseen;
		return this;
	}

	public long getLastSync() {
		return lastSync;
	}

	public NeighbourInformation setLastSync(long lastSync) {
		this.lastSync = lastSync;
		return this;
	}

	public int getModeF() {
		return modeF;
	}

	public void setModeF(int modeF) {
		this.modeF = modeF;
	}

}
