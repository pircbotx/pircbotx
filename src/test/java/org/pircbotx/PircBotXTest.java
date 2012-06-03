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
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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
	final String aString = "I'm some super long string that has multiple words";

	@BeforeMethod
	public void setup() {
		smallBot = new PircBotX();
	}

	@Test(description = "Make sure send* methods have appropiate variations")
	public void sendMethodsNamingTests() {
		//Get all send method variations in one list
		Map<String, List<Method>> sendMethods = new HashMap<String, List<Method>>();
		for (Method curMethod : PircBotX.class.getDeclaredMethods()) {
			String name = curMethod.getName();
			if (name.startsWith("send")) {
				if (!sendMethods.containsKey(name))
					sendMethods.put(name, new ArrayList<Method>());
				sendMethods.get(name).add(curMethod);
			}
		}

		//Exclude methods that don't make sense to have variations of
		sendMethods.remove("sendRawLineSplit");
		sendMethods.remove("sendRawLineNow");
		sendMethods.remove("sendRawLine");

		for (Map.Entry<String, List<Method>> entry : sendMethods.entrySet()) {
			List<Method> methods = entry.getValue();
			final String key = entry.getKey();

			List<Class> requiredClassesBase = Collections.unmodifiableList(new ArrayList() {
				{
					add(String.class);
					add(User.class);
					//sendCTCPResponse shouldn't have a channel variant
					if (!key.startsWith("sendCTCPResponse"))
						add(Channel.class);
				}
			});

			//---Make sure appropiate variations of a method exist----
			List<Class> requiredClasses = new ArrayList(requiredClassesBase);
			for (Method curMethod : methods) {
				Class firstParam = curMethod.getParameterTypes()[0];
				//Must exist
				assertTrue(requiredClassesBase.contains(firstParam), "Unknown first parameter " + firstParam + " in method " + curMethod);
				requiredClasses.remove(firstParam);
			}

			//If something is left, then something is wrong!
			assertTrue(requiredClasses.isEmpty(), "Method group " + key + " doesn't have a method(s) for " + StringUtils.join(requiredClasses, ", "));
		}
	}

	@Test(description = "Make sure getUser doesn't return null and reliably returns the correct value")
	public void getUserTest() {
		User origUser = smallBot.getUser("SomeUser");
		assertNotNull(origUser, "getUser returns null for unknown user");
		assertEquals(origUser, smallBot.getUser("SomeUser"), "getUser doesn't return the same user during second call");
	}

	@Test(description = "Make sure channel doesn't return null and reliably returns the correct value")
	public void getChannelTest() {
		Channel channel = smallBot.getChannel("#aChannel");
		assertNotNull(channel, "getchannel returns null for unknown channel");
		assertEquals(channel, smallBot.getChannel("#aChannel"), "getChannel doesn't return the same channel during second call");
	}

	@Test(dependsOnMethods = "getUserTest", description = "Make sure userExists works")
	public void userExistsTest() {
		//Create user by getting an known one
		smallBot.getUser("SomeUser");
		//Make sure it exists
		smallBot.userExists("SomeUser");
	}

	@Test(dependsOnMethods = "getChannelTest", description = "Make sure channelExists works")
	public void channelExistsTest() {
		//Create channel by getting an unknown one
		smallBot.getChannel("#aChannel");
		//Make sure it exists
		smallBot.channelExists("#aChannel");
	}

	@Test(description = "Verify getting the bots own user object works")
	public void getUserBotTest() {
		smallBot.setNick("BotNick");
		smallBot.setName("BotNick");
		assertNotNull(smallBot.getUser("BotNick"), "Getting bots user with getUser returns null");
		assertNotNull(smallBot.getUser("BotNick"), "Getting existing bots user with getUser returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting new user returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting existing new user returns null");
	}

	@Test(description = "Verify setEncoding behaves correctly when passed null", expectedExceptions = NullPointerException.class)
	public void setEncodingCharsetNullTest() {
		//Since plain null is ambiguous, use a null variable.
		Charset charset = null;
		smallBot.setEncoding(charset);
	}

	@Test(description = "Verify setEncoding behaves correctly when passed null", expectedExceptions = NullPointerException.class)
	public void setEncodingStringNullTest() throws UnsupportedEncodingException {
		//Since plain null is ambiguous, use a null variable.
		String charset = null;
		smallBot.setEncoding(charset);
	}
}