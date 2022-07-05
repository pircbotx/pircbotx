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
package org.pircbotx.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;

/**
 * IRCv3 CAP Negoation commands. See <a
 * href="http://ircv3.atheme.org/">http://ircv3.atheme.org/</a>
 * for more information
 */
@RequiredArgsConstructor
public class OutputCAP {
	@NonNull
	protected final PircBotX bot;

	public void getSupported() {
		bot.sendRaw().rawLineNow("CAP LS");
	}

	public void getEnabled() {
		bot.sendRaw().rawLineNow("CAP LIST");
	}

	public void request(String... capability) {
		bot.sendRaw().rawLineNow("CAP REQ :" + StringUtils.join(capability, " "));
	}

	public void clear() {
		bot.sendRaw().rawLineNow("CAP CLEAR");
	}

	public void end() {
		bot.sendRaw().rawLineNow("CAP END");
	}
}
