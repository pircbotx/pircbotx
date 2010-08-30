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
 * This method is called whenever someone (possibly us) quits from the
 * server.  We will only observe this if the user was in one of the
 * channels to which we are connected.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Quit {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Quit} for an explanation on use 
	 * @see Quit 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason);
	}

	/**
	 * Listener that receives an event. See {@link Quit} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Quit 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onQuit(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Quit} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Quit 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String sourceNick;
		protected final String sourceLogin;
		protected final String sourceHostname;
		protected final String reason;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param sourceNick The nick of the user that quit from the server.
		 * @param sourceLogin The login of the user that quit from the server.
		 * @param sourceHostname The hostname of the user that quit from the server.
		 * @param reason The reason given for quitting the server.
		 */
		public Event(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
			this.timestamp = System.currentTimeMillis();
			this.sourceNick = sourceNick;
			this.sourceLogin = sourceLogin;
			this.sourceHostname = sourceHostname;
			this.reason = reason;
		}

		public String getReason() {
			return reason;
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

		public long getTimestamp() {
			return timestamp;
		}
	}
}
