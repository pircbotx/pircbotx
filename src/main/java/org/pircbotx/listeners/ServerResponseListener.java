package org.pircbotx.listeners;

import org.pircbotx.events.ServerResponseEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ServerResponseListener extends Listener {
	public void onServerResponse(ServerResponseEvent event);
}
