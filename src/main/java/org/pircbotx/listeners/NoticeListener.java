package org.pircbotx.listeners;

import org.pircbotx.events.NoticeEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface NoticeListener extends Listener {
	public void onNotice(NoticeEvent event);
}
