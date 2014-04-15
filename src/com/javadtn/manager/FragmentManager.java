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

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.javadtn.data.Fragment;
import com.javadtn.data.Message;
import com.javadtn.information.FragmentBodyInformation;
import com.javadtn.information.MessageInformation;
import com.javadtn.listner.MessageListener;

public class FragmentManager {
	private DataManager dataManager;
	private EventManager eventManager;

	public FragmentManager(DataManager dataManager, EventManager eventManager) {
		this.dataManager = dataManager;
		this.eventManager = eventManager;
	}

	public void doFragmentAndSave(Message message) {
		try {
			if (SettingsManager.FRAGMENTATION_ENABLED) {
				int bodySize = message.getMessageData().length;
				int fragcount = (int) Math.ceil((double) bodySize
						/ (double) SettingsManager.FRAGMENTATION_SIZE_LIMIT);
				eventManager.publishEvent(EventManager.FRAGMENT_FRAGMENTATION,
						"MESSAGEID=" + message.getMessageId(), "FRAGMENTCOUNT="
								+ fragcount);

				for (int i = 1; i <= fragcount; i++) {
					Fragment fragment = new Fragment();
					fragment.setMessageId(message.getMessageId());
					fragment.setFragmentId(i);
					fragment.setNumberOfFragments(fragcount);
					fragment.setTtl(message.getMessageTtl());
					fragment.setSourceId(SettingsManager.NETWORKING_NODEID);
					fragment.setDestinationId(message.getDestinationId());
					int min = (i - 1)
							* SettingsManager.FRAGMENTATION_SIZE_LIMIT;
					int max = Math.min(bodySize,
							(i * SettingsManager.FRAGMENTATION_SIZE_LIMIT));
					FragmentBodyInformation body = new FragmentBodyInformation();
					body.setData(Arrays.copyOfRange(message.getMessageData(),
							min, max));
					body.setHops(message.getHops());
					fragment.setBody(body);
					dataManager.addMessageFragment(fragment);
				}
			} else {
				Fragment fragment = new Fragment();
				fragment.setMessageId(message.getMessageId());
				fragment.setFragmentId(1);
				fragment.setNumberOfFragments(1);
				fragment.setTtl(message.getMessageTtl());
				fragment.setSourceId(SettingsManager.NETWORKING_NODEID);
				fragment.setDestinationId(message.getDestinationId());
				FragmentBodyInformation body = new FragmentBodyInformation();
				body.setData(message.getMessageData());
				body.setHops(message.getHops());
				fragment.setBody(body);
				dataManager.addMessageFragment(fragment);
			}
		} catch (Exception ex) {
		}
	}

	public void doReAssembleAndSignal(MessageInformation container) {
		if (container.getFragmentCount() == container.getFragments().size()
				&& container.getFragmentCount() > 0) {

			Message message = new Message();
			int totalSize = 0;
			long avgTtl = 0;
			for (Fragment mf : container.getFragments()) {
				totalSize += mf.getBody().getData().length;
				avgTtl += mf.getTtl();
				message.addHops(mf.getBody().getHops());
			}
			avgTtl = avgTtl / container.getFragmentCount();
			message.setMessageTtl(avgTtl);
			message.setMessageId(container.getFragments().get(0).getMessageId());

			byte[] finalData = new byte[totalSize];
			ByteBuffer buffer = ByteBuffer.wrap(finalData);
			for (Fragment mf : container.getFragments())
				buffer.put(mf.getBody().getData());
			message.setMessageData(finalData);
			for (MessageListener listener : eventManager.getMessageListeners())
				listener.onReceive(message);

			eventManager.publishEvent(EventManager.FRAGMENT_REASSEMBLE,
					"MESSAGEID=" + message.getMessageId());

		}

	}
}
