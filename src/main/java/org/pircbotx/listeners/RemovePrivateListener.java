package org.pircbotx.listeners;

import org.pircbotx.events.RemovePrivateEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemovePrivateListener extends Listener {
	public void onRemovePrivate(RemovePrivateEvent event);
}
