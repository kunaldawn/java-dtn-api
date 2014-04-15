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
import java.util.ArrayList;

public class InvestigationInformation implements Serializable {
	private static final long serialVersionUID = -4885899161144936705L;
	String nodeId;
	ArrayList<FragmentInformation> fragmentInfos;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public ArrayList<FragmentInformation> getFragmentInfos() {
		return fragmentInfos;
	}

	public void setFragmentInfos(ArrayList<FragmentInformation> messageInfos) {
		this.fragmentInfos = messageInfos;
	}

}
