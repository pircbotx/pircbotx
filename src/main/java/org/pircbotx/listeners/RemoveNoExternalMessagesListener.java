package org.pircbotx.listeners;

import org.pircbotx.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveNoExternalMessagesListener extends Listener {
	public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent event);
}
