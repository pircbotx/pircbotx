/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
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
package org.pircbotx.hooks.events;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This event is dispatched once we successfully connected to the IRC server.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocketConnectEvent extends Event {
	/**
	 * Default constructor to setup object. Timestamp is automatically set to
	 * current time as reported by {@link System#currentTimeMillis() }
	 */
	public SocketConnectEvent(PircBotX bot) {
		super(bot);
	}

	/**
	 * Responds by sending a <b>raw line</b> to the server.
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getBot().sendRaw().rawLine(response);
	}
}
