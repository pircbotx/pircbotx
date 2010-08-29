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
public class Invite {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel);
	}

	public static interface Listener extends BaseListener {
		public void onInvite(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String targetNick;
		protected final String sourceNick;
		protected final String sourceLogin;
		protected final String sourceHostname;
		protected final String channel;

		public Event(long timestamp, String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
			this.timestamp = timestamp;
			this.targetNick = targetNick;
			this.sourceNick = sourceNick;
			this.sourceLogin = sourceLogin;
			this.sourceHostname = sourceHostname;
			this.channel = channel;
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

		public String getTargetNick() {
			return targetNick;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
