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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * Sends a file to a user
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SendFileTransfer implements FileTransfer {
	protected final Configuration configuration;
	@Getter
	protected final User user;
	@Getter
	protected final String filename;
	protected final Socket socket;
	@Getter
	protected final long startPosition;
	@Getter
	protected long filesize;
	@Getter
	protected long bytesTransfered;
	@Getter
	protected DccState state = DccState.INIT;
	protected final Object stateLock = new Object();

	public void sendFile(File source) throws IOException {
		//Prevent being called multiple times
		if(state != DccState.INIT)
			synchronized(stateLock) {
				if(state != DccState.INIT)
					throw new RuntimeException("Cannot receive file twice (Current state: " + state + ")");
			}
		state = DccState.RUNNING;
		
		filesize = source.length();

		@Cleanup
		BufferedOutputStream socketOutput = new BufferedOutputStream(socket.getOutputStream());
		@Cleanup
		BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
		@Cleanup
		BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(source));

		// Check for resuming.
		if (startPosition > 0) {
			long bytesSkipped = 0;
			while (bytesSkipped < startPosition)
				bytesSkipped += fileInput.skip(startPosition - bytesSkipped);
		}

		byte[] outBuffer = new byte[configuration.getDccTransferBufferSize()];
		byte[] inBuffer = new byte[4];
		int bytesRead = 0;
		while ((bytesRead = fileInput.read(outBuffer, 0, outBuffer.length)) != -1) {
			socketOutput.write(outBuffer, 0, bytesRead);
			socketOutput.flush();
			socketInput.read(inBuffer, 0, inBuffer.length);
			bytesTransfered += bytesRead;
		}
		
		state = DccState.DONE;
	}
}
