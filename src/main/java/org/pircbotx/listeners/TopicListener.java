package org.pircbotx.listeners;

import org.pircbotx.events.TopicEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface TopicListener extends Listener {
	public void onTopic(TopicEvent event);
}
