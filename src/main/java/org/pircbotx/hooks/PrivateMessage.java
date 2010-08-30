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
 * This method is called whenever a private message is sent to the PircBotX.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PrivateMessage {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link PrivateMessage} for an explanation on use 
	 * @see PrivateMessage 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onPrivateMessage(String sender, String login, String hostname, String message);
	}

	/**
	 * Listener that receives an event. See {@link PrivateMessage} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see PrivateMessage 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onPrivateMessage(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link PrivateMessage} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see PrivateMessage 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String sender;
		protected final String login;
		protected final String hostname;
		protected final String message;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param sender The nick of the person who sent the private message.
		 * @param login The login of the person who sent the private message.
		 * @param hostname The hostname of the person who sent the private message.
		 * @param message The actual message.
		 */
		public Event(String sender, String login, String hostname, String message) {
			this.timestamp = System.currentTimeMillis();
			this.sender = sender;
			this.login = login;
			this.hostname = hostname;
			this.message = message;
		}

		public String getHostname() {
			return hostname;
		}

		public String getLogin() {
			return login;
		}

		public String getMessage() {
			return message;
		}

		public String getSender() {
			return sender;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
