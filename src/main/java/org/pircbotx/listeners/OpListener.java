package org.pircbotx.listeners;

import org.pircbotx.events.OpEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface OpListener extends Listener {
	public void onOp(OpEvent event);
}
