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
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.hooks.WaitForQueue;

/**
 * Generic ListenerManager based off of a normal event system. This is backed by
 * a simple {@link HashSet}
 * <p>
 * Please note: This is a very basic manager offering little security and
 * features. Any long running listener will block all bot operations since its
 * executed in the same thread. Adding any listeners during bot operation (Eg by
 * using {@link WaitForQueue}) in another thread is risky since the set might be
 * in use already, throwing a {@link ConcurrentModificationException}.
 * <p>
 * @deprecated Due to multiple new functions of PircBotX theat depend on
 * multiple threads, this class is deprecated and is only kept for legacy
 * reasons or special cases. Use of this class will have unexpected results. All
 * bots should now use {@link ThreadedListenerManager}.
 * @author Leon Blakey
 * @see ThreadedListenerManager
 */
@Deprecated
@Slf4j
public class GenericListenerManager extends AbstractListenerManager {
	protected Set<Listener> listeners = new HashSet<Listener>();
	protected ImmutableSet<Listener> listenersImmutable = ImmutableSet.copyOf(listeners);

	public void addListener(Listener listener) {
		listeners.add(listener);
		rebuildListeners();
	}

	public boolean removeListener(Listener listener) {
		boolean result = listeners.remove(listener);
		rebuildListeners();
		return result;
	}

	public ImmutableSet<Listener> getListeners() {
		return listenersImmutable;
	}

	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		for (Listener curListener : listenersImmutable) {
			executeListener(curListener, event);
		}
	}

	public boolean listenerExists(Listener listener) {
		return listenersImmutable.contains(listener);
	}

	public void shutdown(PircBotX bot) {
		//Do nothing since dispatching an event executes all listeners immediately
	}

	protected void rebuildListeners() {
		listenersImmutable = ImmutableSet.copyOf(listeners);
	}
}
