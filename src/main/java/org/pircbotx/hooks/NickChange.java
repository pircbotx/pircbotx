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
 * This method is called whenever someone (possibly us) changes nick on any
 * of the channels that we are on.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class NickChange {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link NickChange} for an explanation on use 
	 * @see NickChange 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for NickChange Events. See {@link NickChange} for a complete description on when
		 * this is called.
		 * @param oldNick The old nick.
		 * @param newNick The new nick.
		 * @param user The user that changed their nick
		 * @see NickChange
		 * @see SimpleListener
		 */
		public void onNickChange(String oldNick, String newNick, User user);
	}

	/**
	 * Listener that receives an event. See {@link NickChange} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see NickChange 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for NickChange Events. See {@link NickChange} for a complete description on when
		 * this is called.
		 * @see NickChange
		 * @see Listener
		 */
		public void onNickChange(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link NickChange} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see NickChange 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		protected final String oldNick;
		protected final String newNick;
		protected final User user;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param oldNick The old nick.
		 * @param newNick The new nick.
		 * @param user The user that changed their nick
		 */
		public <T extends PircBotX> Event(T bot, String oldNick, String newNick, User user) {
			super(bot);
			this.oldNick = oldNick;
			this.newNick = newNick;
			this.user = user;
		}

		public String getNewNick() {
			return newNick;
		}

		public String getOldNick() {
			return oldNick;
		}

		public User getUser() {
			return user;
		}
	}
}
