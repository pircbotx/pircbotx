package org.pircbotx.listeners;

import org.pircbotx.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveChannelBanListener extends Listener {
	public void onRemoveChannelBan(RemoveChannelBanEvent event);
}
