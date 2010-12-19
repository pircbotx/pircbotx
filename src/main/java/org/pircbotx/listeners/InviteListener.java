package org.pircbotx.listeners;

import org.pircbotx.events.InviteEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface InviteListener extends Listener {
	public void onInvite(InviteEvent event);
}
