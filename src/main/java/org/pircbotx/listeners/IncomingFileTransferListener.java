package org.pircbotx.listeners;

import org.pircbotx.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface IncomingFileTransferListener extends Listener {
	public void onIncomingFileTransfer(IncomingFileTransferEvent event);
}
