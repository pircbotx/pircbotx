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
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * The actions to perform when a PING request comes from the server.
 *  <p>
 * This sends back a correct response, so if you override this method,
 * be sure to either mimic its functionality or to call
 * super.onServerPing(response);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ServerPing {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link ServerPing} for an explanation on use 
	 * @see ServerPing 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for ServerPing Events. See {@link ServerPing} for a complete description on when
		 * this is called.
		 * @param response The response that should be given back in your PONG.
		 * @see ServerPing
		 * @see SimpleListener
		 */
		public void onServerPing(String response);
	}

	/**
	 * Listener that receives an event. See {@link ServerPing} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see ServerPing 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for ServerPing Events. See {@link ServerPing} for a complete description on when
		 * this is called.
		 * @see ServerPing
		 * @see Listener
		 */
		public void onServerPing(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link ServerPing} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see ServerPing 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		protected final String response;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param response The response that should be given back in your PONG.
		 */
		public <T extends PircBotX> Event(T bot, String response) {
			super(bot);
			this.response = response;
		}

		public String getResponse() {
			return response;
		}
	}
}
