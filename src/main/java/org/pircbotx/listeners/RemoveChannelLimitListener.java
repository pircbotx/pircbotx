package org.pircbotx.listeners;

import org.pircbotx.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveChannelLimitListener extends Listener {
	public void onRemoveChannelLimit(RemoveChannelLimitEvent event);
}
