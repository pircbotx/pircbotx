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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Send raw lines to the server with locking and message delay support.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
@Slf4j
public class OutputRaw {
	public static final Marker OUTPUT_MARKER = MarkerFactory.getMarker("pircbotx.output");
	@NonNull
	protected final PircBotX bot;
	protected final ReentrantLock writeLock = new ReentrantLock(true);
	protected final Condition writeNowCondition = writeLock.newCondition();
	protected final long delayNanos;
	protected long lastSentLine = 0;

	public OutputRaw(PircBotX bot) {
		this.bot = bot;
		this.delayNanos = bot.getConfiguration().getMessageDelay() * 1000000;
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
			//Block until we can send, taking into account a changing lastSentLine
			long curNanos = System.nanoTime();
			while (lastSentLine + delayNanos > curNanos) {
				writeNowCondition.await(lastSentLine + delayNanos - curNanos, TimeUnit.NANOSECONDS);
				curNanos = System.nanoTime();
			}
			log.info(OUTPUT_MARKER, line);
			Utils.sendRawLineToServer(bot, line);
			lastSentLine = System.nanoTime();
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
			log.info(OUTPUT_MARKER, line);
			Utils.sendRawLineToServer(bot, line);
			lastSentLine = System.nanoTime();
			if (resetDelay)
				//Reset the 
				writeNowCondition.signalAll();
		} finally {
			writeLock.unlock();
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
		int realMaxLineLength = bot.getConfiguration().getMaxLineLength() - 2;
		if (!bot.getConfiguration().isAutoSplitMessage() || finalMessage.length() < realMaxLineLength) {
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
