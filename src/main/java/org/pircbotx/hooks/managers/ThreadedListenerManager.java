package org.pircbotx.hooks.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ThreadedListenerManager implements ListenerManager {
	protected ExecutorService pool = Executors.newCachedThreadPool();
	protected final boolean perHook;
	protected Set<Listener> listeners = new HashSet<Listener>();

	public ThreadedListenerManager() {
		perHook = false;
	}

	public ThreadedListenerManager(boolean perHook) {
		this.perHook = perHook;
	}

	public ThreadedListenerManager(ExecutorService pool) {
		perHook = false;
		this.pool = pool;
	}

	public ThreadedListenerManager(ExecutorService pool, boolean perHook) {
		this.perHook = perHook;
		this.pool = pool;
	}

	public boolean addListener(Listener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	public Set<Listener> getListeners() {
		return Collections.unmodifiableSet(listeners);
	}

	public boolean listenerExists(Listener listener) {
		return listeners.contains(listener);
	}

	public void dispatchEvent(final Event event) {
		if (perHook)
			//For each Listener, add a new Runnable
			for (final Listener curListener : listeners)
				pool.submit(new Runnable() {
					public void run() {
						try {
							curListener.onEvent(event);
						} catch (Throwable t) {
							event.getBot().logException(t);
						}
					}
				});
		else
			pool.submit(new Runnable() {
				public void run() {
					try {
						for (Listener curListener : listeners)
							curListener.onEvent(event);
					} catch (Throwable t) {
						event.getBot().logException(t);
					}
				}
			});
	}
}
