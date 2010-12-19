package org.pircbotx.listeners;

import org.pircbotx.events.NickChangeEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface NickChangeListener extends Listener {
	public void onNickChange(NickChangeEvent event);
}
