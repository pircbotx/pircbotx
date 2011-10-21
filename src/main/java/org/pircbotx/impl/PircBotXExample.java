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
package org.pircbotx.impl;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Basic example class for various features of PircBotX. Heavily documented
 * to explain what's going on
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
	 * <p>
	 * *WARNING:* This example requires using a Threaded listener manager 
	 *            (this is PircBotX's default)
	 * @param event A MessageEvent
	 * @throws Exception If any Exceptions might be thrown, throw them up and let
	 * the {@link ListenerManager} handle it. This can be removed though if not needed
	 */
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		//Hello world
		//This way to handle commands is useful for listeners that listen for multiple commands
		if (event.getMessage().startsWith("?hello"))
			event.respond("Hello World!");

		//If this isn't a waittest, ignore
		//This way to handle commands is useful for listers that only listen for one command
		if (!event.getMessage().startsWith("?waitTest start"))
			return;

		//WaitTest has started
		event.respond("Started...");
		//Infinate loop since we might recieve messages that aren't WaitTest's. 
		while (true) {
			//Use the waitFor() method to wait for a MessageEvent.
			//This will block (wait) until a message event comes in, ignoring
			//everything else
			MessageEvent currentEvent = event.getBot().waitFor(MessageEvent.class);
			//Check if this message is the "ping" command
			if (currentEvent.getMessage().startsWith("?waitTest ping"))
				event.respond("pong");
			//Check if this message is the "end" command
			else if (currentEvent.getMessage().startsWith("?waitTest end")) {
				event.respond("Stopping");
				//Very important that we end the infinate loop or else the test
				//will continue forever!
				return;
			}
		}
	}

	/**
	 * Older way to handle events. We are given a generic event and must cast
	 * to the event type that we want. This is helpful for when you need to funnel
	 * all events into a single method, eg logging
	 * <p>
	 * This also shows the other way to send messages: With PircBotX's send* 
	 * methods. These should be used when the respond() method of the event
	 * doesn't send the message to where you want it to go. 
	 * <p>
	 * *WARNING:* If you are extending ListenerAdapter and implementing Listener
	 * in the same class (as this does) you *must* call super.onEvent(event);
	 * otherwise none of the methods in ListenerAdapter will get called!
	 * @param rawevent A generic event
	 * @throws Exception If any Exceptions might be thrown, throw them up and let
	 * the {@link ListenerManager} handle it. This can be removed though if not needed
	 */
	@Override
	public void onEvent(Event rawevent) throws Exception {
		//Since we extend ListenerAdapter and implement Listener in the same class
		//call the super onEvent so ListenerAdapter will work
		//Unless you are doing that, this line shouldn't be added
		super.onEvent(rawevent);

		//Make sure were dealing with a message
		if (rawevent instanceof MessageEvent) {
			//Cast to get access to all the MessageEvent specific methods
			MessageEvent event = (MessageEvent) rawevent;

			//Basic hello
			if (event.getMessage().startsWith("?hi"))
				event.getBot().sendMessage(event.getChannel(), "Hello");
		}

	}

	public static void main(String[] args) {
		//Create a new bot
		PircBotX bot = new PircBotX();

		//Setup this bot
		bot.setName("Quackbot5"); //Set the nick of the bot. CHANGE IN YOUR CODE
		bot.setLogin("LQ"); //login part of hostmask, eg name:login@host
		bot.setVerbose(true); //Print everything, which is what you want to do 90% of the time
		bot.setAutoNickChange(true); //Automatically change nick when the current one is in use

		//This class is a listener, so add it to the bots known listeners
		bot.getListenerManager().addListener(new PircBotXExample());

		//bot.connect throws various exceptions for failures
		try {
			//Connect to the freenode IRC network
			bot.connect("irc.freenode.org");
			//Join the #quackbot channel
			bot.joinChannel("#quackbot");
		} //In your code you should catch and handle each exception seperately,
		//but here we just lump them all togeather for simpliciy
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
