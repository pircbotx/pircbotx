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
package org.pircbotx.hooks.managers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * ListenerManager that runs individual listeners in their own thread per event.
 *
 * @author Leon Blakey
 */
@Slf4j
public class ThreadedListenerManager extends AbstractListenerManager {
	protected static final AtomicInteger MANAGER_COUNT = new AtomicInteger();
	protected final int managerNumber;
	protected ExecutorService pool;
	protected Set<Listener> listeners = Collections.synchronizedSet(new HashSet<Listener>());
	protected final Multimap<PircBotX, ManagedFutureTask> runningListeners = LinkedListMultimap.create();

	/**
	 * Configures with default cached thread thread pool.
	 */
	public ThreadedListenerManager() {
		managerNumber = MANAGER_COUNT.getAndIncrement();
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
				.namingPattern("listenerPool" + managerNumber + "-thread%d")
				.daemon(true)
				.build();
		ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
		defaultPool.allowCoreThreadTimeOut(true);
		this.pool = defaultPool;
	}

	/**
	 * Configures with specified thread pool
	 *
	 * @param pool Thread pool to run listeners in
	 */
	public ThreadedListenerManager(ExecutorService pool) {
		managerNumber = MANAGER_COUNT.getAndIncrement();
		this.pool = pool;
	}

	@Override
	public void addListener(Listener listener) {
		getListenersReal().add(listener);
	}

	@Override
	public boolean removeListener(Listener listener) {
		return getListenersReal().remove(listener);
	}

	@Override
	public ImmutableSet<Listener> getListeners() {
		return ImmutableSet.copyOf(getListenersReal());
	}

	protected Set<Listener> getListenersReal() {
		return listeners;
	}

	@Override
	public boolean listenerExists(Listener listener) {
		return getListeners().contains(listener);
	}

	@Override
	@Synchronized("listeners")
	public void onEvent(Event event) {
		super.onEvent(event);
		//For each Listener, add a new Runnable
		for (Listener curListener : getListenersReal())
			submitEvent(pool, curListener, event);
	}

	protected void submitEvent(ExecutorService pool, final Listener listener, final Event event) {
		pool.execute(new ManagedFutureTask(listener, event, new ExecuteListenerRunnable(this, listener, event)));
	}

	/**
	 * Shuts down the internal thread pool. If you need to do more a advanced
	 * shutdown, the pool is returned.
	 *
	 * @return The internal thread pool the ThreadedListenerManager uses
	 */
	public ExecutorService shutdown() {
		pool.shutdown();
		return pool;
	}

	public void shutdown(PircBotX bot) {
		//Make local copy to avoid deadlocking ManagedFutureTask when it removes itself
		List<ManagedFutureTask> remainingTasks;
		synchronized (runningListeners) {
			remainingTasks = Lists.newArrayList(runningListeners.get(bot));
		}

		//Wait for all remaining tasks to return
		for (ManagedFutureTask curFuture : remainingTasks)
			try {
				log.debug("Waiting for listener " + curFuture.getListener() + " to execute event " + curFuture.getEvent());
				curFuture.get();
			} catch (Exception e) {
				throw new RuntimeException("Cannot shutdown listener " + curFuture.getListener() + " executing event " + curFuture.getEvent(), e);
			}
	}

	@Getter
	public class ManagedFutureTask extends FutureTask<Void> {
		protected final Listener listener;
		protected final Event event;

		public ManagedFutureTask(Listener listener, Event event, Runnable run) {
			super(run, null);
			this.listener = listener;
			this.event = event;
			if (event.getBot() != null)
				synchronized (runningListeners) {
					runningListeners.put(event.getBot(), this);
				}
		}

		@Override
		protected void done() {
			if (event.getBot() != null)
				synchronized (runningListeners) {
					runningListeners.remove(event.getBot(), this);
				}
		}
	}
}
