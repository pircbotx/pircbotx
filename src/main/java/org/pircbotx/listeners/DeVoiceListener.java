package org.pircbotx.listeners;

import org.pircbotx.events.DeVoiceEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface DeVoiceListener extends Listener {
	public void onDeVoice(DeVoiceEvent event);
}
