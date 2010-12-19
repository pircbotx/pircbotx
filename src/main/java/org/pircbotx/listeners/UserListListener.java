package org.pircbotx.listeners;

import org.pircbotx.events.UserListEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface UserListListener extends Listener {
	public void onUserList(UserListEvent event);
}
