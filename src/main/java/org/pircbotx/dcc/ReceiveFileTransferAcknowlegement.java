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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class ReceiveFileTransferAcknowlegement extends Thread {

	protected SendFileTransfer sendFileTransfer;
	protected SocketChannel inChannel;
	protected FileChannel outChannel;

	public ReceiveFileTransferAcknowlegement(SendFileTransfer sendFileTransfer, SocketChannel inChannel,
			FileChannel outChannel) {
		this.inChannel = inChannel;
		this.outChannel = outChannel;
	}

	protected int receiveAcknowledge() throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
		inChannel.read(buffer);
		return buffer.getInt(0);
	}

	@Override
	public void run() {
		try {

			long bytesAcknowleged = 0;
			// Wait until the client has some data
			while (bytesAcknowleged == 0) {
				bytesAcknowleged = receiveAcknowledge();
				TimeUnit.SECONDS.sleep(1);
			}

			while (true) {
				int bytesRespondedFromClient = receiveAcknowledge();
				// Break loop after no more bytes transferred.
				// This implies the client has finished the transfer.
				// TODO how does DCC handle files over MAX_INT? It would be nice to compare
				// explicit bytes transferred.
				// TODO add some timeout mechanism if outChannel.pointer() < fileSize instead of
				// ending abruptly.
				if (bytesAcknowleged == bytesRespondedFromClient) {
					break;
				}
				bytesAcknowleged = bytesRespondedFromClient;
			}
		} catch (IOException | InterruptedException e) {
			sendFileTransfer.state = DccState.ERROR;
		}
	}
}
