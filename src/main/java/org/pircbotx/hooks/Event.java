/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.hooks;

import org.pircbotx.PircBotX;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public abstract class Event<T extends PircBotX> {
	protected final long timestamp;
	protected final T bot;
	protected final long id;

	public Event(T bot) {
		this.timestamp = System.currentTimeMillis();
		this.bot = bot;
		this.id = bot.getListenerManager().incrementCurrentId();
	}

	/**
	 * Returns the {@link PircBotX} instance that this event originally came from
	 * @return A {@link PircBotX} instance
	 */
	public T getBot() {
		return bot;
	}

	/**
	 * Returns the timestamp of when the event was created
	 * @return A timestamp as a long
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the id of this event. This id is guaranteed to be unique in the 
	 * context of other events dispatched from the same listener managers
	 * @return The id of this event
	 */
	public long getId() {
		return id;
	}

	/**
	 * A simple abstract method that all events must implement to respond to an
	 * event happening. All implementing classes should delegate to the sendMessage
	 * or other relevant methods in the main PircBotX class, not with custom lines
	 * and calls to {@link PircBotX#sendRawLine(java.lang.String) }
	 * @param response The response to send
	 */
	public abstract void respond(String response);
}
