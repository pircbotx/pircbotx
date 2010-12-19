package org.pircbotx.listeners;

import org.pircbotx.events.SetChannelKeyEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetChannelKeyListener extends Listener {
	public void onSetChannelKey(SetChannelKeyEvent event);
}
