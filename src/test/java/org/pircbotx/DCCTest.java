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

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class DCCTest {
	PircBotX bot;

	@BeforeClass(description = "This will fail if you don't have IPv6 support")
	public void checkIPv6Support() throws UnknownHostException {
		InetAddress.getByName("::1");
	}

	@BeforeMethod
	public void setup() throws UnknownHostException {
		bot = new PircBotX();
		bot._inetAddress = InetAddress.getByName("::1");
	}

	@Test
	public void incommingDccChatTest() throws IOException, InterruptedException {
		//Start a server that the bot can connect to
		Inet6Address localhost6 = (Inet6Address) InetAddress.getByName("::1");
		ServerSocket server = new ServerSocket(0, 50, localhost6);
		int serverPort = server.getLocalPort();
		
		//Create listener to handle everything
		final CountDownLatch latch = new CountDownLatch(1);
		final MutableObject<IncomingChatRequestEvent> mutableEvent = new MutableObject<IncomingChatRequestEvent>();
		bot.getListenerManager().addListener(new ListenerAdapter() {
			@Override
			public void onIncomingChatRequest(IncomingChatRequestEvent event) throws Exception {
				mutableEvent.setValue(event);
				latch.countDown();
			}
		});
		
		System.out.println("localhost6 byte array: " + Arrays.toString(localhost6.getAddress()));
		System.out.println("localhost6 int: " + new BigInteger(localhost6.getAddress()));
		
		bot.handleLine(":ANick!~ALogin@::2 PRIVMSG Quackbot5 :DCC CHAT chat 1 35589");
		latch.await();
		IncomingChatRequestEvent event = mutableEvent.getValue();
		assertNotNull(event, "No IncomingChatRequestEvent dispatched");
		
		System.out.println("Test ran");
	}

	protected void debug(String type, InetAddress address) {
		System.out.println(type + ": " + address + " | Class: " + address.getClass());
	}
}
