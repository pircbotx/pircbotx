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
package org.pircbotx.hooks.managers;

import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.mutable.MutableObject;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.ListenerExceptionEvent;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 */
public class ThreadedListenerManagerTest {
	@Test
	public void exceptionTest() {
		ThreadedListenerManager manager = new ThreadedListenerManager(MoreExecutors.newDirectExecutorService());

		final MutableObject<ListenerExceptionEvent> eventResult = new MutableObject<ListenerExceptionEvent>();
		Listener testListener;
		manager.addListener(testListener = new Listener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event instanceof ListenerExceptionEvent) {
					eventResult.setValue((ListenerExceptionEvent)event);
				} else
				throw new RuntimeException("Default fail");
			}
		});

		Event event = new ConnectEvent(null);
		manager.onEvent(event);
		
		assertEquals(eventResult.getValue().getListener(), testListener);
		assertEquals(eventResult.getValue().getSourceEvent(), event);
		assertNotNull(eventResult.getValue().getException());
	}
}
