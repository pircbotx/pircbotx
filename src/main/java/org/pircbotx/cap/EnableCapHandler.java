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
package org.pircbotx.cap;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.CAPException;

/**
 * Enables the specified capability with the server. This handler should cover
 * almost all CAP features except SASL since most only need to be requested.
 */
@Slf4j
@RequiredArgsConstructor
@ToString
public class EnableCapHandler implements CapHandler {
	@Getter
	protected final String cap;
	protected final boolean ignoreFail;

	/**
	 * Create EnableCapHandler not ignoring if server doesn't support the
	 * requested capability
	 */
	public EnableCapHandler(String cap) {
		this.cap = cap;
		this.ignoreFail = false;
	}

	@Override
	public boolean handleLS(PircBotX bot, ImmutableList<String> capabilities) throws CAPException {
		if (capabilities.contains(cap)) {
			//Server supports capability, send request to use it
			log.debug("Supported capability " + cap);
			bot.sendCAP().request(cap);
			//Need to wait for server ACK
			return false;
		} else if (!ignoreFail)
			throw new CAPException(CAPException.Reason.UNSUPPORTED_CAPABILITY, cap);
		else {
			//Server doesn't support capability but were ignoring exceptions
			log.debug("Unsupported capability " + cap);
			return true;
		}
	}

	@Override
	public boolean handleACK(PircBotX bot, ImmutableList<String> capabilities) throws CAPException {
		//Finished if the server is acknowledging the capability
		return capabilities.contains(cap);
	}

	@Override
	public boolean handleNAK(PircBotX bot, ImmutableList<String> capabilities) throws CAPException {
		if (capabilities.contains(cap)) {
			//Make sure the bot didn't register this capability
			bot.getEnabledCapabilities().remove(cap);
			if (!ignoreFail)
				throw new CAPException(CAPException.Reason.UNSUPPORTED_CAPABILITY, cap);
			else
				//Nothing more to do
				return true;
		}
		//Not applicable to us
		return false;
	}

	@Override
	public boolean handleUnknown(PircBotX bot, String rawLine) {
		return false;
	}
}
