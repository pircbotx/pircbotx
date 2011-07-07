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

import java.io.BufferedReader;
import java.io.IOException;
import org.pircbotx.hooks.managers.GenericListenerManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import javax.net.SocketFactory;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test the output of PircBotX. Depend on ConnectTests to check mocked sockets
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
//@Test(dependsOnGroups = "ConnectTests")
public class PircBotXOutputTest {
	protected final String aString = "I'm some super long string that has multiple words";
	protected PircBotX bot;
	protected SocketFactory socketFactory;
	protected BufferedReader botOut;
	protected User aUser;
	protected Channel aChannel;
	protected CountDownLatch inputLatch;
	protected InputStream in;

	@BeforeMethod
	public void botSetup() throws Exception {
		//Setup bot
		bot = new PircBotX();
		bot.setListenerManager(new GenericListenerManager());
		bot.setNick("PircBotXBot");
		bot.setName("PircBotXBot");
		bot.setMessageDelay(0L);

		//Setup streams for bot
		PipedOutputStream out = new PipedOutputStream();
		//Create an input stream that we'll kill later
		inputLatch = new CountDownLatch(1);
		in = new AutoCloseInputStream(new ByteArrayInputStream("".getBytes()) {
			@Override
			public synchronized int read() {
				try {
					//Block until were killed
					inputLatch.await();
				} catch (InterruptedException ex) {
					//Wrap in an RuntimeException so whatever was using this fails
					throw new RuntimeException("Interrupted while waiting for input", ex);
				}
				//No more input
				return -1;
			}
		});
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(out);
		socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket("example.com", 6667)).thenReturn(socket);

		//Setup ability to read from bots output
		botOut = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));

		//Connect the bot to the socket
		bot.connect("example.com", 6667, null, socketFactory);

		//Make sure the bot is connected
		verify(socketFactory).createSocket("example.com", 6667);

		//Setup useful vars
		aUser = bot.getUser("aUser");
		aChannel = bot.getChannel("#aChannel");
	}

	@AfterMethod
	public void cleanUp() {
		inputLatch.countDown();
		bot.dispose();
	}

	@Test(description = "Verify sendAction to user")
	public void sendActionUserTest() throws Exception {
		bot.sendAction(aUser, aString);
		checkOutput("PRIVMSG aUser :\u0001ACTION " + aString + "\u0001");
	}

	@Test(description = "Verify sendAction to channel")
	public void sendActionChannelTest() throws Exception {
		bot.sendAction(aChannel, aString);
		checkOutput("PRIVMSG #aChannel :\u0001ACTION " + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPCommand to user")
	public void sendCTCPCommandUserTest() throws Exception {
		bot.sendCTCPCommand(aUser, aString);
		checkOutput("PRIVMSG aUser :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPResponse to user")
	public void sendCTCPResponseUserTest() throws Exception {
		bot.sendCTCPResponse(aUser, aString);
		checkOutput("NOTICE aUser :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendInvite to user")
	public void sendInviteUserTest() throws Exception {
		bot.sendInvite(aUser, aChannel);
		checkOutput("INVITE aUser :#aChannel");
	}

	@Test(description = "Verify sendInvite to channel")
	public void sendInviteChannelTest() throws Exception {
		bot.sendInvite(aChannel, bot.getChannel("#otherChannel"));
		checkOutput("INVITE #aChannel :#otherChannel");
	}

	@Test(description = "Verify sendMessage to channel")
	public void sendMessageChannelTest() throws Exception {
		bot.sendMessage(aChannel, aString);
		checkOutput("PRIVMSG #aChannel :" + aString);
	}

	@Test(description = "Verify sendMessage to user")
	public void sendMessageUserTest() throws Exception {
		bot.sendMessage(aUser, aString);
		checkOutput("PRIVMSG aUser :" + aString);
	}

	@Test(description = "Verify sendNotice to channel")
	public void sendNoticeChannelTest() throws Exception {
		bot.sendNotice(aChannel, aString);
		checkOutput("NOTICE #aChannel :" + aString);
	}

	@Test(description = "Verify sendNotice to user")
	public void sendNoticeUserTest() throws Exception {
		bot.sendNotice(aUser, aString);
		checkOutput("NOTICE aUser :" + aString);
	}

	/**
	 * Check the output for one line that equals the expected value.
	 * @param expected 
	 */
	protected void checkOutput(String expected) throws IOException {
		//Handle the first 2 lines from the bot
		System.out.println("Reading first line");
		assertEquals(botOut.readLine(), "NICK PircBotXBot", "Unexecpted first line");
		System.out.println("Reading second line");
		String line = botOut.readLine();
		assertNotNull(line, "Second output line is null");
		assertTrue(line.startsWith("USER PircBotX 8 * :"), "Unexpected second line: " + line);

		//Make sure the remaining line is okay
		System.out.println("Reading third line");
		line = botOut.readLine();
		System.out.println("Finished reading lines");
		//assertEquals(lines.length, 1, "Too many/few lines: " + StringUtils.join(lines, System.getProperty("line.separator")));
		assertEquals(line, expected);
	}
}
