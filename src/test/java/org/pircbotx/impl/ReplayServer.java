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
package org.pircbotx.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.managers.GenericListenerManager;

/**
 * Helpful server for replaying a raw log to the bot.
 * <p>
 * <b>NOTE:</b> In order to avoid write exceptions in the client you must
 * override {@link PircBotX#sendRawLine(java.lang.String) } to simply print the
 * output instead of sending it to this server!
 *
 * @author Leon Blakey
 */
@Slf4j
public class ReplayServer {
	/**
	 * Redirect output to given queue and trick code to believe its connected to
	 * the IRC server
	 */
	static class ReplayPircBotX extends PircBotX {
		protected final Queue<String> outputQueue;
		@Getter
		protected boolean closed = false;

		public ReplayPircBotX(Configuration configuration, Queue<String> outputQueue) {
			super(configuration);
			this.outputQueue = outputQueue;
		}

		@Override
		protected void sendRawLineToServer(String line) throws IOException {
			outputQueue.add(line);
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public void close() {
			closed = true;
		}
	}

	/**
	 * Run all listeners in main thread and seperately queue events
	 */
	@RequiredArgsConstructor
	static class ReplayListenerManager extends GenericListenerManager {
		protected final Queue<Event> eventQueue;

		@Override
		public void dispatchEvent(Event event) {
			eventQueue.add(event);
			super.dispatchEvent(event);
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			//Make sure the user specified a file
			if (args.length != 1 || args[0].trim().length() == 0) {
				System.out.println("Usage: org.pircbotx.impl.ReplayServer [log]");
				System.exit(1);
			}

			//Start replaying file
			File file = new File(args[0].trim());
			replayFile(file);
		} catch (Exception t) {
			log.debug("Caught exception in main, closing", t);
			System.exit(3);
		}
	}

	public static void replayFile(File file) throws Exception {
		if (!file.exists()) {
			throw new IOException("File " + file + " does not exist");
		}
		log.info("---Replaying file {}---", file.getAbsolutePath());

		final LinkedList<Event> eventQueue = Lists.newLinkedList();
		Configuration config = new Configuration.Builder()
				.setName("QuackPirc")
				.setLogin("QP")
				.addServer("example.com")
				.setNickservPassword(System.getProperty("nickserv"))
				.setListenerManager(new ReplayListenerManager(eventQueue))
				.setShutdownHookEnabled(false)
				.buildConfiguration();

		final LinkedList<String> outputQueue = Lists.newLinkedList();
		ReplayPircBotX bot = new ReplayPircBotX(config, outputQueue);

		BufferedReader fileInput = new BufferedReader(new FileReader(file));
		boolean skippedHeader = false;
		while (true) {
			String lineRaw = fileInput.readLine();
			if (bot.isClosed() && StringUtils.isNotBlank(lineRaw)) {
				throw new RuntimeException("bot is closed but file still has line " + lineRaw);
			} else if (!bot.isClosed() && StringUtils.isBlank(lineRaw)) {
				throw new RuntimeException("bot is not closed but file doesn't have any more lines");
			} else if (bot.isClosed() && StringUtils.isBlank(lineRaw)) {
				log.debug("(done) Bot is closed and file doesn't have any more lines");
				break;
			}

			log.debug("(line) " + lineRaw);
			String[] lineParts = StringUtils.split(lineRaw, " ", 2);
			String command = lineParts[0];
			String line = lineParts[1];

			//For now skip the info lines PircBotX is supposed to send on connect
			//They are only sent when connect() is called which requires multithreading
			if (!skippedHeader) {
				if (command.equals("pircbotx.output"))
					continue;
				else if (command.equals("pircbotx.input")) {
					log.debug("Finished skipping header");
					skippedHeader = true;
				} else
					throw new RuntimeException("Unknown line " + lineRaw);
			}

			if (command.equals("pircbotx.input")) {
				bot.getInputParser().handleLine(line);
			} else if (command.equals("pircbotx.output")) {
				String lastOutput = outputQueue.isEmpty() ? null : outputQueue.pop();
				if (StringUtils.startsWith(line, "JOIN")) {
					log.debug("Skipping JOIN output, server should send its own JOIN");
				} else if (StringUtils.startsWith(line, "QUIT")) {
					log.debug("Skipping QUIT output, server should send its own QUIT");
				} else if (!line.equals(lastOutput)) {
					log.debug("Expected last output: " + line);
					log.error("Given last output: " + lastOutput);
					for (String curOutput : outputQueue) {
						log.debug("Queued output: " + curOutput);
					}
					throw new RuntimeException("Failed to verify output (see log)");
				}
			} else {
				throw new RuntimeException("Unknown line " + lineRaw);
			}

			for (Event curEvent : Iterables.consumingIterable(eventQueue))
				log.debug("(events) " + curEvent);
		}

		log.debug("---File parsed successfully---");
	}

	private static void assertEquals(String source, String equals, String error) {
		if (!source.equals(equals))
			throw new RuntimeException(error);
	}
}
