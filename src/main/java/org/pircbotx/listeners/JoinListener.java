package org.pircbotx.listeners;

import org.pircbotx.events.JoinEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface JoinListener extends Listener {
	public void onJoin(JoinEvent event);
}
