/*
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
 * This method is called whenever we receive a FINGER request.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onFinger(...);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Finger {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Finger} for an explanation on use 
	 * @see Finger 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * * Simple Listener for Finger Events. See {@link Finger} for a complete description on when
		 * this is called.
		 * @see Finger
		 * @see SimpleListener
		 * @param User The User object representing the user that sent the message
		 * @param target The target channel of the FINGER request. If the value is <code>null</code>
		 *               then the target is us
		 */
		public void onFinger(User source, Channel channel);
	}

	/**
	 * Listener that receives an event. See {@link Finger} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Finger 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Finger Events. See {@link Finger} for a complete description on when
		 * this is called.
		 * @see Finger
		 * @see Listener
		 */
		public void onFinger(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Finger} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Finger 
	 * @see Listener
	 */
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class Event extends BaseEvent {
		protected final User source;
		protected final Channel channel;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param source The user that sent the FINGER request.
		 * @param target 
		 */
		public <T extends PircBotX> Event(T bot, User source, Channel channel) {
			super(bot);
			this.source = source;
			this.channel = channel;
		}
	}
}
