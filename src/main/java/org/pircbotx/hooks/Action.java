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
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Action {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onAction(String sender, String login, String hostname, String target, String action);
	}

	public static interface Listener extends BaseListener {
		public void onAction(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String sender;
		protected final String login;
		protected final String hostname;
		protected final String target;
		protected final String action;

		public Event(long timestamp, String sender, String login, String hostname, String target, String action) {
			this.timestamp = timestamp;
			this.sender = sender;
			this.login = login;
			this.hostname = hostname;
			this.target = target;
			this.action = action;
		}

		public long getTimestamp() {
			return timestamp;
		}

		/**
		 * Get the value of target
		 *
		 * @return the value of target
		 */
		public String getTarget() {
			return target;
		}

		/**
		 * Get the value of action
		 *
		 * @return the value of action
		 */
		public String getAction() {
			return action;
		}

		/**
		 * Get the value of hostname
		 *
		 * @return the value of hostname
		 */
		public String getHostname() {
			return hostname;
		}

		/**
		 * Get the value of login
		 *
		 * @return the value of login
		 */
		public String getLogin() {
			return login;
		}

		/**
		 * Get the value of Sender
		 *
		 * @return the value of Sender
		 */
		public String getSender() {
			return sender;
		}
	}
}
