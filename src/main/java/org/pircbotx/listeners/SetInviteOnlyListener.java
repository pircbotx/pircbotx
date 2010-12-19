package org.pircbotx.listeners;

import org.pircbotx.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetInviteOnlyListener extends Listener {
	public void onSetInviteOnly(SetInviteOnlyEvent event);
}
