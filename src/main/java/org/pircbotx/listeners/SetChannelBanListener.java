package org.pircbotx.listeners;

import org.pircbotx.events.SetChannelBanEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetChannelBanListener extends Listener {
	public void onSetChannelBan(SetChannelBanEvent event);
}
