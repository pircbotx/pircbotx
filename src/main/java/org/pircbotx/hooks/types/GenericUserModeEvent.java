/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.OpEvent;

/**
 * Any user status change in a channel. Eg {@link OpEvent}
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericUserModeEvent<T extends PircBotX> extends GenericEvent<T> {
	/**
	 * The channel that the mode changed occurred in
	 * @return The affected channel
	 */
	public Channel getChannel();

	/**
	 * The source of the mode change
	 * @return The source user
	 */
	public User getSource();

	/**
	 * The recipient of the mode change
	 * @return The recipient user
	 */
	public User getRecipient();
}
