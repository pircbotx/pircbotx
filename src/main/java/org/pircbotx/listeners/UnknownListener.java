package org.pircbotx.listeners;

import org.pircbotx.events.UnknownEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface UnknownListener extends Listener {
	public void onUnknown(UnknownEvent event);
}
