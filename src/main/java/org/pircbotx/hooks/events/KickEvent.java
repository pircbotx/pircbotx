/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
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
import org.pircbotx.hooks.types.GenericChannelUserEvent;

/**
 * This event is dispatched whenever someone (possibly us) is kicked from
 * any of the channels that we are in.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KickEvent<T extends PircBotX> extends Event<T> implements GenericChannelUserEvent<T> {
	@Getter(onMethod = @_(@Override))
	protected final Channel channel;
	@Getter(onMethod = @_(@Override))
	protected final User user;
	protected final User recipient;
	protected final String reason;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel from which the recipient was kicked.
	 * @param user The user who performed the kick.
	 * @param recipient The unfortunate recipient of the kick.
	 * @param reason The reason given by the user who performed the kick.
	 */
	public KickEvent(T bot, @NonNull Channel channel, @NonNull User user, @NonNull User recipient, @NonNull String reason) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.recipient = recipient;
		this.reason = reason;
	}

	/**
	 * Respond with a channel message in
	 * <code>user: message</code> format to
	 * the <i>user that preformed the kick</i>
	 * @param response The response to send
	 */
	@Override
	public void respond(@Nullable String response) {
		getChannel().send().message(getUser(), response);
	}
}
