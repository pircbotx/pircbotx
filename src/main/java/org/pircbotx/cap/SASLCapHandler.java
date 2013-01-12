/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.cap;

import org.pircbotx.Base64;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.PircBotX;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class SASLCapHandler implements CapHandler {
	protected final String username;
	protected final String password;
	@Getter
	protected boolean done = false;

	public void handleLS(PircBotX bot, List<String> capabilities) {
		if (capabilities.contains("sasl"))
			//Server supports sasl, send request to use it
			bot.sendCAPREQ("sasl");
	}

	public void handleACK(PircBotX bot, List<String> capabilities) {
		if (capabilities.contains("sasl"))
			//Server acknowledges our request to use sasl 
			bot.sendRawLine("AUTHENTICATE PLAIN");

	}

	public void handleUnknown(PircBotX bot, String rawLine) throws Exception {
		if (rawLine.equals("AUTHENTICATE +")) {
			//Server ackowledges our request to use plain authentication
			String encodedAuth = Base64.encodeToString((username + '\0' + username + '\0' + password).getBytes("UTF-8"), false);
			bot.sendRawLine("AUTHENTICATE " + encodedAuth);
			done = true;
		}
	}

	public void handleNAK(PircBotX bot, List<String> capabilities) {
	}
}
