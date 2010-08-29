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
public class NickChange {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onNickChange(String oldNick, String login, String hostname, String newNick);
	}

	public static interface Listener extends BaseListener {
		public void onNickChange(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String oldNick;
		protected final String login;
		protected final String hostname;
		protected final String newNick;

		public Event(long timestamp, String oldNick, String login, String hostname, String newNick) {
			this.timestamp = timestamp;
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
