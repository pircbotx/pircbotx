/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.dcc;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.dcc.DccHandler.PendingFileTransfer;
import org.pircbotx.exception.DccException;
import org.pircbotx.exception.DccException.Reason;
import org.pircbotx.hooks.events.FileTransferCompleteEvent;

/**
 * A general active DCC file transfer
 */
@Slf4j
public abstract class FileTransfer {
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final Configuration configuration;
	@NonNull
	protected final DccHandler dccHandler;
	@NonNull
	protected Socket socket;
	@NonNull
	@Getter
	protected final User user;
	@NonNull
	@Getter
	protected final File file;
	@Getter
	protected FileTransferStatus fileTransferStatus;

	protected PendingFileTransfer pendingFileTransfer;

	protected final Object stateLock = new Object();

	public FileTransfer(PircBotX bot, DccHandler dccHandler, PendingFileTransfer pendingFileTransfer, File file) {
		this.bot = bot;
		this.configuration = bot.getConfiguration();
		this.pendingFileTransfer = pendingFileTransfer;
		this.user = pendingFileTransfer.user;
		this.file = file;
		this.dccHandler = dccHandler;
		fileTransferStatus = new FileTransferStatus(pendingFileTransfer.fileSize, pendingFileTransfer.position);
	}

	private void connectSocket() throws IOException {
		socket = dccHandler.establishSocketConnection(pendingFileTransfer);
	}

	/**
	 * Shut down an active file transfer. This sets the transfer state to
	 * {@link DccState#SHUTDOWN} and closes the underlying socket, which
	 * interrupts any blocking {@code transferFrom}/{@code transferTo} operation
	 * on the transfer thread.
	 */
	public void shutdown() {
		fileTransferStatus.dccState = DccState.SHUTDOWN;
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				log.warn("Failed to close socket during shutdown of transfer for file {}", file.getName(), e);
			}
		}
	}

	/**
	 * Transfer the file to the user
	 *
	 * @throws IOException If an error occurred during transfer
	 */
	public void transfer() {

		// Prevent being called multiple times
		if (fileTransferStatus.dccState != DccState.INIT) {
			synchronized (stateLock) {
				if (fileTransferStatus.dccState != DccState.INIT) {
					throw new RuntimeException(
							"Cannot receive file twice (Current state: " + fileTransferStatus.dccState + ")");
				}
			}
		}

		fileTransferStatus.dccState = DccState.CONNECTING;

		try {
			connectSocket();

			fileTransferStatus.dccState = DccState.RUNNING;

			transferFile();

		} catch (SocketTimeoutException e) {
			fileTransferStatus.dccState = DccState.ERROR;
			fileTransferStatus.exception = new DccException(Reason.FILE_TRANSFER_TIMEOUT, user, "Socket connection timeout", e);
		} catch (IOException e) {
			fileTransferStatus.dccState = DccState.ERROR;
			fileTransferStatus.exception = new DccException(Reason.FILE_TRANSFER_TIMEOUT, user, "General IOException", e);
		} finally {

			bot.getConfiguration().getListenerManager()
					.onEvent(new FileTransferCompleteEvent(bot, fileTransferStatus, user, this.getFile().getName(),
							(socket != null) ? socket.getInetAddress() : null,
							(socket != null) ? socket.getLocalPort() : 0, fileTransferStatus.fileSize,
							pendingFileTransfer.passive, true));
		}

	}

	protected abstract void transferFile();

}
