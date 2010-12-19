package org.pircbotx.listeners;

import org.pircbotx.events.SetModeratedEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetModeratedListener extends Listener {
	public void onSetModerated(SetModeratedEvent event);
}
