/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for MultiBotManager
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MultiBotManagerTest {
	protected MultiBotManager manager;

	@BeforeMethod
	public void setup() {
		manager = new MultiBotManager("TestBot");
	}

	@Test(description = "Make sure setting the encoding by string works")
	public void setEncodingStringTest() throws UnsupportedEncodingException {
		PircBotX bot = new PircBotX();
		Charset aCharset = null;
		//Find a charset that isn't the default PircBotX one
		for (Charset curCharset : Charset.availableCharsets().values())
			if (curCharset != bot.getEncoding())
				aCharset = curCharset;
		assertNotNull(aCharset, "Couldn't find a charset that was't the default PircBotX one");

		//Make sure the encoding we set is the same that we get out
		manager.setEncoding(aCharset.name());
		assertEquals(manager.createBot("some.server").getEncoding(), aCharset, "Charset is different");
	}

	@Test(description = "Make sure the values are all copied correctly")
	public void dummyBotTest() throws UnsupportedEncodingException {
		//Create a master bot to compare everything to
		final PircBotX masterBot = new PircBotX();
		masterBot.setListenerManager(new GenericListenerManager());
		masterBot.setName("SomeName");
		masterBot.setVerbose(true);
		masterBot.setSocketTimeout(9999999);
		masterBot.setMessageDelay(99999999);
		masterBot.setLogin("SomeLogin");
		masterBot.setAutoNickChange(true);
		masterBot.setEncoding(Charset.availableCharsets().firstKey());
		masterBot.setDccInetAddress(InetAddress.getLoopbackAddress());
		masterBot.getDccPorts().clear();
		masterBot.getDccPorts().add(555);
		masterBot.getDccPorts().add(666);
		masterBot.getDccPorts().add(777);

		//Setup the manager to clone it
		manager = new MultiBotManager(masterBot);

		//Create 3 servers
		manager.createBot("some.server1");
		manager.createBot("some.server2");
		manager.createBot("some.server3");

		//Make sure the values are all the same
		for (PircBotX curBot : manager.getBots()) {
			assertEquals(curBot.getListenerManager(), masterBot.getListenerManager(), "ListenerManager in subbot is different from master");
			assertEquals(curBot.getName(), masterBot.getName(), "Name in subbot is different from master");
			assertEquals(curBot.isVerbose(), masterBot.isVerbose(), "Verbose in subbot is different from master");
			assertEquals(curBot.getSocketTimeout(), masterBot.getSocketTimeout(), "SocketTimeout in subbot is different from master");
			assertEquals(curBot.getMessageDelay(), masterBot.getMessageDelay(), "MessageDelay in subbot is different from master");
			assertEquals(curBot.getLogin(), masterBot.getLogin(), "Login in subbot is different from master");
			assertEquals(curBot.isAutoNickChange(), masterBot.isAutoNickChange(), "AutoNickChange in subbot is different from master");
			assertEquals(curBot.getEncoding(), masterBot.getEncoding(), " in subbot is different from master");
			assertEquals(curBot.getDccInetAddress(), masterBot.getDccInetAddress(), "DccInetAddress in subbot is different from master");
			assertEquals(curBot.getDccPorts(), masterBot.getDccPorts(), "DccPorts in subbot is different from master");
		}
	}

	@Test(description = "Make sure the listener manager is shared and not null")
	public void listenerManagerSameAndNotNullTest() {
		PircBotX bot1 = manager.createBot("some.server1");
		PircBotX bot2 = manager.createBot("some.server2");
		assertNotNull(bot1.getListenerManager(), "MultiBotManager's first bot doesn't have a listener manager");
		assertNotNull(bot2.getListenerManager(), "MultiBotManager's second bot doesn't have a listener manager");
		assertEquals(bot1.getListenerManager(), bot2.getListenerManager(), "MultiBotManager's bots don't have the same listener");
	}
}
