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
package org.pircbotx.hooks;

import org.pircbotx.PircBotX;

/**
 * Listener that provides an easy way to make temporary listeners that aren't
 * needed after one use
 * <p>
 * Your listener will only get called if the bot that generated the event matches
 * the given one. Then when you are finished, call {@link #done()} and the listener
 * will be removed.
 * <p>
 * Note: The reason the {@link #done()} method is used instead of automatically
 * removing is that you may need to check for something before executing. 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TemporaryListener extends ListenerAdapter {
	protected final PircBotX bot;

	public TemporaryListener(PircBotX bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getBot() == bot)
			super.onEvent(event);
	}

	public void done() {
		bot.getListenerManager().removeListener(this);
	}
}
