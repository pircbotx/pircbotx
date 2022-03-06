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

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * This event is dispatched whenever a user sets the topic, or when we join a
 * new channel and discovers its topic.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TopicEvent extends Event implements GenericChannelEvent {
	/**
	 * The channel that the topic belongs to.
	 */
	@Getter(onMethod = @__(
			@Override))
	protected final Channel channel;
	/**
	 * If known, the old topic of the channel before it was changed.
	 */
	protected final String oldTopic;
	/**
	 * The topic for the channel.
	 */
	protected final String topic;
	/**
	 * The user that set the topic.
	 */
	protected final UserHostmask user;
	/**
	 * True if the topic has just been changed, false if the topic was already
	 * there.
	 */
	protected final boolean changed;
	/**
	 * When the topic was set (milliseconds since the epoch).
	 */
	protected final long date;
	/**
	 * The IrcV3 tags
	 */
	protected final ImmutableMap<String, String> tags;

	public TopicEvent(PircBotX bot, @NonNull Channel channel, String oldTopic, @NonNull String topic, @NonNull UserHostmask user, long date, boolean changed,
      ImmutableMap<String, String> tags) {
		super(bot);
		this.channel = channel;
		this.oldTopic = oldTopic;
		this.topic = topic;
		this.user = user;
		this.changed = changed;
		this.date = date;
		this.tags = tags;
	}

	/**
	 * Respond with a channel message in <code>user: message</code> format to
	 * the user that set the message
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getChannel().send().message(user, response);
	}
}
