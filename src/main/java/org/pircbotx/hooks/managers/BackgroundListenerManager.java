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

import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * ThreadListenerManager with additional dedicated background threads. Normal
 * Listeners execute in the same thread pool. Background Listeners however
 * execute in their own dedicated single threads separate from the rest
 * <p>
 * This class is useful for logging listeners or any other listener that needs
 * to process events one at a time instead of simultaneously.
 * <p>
 * To mark a listener as a background listener, use {@link #addListener(org.pircbotx.hooks.Listener, boolean)
 * }
 * with isBackground set to true
 */
public class BackgroundListenerManager extends ThreadedListenerManager {
	protected Map<Listener, ExecutorService> backgroundListeners = new HashMap<>();
	protected final AtomicInteger backgroundCount = new AtomicInteger();

	public void addListener(Listener listener, boolean isBackground) {
		if (!isBackground)
			super.addListener(listener);
		else {
			BasicThreadFactory factory = new BasicThreadFactory.Builder()
					.namingPattern("backgroundPool" + managerNumber + "-backgroundThread" + backgroundCount.getAndIncrement() + "-%d")
					.daemon(true)
					.build();
			backgroundListeners.put(listener, Executors.newSingleThreadExecutor(factory));
		}
	}

	@Override
	public void onEvent(Event event) {
		//Dispatch to both standard listeners and background listeners
		super.onEvent(event);
		for (Map.Entry<Listener, ExecutorService> curEntry : backgroundListeners.entrySet())
			submitEvent(curEntry.getValue(), curEntry.getKey(), event);
	}

	@Override
	public ImmutableSet<Listener> getListeners() {
		return ImmutableSet.<Listener>builder()
				.addAll(listeners)
				.addAll(backgroundListeners.keySet())
				.build();
	}

	@Override
	public boolean removeListener(Listener listener) {
		if (backgroundListeners.containsKey(listener))
			return backgroundListeners.remove(listener) != null;
		else
			return super.removeListener(listener);
	}
}
