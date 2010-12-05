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
		/**
		 * Simple Listener for Kick Events. See {@link Kick} for a complete description on when
		 * this is called.
		 * @param channel The channel from which the recipient was kicked.
		 * @param source The user who performed the kick.
		 * @param recipient The unfortunate recipient of the kick.
		 * @param reason The reason given by the user who performed the kick.
		 * @see Kick
		 * @see SimpleListener
		 */
		public void onKick(Channel channel, User source, User recipient, String reason);
	}

	/**
	 * Listener that receives an event. See {@link Kick} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Kick 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Kick Events. See {@link Kick} for a complete description on when
		 * this is called.
		 * @see Kick
		 * @see Listener
		 */
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
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final Channel channel;
		protected final User source;
		protected final User recipient;
		protected final String reason;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel from which the recipient was kicked.
		 * @param source The user who performed the kick.
		 * @param recipient The unfortunate recipient of the kick.
		 * @param reason The reason given by the user who performed the kick.
		 */
		public <T extends PircBotX> Event(T bot, Channel channel, User source, User recipient, String reason) {
			super(bot);
			this.channel = channel;
			this.source = source;
			this.recipient = recipient;
			this.reason = reason;
		}
	}
}
