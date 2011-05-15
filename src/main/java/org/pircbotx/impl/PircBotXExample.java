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
package org.pircbotx.impl;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.UserListEvent;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXExample implements Listener {
	public void onEvent(Event rawevent) throws Exception {
		if (rawevent instanceof MessageEvent) {
			MessageEvent mevent = (MessageEvent) rawevent;
			String message = mevent.getMessage();
			PircBotX bot = rawevent.getBot();

			//If this isn't a waittest, return
			if (!message.startsWith("?waitTest start"))
				return;
			bot.sendMessage(mevent.getChannel(), "Started...");
			while (true) {
				MessageEvent currentEvent = bot.waitFor(MessageEvent.class);
				System.out.println("NEW MESSAGE: "+currentEvent.getMessage());
				if (currentEvent.getMessage().startsWith("?waitTest ping"))
					bot.sendMessage(mevent.getChannel(), "Pong");
				else if (currentEvent.getMessage().startsWith("?waitTest end")) {
					bot.sendMessage(mevent.getChannel(), "Killing...");
					return;
				}
				bot.log("End of MessageEvent");
			}
		}
	}

	public static void main(String[] args) {
		PircBotX bot = new PircBotX();
		bot.setName("Quackbot5");
		bot.setLogin("LQ");
		bot.setVerbose(true);
		bot.getListenerManager().addListener(new PircBotXExample());
		try {
			bot.connect("irc.freenode.org");
			bot.joinChannel("#quackbot");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
