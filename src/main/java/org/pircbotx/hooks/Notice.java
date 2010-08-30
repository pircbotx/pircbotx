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
 * This method is called whenever we receive a notice.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Notice {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Notice} for an explanation on use 
	 * @see Notice 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice);
	}

	/**
	 * Listener that receives an event. See {@link Notice} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Notice 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onNotice(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Notice} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Notice 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String sourceNick;
		protected final String sourceLogin;
		protected final String sourceHostname;
		protected final String target;
		protected final String notice;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param sourceNick The nick of the user that sent the notice.
		 * @param sourceLogin The login of the user that sent the notice.
		 * @param sourceHostname The hostname of the user that sent the notice.
		 * @param target The target of the notice, be it our nick or a channel name.
		 * @param notice The notice message.
		 */
		public Event(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
			this.timestamp = System.currentTimeMillis();
			this.sourceNick = sourceNick;
			this.sourceLogin = sourceLogin;
			this.sourceHostname = sourceHostname;
			this.target = target;
			this.notice = notice;
		}

		public String getNotice() {
			return notice;
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

		public String getTarget() {
			return target;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
