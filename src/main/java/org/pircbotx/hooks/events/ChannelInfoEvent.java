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
package org.pircbotx.hooks.events;

import java.util.Set;
import org.pircbotx.PircBotX;
import org.pircbotx.ChannelListEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.Event;

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
@Data
@EqualsAndHashCode(callSuper = true)
public class ChannelInfoEvent extends Event {
	protected final Set<ChannelListEntry> list;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel
	 * @param userCount The number of users visible in this channel.
	 * @param topic The topic for this channel.
	 */
	public <T extends PircBotX> ChannelInfoEvent(T bot, Set<ChannelListEntry> list) {
		super(bot);
		this.list = list;
	}
}
