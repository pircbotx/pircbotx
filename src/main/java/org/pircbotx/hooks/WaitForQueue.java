/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import java.io.Closeable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.pircbotx.PircBotX;

/**
 * Stores all events in a queue for processing. This is useful for sequential 
 * processing of many similar events.
 * <p>
 * Example:
 * <code>
 * WaitForQueue queue = new WaitForQueue();
 * while(true) {
 *     MessageEvent mevent = queue.waitFor(MessageEvent.class);
 *     //Process event
 * }
 * queue.done();
 * </code>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class WaitForQueue implements Closeable {
	protected final PircBotX bot;
	protected LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
	protected WaitForQueueListener listener;

	/**
	 * Create and store a queue listener in the specified bot's ListenerManager.
	 * It will be removed when {@link #done()} is called
	 * @param bot 
	 */
	public WaitForQueue(PircBotX bot) {
		this.bot = bot;
		bot.getConfiguration().getListenerManager().addListener(listener = new WaitForQueueListener());
	}

	public <E extends Event> E waitFor(Class<E> eventClass) throws InterruptedException {
		return waitFor(eventClass, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	/**
	 * Wait for events of the specified event class to appear in the queue. If 
	 * the event was dispatched before this is called, it will return immediately. 
	 * Events that do not match the specified event class are discarded
	 * @param <E>
	 * @param eventClass
	 * @return
	 * @throws InterruptedException 
	 */
	public <E extends Event> E waitFor(Class<E> eventClass, long timeout, TimeUnit unit) throws InterruptedException {
		if (eventClass == null)
			throw new IllegalArgumentException("Can't wait for null event");
		while (true) {
			Event curEvent = eventQueue.poll(timeout, unit);
			if (eventClass.isInstance(curEvent))
				return (E) curEvent;
		}
	}

	/**
	 * Shuts down the queue; VERY important to call when finished. Since this class 
	 * stores every dispatched event, failure to close will eventually cause you
	 * to run out of memory
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
