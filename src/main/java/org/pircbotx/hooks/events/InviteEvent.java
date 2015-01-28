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
package org.pircbotx.hooks.events;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericUserEvent;

/**
 * Called when we are invited to a channel by a user.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InviteEvent extends Event implements GenericUserEvent {
	/**
	 * The user that sent the invite.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	/**
	 * The user hostmask that sent the invite.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * The channel that we're being invited to. Provided as a string since we
	 * are not joined to the channel yet
	 */
	protected final String channel;

	public InviteEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, @NonNull String channel) {
		super(bot);
		this.user = user;
		this.userHostmask = userHostmask;
		this.channel = channel;
	}

	/**
	 * Respond with a private message to the user who sent the invite
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getUserHostmask().send().message(response);
	}
}
