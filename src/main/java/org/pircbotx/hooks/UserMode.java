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

import org.pircbotx.User;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * Called when the mode of a user is set.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class UserMode {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link UserMode} for an explanation on use 
	 * @see UserMode 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for UserMode Events. See {@link UserMode} for a complete description on when
		 * this is called.
		 * @param target The user that the mode operation applies to.
		 * @param source The user that set the mode.
		 * @param mode The mode that has been set.
		 * @see UserMode
		 * @see SimpleListener
		 */
		public void onUserMode(User target, User source, String mode);
	}

	/**
	 * Listener that receives an event. See {@link UserMode} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see UserMode 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for UserMode Events. See {@link UserMode} for a complete description on when
		 * this is called.
		 * @see UserMode
		 * @see Listener
		 */
		public void onUserMode(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link UserMode} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see UserMode 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		protected final User target;
		protected final User source;
		protected final String mode;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param target The user that the mode operation applies to.
		 * @param source The user that set the mode.
		 * @param mode The mode that has been set.
		 */
		public <T extends PircBotX> Event(T bot, User target, User source, String mode) {
			super(bot);
			this.target = target;
			this.source = source;
			this.mode = mode;
		}

		public User getSource() {
			return source;
		}

		public User getTarget() {
			return target;
		}

		public String getMode() {
			return mode;
		}
	}
}
