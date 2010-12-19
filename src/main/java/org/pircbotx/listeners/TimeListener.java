package org.pircbotx.listeners;

import org.pircbotx.events.TimeEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface TimeListener extends Listener {
	public void onTime(TimeEvent event);
}
