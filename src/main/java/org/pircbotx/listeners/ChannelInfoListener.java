package org.pircbotx.listeners;

import org.pircbotx.events.ChannelInfoEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ChannelInfoListener extends Listener {
	public void onChannelInfo(ChannelInfoEvent event);
}
