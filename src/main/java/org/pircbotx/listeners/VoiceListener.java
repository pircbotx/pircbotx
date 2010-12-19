package org.pircbotx.listeners;

import org.pircbotx.events.VoiceEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface VoiceListener extends Listener {
	public void onVoice(VoiceEvent event);
}
