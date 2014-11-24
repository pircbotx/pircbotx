/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Listener for short one-off tasks. Listener methods will only be called if the
 * event matches the specified bot. Listener can easily be removed with {@link #done()
 * }.
 * <p>
 * @author Leon Blakey
 */
public class TemporaryListener extends ListenerAdapter {
	protected final PircBotX bot;

	/**
	 * Create a TemporaryListener for the specified bot
	 *
	 * @param bot The bot that your interested in
	 */
	public TemporaryListener(@NonNull PircBotX bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getBot() == bot)
			super.onEvent(event);
	}

	/**
	 * Remove this listener from the {@link ListenerManager}
	 */
	public void done() {
		bot.getConfiguration().getListenerManager().removeListener(this);
	}
}
