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
