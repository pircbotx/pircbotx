package org.pircbotx.hooks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/**
 * Generic ListenerManager based off of a normal event system. This is backed
 * by {@link HookUtils#callListener(org.pircbotx.hooks.Event, org.pircbotx.hooks.Listener)}
 * and a simple HashSet. 
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class GenericListenerManager implements ListenerManager {
	protected Set<Listener> listeners = new HashSet<Listener>();

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public Set<Listener> getListeners() {
		return Collections.unmodifiableSet(listeners);
	}

	public void dispatchEvent(Event event) {
		for (Listener curListener : listeners)
			HookUtils.callListener(event, curListener);
	}
}
