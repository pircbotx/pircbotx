package org.pircbotx.listeners;

import org.pircbotx.events.UserModeEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface UserModeListener extends Listener {
	public void onUserMode(UserModeEvent event);
}
