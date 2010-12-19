package org.pircbotx.listeners;

import org.pircbotx.events.KickEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface KickListener extends Listener {
	public void onKick(KickEvent event);
}
