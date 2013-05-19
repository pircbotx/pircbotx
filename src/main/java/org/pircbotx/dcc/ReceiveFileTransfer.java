/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.dcc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import lombok.Cleanup;
import org.pircbotx.Configuration;
import org.pircbotx.User;

/**
 * A DCC File Transfer initiated by another user.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ReceiveFileTransfer extends FileTransfer {
	public ReceiveFileTransfer(Configuration configuration, SocketChannel socket, User user, File file, long startPosition) {
		super(configuration, socket, user, file, startPosition);
	}

	protected void transferFile() throws IOException {
		@Cleanup
		RandomAccessFile fileAccess = new RandomAccessFile(file.getCanonicalPath(), "rw");
		FileChannel fileChannel = fileAccess.getChannel();
		fileChannel.position(startPosition);

		//Recieve file
		ByteBuffer buffer = ByteBuffer.allocate(configuration.getDccTransferBufferSize());
		ByteBuffer transferedBuffer = ByteBuffer.allocate(4);
		int	bytesRead;
		while ((bytesRead = socket.read(buffer)) != -1) {
			//Write to file
			buffer.flip();
			fileChannel.write(buffer);
			
			//Send back an acknowledgement of how many bytes we have got so far.
			bytesTransfered += bytesRead;
			transferedBuffer.putLong(0, bytesTransfered);
			socket.write(transferedBuffer);
		}
	}
}
