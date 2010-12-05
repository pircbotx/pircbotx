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
 * This method is called whenever we receive a TIME request.
 *  <p>
 * This abstract implementation responds correctly, so if you override this
 * method, be sure to either mimic its functionality or to call
 * super.onTime(...);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Time {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Time} for an explanation on use 
	 * @see Time 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Time Events. See {@link Time} for a complete description on when
		 * this is called.
		 * @param source The user that sent the TIME request.
		 * @param target The target channel of the TIME request. A value of <code>null</code>
		 *               means that target is us
		 * @see Time
		 * @see SimpleListener
		 */
		public void onTime(User source, Channel target);
	}

	/**
	 * Listener that receives an event. See {@link Time} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Time 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Time Events. See {@link Time} for a complete description on when
		 * this is called.
		 * @see Time
		 * @see Listener
		 */
		public void onTime(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Time} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Time 
	 * @see Listener
	  */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final User source;
		protected final Channel target;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param source The user that sent the TIME request.
		 * @param target The target channel of the TIME request. A value of <code>null</code>
		 *               means that target is us
		 */
		public <T extends PircBotX> Event(T bot, User source, Channel target) {
			super(bot);
			this.source = source;
			this.target = target;
		}
	}
}
