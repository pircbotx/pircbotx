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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static org.pircbotx.DccFileTransfer.BUFFER_SIZE;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * Handle everything related to receiving a file from another IRC user
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class ReceiveFileTransfer {
	@Getter
	protected final User user;
	@Getter
	protected final Socket socket;
	@Getter
	protected final long size;
	@Getter
	protected final String filename;
	protected boolean resume;
	protected long startPos;
	@Getter
	protected long bytesReceived;
	@Getter
	protected DccState state = DccState.INIT;

	public void receiveFile(File destination) throws IOException {
		//Prevent being called multiple times
		if (state != DccState.INIT)
			synchronized (state) {
				if (state != DccState.INIT)
					throw new RuntimeException("Cannot receive file twice (Current state: " + state + ")");
			}
		state = DccState.RUNNING;

		@Cleanup
		BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
		@Cleanup
		BufferedOutputStream socketOutput = new BufferedOutputStream(socket.getOutputStream());
		@Cleanup
		BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(destination.getCanonicalPath()));

		//Recieve file
		byte[] inBuffer = new byte[BUFFER_SIZE];
		byte[] outBuffer = new byte[4];
		int bytesRead = 0;
		while ((bytesRead = socketInput.read(inBuffer, 0, inBuffer.length)) != -1) {
			fileOutput.write(inBuffer, 0, bytesRead);
			bytesReceived += bytesRead;
			//Send back an acknowledgement of how many bytes we have got so far.
			//TODO: What does this actually do?
			outBuffer[0] = (byte) ((bytesReceived >> 24) & 0xff);
			outBuffer[1] = (byte) ((bytesReceived >> 16) & 0xff);
			outBuffer[2] = (byte) ((bytesReceived >> 8) & 0xff);
			//TODO: Why does netbeans say this does nothing?
			outBuffer[3] = (byte) ((bytesReceived >> 0) & 0xff);
			socketOutput.write(outBuffer);
			socketOutput.flush();
		}

		//Finished recieving file
		fileOutput.flush();

		state = DccState.DONE;
	}
}
