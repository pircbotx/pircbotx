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

import org.pircbotx.User;
import org.pircbotx.exception.DccException;
import org.pircbotx.exception.DccException.Reason;

/**
 * This class will Receive the acknowledgement of bytes when sending a file.
 * This will keep the SendFileTransfer alive until all bytes are received by the
 * client.
 */
public class ReceiveFileTransferAcknowlegement extends Thread {

	protected User user;
	protected SendFileTransfer sendFileTransfer;
	protected SocketChannel inChannel;
	protected FileChannel outChannel;
	protected ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
	protected Boolean running = true;
	protected long totalBytesAcknowleged = 0;
	protected int previousBytesAcknowleged = 0;

	public ReceiveFileTransferAcknowlegement(User user, SendFileTransfer sendFileTransfer, SocketChannel inChannel,
			FileChannel outChannel) {
		this.user = user;
		this.inChannel = inChannel;
		this.outChannel = outChannel;
		this.sendFileTransfer = sendFileTransfer;
	}

	/**
	 * mIRC sends Acks of bytes/4gb to help calculate file position. KVIrc and
	 * HexChat do not.
	 *
	 * Ignore the mIRC position Acks and use this internal counter for bytes
	 * received.
	 *
	 * @throws IOException
	 */
	protected void receiveAcknowledge() throws IOException {
		inChannel.read(byteBuffer);
		byteBuffer.clear();
		int bytesAcknowledged = byteBuffer.getInt(0);
		if (bytesAcknowledged != (int) (totalBytesAcknowleged / ((long) Integer.MAX_VALUE * 2))
				&& totalBytesAcknowleged != bytesAcknowledged) {
			totalBytesAcknowleged += (bytesAcknowledged - previousBytesAcknowleged);
			sendFileTransfer.fileTransferStatus.bytesAcknowledged = totalBytesAcknowleged;
			previousBytesAcknowleged = bytesAcknowledged;
		}
	}

	@Override
	public void run() {
		try {
			totalBytesAcknowleged = outChannel.position();
			previousBytesAcknowleged = (int) totalBytesAcknowleged;
			while (running && totalBytesAcknowleged != outChannel.size()) {
				receiveAcknowledge();
			}
		} catch (IOException e) {
			if (sendFileTransfer != null) {
				sendFileTransfer.fileTransferStatus.exception = new DccException(Reason.FILE_TRANSFER_CANCELLED, user,
						"User closed socket", e);
				sendFileTransfer.fileTransferStatus.dccState = DccState.ERROR;
			}
		}
	}
}
