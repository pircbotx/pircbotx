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
package org.pircbotx.hooks;

import org.pircbotx.hooks.events.MessageEvent;
import org.apache.commons.lang3.mutable.MutableObject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ListenerManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TemporaryListenerTest {
	protected PublicPircBotX bot;
	protected ListenerManager listenerManager;

	@BeforeMethod
	public void setup() {
		bot = new PublicPircBotX();
		bot.setListenerManager(listenerManager = new GenericListenerManager());
	}

	@Test
	public void eventDispatched() {
		final MutableObject<MessageEvent> mutableEvent = new MutableObject();
		Listener listener = new TemporaryListener(bot) {
			@Override
			public void onMessage(MessageEvent event) throws Exception {
				mutableEvent.setValue(event);
			}
		};
		bot.getListenerManager().addListener(listener);
		
		//Make sure the listener is there
		assertTrue(bot.getListenerManager().listenerExists(listener), "Listener doesn't exist in ListenerManager");

		//Send some arbitrary line
		bot.handleLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :Some very long message");
		MessageEvent mevent = mutableEvent.getValue();

		//Verify event contents
		assertNotNull(mevent, "MessageEvent not dispatched");
		assertEquals(mevent.getMessage(), "Some very long message", "Message sent does not match");
		
		//Make sure the listner is still there
		assertTrue(bot.getListenerManager().listenerExists(listener), "Listener doesn't exist in ListenerManager");
	}
	
	@Test
	public void listenerGetsRemoved() {
		TemporaryListener listener = new TemporaryListener(bot) {
			@Override
			public void onMessage(MessageEvent event) throws Exception {
				done();
			}
		};
		bot.getListenerManager().addListener(listener);
		
		assertTrue(bot.getListenerManager().listenerExists(listener), "Listener wasn't added to ListenerManager");
		bot.handleLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :Some very long message");
		assertFalse(bot.getListenerManager().listenerExists(listener), "Listener wasn't removed from ListenerManager");
	}

	public static class PublicPircBotX extends PircBotX {
		/**
		 * Since we need this for testing but aren't in the right package
		 * make it public
		 */
		@Override
		public void handleLine(String line) {
			super.handleLine(line);
		}
	}
}
