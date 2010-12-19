package org.pircbotx.listeners;

import org.pircbotx.events.SetChannelLimitEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetChannelLimitListener extends Listener {
	public void onSetChannelLimit(SetChannelLimitEvent event);
}
