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
 * Called when a user (possibly us) gets operator status taken away.
 *  <p>
 * This is a type of mode change and is also passed to the onMode
 * method in the PircBotX class.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Deop {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Deop} for an explanation on use 
	 * @see Deop 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Deop Events. See {@link Deop} for a complete description on when
		 * this is called.
		 * @see Deop
		 * @see SimpleListener
		 */
		public void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient);
	}

	/**
	 * Listener that receives an event. See {@link Deop} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Deop 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Deop Events. See {@link Deop} for a complete description on when
		 * this is called.
		 * @see Deop
		 * @see Listener
		 */
		public void onDeop(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Deop} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Deop 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String sourceNick;
		protected final String sourceLogin;
		protected final String sourceHostname;
		protected final String recipient;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel in which the mode change took place.
		 * @param sourceNick The nick of the user that performed the mode change.
		 * @param sourceLogin The login of the user that performed the mode change.
		 * @param sourceHostname The hostname of the user that performed the mode change.
		 * @param recipient The nick of the user that got 'deopped'.
		 */
		public Event(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
			this.timestamp = System.currentTimeMillis();
			this.channel = channel;
			this.sourceNick = sourceNick;
			this.sourceLogin = sourceLogin;
			this.sourceHostname = sourceHostname;
			this.recipient = recipient;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public String getChannel() {
			return channel;
		}

		public String getRecipient() {
			return recipient;
		}

		public String getSourceHostname() {
			return sourceHostname;
		}

		public String getSourceLogin() {
			return sourceLogin;
		}

		public String getSourceNick() {
			return sourceNick;
		}
	}
}
