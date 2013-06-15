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
package org.pircbotx.impl;

import java.io.File;
import javax.net.SocketFactory;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.dcc.ReceiveFileTransfer;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Basic example class for various features of PircBotX. Heavily documented
 * to explain what's going on
 * <p/>
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
	 * (this is PircBotX's default)
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

		if (event.getMessage().startsWith("?dccSendFile")) {
			File file = new File("C:\\Users\\Leon\\Downloads\\pircbotx-1.9.jar");
			event.getUser().send().dccFile(file).transfer();
			event.respond("Done sending you a file!");
		}
		
		if(event.getMessage().startsWith("?getMode")) {
			event.respond(event.getChannel().getMode());
		}

		if (event.getMessage().startsWith("?dccChat")) {
			SendChat chat = event.getUser().send().dccChat();
			System.out.println("Chat");
			chat.sendLine("Hello!");
			String line;
			while ((line = chat.readLine()) != null)
				chat.sendLine("You said " + line);
		}

		//If this isn't a waittest, ignore
		//This way to handle commands is useful for listers that only listen for one command
		if (!event.getMessage().startsWith("?waitTest start"))
			return;

		//WaitTest has started
		event.respond("Started...");
		WaitForQueue queue = new WaitForQueue(event.getBot());
		//Infinate loop since we might recieve messages that aren't WaitTest's.
		while (true) {
			//Use the waitFor() method to wait for a MessageEvent.
			//This will block (wait) until a message event comes in, ignoring
			//everything else
			MessageEvent currentEvent = queue.waitFor(MessageEvent.class);
			//Check if this message is the "ping" command
			if (currentEvent.getMessage().startsWith("?waitTest ping"))
				event.respond("pong");
			//Check if this message is the "end" command
			else if (currentEvent.getMessage().startsWith("?waitTest end")) {
				event.respond("Stopping");
				queue.close();
				//Very important that we end the infinate loop or else the test
				//will continue forever!
				return;
			}
		}
	}

	@Override
	public void onNickChange(NickChangeEvent event) throws Exception {
		event.respond("Event oldnick: " + event.getOldNick() + " | Event newnick: " + event.getNewNick()
				+ " | User nick: " + event.getUser().getNick() + " | User realname: " + event.getUser().getRealName()) ;
	}

	@Override
	public void onAction(ActionEvent event) throws Exception {
		event.respond("You said " + event.getMessage());
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
				event.getChannel().send().message("Hello");
		}

	}

	@Override
	public void onIncomingChatRequest(IncomingChatRequestEvent event) throws Exception {
		ReceiveChat chat = event.accept();
		chat.sendLine("Hello incomming request!");
		String line;
		while ((line = chat.readLine()) != null)
			chat.sendLine("You said " + line);
		System.out.println("Chat ended");
	}

	@Override
	public void onIncomingFileTransfer(IncomingFileTransferEvent event) throws Exception {
		event.respond("Receiving file " + event.getSafeFilename());
		File file = File.createTempFile("pircbotx-dcc", event.getSafeFilename());
		event.accept(file).transfer();

		event.respond("Received file " + event.getSafeFilename() + " to " + file.getAbsolutePath());
	}

	public static void main(String[] args) {
		//Setup this bot
		Configuration configuration = new Configuration.Builder()
				.setName("PircBotX") //Set the nick of the bot. CHANGE IN YOUR CODE
				.setLogin("LQ") //login part of hostmask, eg name:login@host
				.setAutoNickChange(true) //Automatically change nick when the current one is in use
				.setCapEnabled(true) //Enable CAP features
				.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
				.addListener(new PircBotXExample()) //This class is a listener, so add it to the bots known listeners
				.setServerHostname("irc.freenode.net")
				.addAutoJoinChannel("#pircbotx") //Join the official #pircbotx channel
				.buildConfiguration();

		//bot.connect throws various exceptions for failures
		try {
			PircBotX bot = new PircBotX(configuration);
			//Connect to the freenode IRC network
			bot.startBot();
		} //In your code you should catch and handle each exception seperately,
		//but here we just lump them all togeather for simpliciy
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
