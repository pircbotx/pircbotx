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

import java.io.BufferedReader;
import java.io.IOException;
import org.pircbotx.hooks.managers.GenericListenerManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import javax.net.SocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.pircbotx.TestUtils;

/**
 * Test the output of PircBotX. Depend on ConnectTests to check mocked sockets
 */
@Test(/*dependsOnGroups = "ConnectTests", */singleThreaded = true)
public class OutputTest {
	protected final String aString = "I'm some super long string that has multiple words";
	protected PircBotX bot;
	protected SocketFactory socketFactory;
	protected ByteArrayOutputStream botOut;
	protected User aUser;
	protected Channel aChannel;
	protected CountDownLatch inputLatch;
	protected InputStream in;

	@BeforeMethod
	public void botSetup() throws Exception {
		InetAddress localhost = InetAddress.getByName("127.1.1.1");

		//Setup streams for bot
		inputLatch = new CountDownLatch(1);
		botOut = new ByteArrayOutputStream();
		in = new ByteArrayInputStream("".getBytes());
		Socket socket = mock(Socket.class);
		when(socket.isConnected()).thenReturn(true);
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(botOut);
		socketFactory = mock(SocketFactory.class);
		when(socketFactory.createSocket()).thenReturn(socket);

		//Configure and connect bot
		bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.setCapEnabled(true)
				.setServerPassword(null)
				.setSocketFactory(socketFactory)
				.buildConfiguration());
		bot.startBot();

		//Setup useful vars
		aUser = TestUtils.generateTestUserSource(bot);
		aChannel = bot.getUserChannelDao().createChannel("#aChannel");
	}

	@AfterMethod
	public void cleanUp() {
		inputLatch.countDown();
	}

	@Test(description = "Verify sendRawLine works correctly")
	public void sendRawLineTest() throws Exception {
		bot.sendRaw().rawLine(aString);
		checkOutput(aString);
	}

	@Test(description = "Verify sendRawLineNow works correctly")
	public void sendRawLineNowTest() throws Exception {
		bot.sendRaw().rawLineNow(aString);
		checkOutput(aString);
	}

	@Test(description = "Verify sendRawLineSplit works correctly with short strings")
	public void sendRawLineSplitShort() throws Exception {
		String beginning = "BEGIN";
		String ending = "END";
		bot.sendRaw().rawLineSplit(beginning, aString, ending);
		checkOutput(beginning + aString + ending);
	}

	@Test(description = "Verify sendAction to user")
	public void sendActionUserTest() throws Exception {
		aUser.send().action(aString);
		checkOutput("PRIVMSG SourceUser :\u0001ACTION " + aString + "\u0001");
	}

	@Test(description = "Verify sendAction to channel")
	public void sendActionChannelTest() throws Exception {
		aChannel.send().action(aString);
		checkOutput("PRIVMSG #aChannel :\u0001ACTION " + aString + "\u0001");
	}

	@Test(description = "Verify sendAction by string")
	public void sendActionStringTest() throws Exception {
		bot.sendIRC().action("#aChannel", aString);
		checkOutput("PRIVMSG #aChannel :\u0001ACTION " + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPCommand to user")
	public void sendCTCPCommandUserTest() throws Exception {
		aUser.send().ctcpCommand(aString);
		checkOutput("PRIVMSG SourceUser :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPCommand to channel")
	public void sendCTCPCommandChannelTest() throws Exception {
		aChannel.send().ctcpCommand(aString);
		checkOutput("PRIVMSG #aChannel :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPCommand by string")
	public void sendCTCPCommandStringTest() throws Exception {
		bot.sendIRC().ctcpCommand("#aChannel", aString);
		checkOutput("PRIVMSG #aChannel :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPResponse to user")
	public void sendCTCPResponseUserTest() throws Exception {
		aUser.send().ctcpResponse(aString);
		checkOutput("NOTICE SourceUser :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendCTCPResponse by string")
	public void sendCTCPResponseStringTest() throws Exception {
		bot.sendIRC().ctcpResponse("aUser", aString);
		checkOutput("NOTICE aUser :\u0001" + aString + "\u0001");
	}

	@Test(description = "Verify sendInvite to user")
	public void sendInviteUserChannelTest() throws Exception {
		aUser.send().invite(aChannel);
		checkOutput("INVITE SourceUser :#aChannel");
	}

	@Test(description = "Verify sendInvite to channel by string")
	public void sendInviteUserStringTest() throws Exception {
		aUser.send().invite("#aChannel");
		checkOutput("INVITE SourceUser :#aChannel");
	}

	@Test(description = "Verify sendInvite to channel")
	public void sendInviteChannelChannelTest() throws Exception {
		bot.getUserChannelDao().createChannel("#aChannel");
		bot.getUserChannelDao().createChannel("#otherChannel");
		aChannel.send().invite(bot.getUserChannelDao().getChannel("#otherChannel"));
		checkOutput("INVITE #otherChannel :#aChannel");
	}

	@Test
	public void sendInviteChannelUserTest() throws Exception {
		aChannel.send().invite(aUser);
		checkOutput("INVITE SourceUser :#aChannel");
	}

	public void sendInviteChannelStringTest() throws Exception {
		aChannel.send().invite("randomUser");
		checkOutput("INVITE randomUser :#aChannel");
	}

	@Test(description = "Verify sendInvite by string")
	public void sendInviteStringTest() throws Exception {
		bot.sendIRC().invite("aUser", "#aChannel");
		checkOutput("INVITE aUser :#aChannel");
	}

	@Test(description = "Verify sendMessage to channel")
	public void sendMessageChannelTest() throws Exception {
		aChannel.send().message(aString);
		checkOutput("PRIVMSG #aChannel :" + aString);
	}

	@Test(description = "Verify sendMessage to user in channel")
	public void sendMessageChannelUserTest() throws Exception {
		aChannel.send().message(aUser, aString);
		checkOutput("PRIVMSG #aChannel :SourceUser: " + aString);
	}

	@Test(description = "Verify sendMessage to user")
	public void sendMessageUserTest() throws Exception {
		aUser.send().message(aString);
		checkOutput("PRIVMSG SourceUser :" + aString);
	}

	@Test(description = "Verify sendMessage by string")
	public void sendMessageStringTest() throws Exception {
		bot.sendIRC().message("aUser", aString);
		checkOutput("PRIVMSG aUser :" + aString);
	}

	@Test(description = "Verify sendNotice to channel")
	public void sendNoticeChannelTest() throws Exception {
		aChannel.send().notice(aString);
		checkOutput("NOTICE #aChannel :" + aString);
	}

	@Test(description = "Verify sendNotice to user")
	public void sendNoticeUserTest() throws Exception {
		aUser.send().notice(aString);
		checkOutput("NOTICE SourceUser :" + aString);
	}

	@Test(description = "Verify sendNotice by String")
	public void sendNoticeStringTest() throws Exception {
		bot.sendIRC().notice("aUser", aString);
		checkOutput("NOTICE aUser :" + aString);
	}
	
	@Test(description = "Verify sendAction to channel through generic Interface")
	public void sendActionChannelInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aChannel.send();
		out.action(aString);
		checkOutput("PRIVMSG #aChannel :\u0001ACTION " + aString + "\u0001");
	}
	
	@Test(description = "Verify sendAction to user through generic Interface")
	public void sendActionUserInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aUser.send();
		out.action(aString);
		checkOutput("PRIVMSG SourceUser :\u0001ACTION " + aString + "\u0001");
	}
	
	@Test(description = "Verify sendMessage to channel through generic Interface")
	public void sendMessageChannelInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aChannel.send();
		out.message(aString);
		checkOutput("PRIVMSG #aChannel :" + aString);
	}
	
	@Test(description = "Verify sendMessage to user through generic Interface")
	public void sendMessageUserInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aUser.send();
		out.message(aString);
		checkOutput("PRIVMSG SourceUser :" + aString);
	}
	
	@Test(description = "Verify sendNotice to channel through generic Interface")
	public void sendNoticeChannelInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aChannel.send();
		out.notice(aString);
		checkOutput("NOTICE #aChannel :" + aString);
	}
	
	@Test(description = "Verify sendNotice to user through generic Interface")
	public void sendNoticeUserInterfaceTest() throws Exception {
		GenericChannelUserOutput out = aUser.send();
		out.notice(aString);
		checkOutput("NOTICE SourceUser :" + aString);
	}

	@Test
	public void sendQuit() throws Exception {
		bot.sendIRC().quitServer();
		checkOutput("QUIT :");
	}

	@Test
	public void sendQuitMessage() throws Exception {
		bot.sendIRC().quitServer(aString);
		checkOutput("QUIT :" + aString);
	}

	/**
	 * Check the output for one line that equals the expected value.
	 *
	 * @param expected
	 */
	protected Iterator<String> checkOutput(String expected) {
		List<String> outputLines = Arrays.asList(StringUtils.split(botOut.toString(), "\n\r"));
		Iterator<String> outputItr = outputLines.iterator();
		//Handle the first 3 lines from the bot
		assertEquals(tryGetNextLine(outputItr), "CAP LS", "Unexpected first line");
		assertEquals(tryGetNextLine(outputItr), "NICK TestBot", "Unexecpted second line");
		assertTrue(tryGetNextLine(outputItr).startsWith("USER PircBotX 8 * :"), "Unexpected third line");

		//Make sure the remaining line is okay
		assertEquals(tryGetNextLine(outputItr), expected);
		//assertEquals(lines.length, 1, "Too many/few lines: " + StringUtils.join(lines, System.getProperty("line.separator")));

		return outputItr;
	}

	protected static String tryGetNextLine(Iterator<String> itr) {
		assertTrue(itr.hasNext(), "No more lines to get");
		return itr.next();
	}
}
