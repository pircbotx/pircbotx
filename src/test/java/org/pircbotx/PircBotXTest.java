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
	final String aString = "I'm some super long string that has multiple words";

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
		PircBotX smallBot = new PircBotX();
		User origUser = smallBot.getUser("SomeUser");
		assertNotNull(origUser, "getUser returns null for unknown user");
		assertEquals(origUser, smallBot.getUser("SomeUser"), "getUser doesn't return the same user during second call");
	}

	@Test(description = "Make sure channel doesn't return null and reliably returns the correct value")
	public void getChannelTest() {
		PircBotX smallBot = new PircBotX();
		Channel channel = smallBot.getChannel("#aChannel");
		assertNotNull(channel, "getchannel returns null for unknown channel");
		assertEquals(channel, smallBot.getChannel("#aChannel"), "getChannel doesn't return the same channel during second call");
	}

	@Test(dependsOnMethods = "getUserTest", description = "Make sure userExists works")
	public void userExistsTest() {
		PircBotX smallBot = new PircBotX();
		//Create user by getting an known one
		smallBot.getUser("SomeUser");
		//Make sure it exists
		smallBot.userExists("SomeUser");
	}

	@Test(dependsOnMethods = "getChannelTest", description = "Make sure channelExists works")
	public void channelExistsTest() {
		PircBotX smallBot = new PircBotX();
		//Create channel by getting an unknown one
		smallBot.getChannel("#aChannel");
		//Make sure it exists
		smallBot.channelExists("#aChannel");
	}

	@Test(dataProvider = "ipToLongDataProvider")
	public void ipToLong(String ipAddress, BigInteger expectedResult) throws UnknownHostException {
		PircBotX smallBot = new PircBotX();
		
		//First, convert to a byte array
		InetAddress address = InetAddress.getByName(ipAddress);
		byte[] byteIp = address.getAddress();
		System.out.println("IP: " + ipAddress);
		System.out.println("BigInt: " + new BigInteger(byteIp));
		System.out.println("BigInt1: " + new BigInteger(1,byteIp));
		System.out.println("Array Length: " + byteIp.length);
		
		//Next, extract the value
		BigInteger ipToLong = smallBot.ipToInteger(address);
		System.out.println("Bot Convert: " + ipToLong);
		
		
		assertEquals(ipToLong, expectedResult, "Converting " + ipAddress + " to long is wrong");
	}

	@DataProvider
	public Object[][] ipToLongDataProvider() {
		//Note: All numbers are verified with another tool
		return new Object[][]{
					//IPv4 Tests	
					{"127.0.0.1", new BigInteger("2130706433")},
					{"192.168.21.6", new BigInteger("3232240902")},
					{"75.221.45.21", new BigInteger("1272786197")},
					//IPv6 tests
					{"2001:0db8:85a3:0000:0000:8a2e:0370:7334", new BigInteger("42540766452641154071740215577757643572")},
					{"fe80:0:0:0:202:b3ff:fe1e:8329", new BigInteger("338288524927261089654163772891438416681")},
					{"fe80::202:b3ff:fe1e:5329", new BigInteger("338288524927261089654163772891438404393")},};
	}

	@Test(description = "Verify getting the bots own user object works")
	public void getUserBotTest() {
		PircBotX smallBot = new PircBotX();
		smallBot.setNick("BotNick");
		smallBot.setName("BotNick");
		assertNotNull(smallBot.getUser("BotNick"), "Getting bots user with getUser returns null");
		assertNotNull(smallBot.getUser("BotNick"), "Getting existing bots user with getUser returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting new user returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting existing new user returns null");
	}
	
	@Test(description = "Verify setEncoding behaves correctly when passed null")
	public void setEncodingCharsetNullTest() throws UnsupportedEncodingException {
		PircBotX smallBot = new PircBotX();
		//Since plain null is ambiguous, use a null variable.
		Charset charset = null;
		smallBot.setEncoding(charset);
	}
}