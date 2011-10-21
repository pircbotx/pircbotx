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
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * This event is dispatched when the Motd is finished being sent. Motd lines are
 * separated by <code>\n</code>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MotdEvent<T extends PircBotX> extends Event<T> {
	protected final String motd;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param motd The full motd separated by newlines (<code>\n</code>)
	 */
	public MotdEvent(T bot, String motd) {
		super(bot);
		this.motd = motd;
	}

	/**
	 * Responds by sending a <b>raw line</b> to the server.
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendRawLine(response);
	}
}
