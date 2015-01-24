/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Queue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 *
 * @author leon
 */
@Slf4j
public class TestPircBotX extends PircBotX {
	public static class EventQueueListener implements Listener {
		public final Queue<Event> eventQueue = Lists.newLinkedList();

		@Override
		public void onEvent(Event event) throws Exception {
			eventQueue.add(event);
		}
	}

	public final Queue<String> outputQueue = Lists.newLinkedList();
	public final Queue<Event> eventQueue;
	protected final EventQueueListener listener;
	@Getter
	protected boolean closed = false;

	public TestPircBotX(Configuration.Builder configuration) {
		super(configuration.addListener(new EventQueueListener()).buildConfiguration());
		for (Listener curListener : configuration.getListenerManager().getListeners())
			if (curListener instanceof EventQueueListener) {
				listener = (EventQueueListener) curListener;
				eventQueue = listener.eventQueue;
				return;
			}
		throw new RuntimeException("Listener not added, should be impossible");
	}

	@Override
	protected void sendRawLineToServer(String line) throws IOException {
		outputQueue.add(line);
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void close() {
		closed = true;
	}

}
