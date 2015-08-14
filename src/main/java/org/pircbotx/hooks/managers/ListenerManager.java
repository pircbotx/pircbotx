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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ExceptionEvent;
import org.pircbotx.hooks.events.ListenerExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages {@link Listener}'s and handles dispatching events
 *
 * @author Leon Blakey
 */
public abstract class ListenerManager implements Listener {
	private static final Logger log = LoggerFactory.getLogger(ListenerManager.class);

	/**
	 * Sends event to all appropriate listeners.
	 *
	 * @param event The event to send
	 */
	@Override
	public abstract void onEvent(Event event);

	/**
	 * Add a listener to this ListenerManager
	 *
	 * @param listener The listener to add
	 */
	public abstract void addListener(Listener listener);

	/**
	 * Remove a listener from this ListenerManager
	 *
	 * @param listener The listener to remove
	 * @return True if the listener was removed, false if it didn't exist
	 */
	public abstract boolean removeListener(Listener listener);

	/**
	 * Check if a listener is in this ListenerManager
	 *
	 * @param listener The listener <i>instance</i> to look for
	 * @return True if it the listener exists, false if it doesn't
	 */
	public abstract boolean listenerExists(Listener listener);

	/**
	 * Gets all listeners that are in this ListenerManager
	 *
	 * @return An <b>immutable</b> set of all listeners that are in this
	 * ListenerManager
	 */
	public abstract ImmutableSet<Listener> getListeners();

	public abstract void shutdown(PircBotX bot);
	
	protected void executeListener(Listener listener, Event event) {
		executeListener(listener, event, "Failed in " + getClass().getName());
	}
	
	protected void executeListener(Listener listener, Event event, String debug) {
		try {
			log.trace("Calling listener " + listener + " with event " + event);
			listener.onEvent(event);
		} catch (Exception listenerException) {
			if (event instanceof ExceptionEvent) {
				log.error("Encountered exception while processing {}, NOT dispatching another ExceptionEvent to stop potential StackOverflow",
						event.getClass(),
						listenerException);
			} else {
				onEvent(new ListenerExceptionEvent(event.getBot(), listenerException, debug, listener, event));
			}
		}
	}
	
	@RequiredArgsConstructor
	protected static class ExecuteListenerRunnable implements Runnable {
		protected final ListenerManager listenerManager;
		protected final Listener listener;
		protected final Event event;

		@Override
		public void run() {
			listenerManager.executeListener(listener, event);
		}
	}
}
