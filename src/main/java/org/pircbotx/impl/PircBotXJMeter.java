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

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;

/**
 *
 * @author lordquackstar
 */
public class PircBotXJMeter extends ListenerAdapter {
	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		doBefore();
		event.respond(event.getMessage().trim());
		doAfter();
	}

	@Override
	public void onGenericChannelMode(GenericChannelModeEvent event) throws Exception {
		doBefore();
		event.respond(event.getUser().getNick());
		doAfter();
	}

	@Override
	public void onGenericUserMode(GenericUserModeEvent event) throws Exception {
		doBefore();
		event.respond(event.getSource().getNick());
		doAfter();
	}

	@Override
	public void onKick(KickEvent event) throws Exception {
		doBefore();
		event.respond(event.getReason().trim());
		doAfter();
	}

	@Override
	public void onQuit(QuitEvent event) throws Exception {
		//Send dummy line
		doBefore();
		event.getBot().sendRawLine(event.getReason().trim());
		doAfter();
	}

	@Override
	public void onJoin(JoinEvent event) throws Exception {
		doBefore();
		event.respond(event.getUser().getNick());
		doAfter();
	}

	@Override
	public void onPart(PartEvent event) throws Exception {
		doBefore();
		event.respond(event.getUser().getNick());
		doAfter();
	}

	@Override
	public void onUnknown(UnknownEvent event) throws Exception {
		System.out.println("Unknown line: " + event.getLine());
	}

	public void doBefore() throws InterruptedException {
		//Thread.sleep(4000);
	}

	public void doAfter() {
	}

	@Override
	public void onConnect(ConnectEvent event) throws Exception {
		System.out.println("Connected to server");
	}

	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception {
		System.out.println("Disconnected from server");
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("No JMeter IRC server specified");
			System.exit(2);
		}
		String server = args[0];
		System.out.println("Connecting to server: " + server);

		//Create a new bot
		PircBotX bot = new PircBotX();

		//Our custom thread pool (copied Executors.newCachedThreadPool)
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		bot.setListenerManager(new ThreadedListenerManager(executor));

		//Print thread count every 2 seconds
		new Thread() {
			@Override
			public void run() {
				while (true) {
					System.out.println("Pool Size: " + executor.getPoolSize());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		//Setup this bot
		bot.setName("PircBotX"); 
		//bot.setVerbose(true); //Print everything, which is what you want to do 90% of the time
		bot.setMessageDelay(0);

		//This class is a listener, so add it to the bots known listeners
		bot.getListenerManager().addListener(new PircBotXJMeter());

		//bot.connect throws various exceptions for failures
		try {
			//Connect to the freenode IRC network
			bot.connect(server);
			//Join the #quackbot channel
			bot.joinChannel("#quackbot");
		} //In your code you should catch and handle each exception seperately,
		//but here we just lump them all togeather for simpliciy
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
