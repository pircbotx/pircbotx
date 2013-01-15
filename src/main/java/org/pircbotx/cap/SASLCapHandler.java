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
	protected final boolean ignoreFail;
	@Getter
	protected boolean done = false;

	/**
	 * Create SASLCapHandler not ignoring failed authentication and throwing
	 * a RuntimeException
	 * @param username
	 * @param password
	 */
	public SASLCapHandler(String username, String password) {
		this.username = username;
		this.password = password;
		this.ignoreFail = false;
	}

	public void handleLS(PircBotX bot, List<String> capabilities) {
		if (capabilities.contains("sasl"))
			//Server supports sasl, send request to use it
			bot.sendCAPREQ("sasl");
	}

	public void handleACK(PircBotX bot, List<String> capabilities) {
		if (capabilities.contains("sasl"))
			//Server acknowledges our request to use sasl 
			bot.sendRawLineNow("AUTHENTICATE PLAIN");

	}

	public void handleUnknown(PircBotX bot, String rawLine) throws Exception {
		if (rawLine.equals("AUTHENTICATE +")) {
			//Server ackowledges our request to use plain authentication
			String encodedAuth = Base64.encodeToString((username + '\0' + username + '\0' + password).getBytes("UTF-8"), false);
			bot.sendRawLineNow("AUTHENTICATE " + encodedAuth);
		}

		//Check for 904 and 905 
		String[] parsedLine = rawLine.split(" ", 4);
		if (parsedLine.length >= 1)
			if (parsedLine[1].equals("904") || parsedLine[1].equals("905")) {
				//Remove sasl as an enabled capability
				bot.getEnabledCapabilities().remove("sasl");

				if (!ignoreFail)
					throw new RuntimeException("SASL Authentication failed with message: " + parsedLine[3].substring(1));

				//Pretend like nothing happened
				done = true;
			} else if (parsedLine[1].equals("900") || parsedLine[1].equals("903"))
				done = true;
	}

	public void handleNAK(PircBotX bot, List<String> capabilities) {
		if (!ignoreFail && capabilities.contains("sasl"))
			throw new RuntimeException("Server does not support SASL");
	}
}
