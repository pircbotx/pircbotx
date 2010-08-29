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
public class Message {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onMessage(String channel, String sender, String login, String hostname, String message);
	}

	public static interface Listener extends BaseListener {
		public void onMessage(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String sender;
		protected final String login;
		protected final String hostname;
		protected final String message;

		public Event(long timestamp, String channel, String sender, String login, String hostname, String message) {
			this.timestamp = timestamp;
			this.channel = channel;
			this.sender = sender;
			this.login = login;
			this.hostname = hostname;
			this.message = message;
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
