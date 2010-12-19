package org.pircbotx.listeners;

import org.pircbotx.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetNoExternalMessagesListener extends Listener {
	public void onSetNoExternalMessages(SetNoExternalMessagesEvent event);
}
