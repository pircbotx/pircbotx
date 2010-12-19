package org.pircbotx.listeners;

import org.pircbotx.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveChannelKeyListener extends Listener {
	public void onRemoveChannelKey(RemoveChannelKeyEvent event);
}
