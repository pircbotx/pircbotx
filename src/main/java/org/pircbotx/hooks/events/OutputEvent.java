/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
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

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * A command sent to the IRC server from PircBotX
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutputEvent extends Event {
	private final String rawLine;
	/**
	 * Raw line split into its individual parts
	 *
	 * @see org.pircbotx.Utils#tokenizeLine(java.lang.String)
	 */
	private final List<String> lineParsed;

	public OutputEvent(PircBotX bot, String rawLine, List<String> lineParsed) {
		super(bot);
		this.rawLine = rawLine;
		this.lineParsed = lineParsed;
	}

	/**
	 * @param response
	 * @deprecated Cannot respond to output
	 */
	@Override
	@Deprecated
	public void respond(String response) {
		throw new UnsupportedOperationException("Not supported");
	}
}
