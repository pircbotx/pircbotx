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
public class SetTopicProtection {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onSetTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname);
	}

	public static interface Listener extends BaseListener {
		public void onSetTopicProtection(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String sourceNick;
		protected final String sourceLogin;
		protected final String sourceHostname;

		public Event(long timestamp, String channel, String sourceNick, String sourceLogin, String sourceHostname) {
			this.timestamp = timestamp;
			this.channel = channel;
			this.sourceNick = sourceNick;
			this.sourceLogin = sourceLogin;
			this.sourceHostname = sourceHostname;
		}

		public String getChannel() {
			return channel;
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
