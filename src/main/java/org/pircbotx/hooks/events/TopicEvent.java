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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * This event is dispatched whenever a user sets the topic, or when
 * we join a new channel and discovers its topic.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TopicEvent<T extends PircBotX> extends Event<T> {
	protected final Channel channel;
	protected final String topic;
	protected final User user;
	protected final boolean changed;
	protected final long date;
	
	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel that the topic belongs to.
	 * @param topic The topic for the channel.
	 * @param user The user that set the topic.
	 * @param date When the topic was set (milliseconds since the epoch).
	 * @param changed True if the topic has just been changed, false if
	 *                the topic was already there.
	 */
	public TopicEvent(T bot, Channel channel, String topic, User user, long date, boolean changed) {
		super(bot);
		this.channel = channel;
		this.topic = topic;
		this.user = user;
		this.changed = changed;
		this.date = date;
	}

	/**
	 * Respond with a channel message in <code>user: message</code> format to the
	 * user that set the message
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendMessage(getChannel(), getUser(), response);
	}
}
