/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import java.io.IOException;
import org.pircbotx.hooks.events.MessageEvent;
import org.apache.commons.lang3.mutable.MutableObject;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.managers.ListenerManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 */
public class TemporaryListenerTest {
	protected PircBotX bot;
	protected ListenerManager listenerManager;
	protected User userSource;

	@BeforeMethod
	public void setup() {
		bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.buildConfiguration());
		listenerManager = bot.getConfiguration().getListenerManager();
		bot.getUserChannelDao().createChannel("#aChannel");
		userSource = TestUtils.generateTestUserOther(bot);
	}

	@Test(singleThreaded = true)
	public void eventDispatched() throws IOException, IrcException {
		final MutableObject<MessageEvent> mutableEvent = new MutableObject<MessageEvent>();
		Listener listener = new TemporaryListener(bot) {
			@Override
			public void onMessage(MessageEvent event) throws Exception {
				mutableEvent.setValue(event);
			}
		};
		listenerManager.addListener(listener);

		//Make sure the listener is there
		assertTrue(listenerManager.listenerExists(listener), "Listener doesn't exist in ListenerManager");

		//Send some arbitrary line
		bot.getInputParser().handleLine(":" + userSource.getHostmask() + " PRIVMSG #aChannel :Some very long message");
		MessageEvent mevent = mutableEvent.getValue();

		//Verify event contents
		assertNotNull(mevent, "MessageEvent not dispatched");
		assertEquals(mevent.getMessage(), "Some very long message", "Message sent does not match");

		//Make sure the listner is still there
		assertTrue(listenerManager.listenerExists(listener), "Listener doesn't exist in ListenerManager");
	}

	@Test(singleThreaded = true)
	public void listenerGetsRemoved() throws IOException, IrcException {
		TemporaryListener listener = new TemporaryListener(bot) {
			@Override
			public void onMessage(MessageEvent event) throws Exception {
				done();
			}
		};
		listenerManager.addListener(listener);

		assertTrue(listenerManager.listenerExists(listener), "Listener wasn't added to ListenerManager");
		bot.getInputParser().handleLine(":" + userSource.getHostmask() + " PRIVMSG #aChannel :Some very long message");
		assertFalse(listenerManager.listenerExists(listener), "Listener wasn't removed from ListenerManager");
	}
}
