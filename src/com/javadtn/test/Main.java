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
package com.javadtn.test;

import com.javadtn.logger.Logger;
import com.javadtn.manager.DataManager;
import com.javadtn.manager.EventManager;
import com.javadtn.manager.FragmentManager;
import com.javadtn.manager.InvestigationManager;
import com.javadtn.manager.NeighbourManager;
import com.javadtn.manager.SettingsManager;

public class Main {
	public static void main(String[] args) {
		System.out.println("STARTING TESTING PROTOCOL...");
		SettingsManager.NETWORKING_BROADCAST_IP = args[0];
		SettingsManager.NETWORKING_HOST_IP = args[1];
		SettingsManager.NETWORKING_NODEID = args[2];
		initDTN();
	}

	private static void initDTN() {

		EventManager eventManager = new EventManager();
		DataManager dataManager = new DataManager();
		FragmentManager fragmentManager = new FragmentManager(dataManager,
				eventManager);
		NeighbourManager neighbourManager = new NeighbourManager(dataManager,
				eventManager);
		new InvestigationManager(neighbourManager, eventManager, dataManager,
				fragmentManager);
		Logger logger = new Logger("log.txt");
		eventManager.registerEventListerers(logger);
	}
}
