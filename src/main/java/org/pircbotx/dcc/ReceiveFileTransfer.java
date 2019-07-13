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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.pircbotx.Configuration;
import org.pircbotx.User;

import lombok.extern.slf4j.Slf4j;

/**
 * A DCC File Transfer initiated by another user.
 *
 * @author Leon Blakey
 */
@Slf4j
public class ReceiveFileTransfer extends FileTransfer {

	SendFileTransferAcknowlegement acknowledge;

	public ReceiveFileTransfer(Configuration configuration, Socket socket, User user, File file, long startPosition,
			long fileSize) {
		super(configuration, socket, user, file, startPosition, fileSize);
	}

	@Override
	protected void transferFile() throws IOException {

		// TODO same as send files, does this buffer matter?
		long bytesToRead = 1024 * 1024;

		try (SocketChannel inChannel = socket.getChannel();
				RandomAccessFile outputStream = new RandomAccessFile(file, "rw");
				FileChannel outChannel = outputStream.getChannel();) {

			acknowledge = new SendFileTransferAcknowlegement(inChannel, outChannel);

			outChannel.position(startPosition);
			while (outChannel.position() < fileSize) {
				if (bytesToRead > (fileSize - outChannel.position())) {
					bytesToRead = (fileSize - outChannel.position());
				}
				outChannel.position(
						outChannel.position() + outChannel.transferFrom(inChannel, outChannel.position(), bytesToRead));

				// TODO move this into a status object
				bytesTransfered = outChannel.position();

				acknowledge.call();
			}
		} catch (IOException e) {
			// TODO catch exceptions here and return an error and reason
			this.state = DccState.ERROR;
			log.debug("Receive file transfer of file {} entered {} state: ", file.getName(), this.state.name());
		}
	}
}
