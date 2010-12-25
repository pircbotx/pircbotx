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
 * This method is called whenever we receive a VERSION request.
 * This abstract implementation responds with the PircBotX's _version string,
 * so if you override this method, be sure to either mimic its functionality
 * or to call super.onVersion(...);
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VersionEvent extends Event {
	protected final User source;
	protected final Channel target;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param source The nick of the user that sent the VERSION request.
	 * @param target The target channel of the VERSION request. A value of <code>null</code>
	 *               means that that the target is us.
	 */
	public <T extends PircBotX> VersionEvent(T bot, User source, Channel target) {
		super(bot);
		this.source = source;
		this.target = target;
	}
}
