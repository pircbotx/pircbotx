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

import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * This method is called whenever we receive a PING request from another
 * user.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onPing(...);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Ping {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Ping} for an explanation on use 
	 * @see Ping 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Ping Events. See {@link Ping} for a complete description on when
		 * this is called.
		 * @param source The user that sent the PING request.
		 * @param target The channel that received the ping request. A value of <code>null</code>
		 *               means the target was us.
		 * @param pingValue The value that was supplied as an argument to the PING command.
		 * @see Ping
		 * @see SimpleListener
		 */
		public void onPing(User source, Channel target, String pingValue);
	}

	/**
	 * Listener that receives an event. See {@link Ping} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Ping 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Ping Events. See {@link Ping} for a complete description on when
		 * this is called.
		 * @see Ping
		 * @see Listener
		 */
		public void onPing(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Ping} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Ping 
	 * @see Listener
	 */
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class Event extends BaseEvent {
		protected final User source;
		protected final Channel target;
		protected final String pingValue;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param source The user that sent the PING request.
		 * @param target The channel that received the ping request. A value of <code>null</code>
		 *               means the target was us.
		 * @param pingValue The value that was supplied as an argument to the PING command.
		 */
		public <T extends PircBotX> Event(T bot, User source, Channel target, String pingValue) {
			super(bot);
			this.source = source;
			this.target = target;
			this.pingValue = pingValue;
		}
	}
}
