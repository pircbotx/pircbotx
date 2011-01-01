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
package org.pircbotx.hooks.listeners;

import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.Listener;

/**
 * Listener for {@link org.pircbotx.hooks.events.PartEvent}events
 * @see org.pircbotx.hooks.events.Part
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
 public interface PartListener extends Listener {
	/**
	 * Invoked when an {@link org.pircbotx.hooks.events.PartEvent}occurs
	 * @param event The generated PartEvent
	 */
	public void onPart(PartEvent event) throws Exception;
}
