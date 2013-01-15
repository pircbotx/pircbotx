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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.pircbotx.PircBotX;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class WaitForQueue {
	protected final PircBotX bot;
	protected BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
	protected WaitForQueueListener listener;

	public WaitForQueue(PircBotX bot) {
		this.bot = bot;
		bot.getListenerManager().addListener(listener = new WaitForQueueListener());
	}

	public <E extends Event> E waitFor(Class<E> eventClass) throws InterruptedException {
		if (eventClass == null)
			throw new IllegalArgumentException("Can't wait for null event");
		while (true) {
			Event curEvent = eventQueue.take();
			if (eventClass.isInstance(curEvent))
				return (E) curEvent;
		}
	}
	
	public void done() {
		bot.getListenerManager().removeListener(listener);
		eventQueue.clear();
	}

	protected class WaitForQueueListener implements Listener {
		public void onEvent(Event event) throws Exception {
			eventQueue.add(event);
		}
	}
}
