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

import org.pircbotx.exception.DaoException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for PircBotX class. Any test that involves processing server lines
 * should be in PircBotXProcessingTest.
 */
@Test(singleThreaded = true)
public class UserChannelDaoTest {
	protected PircBotX smallBot;
	protected UserChannelDao<User,Channel> dao;

	@BeforeMethod
	public void setup() {
		smallBot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.buildConfiguration());
		dao = smallBot.getUserChannelDao();
	}

	@Test(description = "Make sure getting the wrong user gives an exception", expectedExceptions = DaoException.class)
	public void getUserInvalidTest() {
		smallBot.getUserChannelDao().getUser("RandomInvalidUser");
	}

	@Test
	public void getUserValidTest() {
		User origUser = TestUtils.generateTestUserSource(smallBot);
		assertNotNull(origUser, "createUser returns null");
		assertEquals(origUser, dao.getUser(origUser.getNick()), "getUser doesn't return the same user during second call");
	}

	@Test(expectedExceptions = DaoException.class)
	public void getChannelInvalidTest() {
		dao.getChannel("#randomChannel");
	}

	@Test(description = "Make sure channel doesn't return null and reliably returns the correct value")
	public void getChannelValidTest() {
		Channel channel = dao.createChannel("#aChannel");
		assertNotNull(channel, "createChannel returns null for unknown channel");
		assertEquals(channel, dao.getChannel("#aChannel"), "getChannel doesn't return the same channel during second call");
	}

	@Test(description = "Make sure userExists works")
	public void userExistsTest() {
		User origUser = TestUtils.generateTestUserOther(smallBot);
		//Make sure it exists
		assertTrue(dao.containsUser(origUser.getNick()));
	}

	@Test(description = "Make sure channelExists works")
	public void channelExistsTest() {
		dao.createChannel("#aChannel");
		//Make sure it exists
		assertTrue(dao.containsChannel("#aChannel"));
	}

	@Test
	public void getUserByHostmaskFull() {
		dao.createUser(TestUtils.generateTestUserSourceHostmask(smallBot));
		assertTrue(dao.containsUser("SourceUser"));
		assertTrue(dao.containsUser(TestUtils.generateTestUserSourceHostmask(smallBot)));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", "~SomeTest", null)));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", "~SomeTest", "")));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", null, null)));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", "", "")));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", null, "")));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, null, "SourceUser", "", null)));
		assertTrue(dao.containsUser(new UserHostmask(smallBot, "SourceUser")));
		assertFalse(dao.containsUser(TestUtils.generateTestUserOtherHostmask(smallBot)));
	}

	@Test
	public void userHostmaskEqualsAndHashCodeTest() {
		UserHostmask user1 = TestUtils.generateTestUserOtherHostmask(smallBot);
		UserHostmask user2 = TestUtils.generateTestUserOtherHostmask(smallBot);
		assertEquals(user1, user2);
		assertEquals(user1.hashCode(), user2.hashCode());
	}

	@Test
	public void userEqualsAndHashCodeTest() {
		User user1 = new User(TestUtils.generateTestUserOtherHostmask(smallBot));
		User user2 = new User(TestUtils.generateTestUserOtherHostmask(smallBot));
		assertEquals(user1, user2);
		//The following was removed since Users need to rely on uuid's to keep the DAO in sync
		//assertEquals(user1.hashCode(), user2.hashCode());
	}
}
