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
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelModeEvent;

/**
 * Called when a user limit is set for a channel. The number of users in the
 * channel cannot exceed this limit.
 * <p>
 * This is a type of mode change and therefor is also dispatched in a
 * {@link org.pircbotx.hooks.events.ModeEvent}
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SetChannelLimitEvent extends Event implements GenericChannelModeEvent {
	@Getter(onMethod = @_(
			@Override))
	protected final Channel channel;
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	@Getter(onMethod = @_(
			@Override))
	protected final User user;
	/**
	 * The maximum number of users that may be in this channel at the same time.
	 */
	protected final int limit;

	public SetChannelLimitEvent(PircBotX bot, @NonNull Channel channel, @NonNull UserHostmask userHostmask, User user, int limit) {
		super(bot);
		this.channel = channel;
		this.userHostmask = userHostmask;
		this.user = user;
		this.limit = limit;
	}

	/**
	 * Respond by send a message in the channel to the user that set the mode in
	 * <code>user: message</code> format
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(@Nullable String response) {
		getChannel().send().message(getUser(), response);
	}
}
