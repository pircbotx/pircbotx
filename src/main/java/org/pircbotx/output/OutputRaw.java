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
package org.pircbotx.output;

import static com.google.common.base.Preconditions.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
@Slf4j
public class OutputRaw {
	protected static final Marker OUTPUT_MARKER = MarkerFactory.getMarker("pircbotx.output");
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final Configuration configuration;
	protected Writer outputWriter;
	protected final ReentrantLock writeLock = new ReentrantLock(true);
	protected final Condition writeNowCondition = writeLock.newCondition();

	protected void init(Socket socket) throws IOException {
		outputWriter = new OutputStreamWriter(socket.getOutputStream(), configuration.getEncoding());
	}
	
	/**
	 * Sends a raw line through the outgoing message queue.
	 *
	 * @param line The raw line to send to the IRC server.
	 */
	public void rawLine(String line) {
		checkNotNull(line, "Line cannot be null");
		if (line == null)
			throw new NullPointerException("Cannot send null messages to server");
		if (!bot.isConnected())
			throw new RuntimeException("Not connected to server");
		writeLock.lock();
		try {
			rawLineToServer(line);
			//Block for messageDelay. If rawLineNow is called with resetDelay
			//the condition is tripped and we wait again
			while (writeNowCondition.await(configuration.getMessageDelay(), TimeUnit.MILLISECONDS)) {
			}
		} catch (Exception e) {
			throw new RuntimeException("Couldn't pause thread for message delay", e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Sends a raw line to the IRC server as soon as possible without resetting
	 * the message delay for messages waiting to send
	 *
	 * @param line The raw line to send to the IRC server.
	 * @see #rawLineNow(java.lang.String, boolean) 
	 */
	public void rawLineNow(String line) {
		rawLineNow(line, false);
	}

	/**
	 * Sends a raw line to the IRC server as soon as possible
	 * @param line The raw line to send to the IRC server
	 * @param resetDelay If true, pending messages will reset their delay.
	 */
	public void rawLineNow(String line, boolean resetDelay) {
		checkNotNull(line, "Line cannot be null");
		if (!bot.isConnected())
			throw new RuntimeException("Not connected to server");
		writeLock.lock();
		try {
			rawLineToServer(line);
			if (resetDelay)
				//Reset the 
				writeNowCondition.signalAll();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Actually sends the raw line to the server. This method is NOT SYNCHRONIZED 
	 * since it's only called from methods that handle locking
	 * @param line 
	 */
	protected void rawLineToServer(String line) {
		if (line.length() > configuration.getMaxLineLength() - 2)
			line = line.substring(0, configuration.getMaxLineLength() - 2);
		try {
			log.info(OUTPUT_MARKER, line);
			outputWriter.write(line + "\r\n");
			outputWriter.flush();
		} catch (Exception e) {
			//Not much else we can do, but this requires attention of whatever is calling this
			throw new RuntimeException("Exception encountered when writing to socket", e);
		}
	}

	public void rawLineSplit(String prefix, String message) {
		rawLineSplit(prefix, message, "");
	}

	public void rawLineSplit(String prefix, String message, String suffix) {
		checkNotNull(prefix, "Prefix cannot be null");
		checkNotNull(message, "Message cannot be null");
		checkNotNull(suffix, "Suffix cannot be null");

		//Find if final line is going to be shorter than the max line length
		String finalMessage = prefix + message + suffix;
		int realMaxLineLength = configuration.getMaxLineLength() - 2;
		if (!configuration.isAutoSplitMessage() || finalMessage.length() < realMaxLineLength) {
			//Length is good (or auto split message is false), just go ahead and send it
			rawLine(finalMessage);
			return;
		}

		//Too long, split it up
		int maxMessageLength = realMaxLineLength - (prefix + suffix).length();
		//Oh look, no function to split every nth char. Since regex is expensive, use this nonsense
		int iterations = (int) Math.ceil(message.length() / (double) maxMessageLength);
		for (int i = 0; i < iterations; i++) {
			int endPoint = (i != iterations - 1) ? ((i + 1) * maxMessageLength) : message.length();
			String curMessagePart = prefix + message.substring(i * maxMessageLength, endPoint) + suffix;
			rawLine(curMessagePart);
		}
	}

	/**
	 * Gets the number of lines currently waiting in the outgoing message Queue.
	 * If this returns 0, then the Queue is empty and any new message is likely
	 * to be sent to the IRC server immediately.
	 *
	 * @since PircBot 0.9.9
	 *
	 * @return The number of lines in the outgoing message Queue.
	 */
	public int getOutgoingQueueSize() {
		return writeLock.getHoldCount();
	}
}
