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
package org.pircbotx.output;

import java.util.Arrays;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pircbotx.Utils;

/**
 * IRCv3 CAP Negoation commands. See <a href="http://ircv3.atheme.org/">http://ircv3.atheme.org/</a>
 * for more information
 * @author Leon Blakey <lord.quackstar@gmail.com>
 */
@RequiredArgsConstructor
public class OutputCAP {
	@NonNull
	protected final OutputRaw sendRaw;

	public void getSupported() {
		sendRaw.rawLineNow("CAP LS");
	}

	public void getEnabled() {
		sendRaw.rawLineNow("CAP LIST");
	}

	public void request(String... capability) {
		sendRaw.rawLineNow("CAP REQ :" + Utils.join(Arrays.asList(capability), " "));
	}
	
	public void clear() {
		sendRaw.rawLineNow("CAP CLEAR");
	}
	
	public void end() {
		sendRaw.rawLineNow("CAP END");
	}
}
