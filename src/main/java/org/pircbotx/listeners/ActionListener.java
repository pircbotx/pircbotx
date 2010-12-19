package org.pircbotx.listeners;

import org.pircbotx.events.ActionEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface ActionListener extends Listener {
	public void onAction(ActionEvent event);
}
