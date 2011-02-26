
package org.pircbotx.hooks.managers;

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
	protected final ListenerManager subManager;
	protected final boolean perHook;

	public ThreadedListenerManager() {
		subManager = new GenericListenerManager();
		this.perHook = false;
	}

	public ThreadedListenerManager(boolean perHook) {
		subManager = new GenericListenerManager();
		this.perHook = perHook;
	}

	public ThreadedListenerManager(ListenerManager subManager) {
		this.subManager = subManager;
		this.perHook = false;
	}
	
	public ThreadedListenerManager(ListenerManager subManager, boolean perHook) {
		this.subManager = subManager;
		this.perHook = perHook;
	}

	public boolean addListener(Listener listener) {
		return subManager.addListener(listener);
	}

	public boolean removeListener(Listener listener) {
		return subManager.removeListener(listener);
	}

	public boolean listenerExists(Listener listener) {
		return subManager.listenerExists(listener);
	}

	public Set<Listener> getListeners() {
		return subManager.getListeners();
	}
	
	public void dispatchEvent(final Event event) {
		pool.submit(new Runnable() {
			public void run() {
				subManager.dispatchEvent(event);
			}
		});
	}
}
