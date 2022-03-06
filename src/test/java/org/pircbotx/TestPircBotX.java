/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
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
package org.pircbotx;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class TestPircBotX extends PircBotX {
	public static class EventQueueListener implements Listener {
		public final Queue<Event> eventQueue = new LinkedList<>();

		@Override
		public void onEvent(Event event) throws Exception {
			eventQueue.add(event);
		}
	}

	public final Queue<String> outputQueue = new LinkedList<>();
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

	/**
	 * After simulating a server response, call this to get a specific Event
	 * from the Event set. Note that if the event does not exist an Assertion
	 * error will be thrown. Also note that only the last occurrence of the
	 * event will be fetched
	 *
	 * @param clazz The class of the event type
	 * @param errorMessage An error message if the event type does not exist
	 * @return A single requested event
	 */
	@SuppressWarnings("unchecked")
	public <E> E getTestEvent(Class<E> clazz, String errorMessage) {
		E cevent = null;
		for (Event curEvent : eventQueue) {
			log.debug("Dispatched event: " + curEvent);
			if (curEvent.getClass().isAssignableFrom(clazz))
				cevent = (E) curEvent;
		}
		//Failed, does not exist
		assertNotNull(cevent, errorMessage);
		return cevent;
	}

	public <E> E getTestEvent(Class<E> clazz) {
		return getTestEvent(clazz, clazz.getSimpleName() + " not dispatched");
	}
	
}
