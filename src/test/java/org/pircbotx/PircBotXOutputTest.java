/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import org.apache.commons.lang.StringUtils;
import java.io.InputStream;
import java.io.OutputStream;
import org.pircbotx.hooks.managers.GenericListenerManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import javax.net.SocketFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test the output of PircBotX
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXOutputTest {	
	PircBotX bot;
	SocketFactory socketFactory;
	ByteArrayInputStream botIn;
	ByteArrayOutputStream botOut;
	
	@BeforeMethod
	public void botProvider() throws Exception {
		//Setup bot
		bot = new PircBotX();
		bot.setListenerManager(new GenericListenerManager());
		bot.setNick("PircBotXBot");
		bot.setName("PircBotXBot");
		
		//Setup stream
		botIn = new ByteArrayInputStream("".getBytes());
		botOut = new ByteArrayOutputStream();
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(botIn);
		when(socket.getOutputStream()).thenReturn(botOut);
		socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket("example.com", 6667)).thenReturn(socket);
	}
	
	@Test
	public void connectTest() throws Exception {
		//Connect the bot to the socket
		bot.connect("example.com", 6667, null, socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667);
		
		String[] lines = botOut.toString().split("\r\n");
		assertEquals(lines.length, 2, "Extra line: " + StringUtils.join(lines, System.getProperty("line.separator")));
		assertEquals(lines[0], "NICK PircBotXBot");
		assertEquals(lines[1], "USER " + bot.getLogin() + " 8 * :" + bot.getVersion());
	}
	
	@Test(dependsOnMethods = "connectTest")
	public void connectWithPasswordTest() throws Exception {
		//Connect the bot to the socket
		bot.connect("example.com", 6667, "pa55w0rd", socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667);
		
		String[] lines = botOut.toString().split("\r\n");
		assertEquals(lines.length, 3);
		assertEquals(lines[0], "PASS pa55w0rd");
		assertEquals(lines[1], "NICK PircBotXBot");
	}
}
