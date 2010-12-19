package org.pircbotx.listeners;

import org.pircbotx.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetTopicProtectionListener extends Listener {
	public void onSetTopicProtection(SetTopicProtectionEvent event);
}
