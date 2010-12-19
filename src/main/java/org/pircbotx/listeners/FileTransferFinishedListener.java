package org.pircbotx.listeners;

import org.pircbotx.events.FileTransferFinishedEvent;
import org.pircbotx.hooks.helpers.Listener;

public interface FileTransferFinishedListener extends Listener {
	public void onFileTransferFinished(FileTransferFinishedEvent event);
}
