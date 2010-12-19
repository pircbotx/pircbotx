package org.pircbotx.listeners;

import org.pircbotx.events.PartEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface PartListener extends Listener {
	public void onPart(PartEvent event);
}
