package org.pircbotx.listeners;

import org.pircbotx.events.RemoveModeratedEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveModeratedListener extends Listener {
	public void onRemoveModerated(RemoveModeratedEvent event);
}
