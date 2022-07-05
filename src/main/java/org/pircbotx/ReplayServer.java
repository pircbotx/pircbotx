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
package org.pircbotx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.pircbotx.delay.StaticReadonlyDelay;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * Helpful server for replaying a raw log to the bot.
 */
@Slf4j
public class ReplayServer {
	/**
	 * Redirect output to given queue and trick code to believe its connected to
	 * the IRC server
	 */
	@Slf4j
	protected static class ReplayPircBotX extends PircBotX {
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
	@Slf4j
	protected static class WrapperListenerManager implements ListenerManager {
		private static interface ImplExclude {
			public void onEvent(Event event);
		}
		@Delegate(excludes = ImplExclude.class)
		protected final ListenerManager impl;
		protected final Queue<Event> eventQueue;

		@Override
		public void onEvent(Event event) {
			eventQueue.add(event);
			impl.onEvent(event);
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

	static class ReplayListener extends ListenerAdapter {
		@Override
		public void onGenericMessage(GenericMessageEvent event) throws Exception {
			if (event.getMessage().startsWith("?dumpusers")) {
				System.out.println("===command dumpusers start===");
				for (User curUser : event.getBot().getUserChannelDao().getAllUsers())
					log.debug(curUser.getNick() + "!" + curUser.getLogin() + "@" + curUser.getHostname() + " - " + curUser.getHostmask());
				System.out.println("===command dumpusers end===");
			}
		}
	}

	public static void replayFile(File file) throws Exception {
		replayFile(file, generateConfig());
	}

	public static void replayFile(File file, Configuration.Builder config) throws Exception {
		if (!file.exists()) {
			throw new IOException("File " + file + " does not exist");
		}
		@Cleanup FileInputStream fileInput = new FileInputStream(file);
		replay(config, fileInput, "file " + file.getCanonicalPath());
	}

	public static Configuration.Builder generateConfig() {
		return new Configuration.Builder()
				.setName("QuackPirc")
				.setLogin("QP")
				.addServer("example.com")
				.setNickservPassword(System.getProperty("nickserv"))
				.setMessageDelay( new StaticReadonlyDelay(0) )
				.setListenerManager(new GenericListenerManager())
				.setShutdownHookEnabled(false);
	}

	public static void replay(Configuration.Builder config, InputStream input, String title) throws Exception {
		log.info("---Replaying {}---", title);
		StopWatch timer = new StopWatch();
		timer.start();

		//Wrap listener manager with ours that siphons off events
		final Queue<Event> eventQueue = new LinkedList<>();
		WrapperListenerManager newManager = new WrapperListenerManager(config.getListenerManager(), eventQueue);
		config.setListenerManager(newManager);
		config.addListener(new ReplayListener());

		final LinkedList<String> outputQueue = new LinkedList<>();
		ReplayPircBotX bot = new ReplayPircBotX(config.buildConfiguration(), outputQueue);

		BufferedReader fileInput = new BufferedReader(new InputStreamReader(input));
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
					log.error("Expected last output: " + line);
					log.error("Given last output: " + lastOutput);
					for (String curOutput : outputQueue) {
						log.error("Queued output: " + curOutput);
					}
					throw new RuntimeException("Failed to verify output (see log)");
				}
			} else {
				throw new RuntimeException("Unknown line " + lineRaw);
			}

			for (Event curEvent : Iterables.consumingIterable(eventQueue))
				log.debug("(events) " + curEvent);

			log.debug("");
		}

		timer.stop();
		log.debug("---Replay successful in {}---",
				DurationFormatUtils.formatDuration(timer.getTime(), "mm'min'ss'sec'SSS'ms'"));
	}
}
