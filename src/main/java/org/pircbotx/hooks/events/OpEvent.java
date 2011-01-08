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
 * Called when a user (possibly us) gets granted operator status for a channel.
 *  <p>
 * This is a type of mode change and is also passed to the onMode
 * method in the PircBotX class.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpEvent extends Event {
	protected final Channel channel;
	protected final User source;
	protected final User recipient;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel in which the mode change took place.
	 * @param source The user that performed the mode change.
	 * @param recipient The user that got 'opped'.
	 */
	public <T extends PircBotX> OpEvent(T bot, Channel channel, User source, User recipient) {
		super(bot);
		this.channel = channel;
		this.source = source;
		this.recipient = recipient;
	}
}
