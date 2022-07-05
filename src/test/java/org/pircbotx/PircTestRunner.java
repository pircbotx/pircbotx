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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import javax.net.SocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.pircbotx.hooks.managers.SequentialListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import lombok.NonNull;

/**
 * Various hooks for easier detailed testing
 */
public class PircTestRunner implements Closeable {
	public static final ThreadLocal<PircTestRunner> THREAD_INSTANCE = new ThreadLocal<PircTestRunner>();
	public static final String BOT_NICK = "TestBot";
	public static final String USER_SOURCE_HOSTMASK = "SourceUser!~SomeTest@host.test";
	public static final String USER_OTHER_HOSTMASK = "OtherUser!~SomeTest@host.test";
	public static final String USER_BOT_HOSTMASK = BOT_NICK + "!PircBotX@host.test";
	private static final Logger log = LoggerFactory.getLogger(PircTestRunner.class);	
	private final LinkedList<Event> eventQueue = new LinkedList<Event>();
	private final LinkedList<String> outputQueue = new LinkedList<String>();
	private final FakeReader in;
	public final CapturedPircBotX bot;

	public PircTestRunner(Configuration.Builder config) throws IOException, IrcException {

		Socket socket = mock(Socket.class);
		when(socket.isConnected()).thenReturn(true);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		SocketFactory socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket()).thenReturn(socket);
		config.setSocketFactory(socketFactory);

		config.setListenerManager(SequentialListenerManager.newDefault()
				.updateExecutorAllInline()
				.addListenerInline(new Listener() {
					final Logger log = LoggerFactory.getLogger(getClass());

					@Override
					public void onEvent(Event event) {
						log.debug("Dispatched event " + event);
						eventQueue.addLast(event);
					}
				}));

		in = new FakeReader();

		bot = new CapturedPircBotX(config.buildConfiguration());
		bot.startBot();
		
		THREAD_INSTANCE.set(this);
	}

	/**
	 * Replaces "%server",
	 * "%usersource",
	 * "%userother",
	 * "%userbot",
	 * "%nickbot",
	 */
	public PircTestRunner botIn(@NonNull String line) {
		checkInputEmpty();
		checkOutputEmpty();
		checkEventsEmpty();

		in.nextLine = StringUtils.replaceEach(line,
				new String[]{
					"%server",
					"%usersource",
					"%userother",
					"%userbot",
					"%nickbot",
				}, new String[]{
					"irc.someserver.net",
					USER_SOURCE_HOSTMASK,
					USER_OTHER_HOSTMASK,
					USER_BOT_HOSTMASK,
					BOT_NICK,
				});
		assertTrue(bot.processNextLine(), "Bot was stopped");

		return this;
	}

	public PircTestRunner assertBotOut(@NonNull String expectedLine) {
		checkInputEmpty();

		log.info("Asserting output equals {}", expectedLine);
		String next = outputQueue.removeFirst();
		assertEquals(next, expectedLine, "Remaining lines "
				+ SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(outputQueue, SystemUtils.LINE_SEPARATOR)
				+ SystemUtils.LINE_SEPARATOR
		);

		return this;
	}

	public PircTestRunner assertEventClass(Class<? extends Event> eventClass) {
		checkInputEmpty();

		//If this returns then the event class matches
		getNextEvent(eventClass);

		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Event> E getNextEvent(Class<E> eventClass) {
		checkInputEmpty();
		
		log.info("Asserting output event equals {}", eventClass);
		Event next = eventQueue.removeFirst();
		assertTrue(eventClass.isInstance(next), "Event " + next.getClass() + " doesn't match " + eventClass);
		return (E) next;
	}

	public PircTestRunner assertBotHello() {
		assertEventClass(SocketConnectEvent.class);
		assertBotOut("NICK TestBot");
		assertBotOut("USER PircBotX 8 * :" + bot.getConfiguration().getVersion());

		checkAllEmpty();

		return this;
	}
	
	public PircTestRunner assertBotConnect() {
		botIn(":%server 004 TestBot ircd.test jmeter-ircd-basic-0.1 ov b");
		assertEventClass(ConnectEvent.class);
		assertEventClass(ServerResponseEvent.class);
		
		checkAllEmpty();
		
		return this;
	}

	public PircTestRunner assertBotHelloAndConnect() {
		assertBotHello();
		assertBotConnect();
		
		checkAllEmpty();

		return this;
	}

	public PircTestRunner joinChannel() {
		botIn(":%userbot JOIN #aChannel");
		assertEventClass(JoinEvent.class);
		assertBotOut("WHO #aChannel");
		assertBotOut("MODE #aChannel");
		
		checkAllEmpty();

		return this;
	}
	
	public PircTestRunner runCheck(RunCheck callback) {
		callback.check(bot, this);
		return this;
	}
	
	public static interface RunCheck {
		public void check(PircBotX bot, PircTestRunner test);
	}

	@Override
	public void close() {
		checkAllEmpty();

		bot.shutdownEnabled = true;
		botIn("ERROR: test is over");
		assertTrue(bot.closeCalled, "Shutdown wasn't called");
		THREAD_INSTANCE.remove();
	}
	
	protected void checkAllEmpty() {
		checkInputEmpty();
		checkOutputEmpty();
		checkEventsEmpty();
	}

	protected void checkInputEmpty() {
		assertNull(in.nextLine, "Line unread by server");
	}

	protected void checkOutputEmpty() {
		assertTrue(outputQueue.isEmpty(), "Unhandled lines "
				+ SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(outputQueue, SystemUtils.LINE_SEPARATOR)
				+ SystemUtils.LINE_SEPARATOR
		);
	}

	protected void checkEventsEmpty() {
		assertTrue(eventQueue.isEmpty(), "Unhandled events"
				+ SystemUtils.LINE_SEPARATOR
				+ Joiner.on(SystemUtils.LINE_SEPARATOR).join(Lists.transform(eventQueue, new Function<Event, String>() {
					public String apply(Event input) {
						return input.toString();
					}
				}))
				+ SystemUtils.LINE_SEPARATOR
		);
	}

	public class CapturedPircBotX extends PircBotX {
		private final Logger log = LoggerFactory.getLogger(CapturedPircBotX.class);
		boolean shutdownEnabled = false;
		boolean closeCalled = false;

		public CapturedPircBotX(Configuration configuration) {
			super(configuration);
		}

		@Override
		protected void changeSocket(Socket socket) throws IOException {
			super.changeSocket(socket);
			this.inputReader = in;
		}

		@Override
		protected void sendRawLineToServer(String line) throws IOException {
			outputQueue.addLast(line);
		}

		@Override
		protected void startLineProcessing() {
			//Do nothing, we will trigger handleLine ourselves
		}

		@Override
		public void close() {
			super.close();
			closeCalled = true;
		}

		@Override
		protected void shutdown() {
			//TODO: Fragile
			if (shutdownEnabled)
				super.shutdown();
			else
				log.warn("Shutdown called");
		}
	}

	private class FakeReader extends BufferedReader {
		private String nextLine;

		public FakeReader() {
			//Doesn't matter
			super(new InputStreamReader(new ByteArrayInputStream(new byte[0])));
		}

		@Override
		public String readLine() throws IOException {
			if (nextLine == null)
				throw new RuntimeException("No nextLine given");
			String result = nextLine;
			nextLine = null;
			return result;
		}
	}
}
