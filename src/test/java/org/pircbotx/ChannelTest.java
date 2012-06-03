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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ChannelTest {
	protected Channel channel;

	@BeforeMethod
	public void setup() {
		channel = new Channel(null, "#testChannel");
	}

	protected void setUsedModes() {
		//Sandwhich important modes among garbage in the center with garbage arguments
		channel.setMode("+dfanimnstdfe arg1 2 #arg3 arg4");
	}

	protected void setUnusedModes() {
		//Have only garbage modes and garbage arguments
		channel.setMode("+dfadfe arg1 2 #arg3 arg4");
	}

	@Test
	public void isInviteOnlyUsedTest() {
		setUsedModes();
		assertTrue(channel.isInviteOnly());
	}

	@Test
	public void isInviteOnlyUnusedTest() {
		setUnusedModes();
		assertFalse(channel.isInviteOnly());
	}

	@Test
	public void isModeratedUsedTest() {
		setUsedModes();
		assertTrue(channel.isModerated());
	}

	@Test
	public void isModeratedUnusedTest() {
		setUnusedModes();
		assertFalse(channel.isModerated());
	}

	@Test
	public void isNoExternalMessagesUsedTest() {
		setUsedModes();
		assertTrue(channel.isNoExternalMessages());
	}

	@Test
	public void isNoExternalMessagesUnusedTest() {
		setUnusedModes();
		assertFalse(channel.isNoExternalMessages());
	}

	@Test
	public void isSecretUsedTest() {
		setUsedModes();
		assertTrue(channel.isSecret());
	}

	@Test
	public void isSecretUnusedTest() {
		setUnusedModes();
		assertFalse(channel.isSecret());
	}

	@Test
	public void hasTopicProtectionUsedTest() {
		setUsedModes();
		assertTrue(channel.hasTopicProtection());
	}

	@Test
	public void hasTopicProtectionUnusedTest() {
		setUnusedModes();
		assertFalse(channel.hasTopicProtection());
	}

	@Test
	public void getChannelLimitUnusedTest() {
		channel.setMode("+pmn 10");
		assertEquals(channel.getChannelLimit(), -1, "GetChannelLimit when no limit exists isn't -1");
	}

	@Test
	public void getChannelLimitFirstTest() {
		channel.setMode("+lpmn 10");
		assertEquals(channel.getChannelLimit(), 10, "Channel limit in mode +lpmn 10 doesn't match given");
	}

	@Test
	public void getChannelLimitMiddleTest() {
		channel.setMode("+plmn 10");
		assertEquals(channel.getChannelLimit(), 10, "Channel limit in mode +plmn 10 doesn't match given");
	}

	@Test
	public void getChannelLimitEndTest() {
		channel.setMode("+pmnl 10");
		assertEquals(channel.getChannelLimit(), 10, "Channel limit in mode +pmnl 10 doesn't match given");
	}

	@Test
	public void getChannelKeyUnusedTest() {
		channel.setMode("+pmn someKey");
		assertEquals(channel.getChannelKey(), null, "GetChannelKey when no Key exists isn't -1");
	}

	@Test
	public void getChannelKeyFirstTest() {
		channel.setMode("+kpmn someKey");
		assertEquals(channel.getChannelKey(), "someKey", "Channel Key in mode +lpmn 10 doesn't match given");
	}

	@Test
	public void getChannelKeyMiddleTest() {
		channel.setMode("+pkmn someKey");
		assertEquals(channel.getChannelKey(), "someKey", "Channel Key in mode +plmn 10 doesn't match given");
	}

	@Test
	public void getChannelKeyEndTest() {
		channel.setMode("+pmnk someKey");
		assertEquals(channel.getChannelKey(), "someKey", "Channel Key in mode +pmnl 10 doesn't match given");
	}
}
