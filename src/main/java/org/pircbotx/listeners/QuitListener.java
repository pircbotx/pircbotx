package org.pircbotx.listeners;

import org.pircbotx.events.QuitEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface QuitListener extends Listener {
	public void onQuit(QuitEvent event);
}
