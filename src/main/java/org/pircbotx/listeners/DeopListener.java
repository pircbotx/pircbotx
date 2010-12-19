package org.pircbotx.listeners;

import org.pircbotx.events.DeopEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface DeopListener extends Listener {
	public void onDeop(DeopEvent event);
}
