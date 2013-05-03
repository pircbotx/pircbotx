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

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.CAPException;

/**
 * Enables the specified capability with the server. This handler should cover
 * almost all CAP features except SASL since most only need to be requested.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class EnableCapHandler implements CapHandler {
	@Getter
	protected boolean done = false;
	@Getter
	protected final String cap;
	protected final boolean ignoreFail;

	/**
	 * Create EnableCapHandler not ignoring if server doesn't support the requested
	 * capability
	 */
	public EnableCapHandler(String cap) {
		this.cap = cap;
		this.ignoreFail = false;
	}

	public void handleLS(PircBotX bot, List<String> capabilities) throws CAPException {
		if (capabilities.contains(cap))
			//Server supports capability, send request to use it
			bot.sendCAP().request(cap);
		else if (!ignoreFail)
			throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
		else
			//Nothing more to do
			done = true;
	}

	public void handleACK(PircBotX bot, List<String> capabilities) throws CAPException {
		if (capabilities.contains(cap))
			//Capability is now enabled
			done = true;
	}

	public void handleNAK(PircBotX bot, List<String> capabilities) throws CAPException {
		if (capabilities.contains(cap)) {
			//Make sure the bot didn't register this capability
			bot.getEnabledCapabilities().remove(cap);
			if (!ignoreFail)
				throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
			else
				//Nothing more to do
				done = true;
		}
	}

	public void handleUnknown(PircBotX bot, String rawLine) {
	}
}
