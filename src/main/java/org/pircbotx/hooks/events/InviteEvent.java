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

import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * Called when we are invited to a channel by a user.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InviteEvent<T extends PircBotX> extends Event<T> {
	protected final String user;
	protected final String channel;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param user The user that sent the invitation. Provided as a string since
	 *             the user may or may not be in a channel were in
	 * @param channel The channel that we're being invited to. Provided as a string
	 *                since we are not joined to the channel yet
	 */
	public InviteEvent(T bot, String user, String channel) {
		super(bot);
		this.user = user;
		this.channel = channel;
	}

	/**
	 * Respond with a private message to the user who sent the invite
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendMessage(getUser(), response);
	}
}
