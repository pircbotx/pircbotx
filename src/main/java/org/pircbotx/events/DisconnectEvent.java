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
package org.pircbotx.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;

/**
 * This method carries out the actions to be performed when the PircBotX
 * gets disconnected.  This may happen if the PircBotX quits from the
 * server, or if the connection is unexpectedly lost.
 *  <p>
 * Disconnection from the IRC server is detected immediately if either
 * we or the server close the connection normally. If the connection to
 * the server is lost, but neither we nor the server have explicitly closed
 * the connection, then it may take a few minutes to detect (this is
 * commonly referred to as a "ping timeout").
 *  <p>
 * If you wish to get your IRC bot to automatically rejoin a server after
 * the connection has been lost, then this is probably the ideal method to
 * override to implement such functionality.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DisconnectEvent extends BaseEvent {
	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param timestamp
	 */
	public <T extends PircBotX> DisconnectEvent(T bot) {
		super(bot);
	}
}
