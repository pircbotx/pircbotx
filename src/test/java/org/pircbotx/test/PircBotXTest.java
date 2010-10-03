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

package org.pircbotx.test;

import org.pircbotx.User;
import org.pircbotx.hooks.helpers.BaseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Action;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXTest {
	public Class<PircBotX> botClass = PircBotX.class;

	@Test
	public void sendNamingTests() {
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
					add(BaseEvent.class);
					//sendCTCP* and sendInvite shouldn't be sent to a channel
					if (!key.startsWith("sendCTCP"))
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
		System.out.println("Sucess: PircBotX.send* methods work ");
	}

	@Test
	public void sendTest() {
		//Setup
		final Signal signal = new Signal();
		PircBotX bot = new PircBotX() {
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
		};

		//Make sure all methods call each other appropiatly
		final String string = "AString";
		final User user = new User(bot,"AUser");
		final Channel chan = new Channel(bot,"AChannel");
		final BaseEvent event = new Action.Event(bot, user, chan, string);


		bot.sendAction(event, string);
		signal.compare("AUser", string);

		bot.sendAction(user, string);
		signal.compare("AUser", string);

		bot.sendAction(chan, string);
		signal.compare("AChannel", string);


		bot.sendCTCPCommand(event, string);
		signal.compare("AUser", string);

		bot.sendCTCPCommand(user, string);
		signal.compare("AUser", string);


		bot.sendCTCPResponse(event, string);
		signal.compare("AUser", string);

		bot.sendCTCPResponse(user, string);
		signal.compare("AUser", string);


		bot.sendInvite(event, string);
		signal.compare("AUser", string);

		bot.sendInvite(event, chan);
		signal.compare("AUser", "AChannel");

		bot.sendInvite(event, chan);
		signal.compare("AUser", "AChannel");

		bot.sendInvite(user, chan);
		signal.compare("AUser", "AChannel");

		bot.sendInvite(chan, chan);
		signal.compare("AChannel", "AChannel");


		bot.sendMessage(event, string);
		signal.compare("AUser", string);

		bot.sendMessage(user, string);
		signal.compare("AUser", string);

		bot.sendMessage(chan, string);
		signal.compare("AChannel", string);


		bot.sendNotice(event, string);
		signal.compare("AUser", string);

		bot.sendNotice(user, string);
		signal.compare("AUser", string);

		bot.sendNotice(chan, string);
		signal.compare("AChannel", string);
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
