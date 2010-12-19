package org.pircbotx.listeners;

import org.pircbotx.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveTopicProtectionListener extends Listener {
	public void onRemoveTopicProtection(RemoveTopicProtectionEvent event);
}
