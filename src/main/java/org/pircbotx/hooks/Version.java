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
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * This method is called whenever we receive a VERSION request.
 * This abstract implementation responds with the PircBotX's _version string,
 * so if you override this method, be sure to either mimic its functionality
 * or to call super.onVersion(...);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Version {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link Version} for an explanation on use 
	 * @see Version 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for Version Events. See {@link Version} for a complete description on when
		 * this is called.
		 * @param source The nick of the user that sent the VERSION request.
		 * @param target The target channel of the VERSION request. A value of <code>null</code>
		 *               means that that the target is us.
		 * @see Version
		 * @see SimpleListener
		 */
		public void onVersion(User source, Channel target);
	}

	/**
	 * Listener that receives an event. See {@link Version} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see Version 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for Version Events. See {@link Version} for a complete description on when
		 * this is called.
		 * @see Version
		 * @see Listener
		 */
		public void onVersion(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Version} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Version 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final User source;
		protected final Channel target;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param source The nick of the user that sent the VERSION request.
		 * @param target The target channel of the VERSION request. A value of <code>null</code>
		 *               means that that the target is us.
		 */
		public Event(User source, Channel target) {
			this.timestamp = System.currentTimeMillis();
			this.source = source;
			this.target = target;
		}

		public User getSource() {
			return source;
		}

		public Channel getTarget() {
			return target;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
