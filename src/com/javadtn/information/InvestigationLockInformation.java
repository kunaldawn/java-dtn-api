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

public class InvestigationLockInformation {
	private String nodeId;
	private int timer;
	private boolean dynamicMode;

	public String getNodeId() {
		return nodeId;
	}

	public int getTimer() {
		return timer;
	}

	public boolean isDynamicMode() {
		return dynamicMode;
	}

	public InvestigationLockInformation setNodeId(String nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public InvestigationLockInformation setTimer(int timer) {
		this.timer = timer;
		return this;
	}

	public InvestigationLockInformation setDynamicMode(boolean dynamicMode) {
		this.dynamicMode = dynamicMode;
		return this;
	}

}
