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

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.ChannelListEntry;
import lombok.Data; 
import lombok.EqualsAndHashCode; 
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * After calling the listChannels() method in PircBotX, the server
 * will start to send us information about each channel on the
 * server.  You may override this method in order to receive the
 * information about each channel as soon as it is received.
 *  <p>
 * Note that certain channels, such as those marked as hidden,
 * may not appear in channel listings.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see PircBotX#listChannels()
 * @see PircBotX#listChannels(java.lang.String) 
 */
public class ChannelInfo {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link ChannelInfo} for an explanation on use 
	 * @see ChannelInfo 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for ChannelInfo Events. See {@link ChannelInfo} for a complete description on when
		 * this is called.
		 * @param channel The channel.
		 * @param userCount The number of users visible in this channel.
		 * @param topic The topic for this channel.
		 * @see ChannelInfo
		 * @see SimpleListener
		 */
		public void onChannelInfo(Set<ChannelListEntry> list);
	}

	/**
	 * Listener that receives an event. See {@link ChannelInfo} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see ChannelInfo 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for ChannelInfo Events. See {@link ChannelInfo} for a complete description on when
		 * this is called.
		
		 * @see ChannelInfo
		 * @see Listener
		 */
		public void onChannelInfo(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link ChannelInfo} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see ChannelInfo 
	 * @see Listener
	 */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class Event extends BaseEvent {
		protected final Set<ChannelListEntry> list;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel
		 * @param userCount The number of users visible in this channel.
		 * @param topic The topic for this channel.
		 */
		public <T extends PircBotX> Event(T bot, Set<ChannelListEntry> list) {
			super(bot);
			this.list = list;
		}
	}
}
