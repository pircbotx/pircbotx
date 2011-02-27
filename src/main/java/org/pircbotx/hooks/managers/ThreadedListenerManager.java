package org.pircbotx.hooks.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Synchronized;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * Wraps a ListenerManager and adds multithreading to {@link #dispatchEvent(event)}. 
 *  This makes {@link #dispatchEvent(event)} return almost immediately freeing
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
public class ThreadedListenerManager implements ListenerManager {
	protected ExecutorService pool = Executors.newCachedThreadPool();
	protected final boolean perHook;
	protected Set<Listener> listeners = Collections.synchronizedSet(new HashSet<Listener>());

	/**
	 * Configures with default options: perHook is false and a 
	 * {@link Executors#newCachedThreadPool() cached threadpool} is used
	 */
	public ThreadedListenerManager() {
		perHook = false;
	}

	/**
	 * Configures with default {@link Executors#newCachedThreadPool() cached threadpool}
	 * and the specified perHook mode
	 * @param perHook 
	 */
	public ThreadedListenerManager(boolean perHook) {
		this.perHook = perHook;
	}

	/**
	 * Configures with default perHook mode (false) and specified 
	 * {@link ExecutorService}
	 * @param pool 
	 */
	public ThreadedListenerManager(ExecutorService pool) {
		perHook = false;
		this.pool = pool;
	}

	/**
	 * Configures with specified perHook mode and {@link ExecutorService}
	 * @param pool
	 * @param perHook 
	 */
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

	@Synchronized("listeners")
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
						for (Listener curListener : listeners)
							try {
								curListener.onEvent(event);
							} catch (Throwable t) {
								event.getBot().logException(t);
							}
					}
			});
	}
}
