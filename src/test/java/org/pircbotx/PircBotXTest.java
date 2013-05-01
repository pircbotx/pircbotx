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
package org.pircbotx;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for PircBotX class. Any test that involves processing server lines
 * should be in PircBotXProcessingTest.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Test(singleThreaded = true)
public class PircBotXTest {
	protected PircBotX smallBot;

	@BeforeMethod
	public void setup() {
		smallBot = new PircBotX(new Configuration.Builder().buildConfiguration());
	}

	@Test(description = "Make sure getUser doesn't return null and reliably returns the correct value")
	public void getUserTest() {
		User origUser = smallBot.getUserChannelDao().getUser("SomeUser");
		assertNotNull(origUser, "getUser returns null for unknown user");
		assertEquals(origUser, smallBot.getUserChannelDao().getUser("SomeUser"), "getUser doesn't return the same user during second call");
	}

	@Test(description = "Make sure channel doesn't return null and reliably returns the correct value")
	public void getChannelTest() {
		Channel channel = smallBot.getUserChannelDao().getChannel("#aChannel");
		assertNotNull(channel, "getchannel returns null for unknown channel");
		assertEquals(channel, smallBot.getUserChannelDao().getChannel("#aChannel"), "getChannel doesn't return the same channel during second call");
	}

	@Test(dependsOnMethods = "getUserTest", description = "Make sure userExists works")
	public void userExistsTest() {
		//Create user by getting an known one
		smallBot.getUserChannelDao().getUser("SomeUser");
		//Make sure it exists
		assertTrue(smallBot.getUserChannelDao().userExists("SomeUser"));
	}

	@Test(dependsOnMethods = "getChannelTest", description = "Make sure channelExists works")
	public void channelExistsTest() {
		//Create channel by getting an unknown one
		smallBot.getUserChannelDao().getChannel("#aChannel");
		//Make sure it exists
		assertTrue(smallBot.getUserChannelDao().channelExists("#aChannel"));
	}

	@Test(description = "Verify getting the bots own user object works")
	public void getUserBotTest() {
		smallBot.setNick("BotNick");
		assertNotNull(smallBot.getUserChannelDao().getUser("BotNick"), "Getting bots user with getUser returns null");
		assertNotNull(smallBot.getUserChannelDao().getUser("BotNick"), "Getting existing bots user with getUser returns null");
		assertNotNull(smallBot.getUserChannelDao().getUser("SomeOtherNick"), "Getting new user returns null");
		assertNotNull(smallBot.getUserChannelDao().getUser("SomeOtherNick"), "Getting existing new user returns null");
	}
}