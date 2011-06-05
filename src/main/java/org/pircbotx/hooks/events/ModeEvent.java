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

import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * Used when the mode of a channel is set.
 *  <p>
 * You may find it more convenient to decode the meaning of the mode
 * string by using instead {@link OpEvent}, {@link DeopEvent}, {@link VoiceEvent},
 * {@link DeVoiceEvent}, {@link SetChannelKeyEvent}, {@link RemoveChannelKeyEvent},
 * {@link SetChannelLimitEvent}, {@link RemoveChannelLimitEvent}, 
 * {@link SetChannelBanEvent} or {@link RemoveChannelBanEvent} as appropriate.
 *  <p>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModeEvent<T extends PircBotX> extends Event<T> {
	protected final Channel channel;
	protected final User user;
	protected final String mode;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel that the mode operation applies to.
	 * @param user The user that set the mode.
	 * @param mode The mode that has been set.
	 */
	public ModeEvent(T bot, Channel channel, User user, String mode) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.mode = mode;
	}
	
	/**
	 * Respond by send a message in the channel to the user that set the mode
	 * in <code>user: message</code> format
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendMessage(getChannel(), getUser(), response);
	}
}
