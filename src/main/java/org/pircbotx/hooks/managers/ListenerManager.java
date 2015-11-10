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
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * Track {@link Listener}'s and handles dispatching events
 *
 * @author Leon Blakey
 */
public interface ListenerManager {
	/**
	 * Sends event to all appropriate listeners.
	 *
	 * @param event The event to send
	 */
	public void onEvent(Event event);

	/**
	 * Add a listener to this ListenerManager
	 *
	 * @param listener The listener to add
	 */
	public void addListener(Listener listener);

	/**
	 * Remove a listener from this ListenerManager
	 *
	 * @param listener The listener to remove
	 * @return True if the listener was removed, false if it didn't exist
	 */
	public boolean removeListener(Listener listener);

	/**
	 * Check if a listener is in this ListenerManager
	 *
	 * @param listener The listener <i>instance</i> to look for
	 * @return True if it the listener exists, false if it doesn't
	 */
	public boolean listenerExists(Listener listener);

	/**
	 * Gets all listeners that are in this ListenerManager
	 *
	 * @return An <b>immutable</b> set of all listeners that are in this
	 * ListenerManager
	 */
	public ImmutableSet<Listener> getListeners();

	public void shutdown(PircBotX bot);
}
