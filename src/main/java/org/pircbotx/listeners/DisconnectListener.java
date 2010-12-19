package org.pircbotx.listeners;

import org.pircbotx.events.DisconnectEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface DisconnectListener extends Listener {
	public void onDisconnect(DisconnectEvent event);
}
