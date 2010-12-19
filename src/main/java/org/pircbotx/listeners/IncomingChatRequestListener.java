package org.pircbotx.listeners;

import org.pircbotx.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface IncomingChatRequestListener extends Listener {
	public void onIncomingChatRequest(IncomingChatRequestEvent event);
}
