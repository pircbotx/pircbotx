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
package org.pircbotx.hooks.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when a reconnect happens
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReconnectEvent<T extends PircBotX> extends Event<T> {
	protected boolean success;
	protected Exception ex;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 */
	public ReconnectEvent(T bot, boolean success, Exception ex) {
		super(bot);
		this.success = success;
		this.ex = ex;
	}

	/**
	 * Does NOT respond to the server! This will throw an {@link UnsupportedOperationException} 
	 * since we can't respond to a server we might not be connected to yet. 
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		throw new UnsupportedOperationException("Attepting to respond to a reconnected server");
	}
}
