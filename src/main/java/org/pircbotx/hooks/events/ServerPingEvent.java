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
package org.pircbotx.hooks.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.CoreHooks;

/**
 * The actions to perform when a PING request comes from the server.
 *  <p>
 * {@link CoreHooks} automatically responds correctly. Unless {@link CoreHooks}
 * is removed from the {@link PircBotX#getListenerManager() bot's ListenerManager},
 * Listeners of this event should <b>not</b> send a response as the user will get
 * two responses
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerPingEvent<T extends PircBotX> extends Event<T> {
	protected final String response;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param response The response that should be given back in your PONG.
	 */
	public ServerPingEvent(T bot, String response) {
		super(bot);
		this.response = response;
	}
	
	/**
	 * Respond with a <i>raw line</i> to the server
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendRawLineViaQueue(response);
	}
}
