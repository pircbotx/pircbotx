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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.pircbotx.Configuration;
import org.pircbotx.User;

/**
 * A DCC File Transfer initiated by another user.
 *
 * @author Leon Blakey
 */
public class ReceiveFileTransfer extends FileTransfer {
	public ReceiveFileTransfer(Configuration configuration, Socket socket, User user, File file, long startPosition,
			long fileSize) {
		super(configuration, socket, user, file, startPosition, fileSize);
	}

	protected void transferFile() throws IOException {
		byte[] outBuffer = new byte[4];
		long bytesToRead = 1024 * 1024;

		try (SocketChannel inChannel = socket.getChannel();
				FileOutputStream outputStream = new FileOutputStream(file);
				FileChannel outChannel = outputStream.getChannel();) {

			long bytesAcknowledged = 0;
			outChannel.position(this.startPosition);
			while (bytesAcknowledged < fileSize) {
				if (bytesToRead > (fileSize - bytesAcknowledged)) {
					bytesToRead = (fileSize - bytesAcknowledged);
				}
				bytesAcknowledged += outChannel.transferFrom(inChannel, outChannel.position(), bytesToRead);
				outChannel.position(bytesAcknowledged);

				outBuffer[0] = (byte) ((bytesAcknowledged >> 24) & 0xff);
				outBuffer[1] = (byte) ((bytesAcknowledged >> 16) & 0xff);
				outBuffer[2] = (byte) ((bytesAcknowledged >> 8) & 0xff);
				outBuffer[3] = (byte) (bytesAcknowledged & 0xff);
				inChannel.write(ByteBuffer.wrap(outBuffer));
				onAfterSend();
			}
		}
	}
}
