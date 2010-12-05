/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
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
 * Used whenever a message is sent to a channel.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Message {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Message} for an explanation on use 
	 * @see Message 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Message Events. See {@link Message} for a complete description on when
		 * this is called.
		 * @param channel The channel to which the message was sent.
		 * @param source The user who sent the message.
		 * @param message The actual message sent to the channel.
		 * @see Message
		 * @see SimpleListener
		 */
		public void onMessage(Channel channel, User source, String message);
	}

	/**
	 * Listener that receives an event. See {@link Message} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Message 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Message Events. See {@link Message} for a complete description on when
		 * this is called.
		 * @see Message
		 * @see Listener
		 */
		public void onMessage(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Message} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Message 
	 * @see Listener
	  */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final Channel channel;
		protected final User source;
		protected final String message;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel to which the message was sent.
		 * @param source The user who sent the message.
		 * @param message The actual message sent to the channel.
		 */
		public <T extends PircBotX> Event(T bot, Channel channel, User source, String message) {
			super(bot);
			this.channel = channel;
			this.source = source;
			this.message = message;
		}

	}
}
