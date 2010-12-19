package org.pircbotx.listeners;

import org.pircbotx.events.SetSecretEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface SetSecretListener extends Listener {
	public void onSetSecret(SetSecretEvent event);
}
