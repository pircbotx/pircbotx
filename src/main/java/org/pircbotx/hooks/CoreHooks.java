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
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Core Hooks of PircBotX that preform basic and expected operations. Any listener
 * that wishes to duplicate functionality should <b>replace</b> CoreHooks in
 * the {@link ListenerManager} with a subclass of this class (this way you don't
 * have to duplicate all the functionality).
 * <p>
 * <b>Warning:</b> Removing CoreHooks without providing a replacement will produce
 * undesired results like server timeouts due to not responding to pings.
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks implements Listener {
	public void onFinger(FingerEvent event) {
		event.getBot().sendCTCPResponse(event.getUser(), "FINGER " + event.getBot().getFinger());
	}

	public void onPing(PingEvent event) {
		event.getBot().sendCTCPResponse(event.getUser(), "PING " + event.getPingValue());
	}

	public void onServerPing(ServerPingEvent event) {
		event.getBot().sendRawLine("PONG " + event.getResponse());
	}

	public void onTime(TimeEvent event) {
		event.getBot().sendCTCPResponse(event.getUser(), "TIME " + new Date().toString());
	}

	public void onVersion(VersionEvent event) {
		event.getBot().sendCTCPResponse(event.getUser(), "VERSION " + event.getBot().getVersion());
	}

	public void onEvent(Event event) {
		//Use a small custom onEvent here instead of ListenerAdapter for performance
		if (event instanceof VersionEvent)
			onVersion((VersionEvent) event);
		else if (event instanceof TimeEvent)
			onTime((TimeEvent) event);
		else if (event instanceof ServerPingEvent)
			onServerPing((ServerPingEvent) event);
		else if (event instanceof PingEvent)
			onPing((PingEvent) event);
		else if (event instanceof FingerEvent)
			onFinger((FingerEvent) event);
	}
}
