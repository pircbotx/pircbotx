/*
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx.hooks.helpers;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface ListenerManager<I extends Listener> {
	/**
	 * Sends event to all appropriate listeners.
	 * @param event The event to send
	 */
	public void dispatchEvent(BaseEvent event);

	/**
	 * Adds an I listener to the list of
	 * listeners for an event.
	 * @param listener The listener to add
	 */
	public void addListener(I listener);

	/**
	 * Removes an I Listener from the list
	 * listeners for an event
	 * @param listener
	 */
	public void removeListener(I listener);

	/**
	 * Gets all the listeners to an event
	 * @return An <b>Immutable set</b> of I listeners
	 */
	public Set<I> getListeners();
}
