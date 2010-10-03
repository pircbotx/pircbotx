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

package org.pircbotx.hooks.helpers;

import java.util.Date;
import org.pircbotx.hooks.Finger;
import org.pircbotx.hooks.Ping;
import org.pircbotx.hooks.ServerPing;
import org.pircbotx.hooks.Time;
import org.pircbotx.hooks.Version;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks implements Finger.Listener, Ping.Listener, ServerPing.Listener, Time.Listener, Version.Listener {

	public void onFinger(Finger.Event event) {
		event.getBot().sendCTCPResponse(event, "FINGER " + event.getBot().getFinger());
	}

	public void onPing(Ping.Event event) {
		event.getBot().sendCTCPResponse(event, "PING " + event.getPingValue());
	}

	public void onServerPing(ServerPing.Event event) {
		event.getBot().sendRawLine("PONG " + event.getResponse());
	}

	public void onTime(Time.Event event) {
		event.getBot().sendCTCPResponse(event, "TIME " + new Date().toString());
	}

	public void onVersion(Version.Event event) {
		event.getBot().sendCTCPResponse(event, "VERSION " + event.getBot().getVersion());
	}
}
