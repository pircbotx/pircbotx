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
 * This method is called once the PircBotX has successfully connected to
 * the IRC server.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Connect {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Connect} for an explanation on use 
	 * @see Connect 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Connect Events. See {@link Connect} for a complete description on when
		 * this is called.
		 * @see Connect
		 * @see SimpleListener
		 */
		public void onConnect();
	}

	/**
	 * Listener that receives an event. See {@link Connect} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Connect 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Connect Events. See {@link Connect} for a complete description on when
		 * this is called.
		 * @see Connect
		 * @see Listener
		 */
		public void onConnect(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Connect} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Connect 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param timestamp
		 */
		public <T extends PircBotX> Event(T bot) {
			super(bot);
		}
	}
}
