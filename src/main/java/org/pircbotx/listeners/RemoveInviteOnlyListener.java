package org.pircbotx.listeners;

import org.pircbotx.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveInviteOnlyListener extends Listener {
	public void onRemoveInviteOnly(RemoveInviteOnlyEvent event);
}
