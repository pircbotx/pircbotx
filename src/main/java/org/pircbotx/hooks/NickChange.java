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
 * This method is called whenever someone (possibly us) changes nick on any
 * of the channels that we are on.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class NickChange {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link NickChange} for an explanation on use 
	 * @see NickChange 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onNickChange(String oldNick, String login, String hostname, String newNick);
	}

	/**
	 * Listener that receives an event. See {@link NickChange} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see NickChange 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onNickChange(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link NickChange} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see NickChange 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String oldNick;
		protected final String login;
		protected final String hostname;
		protected final String newNick;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param oldNick The old nick.
		 * @param login The login of the user.
		 * @param hostname The hostname of the user.
		 * @param newNick The new nick.
		 */
		public Event(String oldNick, String login, String hostname, String newNick) {
			this.timestamp = System.currentTimeMillis();
			this.oldNick = oldNick;
			this.login = login;
			this.hostname = hostname;
			this.newNick = newNick;
		}

		public String getHostname() {
			return hostname;
		}

		public String getLogin() {
			return login;
		}

		public String getNewNick() {
			return newNick;
		}

		public String getOldNick() {
			return oldNick;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
