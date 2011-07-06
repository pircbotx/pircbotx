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

import org.pircbotx.hooks.managers.GenericListenerManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import javax.net.SocketFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test the output of PircBotX
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXOutputTest {
	@DataProvider
	public Object[][] botProvider() {
		PircBotX bot = new PircBotX();
		bot.setListenerManager(new GenericListenerManager());
		bot.setNick("PircBotXBot");
		bot.setName("PircBotXBot");
		return new Object[][]{{bot}};
	}

	@Test(dataProvider = "botProvider")
	public void connectWithPasswordTest(PircBotX bot) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		SocketFactory factory = createSocketFactoryStub(stream);

		bot.connect("example.com", 6667, "pa55w0rd", factory);

		verify(factory).createSocket("example.com", 6667);

		String[] lines = stream.toString().split("\r\n");
		assertEquals(lines.length, 3);
		assertEquals(lines[0], "PASS pa55w0rd");
		assertEquals(lines[1], "NICK PircBotXBot");

	}

	protected SocketFactory createSocketFactoryStub(ByteArrayOutputStream out) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());

		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(out);

		SocketFactory factory = mock(SocketFactory.class);
		when(factory.createSocket("example.com", 6667)).thenReturn(socket);

		return factory;
	}
}
