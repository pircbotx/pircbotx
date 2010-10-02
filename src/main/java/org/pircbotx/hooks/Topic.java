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
package org.pircbotx.hooks;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * This method is called whenever a user sets the topic, or when
 * PircBotX joins a new channel and discovers its topic.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Topic {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Topic} for an explanation on use 
	 * @see Topic 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Topic Events. See {@link Topic} for a complete description on when
		 * this is called.
		 * @param channel The channel that the topic belongs to.
		 * @param topic The topic for the channel.
		 * @param setBy The user that set the topic.
		 * @param date When the topic was set (milliseconds since the epoch).
		 * @param changed True if the topic has just been changed, false if
		 *                the topic was already there.
		 * @see Topic
		 * @see SimpleListener
		 */
		public void onTopic(Channel channel, String topic, User setBy, boolean changed);
	}

	/**
	 * Listener that receives an event. See {@link Topic} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Topic 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Topic Events. See {@link Topic} for a complete description on when
		 * this is called.
		 * @see Topic
		 * @see Listener
		 */
		public void onTopic(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Topic} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Topic 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		protected final Channel channel;
		protected final String topic;
		protected final User setBy;
		protected final boolean changed;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel that the topic belongs to.
		 * @param topic The topic for the channel.
		 * @param setBy The user that set the topic.
		 * @param date When the topic was set (milliseconds since the epoch).
		 * @param changed True if the topic has just been changed, false if
		 *                the topic was already there.
		 */
		public <T extends PircBotX> Event(T bot, Channel channel, String topic, User setBy, boolean changed) {
			super(bot);
			this.channel = channel;
			this.topic = topic;
			this.setBy = setBy;
			this.changed = changed;
		}

		public boolean isChanged() {
			return changed;
		}

		public Channel getChannel() {
			return channel;
		}

		public User getSetBy() {
			return setBy;
		}

		public String getTopic() {
			return topic;
		}
	}
}
