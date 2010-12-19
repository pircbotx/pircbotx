package org.pircbotx.listeners;

import org.pircbotx.events.ServerPingEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ServerPingListener extends Listener {
	public void onServerPing(ServerPingEvent event);
}
