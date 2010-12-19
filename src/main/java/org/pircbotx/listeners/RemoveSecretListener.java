package org.pircbotx.listeners;

import org.pircbotx.events.RemoveSecretEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface RemoveSecretListener extends Listener {
	public void onRemoveSecret(RemoveSecretEvent event);
}
