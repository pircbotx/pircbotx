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
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXExample extends ListenerAdapter implements Listener {
	/**
	 * Easy and recommended way to handle events: Override respective methods in 
	 * {@link ListenerAdapter}. 
	 * <p>
	 * This example shows how to work with the waitFor ability of PircBotX. Follow
	 * the inline comments for how this works 
	 * @param event A MessageEvent
	 * @throws Exception If any Exceptions might be thrown, throw them up and let
	 * the {@link ListenerManager} handle it. This can be removed though if not needed
	 */
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		String message = event.getMessage();
		PircBotX bot = event.getBot();

		//If this isn't a waittest, return
		if (!message.startsWith("?waitTest start"))
			return;
		bot.sendMessage(event.getChannel(), "Started...");
		while (true) {
			MessageEvent currentEvent = bot.waitFor(MessageEvent.class);
			System.out.println("NEW MESSAGE: " + currentEvent.getMessage());
			if (currentEvent.getMessage().startsWith("?waitTest ping"))
				bot.sendMessage(event.getChannel(), "Pong");
			else if (currentEvent.getMessage().startsWith("?waitTest end")) {
				bot.sendMessage(event.getChannel(), "Killing...");
				return;
			}
			bot.log("End of MessageEvent");
		}
	}

	/**
	 * 
	 * @param rawevent
	 * @throws Exception 
	 */
	public void onEvent(Event rawevent) throws Exception {
		
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
