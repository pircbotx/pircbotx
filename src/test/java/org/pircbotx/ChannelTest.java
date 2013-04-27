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
		channel = new Channel(null, null, "#testChannel");
	}

	@DataProvider
	public Object[][] modeDataProvider() {
		return new Object[][]{
			//Sandwhich important modes among garbage in the center with garbage arguments
			{"+dfanimnstdfe garbage1 2 #garbage3 garbage4", true},
			//Have only garbage modes and garbage arguments
			{"+dfadfe garbage1 2 #garbage3 garbage4", false}
		};
	}

	@Test(dataProvider = "modeDataProvider")
	public void isInviteOnlyTest(String mode, boolean value) {
		channel.setMode(mode);
		assertEquals(channel.isInviteOnly(), value);
	}

	@Test(dataProvider = "modeDataProvider")
	public void isModeratedTest(String mode, boolean value) {
		channel.setMode(mode);
		assertEquals(channel.isModerated(), value);
	}

	@Test(dataProvider = "modeDataProvider")
	public void isNoExternalMessagesTest(String mode, boolean value) {
		channel.setMode(mode);
		assertEquals(channel.isNoExternalMessages(), value);
	}

	@Test(dataProvider = "modeDataProvider")
	public void isSecretTest(String mode, boolean value) {
		channel.setMode(mode);
		assertEquals(channel.isSecret(), value);
	}

	@Test(dataProvider = "modeDataProvider")
	public void hasTopicProtectionTest(String mode, boolean value) {
		channel.setMode(mode);
		assertEquals(channel.hasTopicProtection(), value);
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
