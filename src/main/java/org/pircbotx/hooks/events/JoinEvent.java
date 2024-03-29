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

import com.google.common.collect.ImmutableMap;
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
import org.pircbotx.hooks.types.GenericChannelUserEvent;

/**
 * This event is dispatched whenever someone (possibly us) joins a channel which
 * we are on.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JoinEvent extends Event implements GenericChannelUserEvent {
	/**
	 * The channel which somebody joined.
	 */
	@Getter(onMethod = @__({
			@Override}))
	protected final Channel channel;
	/**
	 * The user who joined the channel.
	 */
	@Getter(onMethod = @__({
			@Override,
			@Nullable}))
	protected final User user;
	/**
	 * The user hostmask who joined the channel.
	 */
	@Getter(onMethod = @__({
			@Override}))
	protected final UserHostmask userHostmask;
	/**
	 * The IrcV3 tags
	 */
	protected final ImmutableMap<String, String> tags;

	public JoinEvent(PircBotX bot, @NonNull Channel channel, @NonNull UserHostmask userHostmask, User user, ImmutableMap<String, String> tags) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.userHostmask = userHostmask;
    this.tags = tags;
	}

	/**
	 * Respond with a channel message in <code>user: message</code> format to
	 * the user that joined
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getChannel().send().message(getUser(), response);
	}
}
