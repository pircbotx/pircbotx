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

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import java.util.Map;
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
import org.pircbotx.hooks.types.GenericMessageEvent;

/**
 * Used whenever a message is sent to a channel.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {
	/**
	 * The channel to which the message was sent.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final Channel channel;
	/**
	 * The raw channel name, could be a special mode message eg +#channel that
	 * only goes to voiced users.
	 */
	protected final String channelSource;
	/**
	 * The user hostmask who sent the message.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * The user who sent the message.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	/**
	 * The actual message sent to the channel.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final String message;
	/**
	 * The IrcV3 tags
	 */
	protected final ImmutableMap<String, String> tags;

	public MessageEvent(PircBotX bot, @NonNull Channel channel, @NonNull String channelSource, @NonNull UserHostmask userHostmask, User user, @NonNull String message, ImmutableMap<String, String> tags) {
		super(bot);
		this.channel = channel;
		this.channelSource = channelSource;
		this.userHostmask = userHostmask;
		this.user = user;
		this.message = message;
		this.tags = tags;
	}

	/**
	 * Respond with a channel message in <code>user: message</code> format to
	 * the user that sent the message
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		respondWith(getUser().getNick() + ": " + response);
	}
	
	@Override
	public void respondWith(String fullLine) {
		getBot().sendIRC().message(channelSource, fullLine);
	}
 
	/**
	 * Respond with a message to the channel without the prefix
	 *
	 * @param response The response to send
	 */
	public void respondChannel(String response) {
		if (getChannel() == null)
			throw new RuntimeException("Event does not contain a channel");
		getBot().sendIRC().message(channelSource, response);
	}

	/**
	 * Respond with a PM directly to the user
	 *
	 * @param response The response to send
	 */
	@Override
	public void respondPrivateMessage(String response) {
		getUser().send().message(response);
	}
	
	/**
	 * Alias of {@link #getTags() }
	 */
	public ImmutableMap<String, String> getV3Tags() {
		return tags;
	}
}
