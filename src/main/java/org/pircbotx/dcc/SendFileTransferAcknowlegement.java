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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

/**
 * Send current number of bytes received from a file transfer
 */
public class SendFileTransferAcknowlegement implements Callable<Long> {

	protected SocketChannel socketChannel;
	protected FileChannel fileChannel;

	public SendFileTransferAcknowlegement(SocketChannel socketChannel, FileChannel fileChannel) {
		this.socketChannel = socketChannel;
		this.fileChannel = fileChannel;
	}

	@Override
	public Long call() throws IOException {
		sendAcknowledge();
		return fileChannel.position();
	}

	/**
	 * Send acknowledge bytes
	 * 
	 * @param SocketChannel
	 * @param FileChannel
	 * @throws IOException
	 */
	protected void sendAcknowledge() throws IOException {
		socketChannel.write(ByteBuffer.allocate(4).putInt(0, (int) fileChannel.position()));
	}

}
