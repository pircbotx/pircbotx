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
 * This method is called whenever someone (possibly us) is kicked from
 * any of the channels that we are in.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Kick {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Kick} for an explanation on use 
	 * @see Kick 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason);
	}

	/**
	 * Listener that receives an event. See {@link Kick} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Kick 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onKick(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Kick} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Kick 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final String channel;
		protected final String kickerNick;
		protected final String kickerLogin;
		protected final String kickerHostname;
		protected final String recipientNick;
		protected final String reason;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel from which the recipient was kicked.
		 * @param kickerNick The nick of the user who performed the kick.
		 * @param kickerLogin The login of the user who performed the kick.
		 * @param kickerHostname The hostname of the user who performed the kick.
		 * @param recipientNick The unfortunate recipient of the kick.
		 * @param reason The reason given by the user who performed the kick.
		 */
		public Event(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
			this.timestamp = System.currentTimeMillis();
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
