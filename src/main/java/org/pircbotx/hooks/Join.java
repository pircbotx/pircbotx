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

import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * This method is called whenever someone (possibly us) joins a channel
 * which we are on.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Join {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Join} for an explanation on use 
	 * @see Join 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onJoin(String channel, String sender, String login, String hostname);
	}

	/**
	 * Listener that receives an event. See {@link Join} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Join 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onJoin(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Join} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Join 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String sender;
		protected final String login;
		protected final String hostname;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel which somebody joined.
		 * @param sender The nick of the user who joined the channel.
		 * @param login The login of the user who joined the channel.
		 * @param hostname The hostname of the user who joined the channel.
		 */
		public Event(String channel, String sender, String login, String hostname) {
			this.timestamp = System.currentTimeMillis();
			this.channel = channel;
			this.sender = sender;
			this.login = login;
			this.hostname = hostname;
		}

		public String getChannel() {
			return channel;
		}

		public String getHostname() {
			return hostname;
		}

		public String getLogin() {
			return login;
		}

		public String getSender() {
			return sender;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
