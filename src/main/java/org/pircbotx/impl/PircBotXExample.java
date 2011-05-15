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
	 * <b>WARNING:</b> This example requires using a Threaded listener manager 
	 *                 (this is PircBotX's default)
	 * @param event A MessageEvent
	 * @throws Exception If any Exceptions might be thrown, throw them up and let
	 * the {@link ListenerManager} handle it. This can be removed though if not needed
	 */
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		PircBotX bot = event.getBot();

		//If this isn't a waittest, ignore
		if (!event.getMessage().startsWith("?waitTest start"))
			return;
		
		//WaitTest has started
		bot.sendMessage(event.getChannel(), "Started...");
		//Infinate loop since we might recieve messages that aren't WaitTest's. 
		while (true) {
			//Use the waitFor() method to wait for a MessageEvent.
			//This will block (wait) until a message event comes in, ignoring
			//everything else
			MessageEvent currentEvent = bot.waitFor(MessageEvent.class);
			//Check if this message is the "ping" command
			if (currentEvent.getMessage().startsWith("?waitTest ping"))
				bot.sendMessage(currentEvent.getChannel(), "Pong");
			//Check if this message is the "end" command
			else if (currentEvent.getMessage().startsWith("?waitTest end")) {
				bot.sendMessage(currentEvent.getChannel(), "Killing...");
				//Very important that we end the infinate loop or else the test
				//will continue forever!
				return;
			}
		}
	}

	/**
	 * Older way to handle events. We are given a generic event and must cast
	 * to the event type that we want
	 * <p>
	 * <b>WARNING:</b> If you are extending ListenerAdapter and implementing Listener
	 * in the same class (as this does) you <b>must</b> call <code>super.onEvent(event)</code>
	 * otherwise none of the methods in ListenerAdapter will get called!
	 * @param rawevent A generic event
	 * @throws Exception If any Exceptions might be thrown, throw them up and let
	 * the {@link ListenerManager} handle it. This can be removed though if not needed
	 */
	@Override
	public void onEvent(Event rawevent) throws Exception {
		//Since we extend ListenerAdapter and implement Listener in the same class
		//call the super onEvent so ListenerAdapter will work
		super.onEvent(rawevent);
		
		//Make sure were dealing with a message
		if(rawevent instanceof MessageEvent) {
			//Cast to get access to all the MessageEvent specific methods
			MessageEvent event = (MessageEvent)rawevent;
			
			//Basic hello world
			if(event.getMessage().startsWith("?hello"))
				event.getBot().sendMessage(event.getChannel(), "World");
		}
		
	}

	public static void main(String[] args) {
		//Create a new bot
		PircBotX bot = new PircBotX();
		
		//Setup this bot
		bot.setName("Quackbot5"); //Set the nick of the bot. CHANGE IN YOUR CODE
		bot.setLogin("LQ"); //login part of hostmask, eg name:login@host
		bot.setVerbose(true); //Print everything, which is what you want to do 90% of the time
		
		//This class is a listener, so add it to the bots known listeners
		bot.getListenerManager().addListener(new PircBotXExample());
		
		//bot.connect throws various exceptions for failures
		try {
			//Connect to the freenode IRC network
			bot.connect("irc.freenode.org");
			//Join the #quackbot channel
			bot.joinChannel("#quackbot");
		} 
		//In your code you should catch and handle each exception seperately,
		//but here we just lump them all togeather for simpliciy
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
