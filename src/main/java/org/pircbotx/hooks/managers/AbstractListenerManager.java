/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.managers;

import lombok.RequiredArgsConstructor;
import org.pircbotx.Utils;
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
	/**
	 * Aid in log submission
	 */
	private final Logger sublog = LoggerFactory.getLogger(getClass());
	
	protected void executeListener(Listener listener, Event event) {
		executeListener(listener, event, "Failed in " + getClass().getName());
	}
	
	protected void executeListener(Listener listener, Event event, String debug) {
		try {
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

	public void onEvent(Event event) {
		if (event.getBot() != null)
			Utils.addBotToMDC(event.getBot());
		sublog.debug("Recieved event " + event);
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
