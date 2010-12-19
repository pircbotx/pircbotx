package org.pircbotx.listeners;

import org.pircbotx.events.ConnectEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ConnectListener extends Listener {
	public void onConnect(ConnectEvent event);
}
