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
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * Called when a user (possibly us) gets banned from a channel.  Being
 * banned from a channel prevents any user with a matching hostmask from
 * joining the channel.  For this reason, most bans are usually directly
 * followed by the user being kicked :-)
 *  <p>
 * This is a type of mode change and is also passed to the onMode
 * method in the PircBotX class.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class SetChannelBan {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link SetChannelBan} for an explanation on use 
	 * @see SetChannelBan 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for SetChannelBan Events. See {@link SetChannelBan} for a complete description on when
		 * this is called.
		 * @param channel The channel in which the mode change took place.
		 * @param source The user that performed the mode change.
		 * @see SetChannelBan
		 * @see SimpleListener
		 */
		public void onSetChannelBan(Channel channel, User source, String hostmask);
	}

	/**
	 * Listener that receives an event. See {@link SetChannelBan} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see SetChannelBan 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for SetChannelBan Events. See {@link SetChannelBan} for a complete description on when
		 * this is called.
		 * @see SetChannelBan
		 * @see Listener
		 */
		public void onSetChannelBan(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link SetChannelBan} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see SetChannelBan 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final Channel channel;
		protected final User source;
		protected final String hostmask;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel in which the mode change took place.
		 * @param sourceNick The user that performed the mode change.
		 * @param hostmask The hostmask of the user that has been banned.
		 */
		public Event(Channel channel, User source, String hostmask) {
			this.timestamp = System.currentTimeMillis();
			this.channel = channel;
			this.source = source;
			this.hostmask = hostmask;
		}

		public Channel getChannel() {
			return channel;
		}

		public String getHostmask() {
			return hostmask;
		}

		public User getSource() {
			return source;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
