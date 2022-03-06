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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.pircbotx.PircBotX;
import org.pircbotx.dcc.DccHandler.PendingFileTransfer;
import org.pircbotx.exception.DccException;
import org.pircbotx.exception.DccException.Reason;

import lombok.extern.slf4j.Slf4j;

/**
 * Send a file to a user and wait for all acknowledgement. Report statistics
 * about the file and file transfer.
 */
@Slf4j
public class SendFileTransfer extends FileTransfer {

	private ReceiveFileTransferAcknowlegement acknowledgement;

	public SendFileTransfer(PircBotX bot, DccHandler dccHandler, PendingFileTransfer pendingFileTransfer, File file) {
		super(bot, dccHandler, pendingFileTransfer, file);
	}

	// TODO Does this need to be configurable?
	// The only benefit to having it small is updating the file pointer more
	// frequently
	// Transfer stats can be polled via Acknowledge bytes
	long bytesToTransfer = 8192;

	@Override
	protected void transferFile() {
		try (SocketChannel outChannel = socket.getChannel();
				FileInputStream inputStream = new FileInputStream(file);
				FileChannel inChannel = inputStream.getChannel();) {

			acknowledgement = new ReceiveFileTransferAcknowlegement(user, this, outChannel, inChannel);
			acknowledgement.start();
			fileTransferStatus.start();

			inChannel.position(fileTransferStatus.startPosition);
			while (inChannel.position() < fileTransferStatus.fileSize) {
				if (dccHandler.shuttingDown || fileTransferStatus.dccState == DccState.SHUTDOWN) {
					acknowledgement.running = false;
					break;
				}

				if (fileTransferStatus.dccState == DccState.ERROR) {
					// Acknowledgement failed
					throw fileTransferStatus.exception;
				}

				if (bytesToTransfer > (fileTransferStatus.fileSize - inChannel.position())) {
					bytesToTransfer = (fileTransferStatus.fileSize - inChannel.position());
				}
				inChannel.transferTo(inChannel.position(), bytesToTransfer, outChannel);
				inChannel.position(inChannel.position() + bytesToTransfer);
				fileTransferStatus.bytesTransfered = inChannel.position();

			}

			fileTransferStatus.dccState = DccState.WAITING;
			log.info("Send file transfer of file {} entered {} state for client acknowledgement", file.getName(),
					fileTransferStatus.dccState);

			try {
				acknowledgement.join();
				fileTransferStatus.join();

				fileTransferStatus.dccState = DccState.DONE;

			} catch (InterruptedException e) {
				fileTransferStatus.dccState = DccState.ERROR;
				log.error(
						"Send file transfer of file {} failed to clean up gracefully! Please report this error with logs.",
						file.getName(), e);
			}

		} catch (IOException e) {
			fileTransferStatus.dccState = DccState.ERROR;
			fileTransferStatus.exception = new DccException(Reason.FILE_TRANSFER_CANCELLED, user, "User closed socket",
					e);
		} finally {

			log.info("Send file transfer of file {} ended with state {}", file.getName(), fileTransferStatus.dccState);

		}
	}
}
