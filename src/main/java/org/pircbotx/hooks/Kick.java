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
public class Kick {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason);
	}

	public static interface Listener extends BaseListener {
		public void onKick(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String kickerNick;
		protected final String kickerLogin;
		protected final String kickerHostname;
		protected final String recipientNick;
		protected final String reason;

		public Event(long timestamp, String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
			this.timestamp = timestamp;
			this.channel = channel;
			this.kickerNick = kickerNick;
			this.kickerLogin = kickerLogin;
			this.kickerHostname = kickerHostname;
			this.recipientNick = recipientNick;
			this.reason = reason;
		}

		public String getChannel() {
			return channel;
		}

		public String getKickerHostname() {
			return kickerHostname;
		}

		public String getKickerLogin() {
			return kickerLogin;
		}

		public String getKickerNick() {
			return kickerNick;
		}

		public String getReason() {
			return reason;
		}

		public String getRecipientNick() {
			return recipientNick;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
