package org.pircbotx.listeners;

import org.pircbotx.events.MessageEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface MessageListener extends Listener {
	public void onMessage(MessageEvent event);
}
