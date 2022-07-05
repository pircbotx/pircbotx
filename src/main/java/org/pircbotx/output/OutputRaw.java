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
package org.pircbotx.output;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Send raw lines to the server with locking and message delay support.
 */
//@RequiredArgsConstructor
@Slf4j
public class OutputRaw {
	public static final Marker OUTPUT_MARKER = MarkerFactory.getMarker("pircbotx.output");
	@NonNull
	protected final PircBotX bot;
	protected final ReentrantLock writeLock = new ReentrantLock(true);
	
	
	protected final RateLimiter limiter;
	
	public OutputRaw(PircBotX bot) {
		this.bot = bot;
		long delayMs = bot.getConfiguration().getMessageDelay().getDelay(); 
		
		if (delayMs >= 1)
			limiter = RateLimiter.create(1000.0 / delayMs);
		else
			limiter = RateLimiter.create(10000);
	}

	/**
	 * Sends a raw line through the outgoing message queue.
	 *
	 * @param line The raw line to send to the IRC server.
	 */
	public void rawLine(String line) {
		rawLine(line, null); 
	}
	
	
	/**
	 * Sends a raw line through the outgoing message queue.
	 *
	 * @param line The raw line to send to the IRC server.
	 * @param logline the line to be used in log, if you don't want the real line logged because it contains secrets.
	 */
	public void rawLine(String line, String logline) {
		checkArgument(StringUtils.isNotBlank(line), "Cannot send empty line to server: '%s'", line);
		checkArgument(bot.isConnected(), "Not connected to server");				
		
		limiter.acquire();
		
		if (StringUtils.isNotBlank(logline))
			log.info(OUTPUT_MARKER, logline);
		else
			log.info(OUTPUT_MARKER, line);		
		writeLock.lock();

		
		try {
			Utils.sendRawLineToServer(bot, line);
		} catch (IOException e) {
			throw new RuntimeException("IO exception when sending line to server, is the network still up? " + exceptionDebug(), e);
		} catch (Exception e) {
			throw new RuntimeException("Could not send line to server. " + exceptionDebug(), e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Sends a raw line to the IRC server as soon as possible without resetting
	 * the message delay for messages waiting to send
	 *
	 * @param line The raw line to send to the IRC server.
	 * @see #rawLineNow(java.lang.String, java.lang.String)
	 */
	public void rawLineNow(String line) {
		rawLineNow(line, null);
	}

	/**
	 * Sends a raw line to the IRC server as soon as possible
	 * <p>
	 * @param line The raw line to send to the IRC server
	 * @param logline the line to be used in log, if you don't want the real line logged because it contains secrets.
	 */
	public void rawLineNow(String line, String logline) {
		checkNotNull(line, "Line cannot be null");
		checkArgument(bot.isConnected(), "Not connected to server");
		
		if (StringUtils.isNotBlank(logline))
			log.info(OUTPUT_MARKER, logline);
		else
			log.info(OUTPUT_MARKER, line);		
		writeLock.lock();
		try {
			
			Utils.sendRawLineToServer(bot, line);
			

		} catch (IOException e) {
			throw new RuntimeException("IO exception when sending line to server, is the network still up? " + exceptionDebug(), e);
		} catch (Exception e) {
			throw new RuntimeException("Could not send line to server. " + exceptionDebug(), e);
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
		if (!bot.getConfiguration().isAutoSplitMessage() || (finalMessage.length() < realMaxLineLength && finalMessage.indexOf('\n')== -1) ) {
			//Length is good (or auto split message is false), just go ahead and send it
			rawLine(finalMessage);
			return;
		}

		int maxMessageLength = realMaxLineLength - (prefix + suffix).length();
		
		
		List<String> lines = Splitter.on('\n').omitEmptyStrings().trimResults().splitToList(message);
		for(String line : lines) {
			finalMessage = prefix + line + suffix;
		
			//Too long, split it up
			//v3 word split, just use Apache commons lang
			for (String curPart : StringUtils.split(WordUtils.wrap(line, maxMessageLength, "\r\n", true), "\r\n")) {
				rawLine(prefix + curPart + suffix);
			}
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

	protected String exceptionDebug() {
		return "Connected: " + bot.isConnected() + " | Bot State: " + bot.getState();
	}
}
