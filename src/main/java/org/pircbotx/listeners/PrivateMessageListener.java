package org.pircbotx.listeners;

import org.pircbotx.events.PrivateMessageEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface PrivateMessageListener extends Listener {
	public void onPrivateMessage(PrivateMessageEvent event);
}
