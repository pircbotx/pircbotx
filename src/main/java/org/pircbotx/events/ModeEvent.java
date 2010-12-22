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

package org.pircbotx.events;

import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.helpers.Event;
import org.pircbotx.PircBotX;

/**
 * Used when the mode of a channel is set.
 *  <p>
 * You may find it more convenient to decode the meaning of the mode
 * string by using instead {@link Op}, {@link DeOp}, {@link Voice},
 * {@link DeVoice, ChannelKey}, {@link DeChannelKey},
 * {@link ChannelLimit}, {@link DeChannelLimit}, {@link ChannelBan} or
 * {@link DeChannelBan} as appropriate.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModeEvent extends Event {
	protected final Channel channel;
	protected final User source;
	protected final String mode;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel that the mode operation applies to.
	 * @param source The user that set the mode.
	 * @param mode The mode that has been set.
	 */
	public <T extends PircBotX> ModeEvent(T bot, Channel channel, User source, String mode) {
		super(bot);
		this.channel = channel;
		this.source = source;
		this.mode = mode;
	}
}
