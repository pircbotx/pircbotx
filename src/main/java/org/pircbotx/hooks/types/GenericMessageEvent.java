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
package org.pircbotx.hooks.types;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * Generic event for an incoming message from a user, whether it be a private
 * message or a channel message. Note that if you cast directly to this interface
 * you will not know whether to respond in the channel or the user since the
 * channel is not part of this interface. However most PircBotX methods internally
 * cast to the base event and send it to the correct place
 * <p>
 * Used in {@link MessageEvent} and {@link PrivateMessageEvent}
 * @author Leon Blakey <lord.quackstar@gmail.com>
 */
public interface GenericMessageEvent extends GenericUserEvent {
	/**
	 * The message the user sent
	 * @return The message
	 */
	public String getMessage();

	/**
	 * The user that sent the message
	 * @return The user
	 */
	public User getUser();
}
