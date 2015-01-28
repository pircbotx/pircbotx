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
import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelModeRecipientEvent;

/**
 * This event is dispatched whenever someone (possibly us) is kicked from any of
 * the channels that we are in.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KickEvent extends Event implements GenericChannelModeRecipientEvent {
	/**
	 * The channel from which the recipient was kicked.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final Channel channel;
	/**
	 * The user hostmask that performed the kick.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * The user who performed the kick.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	/**
	 * The unfortunate recipient hostmask of the kick.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask recipientHostmask;
	/**
	 * The unfortunate recipient of the kick.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User recipient;
	/**
	 * The reason given by the user who performed the kick.
	 */
	protected final String reason;

	public KickEvent(PircBotX bot, @NonNull Channel channel, @NonNull UserHostmask userHostmask, User user,
			@NonNull UserHostmask recipientHostmask, User recipient, @NonNull String reason) {
		super(bot);
		this.channel = channel;
		this.userHostmask = userHostmask;
		this.user = user;
		this.recipientHostmask = recipientHostmask;
		this.recipient = recipient;
		this.reason = reason;
	}

	/**
	 * Respond with a channel message in <code>user: message</code> format to
	 * the <i>user that preformed the kick</i>
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getChannel().send().message(getUser(), response);
	}
}
