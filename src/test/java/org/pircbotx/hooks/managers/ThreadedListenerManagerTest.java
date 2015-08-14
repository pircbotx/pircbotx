/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Leon Blakey <leon.m.blakey at gmail.com>
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
