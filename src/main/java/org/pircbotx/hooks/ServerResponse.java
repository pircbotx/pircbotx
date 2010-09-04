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
 *This is called when we receive a numeric response from the
 * IRC server.
 *  <p>
 * Numerics in the range from 001 to 099 are used for client-server
 * connections only and should never travel between servers.  Replies
 * generated in response to commands are found in the range from 200
 * to 399.  Error replies are found in the range from 400 to 599.
 *  <p>
 * For example, we can use this method to discover the topic of a
 * channel when we join it.  If we join the channel #test which
 * has a topic of &quot;I am King of Test&quot; then the response
 * will be &quot;<code>PircBotX #test :I Am King of Test</code>&quot;
 * with a code of 332 to signify that this is a topic.
 * (This is just an example - note that overriding the
 * <code>onTopic</code> method is an easier way of finding the
 * topic for a channel). Check the IRC RFC for the full list of other
 * command response codes.
 *  <p>
 * PircBotX implements the interface ReplyConstants, which contains
 * contstants that you may find useful here.
 *
 * @see ReplyConstants
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ServerResponse {
	/**
	 * Simple listener that takes event parameters as parameters. See
	 * {@link ServerResponse} for an explanation on use
	 * @see ServerResonse
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for ServerResponse Events. See {@link ServerResponse} for a complete description on when
		 * this is called.
		 * @see ServerResponse
		 * @see SimpleListener
		 */
		public void onServerResponse(int code, String response);
	}

	/**
	 * Listener that receives an event. See {@link ServerResponse} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see ServerResponse 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for ServerResponse Events. See {@link ServerResponse} for a complete description on when
		 * this is called.
		 * @see ServerResponse
		 * @see Listener
		 */
		public void onServerResponse(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link ServerResponse} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see ServerResponse 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final int code;
		protected final String response;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param code The three-digit numerical code for the response.
		 * @param response The full response from the IRC server.
		 */
		public Event(int code, String response) {
			this.timestamp = System.currentTimeMillis();
			this.code = code;
			this.response = response;
		}

		public int getCode() {
			return code;
		}

		public String getResponse() {
			return response;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
/**
 * Listener that receives event. See {@link ServerResponse}
 * @see ServerResponse
 */
