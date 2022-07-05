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
package org.pircbotx.hooks;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericEvent;

/**
 * Stores all events in a queue for processing. This is useful for sequential
 * processing of many similar events.
 * <p>
 * Example:
 * <pre>
 * WaitForQueue queue = new WaitForQueue();
 * while(true) {
 *     MessageEvent mevent = queue.waitFor(MessageEvent.class);
 *     //Process event
 * }
 * queue.done();
 * </pre>
 */
@Slf4j
public class WaitForQueue implements Closeable {
	protected final PircBotX bot;
	protected LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
	protected WaitForQueueListener listener;

	/**
	 * Create and store a queue listener in the specified bot's ListenerManager.
	 * It will be removed when {@link #close() } is called
	 *
	 * @param bot
	 */
	public WaitForQueue(@NonNull PircBotX bot) {
		this.bot = bot;
		bot.getConfiguration().getListenerManager().addListener(listener = new WaitForQueueListener());
	}

	/**
	 * Testing constructor with 0 init
	 */
	WaitForQueue() {
		this.bot = null;
	}

	/**
	 * Wait indefinitely for the specified event to be dispatched
	 *
	 * @param <E> Event class
	 * @param eventClass The event class to wait for
	 * @return The event
	 * @throws InterruptedException
	 * @see #waitFor(java.util.List, long, java.util.concurrent.TimeUnit)
	 */
	public <E extends GenericEvent> E waitFor(@NonNull Class<E> eventClass) throws InterruptedException {
		return waitFor(eventClass, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	/**
	 * Wait indefinitely for the one of the specified events to be dispatched
	 *
	 * @param eventClasses List of events to wait for
	 * @return One of the possible events
	 * @throws InterruptedException
	 * @see #waitFor(java.util.List, long, java.util.concurrent.TimeUnit)
	 */
	public <E extends GenericEvent> Event waitFor(@NonNull Class<? extends E>... eventClasses) throws InterruptedException {
		//Work around generics problems
		return waitFor(Arrays.asList(eventClasses));
	}

	/**
	 * Wait indefinitely for the one of the specified events to be dispatched
	 *
	 * @param eventClasses List of events to wait for
	 * @return One of the possible events
	 * @throws InterruptedException
	 * @see #waitFor(java.util.List, long, java.util.concurrent.TimeUnit)
	 */
	public <E extends GenericEvent> Event waitFor(@NonNull List<Class<? extends E>> eventClasses) throws InterruptedException {
		return waitFor(eventClasses, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	@SuppressWarnings("unchecked")
	public <E extends GenericEvent> E waitFor(@NonNull Class<E> eventClass, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
		List<Class<E>> eventList = new ArrayList<Class<E>>();
		eventList.add(eventClass);
		return (E) waitFor((List<Class<? extends E>>) (Object) eventList, timeout, unit);
	}

	/**
	 * Wait for events of the specified event class to appear in the queue. If
	 * the event was dispatched before this is called, it will return
	 * immediately. Events that do not match the specified event class are
	 * discarded
	 *
	 * @param eventClasses Events to wait for
	 * @param timeout Timeout value
	 * @param unit Unit of timeout value
	 * @return One of the possible events or null if timed out
	 * @throws InterruptedException
	 */
	public <E extends GenericEvent> Event waitFor(@NonNull List<Class<? extends E>> eventClasses, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
		while (true) {
			Event curEvent = eventQueue.poll(timeout, unit);
			//When poll times out it returns null. Repeat that behavior here
			if (curEvent == null)
				return null;
			for (Class<? extends GenericEvent> curEventClass : eventClasses)
				if (curEventClass.isInstance(curEvent))
					return curEvent;
		}
	}

	/**
	 * Shuts down the queue; VERY important to call when finished. Since this
	 * class stores every dispatched event, failure to close will eventually
	 * cause you to run out of memory
	 */
	@Override
	public void close() {
		bot.getConfiguration().getListenerManager().removeListener(listener);
		eventQueue.clear();
	}

	protected class WaitForQueueListener implements Listener {
		public void onEvent(Event event) throws Exception {
			eventQueue.add(event);
		}
	}
}
