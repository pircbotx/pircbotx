package org.pircbotx.listeners;

import org.pircbotx.events.VersionEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface VersionListener extends Listener {
	public void onVersion(VersionEvent event);
}
