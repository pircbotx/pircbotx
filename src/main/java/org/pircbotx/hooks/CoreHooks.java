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

package org.pircbotx.hooks;

import java.util.Date;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.listeners.FingerListener;
import org.pircbotx.hooks.listeners.PingListener;
import org.pircbotx.hooks.listeners.ServerPingListener;
import org.pircbotx.hooks.listeners.TimeListener;
import org.pircbotx.hooks.listeners.VersionListener;
/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks implements FingerListener, PingListener, ServerPingListener, TimeListener, VersionListener {

	public void onFinger(FingerEvent event) {
		event.getBot().sendCTCPResponse(event, "FINGER " + event.getBot().getFinger());
	}

	public void onPing(PingEvent event) {
		event.getBot().sendCTCPResponse(event, "PING " + event.getPingValue());
	}

	public void onServerPing(ServerPingEvent event) {
		event.getBot().sendRawLine("PONG " + event.getResponse());
	}

	public void onTime(TimeEvent event) {
		event.getBot().sendCTCPResponse(event, "TIME " + new Date().toString());
	}

	public void onVersion(VersionEvent event) {
		event.getBot().sendCTCPResponse(event, "VERSION " + event.getBot().getVersion());
	}
}
