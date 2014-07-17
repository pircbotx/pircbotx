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
 * <p/>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks extends ListenerAdapter {
	@Override
	public void onFinger(FingerEvent event) {
		event.getUser().send().ctcpResponse("FINGER " + event.getBot().getConfiguration().getFinger());
	}

	@Override
	public void onPing(PingEvent event) {
		event.getUser().send().ctcpResponse("PING " + event.getPingValue());
	}

	@Override
	public void onServerPing(ServerPingEvent event) {
		event.getBot().sendRaw().rawLine("PONG " + event.getResponse());
	}

	@Override
	public void onTime(TimeEvent event) {
		event.getUser().send().ctcpResponse("TIME " + new Date().toString());
	}

	@Override
	public void onVersion(VersionEvent event) {
		event.getUser().send().ctcpResponse("VERSION " + event.getBot().getConfiguration().getVersion());
	}
}
