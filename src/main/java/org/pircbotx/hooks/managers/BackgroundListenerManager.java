/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * A standard ThreadListenerManager with dedicated background threads. Normal
 * Listeners execute in the same Thread Pool. Background Listeners however execute
 * in their own dedicated single threads separate from the rest
 * <p>
 * This class is useful for logging listeners or any other listener that needs
 * to process events one at a time instead of simultaneously. 
 * <p>
 * To mark a listener as a background listener, use {@link #addListener(org.pircbotx.hooks.Listener, boolean) }
 * with isBackground set to true
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class BackgroundListenerManager extends ThreadedListenerManager {
	protected Map<Listener, ExecutorService> backgroundListeners = new HashMap();

	public boolean addListener(Listener listener, boolean isBackground) {
		if (!isBackground)
			return super.addListener(listener);
		backgroundListeners.put(listener, Executors.newSingleThreadExecutor(new BackgroundThreadFactory(poolCount.get())));
		return true;
	}

	@Override
	public void dispatchEvent(Event event) {
		//Dispatch to both standard listeners and background listeners
		super.dispatchEvent(event);
		for (Map.Entry<Listener, ExecutorService> curEntry : backgroundListeners.entrySet())
			submitEvent(curEntry.getValue(), curEntry.getKey(), event);
	}

	@Override
	public Set<Listener> getListeners() {
		Set<Listener> allListeners = new HashSet(listeners);
		allListeners.addAll(backgroundListeners.keySet());
		return Collections.unmodifiableSet(allListeners);
	}

	@Override
	public boolean removeListener(Listener listener) {
		if (backgroundListeners.containsKey(listener))
			return backgroundListeners.remove(listener) != null;
		else
			return super.removeListener(listener);
	}

	protected static class BackgroundThreadFactory implements ThreadFactory {
		protected static AtomicInteger backgroundCount = new AtomicInteger();
		protected AtomicInteger threadCount = new AtomicInteger();
		protected String prefix;

		public BackgroundThreadFactory(int poolNum) {
			prefix = "pool-" + poolNum + "-backgroundThread-" + backgroundCount.getAndIncrement();
		}

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, prefix + threadCount.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		}
	}
}
