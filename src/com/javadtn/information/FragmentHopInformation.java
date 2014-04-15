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
import java.util.Date;

public class FragmentHopInformation implements Serializable {

	private static final long serialVersionUID = -3291728009823025922L;
	private String fromNode;
	private String toNode;
	private Date timeStamp;

	public String getFromNode() {
		return fromNode;
	}

	public String getToNode() {
		return toNode;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public FragmentHopInformation setFromNode(String fromNode) {
		this.fromNode = fromNode;
		return this;
	}

	public FragmentHopInformation setToNode(String toNode) {
		this.toNode = toNode;
		return this;
	}

	public FragmentHopInformation setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
		return this;
	}

}
