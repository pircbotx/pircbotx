/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.managers;

import lombok.RequiredArgsConstructor;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ExceptionEvent;
import org.pircbotx.hooks.events.ListenerExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author leon
 */
public abstract class AbstractListenerManager implements ListenerManager {
	private static final Logger log = LoggerFactory.getLogger(ListenerManager.class);
	
	protected void executeListener(Listener listener, Event event) {
		executeListener(listener, event, "Failed in " + getClass().getName());
	}
	
	protected void executeListener(Listener listener, Event event, String debug) {
		try {
			log.trace("Calling listener " + listener + " with event " + event);
			listener.onEvent(event);
		} catch (Exception listenerException) {
			if (event instanceof ExceptionEvent) {
				log.error("Encountered exception while processing {}, NOT dispatching another ExceptionEvent to stop potential StackOverflow",
						event.getClass(),
						listenerException);
			} else {
				onEvent(new ListenerExceptionEvent(event.getBot(), listenerException, debug, listener, event));
			}
		}
	}
	
	@RequiredArgsConstructor
	protected static class ExecuteListenerRunnable implements Runnable {
		protected final AbstractListenerManager listenerManager;
		protected final Listener listener;
		protected final Event event;

		@Override
		public void run() {
			listenerManager.executeListener(listener, event);
		}
	}
}
