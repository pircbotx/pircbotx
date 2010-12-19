package org.pircbotx.listeners;

import org.pircbotx.events.FingerEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface FingerListener extends Listener {
	public void onFinger(FingerEvent event);
}
