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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Synchronized;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * Wraps a ListenerManager and adds multithreading to {@link #dispatchEvent(org.pircbotx.hooks.Event)}. 
 *  This makes {@link #dispatchEvent(org.pircbotx.hooks.Event)} return almost immediately freeing
 * up the bot to execute other incoming events
 * <p>
 * This multithreading can be controlled with the perHook flag in the constructors.
 * <ul>
 *    <li><code>false</code> - Default. Listeners are executed sequentially in
 *                             a separate thread per event.</li>
 *    <li><code>true</code> - Every listener is executed in a different thread 
 *                            all at once per event.</li>
 * </ul>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ThreadedListenerManager<E extends PircBotX> implements ListenerManager<E> {
	protected ExecutorService pool;
	protected Set<Listener> listeners = Collections.synchronizedSet(new HashSet<Listener>());
	protected AtomicLong currentId = new AtomicLong();
	protected static AtomicInteger poolCount = new AtomicInteger();

	/**
	 * Configures with default options: perHook is false and a 
	 * {@link Executors#newCachedThreadPool() cached threadpool} is used
	 */
	public ThreadedListenerManager() {
		pool = Executors.newCachedThreadPool(new ListenerThreadFactory());
	}

	/**
	 * Configures with default perHook mode (false) and specified 
	 * {@link ExecutorService}
	 * @param pool 
	 */
	public ThreadedListenerManager(ExecutorService pool) {
		this.pool = pool;
	}

	public boolean addListener(Listener listener) {
		return getListenersReal().add(listener);
	}

	public boolean removeListener(Listener listener) {
		return getListenersReal().remove(listener);
	}

	public Set<Listener> getListeners() {
		return Collections.unmodifiableSet(getListenersReal());
	}
	
	protected Set<Listener> getListenersReal() {
		return listeners;
	}

	public boolean listenerExists(Listener listener) {
		return getListenersReal().contains(listener);
	}

	@Synchronized("listeners")
	public void dispatchEvent(final Event<E> event) {
		//For each Listener, add a new Runnable
		for (final Listener curListener : getListenersReal())
			pool.submit(new Runnable() {
				public void run() {
					try {
						curListener.onEvent(event);
					} catch (Throwable t) {
						event.getBot().logException(t);
					}
				}
			});
	}

	public void setCurrentId(long currentId) {
		this.currentId.set(currentId);
	}

	public long getCurrentId() {
		return currentId.get();
	}

	public long incrementCurrentId() {
		return currentId.getAndIncrement();
	}
	
	protected class ListenerThreadFactory implements ThreadFactory {
		protected final AtomicInteger threadCount = new AtomicInteger();
		protected String prefix = "pool-listenerThread-";

		public ListenerThreadFactory() {
			prefix = "pool " + poolCount + "-listenerThread-";
		}
		
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, prefix + threadCount.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		}
	}
}
