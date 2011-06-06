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

import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.managers.GenericListenerManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for PircBotX class. Any test that involves processing server lines 
 * should be in PircBotXProcessingTest.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Test(singleThreaded=true)
public class PircBotXTest {
	public final Class<PircBotX> botClass = PircBotX.class;
	final Signal signal = new Signal();
	final PircBotXMod bot = new PircBotXMod();
	//Various useful variables for comparing
	final String aString = "I'm some super long string that has multiple words";
	final String string = "AString";
	final User user = new User(bot, "AUser");
	final Channel chan = new Channel(bot, "AChannel");
	final Event event = new ActionEvent(bot, user, chan, string);

	@Test
	public void sendMethodsNamingTests() {
		//Get all send method variations in one list
		Map<String, List<Method>> sendMethods = new HashMap<String, List<Method>>();
		for (Method curMethod : botClass.getDeclaredMethods()) {
			String name = curMethod.getName();
			if (name.startsWith("send")) {
				if (!sendMethods.containsKey(name))
					sendMethods.put(name, new ArrayList<Method>());
				sendMethods.get(name).add(curMethod);
			}
		}

		//Exclude methods that don't make sense to have variations of
		sendMethods.remove("sendRawLineViaQueue");
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

		System.out.println("Success: PircBotX.send* methods have appropiate variations ");
	}
	
	@Test
	public void getUserTest() {
		PircBotX smallBot = new PircBotX();
		smallBot.setNick("BotNick");
		smallBot.setName("BotNick");
		assertNotNull(smallBot.getUser("BotNick"), "Getting bots user with getUser returns null");
		assertNotNull(smallBot.getUser("BotNick"), "Getting existing bots user with getUser returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting new user returns null");
		assertNotNull(smallBot.getUser("SomeOtherNick"), "Getting existing new user returns null");
	}

	@Test
	public void sendActionTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendAction(user, string);
		signal.compare("AUser", string);

		bot.sendAction(chan, string);
		signal.compare("AChannel", string);

		System.out.println("Success: sendAction variations get same result");
	}

	@Test
	public void sendCTCPCommandTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendCTCPCommand(user, string);
		signal.compare("AUser", string);

		System.out.println("Success: sendCTCPCommand variations get same result");
	}

	@Test
	public void sendCTCPResponseTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendCTCPResponse(user, string);
		signal.compare("AUser", string);

		System.out.println("Success: sendCTCPResponse variations get same result");
	}

	@Test
	public void sendInviteTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendInvite(user, chan);
		signal.compare("AUser", "AChannel");

		bot.sendInvite(chan, chan);
		signal.compare("AChannel", "AChannel");

		System.out.println("Success: sendInvite variations get same result");
	}

	@Test
	public void sendMessageTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendMessage(user, string);
		signal.compare("AUser", string);

		bot.sendMessage(chan, string);
		signal.compare("AChannel", string);

		System.out.println("Success: sendMessage variations get same result");
	}

	@Test
	public void sendNoiceTest() {
		//Make sure the same result is given no matter which method we call
		bot.sendNotice(user, string);
		signal.compare("AUser", string);

		bot.sendNotice(chan, string);
		signal.compare("AChannel", string);

		System.out.println("Success: sendNotice variations get same result");
	}

	/**
	 * Modified PircBotX for this testing
	 * -Adds testing listenerManager
	 * -Makes processServerResponse more visible
	 * -Makes send* methods signal result
	 */
	public class PircBotXMod extends PircBotX {
		public PircBotXMod() {
			setListenerManager(new GenericListenerManager());
		}

		@Override
		public void sendAction(String target, String action) {
			signal.set(target, action);
		}

		@Override
		public void sendCTCPCommand(String target, String command) {
			signal.set(target, command);
		}

		@Override
		public void sendCTCPResponse(String target, String message) {
			signal.set(target, message);
		}

		@Override
		public void sendInvite(String nick, String channel) {
			signal.set(nick, channel);
		}

		@Override
		public void sendMessage(String target, String message) {
			signal.set(target, message);
		}

		@Override
		public void sendNotice(String target, String notice) {
			signal.set(target, notice);
		}
	}

	public class Signal {
		public String target = null;
		public String message = null;
		public Channel chan = null;

		public void set(String target, String message) {
			this.target = target;
			this.message = message;
		}

		public void set(String target, Channel chan) {
			this.target = target;
			this.chan = chan;
		}

		public void compare(String expectTarget, String expectMessage) {
			assertEquals(target, expectTarget);
			assertEquals(message, expectMessage);
			reset();
		}

		public void reset() {
			target = null;
			message = null;
			chan = null;
		}
	}
}