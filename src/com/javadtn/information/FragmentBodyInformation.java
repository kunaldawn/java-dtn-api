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

public class FragmentBodyInformation implements Serializable {
	private static final long serialVersionUID = -7374291597602441010L;
	private ArrayList<FragmentHopInformation> hops;
	private byte[] data;

	public ArrayList<FragmentHopInformation> getHops() {
		return hops;
	}

	public byte[] getData() {
		return data;
	}

	public FragmentBodyInformation setHops(ArrayList<FragmentHopInformation> hops) {
		this.hops = hops;
		return this;
	}

	public FragmentBodyInformation setData(byte[] data) {
		this.data = data;
		return this;
	}

	public FragmentBodyInformation addHop(FragmentHopInformation hop) {
		if (hops == null)
			hops = new ArrayList<FragmentHopInformation>();
		hops.add(hop);
		return this;
	}

}
