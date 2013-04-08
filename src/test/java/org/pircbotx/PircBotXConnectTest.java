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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import org.pircbotx.hooks.events.ConnectEvent;
import java.util.List;
import org.pircbotx.hooks.Event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.SocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.events.SocketConnectEvent;

/**
 * Do various connect tests. Note that this is in a separate class since PircBotXOutputTest
 * relies on a working mock implementation
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Test(groups = "ConnectTests", singleThreaded = true)
public class PircBotXConnectTest {
	protected PircBotX bot;
	protected SocketFactory socketFactory;
	protected Socket socket;
	protected ByteArrayInputStream botIn;
	protected ByteArrayOutputStream botOut;
	protected List<Event> events;

	@BeforeMethod
	public void botProvider() throws Exception {
		//Setup bot
		bot = new PircBotX();
		bot.setCapEnabled(true);
		events = new ArrayList<Event>();
		bot.setListenerManager(new GenericListenerManager() {
			@Override
			public void dispatchEvent(Event event) {
				events.add(event);
			}
		});
		bot.setNick("PircBotXBot");
		bot.setName("PircBotXBot");

		//Setup stream
		botIn = new ByteArrayInputStream(":ircd.test CAP * LS :sasl\r\n:ircd.test 004 PircBotXUser ircd.test jmeter-ircd-basic-0.1 ov b\r\n".getBytes());
		botOut = new ByteArrayOutputStream();
		socket = mock(Socket.class);
		when(socket.isConnected()).thenReturn(true);
		when(socket.getInputStream()).thenReturn(botIn);
		when(socket.getOutputStream()).thenReturn(botOut);
		socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket("example.com", 6667, null, 0)).thenReturn(socket);
	}

	protected void validateEvents() throws Exception {
		//Make sure events are dispatched
		int rance = 0;
		int ransce = 0;

		for (Event curEvent : events)
			if (curEvent instanceof ConnectEvent) {
				ConnectEvent cevent = (ConnectEvent) curEvent;
				rance++;

				//Verify values
				assertEquals(cevent.getBot(), bot);
			} else if (curEvent instanceof SocketConnectEvent) {
				SocketConnectEvent scevent = (SocketConnectEvent) curEvent;
				ransce++;

				//Verify values
				assertEquals(scevent.getBot(), bot);
			}

		assertEquals(rance, 1, "ConnectEvent not dispatched/dispatched more than once");
		assertEquals(ransce, 1, "SocketConnectEvent not dispatched/dispatched more than once");
	}

	@Test
	public void connectTest() throws Exception {
		//Connect the bot to the socket
		bot.connect("example.com", 6667, null, socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667, null, 0);

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, System.getProperty("line.separator")));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + bot.getLogin() + " 8 * :" + bot.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents();
	}

	@Test(dependsOnMethods = "connectTest")
	public void connectWithDifferentPortTest() throws Exception {
		//Connect the bot to the socket
		when(socketFactory.createSocket("example.com", 25622, null, 0)).thenReturn(socket);
		bot.connect("example.com", 25622, null, socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 25622, null, 0);

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, System.getProperty("line.separator")));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + bot.getLogin() + " 8 * :" + bot.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents();
	}

	@Test(dependsOnMethods = "connectTest")
	public void connectWithPasswordTest() throws Exception {
		//Connect the bot to the socket
		bot.connect("example.com", 6667, "pa55w0rd", socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667, null, 0);

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 5, "Extra line: " + StringUtils.join(lines, System.getProperty("line.separator")));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "PASS pa55w0rd");
		assertEquals(lines[2], "NICK PircBotXBot");
		assertEquals(lines[3], "USER " + bot.getLogin() + " 8 * :" + bot.getVersion());
		assertEquals(lines[4], "CAP END");

		validateEvents();
	}
	
	@Test(dependsOnMethods = "connectTest")
	public void connectTestWithUnknownCap() throws Exception {
		bot.getCapHandlers().clear();
		bot.getCapHandlers().add(new EnableCapHandler("jdshflkashfalksjh", true));
		
		//Connect the bot to the socket
		bot.connect("example.com", 6667, null, socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667, null, 0);

		//Verify lines
		String[] lines = botOut.toString().split("\r\n");

		assertEquals(lines.length, 4, "Extra line: " + StringUtils.join(lines, System.getProperty("line.separator")));

		assertEquals(lines[0], "CAP LS");
		assertEquals(lines[1], "NICK PircBotXBot");
		assertEquals(lines[2], "USER " + bot.getLogin() + " 8 * :" + bot.getVersion());
		assertEquals(lines[3], "CAP END");

		validateEvents();
	}
}
