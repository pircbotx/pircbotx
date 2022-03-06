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
package org.pircbotx.hooks;

import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Configuration;
import org.pircbotx.UserHostmask;
import org.pircbotx.Utils;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.types.GenericMessageEvent;

/**
 * Several standard IRC client default responses. Any listener that wishes to
 * duplicate functionality should <b>replace</b>
 * CoreHooks in the {@link ListenerManager} with a subclass of this class (this
 * way you don't have to duplicate all the functionality).
 * <p>
 * <b>Warning:</b> Removing CoreHooks without providing a replacement will
 * produce undesired results like server timeouts due to not responding to
 * pings.
 * <p/>
 * @see
 * org.pircbotx.Configuration.Builder#replaceCoreHooksListener(org.pircbotx.hooks.CoreHooks)
 */
@Slf4j
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

	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		Configuration config = event.getBot().getConfiguration();
		UserHostmask hostmask = event.getUserHostmask();
		//There must be a passwork and on success text
		//The hostmask must contain "nickserv"
		//The message must contain the on success text
		if (config.getNickservOnSuccess() != null
				&& StringUtils.containsIgnoreCase(hostmask.getHostmask(), config.getNickservNick())
				&& StringUtils.containsIgnoreCase(event.getMessage(), config.getNickservOnSuccess())) {
			log.info("Successfully identified to nickserv");
			Utils.setNickServIdentified(event.getBot());
			if (config.isNickservDelayJoin()) {
				for (Map.Entry<String, String> channelEntry : config.getAutoJoinChannels().entrySet())
					event.getBot().sendIRC().joinChannel(channelEntry.getKey(), channelEntry.getValue());
			}
		}
	}
}
