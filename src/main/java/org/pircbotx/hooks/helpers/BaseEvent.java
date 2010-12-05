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

import org.pircbotx.PircBotX;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class BaseEvent<T extends PircBotX> {
	protected final long timestamp;
	protected final T bot;
	
	public BaseEvent(T bot) {
		this.timestamp = System.currentTimeMillis();
		this.bot = bot;
	}

	/**
	 * Returns the {@link PircBotX} instance that this event originally came from
	 * @return A {@link PircBotX} instance
	 */
	protected final T getBot() {
		return bot;
	}

	/**
	 * Returns the timestamp of when the event was created
	 * @return A timestamp as a long
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
