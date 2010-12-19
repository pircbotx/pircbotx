package org.pircbotx.listeners;

import org.pircbotx.events.SetPrivateEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetPrivateListener extends Listener {
	public void onSetPrivate(SetPrivateEvent event);
}
