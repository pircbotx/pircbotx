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

import org.pircbotx.User;
import lombok.Data; 
import lombok.EqualsAndHashCode; 
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Motd {
	/**
	 * Simple listener that takes event parameters as parameters. See
	 * {@link Motd} for an explanation on use
	 * @see Motd
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for NickChange Events. See {@link Motd} for a complete description on when
		 * this is called.
		 * @param motd The full motd separated by newlines (<code>\n</code>)
		 * @see Motd
		 * @see SimpleListener
		 */
		public void onMotd(String motd);
	}

	/**
	 * Listener that receives an event. See {@link Motd} for an explanation
	 * on use and {@link Event} for an explanation on the event.
	 * @see Motd
	 * @see Event
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for NickChange Events. See {@link Motd} for a complete description on when
		 * this is called.
		 * @see Motd
		 * @see Listener
		 */
		public void onMotd(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link Motd} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see Motd
	 * @see Listener
	  */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final String motd;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param motd The full motd separated by newlines (<code>\n</code>)
		 */
		public <T extends PircBotX> Event(T bot, String motd) {
			super(bot);
			this.motd = motd;
		}
	}
}
