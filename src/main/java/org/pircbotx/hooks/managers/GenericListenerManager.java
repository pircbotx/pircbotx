/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.PircBotX;

/**
 * Generic ListenerManager based off of a normal event system. This is backed
 * by a simple {@link HashSet} 
 *  <p>
 * Please note: This is a very basic manager offering little security and 
 * features. Any long running listener will block all bot operations since its
 * executed in the same thread. Adding any listeners during bot operation 
 * ( Eg {@link PircBotX#waitFor(java.lang.Class) PircBotX's waitFor method}) in
 * another thread is risky since the set might be in use already, throwing a 
 * {@link ConcurrentModificationException}. 
 * <p>
 * Due to these issues any bot of complexity should use {@link ThreadedListenerManager},
 * however most simple bots can use this just fine. 
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class GenericListenerManager implements ListenerManager {
	protected Set<Listener> listeners = new HashSet<Listener>();

	public boolean addListener(Listener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	public Set<Listener> getListeners() {
		return Collections.unmodifiableSet(listeners);
	}

	public void dispatchEvent(Event event) {
		try {
			for (Listener curListener : listeners)
				curListener.onEvent(event);
		} catch (Throwable t) {
			event.getBot().logException(t);
		}
	}

	public boolean listenerExists(Listener listener) {
		return listeners.contains(listener);
	}
}
