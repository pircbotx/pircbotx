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
 * This event is dispatched whenever someone (possibly us) is kicked from
 * any of the channels that we are in.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KickEvent extends Event {
		protected final Channel channel;
		protected final User source;
		protected final User recipient;
		protected final String reason;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel from which the recipient was kicked.
		 * @param source The user who performed the kick.
		 * @param recipient The unfortunate recipient of the kick.
		 * @param reason The reason given by the user who performed the kick.
		 */
		public <T extends PircBotX> KickEvent(T bot, Channel channel, User source, User recipient, String reason) {
			super(bot);
			this.channel = channel;
			this.source = source;
			this.recipient = recipient;
			this.reason = reason;
		}
	}

