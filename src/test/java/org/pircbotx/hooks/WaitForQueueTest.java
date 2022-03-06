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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.types.GenericCTCPEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.testng.collections.Lists;

/**
 *
 */
public class WaitForQueueTest {
	@SuppressWarnings("unchecked")
	private void syntaxCompileTest() throws InterruptedException {
		if (true)
			throw new RuntimeException("DO NOT CALL THIS");

		WaitForQueue queue = new WaitForQueue();

		//These should all be legal and compile
		MessageEvent mevent = queue.waitFor(MessageEvent.class);

		Event event;
		event = queue.waitFor(MessageEvent.class);
		event = queue.waitFor(MessageEvent.class, ActionEvent.class);
		event = queue.waitFor(MessageEvent.class, ActionEvent.class, MotdEvent.class);

		event = queue.waitFor(Arrays.asList(MessageEvent.class, ActionEvent.class, MotdEvent.class));
		event = queue.waitFor(Arrays.asList(MessageEvent.class, ActionEvent.class));

		GenericMessageEvent gevent = queue.waitFor(GenericMessageEvent.class);
		event = queue.waitFor(Arrays.asList(GenericMessageEvent.class, GenericCTCPEvent.class));
		event = queue.waitFor(Arrays.asList(GenericMessageEvent.class, ActionEvent.class));

		event = queue.waitFor(MessageEvent.class, 20, TimeUnit.DAYS);

		event = queue.waitFor(Arrays.asList(MessageEvent.class, ActionEvent.class), 20, TimeUnit.DAYS);

		//This does not compile for some reason
		//However there is 0 reason to use this syntax
		//event = queue.waitFor(Arrays.asList(MessageEvent.class));
		List<Class<? extends Event>> eventList = Lists.newArrayList();
		eventList.add(MessageEvent.class);
		eventList.add(ActionEvent.class);
		eventList.add(MotdEvent.class);
		event = queue.waitFor(eventList);
	}
}
