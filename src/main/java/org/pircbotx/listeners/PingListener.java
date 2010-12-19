package org.pircbotx.listeners;

import org.pircbotx.events.PingEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface PingListener extends Listener {
	public void onPing(PingEvent event);
}
