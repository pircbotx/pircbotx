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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data; 
import lombok.EqualsAndHashCode; 
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * Called when a channel is marked as not being in private mode.
 *  <p>
 * This is a type of mode change and is also passed to the onMode
 * method in the PircBotX class.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class RemovePrivate {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link RemovePrivate} for an explanation on use 
	 * @see RemovePrivate 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for RemovePrivate Events. See {@link RemovePrivate} for a complete description on when
		 * this is called.
		 * @param channel The channel in which the mode change took place.
		 * @param source The user that performed the mode change.
		 * @see RemovePrivate
		 * @see SimpleListener
		 */
		public void onRemovePrivate(Channel channel, User user);
	}

	/**
	 * Listener that receives an event. See {@link RemovePrivate} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see RemovePrivate 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for RemovePrivate Events. See {@link RemovePrivate} for a complete description on when
		 * this is called.
		 * @see RemovePrivate
		 * @see Listener
		 */
		public void onRemovePrivate(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link RemovePrivate} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see RemovePrivate 
	 * @see Listener
	 */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final Channel channel;
		protected final User source;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel in which the mode change took place.
		 * @param sourceNick The user that performed the mode change.
		 */
		public <T extends PircBotX> Event(T bot, Channel channel, User source) {
			super(bot);
			this.channel = channel;
			this.source = source;
		}
	}
}
