package org.pircbotx.listeners;

import org.pircbotx.events.MotdEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface MotdListener extends Listener {
	public void onMotd(MotdEvent event);
}
