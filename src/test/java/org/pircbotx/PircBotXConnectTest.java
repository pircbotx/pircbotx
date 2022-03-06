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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import org.pircbotx.hooks.events.ConnectEvent;
import java.util.List;
import org.pircbotx.hooks.Event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.net.SocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

/**
 * Do various connect tests. Note that this is in a separate class since
 * PircBotXOutputTest relies on a working mock implementation
 */
@Slf4j
@Test(groups = "ConnectTests", singleThreaded = true)
public class PircBotXConnectTest {
	protected Configuration.Builder configurationBuilder;
	protected SocketFactory socketFactory;
	protected Socket socket;
	protected ByteArrayInputStream botIn;
	protected ByteArrayOutputStream botOut;
	protected List<Event> events;
	protected InetAddress address;

	@BeforeClass
	public void setUp() throws UnknownHostException {
		address = InetAddress.getByName("127.1.1.1");
	}

	@BeforeMethod
	public void botProvider() throws Exception {
		//Setup stream
		botIn = new ByteArrayInputStream(StringUtils.join(new String[]{
			":ircd.test CAP * LS :sasl",
			":ircd.test 004 PircBotXUser ircd.test jmeter-ircd-basic-0.1 ov b",
			":ircd.test NOTICE * :*** Looking up your hostname...",
			//Need to end with a newline
			""
		}, "\r\n").getBytes());
		botOut = new ByteArrayOutputStream();
		socket = mock(Socket.class);
		when(socket.isConnected()).thenReturn(true);
		when(socket.getInputStream()).thenReturn(botIn);
		when(socket.getOutputStream()).thenReturn(botOut);
		socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket()).thenReturn(socket);

		//Setup bot
		events = new ArrayList<Event>();
		configurationBuilder = TestUtils.generateConfigurationBuilder()
				.addListener(new Listener() {
					public void onEvent(Event event) throws Exception {
						LoggerFactory.getLogger(getClass()).debug("Called for " + event.getClass());
						events.add(event);
					}
				})
				.setName("PircBotXBot");
		configurationBuilder.getServers().clear();
	}

	@SuppressWarnings("unchecked")
	protected void validateEvents(PircBotX bot) throws Exception {
		ClassToInstanceMap<Event> eventClasses = MutableClassToInstanceMap.create();
		for (Event curEvent : events) {
			Class clazz = curEvent.getClass();
			if (eventClasses.containsKey(clazz))
				eventClasses.putInstance(clazz, null);
			else
				eventClasses.putInstance(clazz, curEvent);
		}

		Event event = eventClasses.get(SocketConnectEvent.class);
		assertNotNull(event, "No SocketConnectEvent dispatched");
		assertEquals(event.getBot(), bot);

		event = eventClasses.get(ConnectEvent.class);
		assertNotNull(event, "No ConnectEvent dispatched");
		assertEquals(event.getBot(), bot);

		event = eventClasses.get(DisconnectEvent.class);
		assertNotNull(event, "No DisconnectEvent dispatched");
		assertEquals(event.getBot(), bot);
	}

	@Test
	public void connectTest() throws Exception {
		//Connect the bot to the socket
		PircBotX bot = new PircBotX(configurationBuilder
				.addServer(address.getHostName())
				.setServerPassword(null)
				.setSocketFactory(socketFactory)
				.setCapEnabled(true)
				.buildConfiguration());
		bot.connect();

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, SystemUtils.LINE_SEPARATOR));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + configurationBuilder.getLogin() + " 8 * :" + configurationBuilder.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents(bot);
	}

	@Test
	public void connectNoCapTest() throws Exception {
		//Connect the bot to the socket
		PircBotX bot = new PircBotX(configurationBuilder
				.addServer(address.getHostName())
				.setServerPassword(null)
				.setSocketFactory(socketFactory)
				.setCapEnabled(false)
				.buildConfiguration());
		bot.connect();

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 2, "Extra line: " + StringUtils.join(lines, SystemUtils.LINE_SEPARATOR));

		assertEquals(lines[0], "NICK PircBotXBot");
		assertEquals(lines[1], "USER " + configurationBuilder.getLogin() + " 8 * :" + configurationBuilder.getVersion());

		validateEvents(bot);
	}

	@Test(dependsOnMethods = "connectTest")
	public void connectWithDifferentPortTest() throws Exception {
		//Connect the bot to the socket
		when(socketFactory.createSocket(address, 25622, null, 0)).thenReturn(socket);
		PircBotX bot = new PircBotX(configurationBuilder
				.addServer(address.getHostName(), 25622)
				.setServerPassword(null)
				.setSocketFactory(socketFactory)
				.setCapEnabled(true)
				.buildConfiguration());
		bot.connect();

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, SystemUtils.LINE_SEPARATOR));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + configurationBuilder.getLogin() + " 8 * :" + configurationBuilder.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents(bot);
	}

	@Test(dependsOnMethods = "connectTest")
	public void connectWithPasswordTest() throws Exception {
		//Connect the bot to the socket
		PircBotX bot = new PircBotX(configurationBuilder
				.addServer(address.getHostName(), 6667)
				.setServerPassword("pa55w0rd")
				.setSocketFactory(socketFactory)
				.setCapEnabled(true)
				.buildConfiguration());
		bot.connect();

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 5, "Extra line: " + StringUtils.join(lines, SystemUtils.LINE_SEPARATOR));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "PASS pa55w0rd");
		assertEquals(lines[2], "NICK PircBotXBot");
		assertEquals(lines[3], "USER " + configurationBuilder.getLogin() + " 8 * :" + configurationBuilder.getVersion());
		assertEquals(lines[4], "CAP END");

		validateEvents(bot);
	}

	@Test(dependsOnMethods = "connectTest")
	public void connectTestWithUnknownCap() throws Exception {
		configurationBuilder.getCapHandlers().clear();
		configurationBuilder.addCapHandler(new EnableCapHandler("jdshflkashfalksjh", true));

		//Connect the bot to the socket
		PircBotX bot = new PircBotX(configurationBuilder
				.addServer(address.getHostName(), 6667)
				.setServerPassword(null)
				.setSocketFactory(socketFactory)
				.setCapEnabled(true)
				.buildConfiguration());
		bot.connect();

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, SystemUtils.LINE_SEPARATOR));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + configurationBuilder.getLogin() + " 8 * :" + configurationBuilder.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents(bot);
	}
}
