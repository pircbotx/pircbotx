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
package org.pircbotx.cap;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.CAPException;

/**
 *
 * @author Leon Blakey
 */
@ToString(exclude = "password")
public class SASLCapHandler extends EnableCapHandler {
	protected final String username;
	protected final String password;

	/**
	 * Create SASLCapHandler not ignoring failed authentication and throwing a
	 * CapException
	 * <p>
	 * @param username
	 * @param password
	 */
	public SASLCapHandler(String username, String password) {
		this(username, password, false);
	}
	
	public SASLCapHandler(String username, String password, boolean ignoreFail) {
		super("sasl", ignoreFail);
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean handleACK(PircBotX bot, ImmutableList<String> capabilities) {
		if (capabilities.contains("sasl")) {
			//Server acknowledges our request to use sasl 
			bot.sendRaw().rawLineNow("AUTHENTICATE PLAIN");
		}
		//If this is our ack, we still need to send user information
		//If this isn't, it might be later
		return false;
	}

	@Override
	public boolean handleUnknown(PircBotX bot, String rawLine) throws CAPException {
		if (rawLine.equals("AUTHENTICATE +")) {
			//Server ackowledges our request to use plain authentication
			byte[] rawAuth = (username + '\0' + username + '\0' + password).getBytes(Charsets.UTF_8);
			//Issue #256: Use method available in commons-codec 1.3 for android, copied from encodeBase64String(byte[])
			String encodedAuth = new String(Base64.encodeBase64(rawAuth, false), Charsets.UTF_8);
			bot.sendRaw().rawLineNow("AUTHENTICATE " + encodedAuth);
		}

		//Check for 904 and 905 
		String[] parsedLine = rawLine.split(" ", 4);
		if (parsedLine.length >= 1)
			if (parsedLine[1].equals("904") || parsedLine[1].equals("905")) {
				//Remove sasl as an enabled capability
				bot.getEnabledCapabilities().remove("sasl");

				if (!ignoreFail)
					throw new CAPException(CAPException.Reason.SASLFailed, "SASL Authentication failed with message: " + parsedLine[3].substring(1));

				//Pretend like nothing happened
				return true;
			} else if (parsedLine[1].equals("900") || parsedLine[1].equals("903"))
				//Success!
				return true;
		return false;
	}
}
