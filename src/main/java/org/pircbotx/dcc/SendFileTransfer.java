/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.pircbotx.Configuration;
import org.pircbotx.User;

import lombok.extern.slf4j.Slf4j;

/**
 * A DCC File Transfer initiated by the bot
 *
 * @author Leon Blakey
 */
@Slf4j
public class SendFileTransfer extends FileTransfer {

	private ReceiveFileTransferAcknowlegement acknowledgement;

	public SendFileTransfer(Configuration configuration, Socket socket, User user, File file, long startPosition) {
		super(configuration, socket, user, file, startPosition, file.length());
	}

	// TODO Does this need to be configurable?
	// The only benefit to having it small is updating the file pointer more
	// frequently
	// Transfer stats can be polled via Acknowledge bytes
	long bytesToTransfer = (1024 * 1024);

	@Override
	protected void transferFile() throws IOException {

		try (SocketChannel outChannel = socket.getChannel();
				FileInputStream inputStream = new FileInputStream(file);
				FileChannel inChannel = inputStream.getChannel();) {
			acknowledgement = new ReceiveFileTransferAcknowlegement(this, outChannel, inChannel);
			acknowledgement.start();
			inChannel.position(this.startPosition);
			while (inChannel.position() < fileSize) {
				if (bytesToTransfer > (fileSize - inChannel.position())) {
					bytesToTransfer = (fileSize - inChannel.position());
				}
				inChannel.transferTo(inChannel.position(), bytesToTransfer, outChannel);
				inChannel.position(inChannel.position() + bytesToTransfer);

				// TODO move this into a status object
				bytesTransfered = inChannel.position();
			}
			this.state = DccState.WAITING;
			log.debug("Send file transfer of file {} entered {} state for client acknowledgement", file.getName(),
					this.state.name());
			acknowledgement.join();
		} catch (IOException | InterruptedException e) {
			// TODO catch exceptions here and return an error and reason
			this.state = DccState.ERROR;
			log.debug("Send file transfer of file {} entered {} state: ", file.getName(), this.state.name());
		}

	}
}
