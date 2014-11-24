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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * A DCC File Transfer initiated by another user.
 *
 * @author Leon Blakey
 */
@Slf4j
public class ReceiveFileTransfer extends FileTransfer {
	public ReceiveFileTransfer(Configuration configuration, Socket socket, User user, File file, long startPosition, long fileSize) {
		super(configuration, socket, user, file, startPosition, fileSize);
	}

	protected void transferFile() throws IOException {
		@Cleanup
		BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
		@Cleanup
		OutputStream socketOutput = socket.getOutputStream();
		@Cleanup
		RandomAccessFile fileOutput = new RandomAccessFile(file.getCanonicalPath(), "rw");
		fileOutput.seek(startPosition);

		//Recieve file
		int defaultBufferSize = configuration.getDccTransferBufferSize();
		byte[] inBuffer = new byte[defaultBufferSize];
		byte[] outBuffer = new byte[4];
		while (true) {
			//Adjust buffer based on remaining bytes (if we know how big the file is)
			long remainingBytes = fileSize - bytesTransfered;
			int bufferSize = (remainingBytes > 0 && remainingBytes < defaultBufferSize)
					? (int)remainingBytes : defaultBufferSize;
			
			//Read next part of incomming file
			int bytesRead = socketInput.read(inBuffer, 0, bufferSize);
			if(bytesRead == -1)
				//Done
				break;
			
			//Write to file
			fileOutput.write(inBuffer, 0, bytesRead);
			bytesTransfered += bytesRead;
			
			//Send back an acknowledgement of how many bytes we have got so far.
			//Convert bytesTransfered to an "unsigned, 4 byte integer in network byte order", per DCC specification
			outBuffer[0] = (byte) ((bytesTransfered >> 24) & 0xff);
			outBuffer[1] = (byte) ((bytesTransfered >> 16) & 0xff);
			outBuffer[2] = (byte) ((bytesTransfered >> 8) & 0xff);
			outBuffer[3] = (byte) (bytesTransfered & 0xff);
			socketOutput.write(outBuffer);
			onAfterSend();
			
			if(remainingBytes - bufferSize == 0)
				//Were done
				break;
		}
	}
}
