package org.pircbotx.listeners;

import org.pircbotx.events.ModeEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ModeListener extends Listener {
	public void onMode(ModeEvent event);
}
