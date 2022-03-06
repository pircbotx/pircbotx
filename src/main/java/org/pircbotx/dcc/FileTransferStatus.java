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

import java.util.concurrent.TimeUnit;

import org.pircbotx.exception.DccException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Information about a file transfer This is kept in sync by the instances of
 * SendFileTransfer and ReceiveFileTransfer
 */
@Slf4j
public class FileTransferStatus extends Thread {

	@Getter
	protected DccState dccState = DccState.INIT;
	@Getter
	protected long startPosition = 0;
	@Getter
	protected long fileSize = 0;
	@Getter
	protected long bytesTransfered = 0;
	@Getter
	protected long bytesAcknowledged = 0;
	@Getter
	protected long bytesPerSecond = 0;
	@Getter
	protected long averageBytesPerSecond = 0;
	@Getter
	protected DccException exception;

	public FileTransferStatus(long fileSize, long startPosition) {
		this.fileSize = fileSize;
		this.startPosition = startPosition;
	}

	/**
	 * Is the transfer finished?
	 *
	 * @return True if its finished
	 */
	public boolean isFinished() {
		return (dccState == DccState.DONE || dccState == DccState.ERROR);
	}

	/**
	 * Was the transfer successful
	 * 
	 * @return True if done and bytes acknowledged match file size
	 */
	public boolean isSuccessful() {
		return (dccState == DccState.DONE && bytesAcknowledged == fileSize);
	}

	/**
	 * Get percentage of file transfer. This is calculated based on the bytes
	 * received by the transfer requester
	 * 
	 * @return integer
	 */
	public double getPercentageComplete() {
		return (100 * ((double) bytesAcknowledged / fileSize));
	}

	@Override
	public void run() {
		int counter = 0;
		long myBytesAcknowleged = startPosition;
		long myBytesAveraged = 0;
		long myBytesPerSecond = 0;
		while (dccState == DccState.RUNNING) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				log.error("Speed calculation has interrupted?", e);
			}
			bytesPerSecond = bytesAcknowledged - myBytesAcknowleged;
			if (bytesPerSecond < 0) {
				continue;
			}
			myBytesPerSecond = bytesPerSecond;
			// Attempt to make this a smooth average. Is there some calculus way to do this?
			myBytesAveraged = (myBytesPerSecond + myBytesAveraged) / 2;
			counter++;
			if (counter >= 3) {
				averageBytesPerSecond = (myBytesAveraged + averageBytesPerSecond) / 2;
				counter = 0;
			}
			myBytesAcknowleged = bytesAcknowledged;
		}
	}
}
