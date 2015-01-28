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
package org.pircbotx.hooks.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * This event is dispatched whenever we receive a line from the server that
 * PircBotX has not been programmed to recognize.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnknownEvent extends Event {
	/**
	 * The raw line that was received from the server.
	 */
	protected final String line;

	public UnknownEvent(PircBotX bot, @NonNull String line) {
		super(bot);
		this.line = line;
	}

	/**
	 * Responds by sending a <b>raw line</b> to the server.
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getBot().sendRaw().rawLine(response);
	}
}
