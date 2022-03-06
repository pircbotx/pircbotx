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
import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PircBotX receives and processes all input from the server in the "bot/network
 * loop". As the input is parsed events are dispatched to this class.
 * <p>
 * Listeners are executed in insertion order. This provides a clear flow of
 * execution unlike other ListenerManagers. As a result, if needed this supports
 * completely single thread execution of PircBotX.
 * <p>
 * If a listener throws an Exception a ListenerExceptionEvent is dispatched. If
 * an exception is thrown when handling that event, the exception is logged to
 * prevent a potential StackOverflow.
 * <p>
 * As not all listeners should run in the network loop, this provides 3 listener
 * executors
 * <ul>
 * <li>Pooled (default for {@link #addListener(org.pircbotx.hooks.Listener) }) -
 * Run each event in the provided {@link #getExecutorPool() }. By default uses
 * the unbounded {@link Executors#newCachedThreadPool(java.util.concurrent.ThreadFactory)
 * }
 * but this means listeners can potentially run into synchronization issues.
 * Useful as mean loop will never deadlock</li>
 * <li>Sequential - This executes the listener with one event at a time. This is
 * done in a {@link Executors#newSingleThreadExecutor(java.util.concurrent.ThreadFactory)
 * }. Any blocking calls (eg send(), WaitforQueue, querying an API on the
 * internet) will stall processing of further events for that listener. Useful
 * for logging and stats collection</li>
 * <li>Inline - Executes listener in the bot loop. This has the smallest
 * overhead. Dangerous as blocking this thread could cause server timeouts.
 * Useful for bulk parsing and internal testing</li>
 * </ul>
 */
@Builder
public class SequentialListenerManager extends AbstractListenerManager {
	private static final Logger log = LoggerFactory.getLogger(SequentialListenerManager.class);
	/**
	 * Key: The actual listener, Value: The wrapper that calls it
	 */
	protected final LinkedList<Listener> listeners = new LinkedList<>();
	protected final LinkedList<ListenerExecutor> listenerExecutors = new LinkedList<>();
	/**
	 * Creates threads used in sequential listeners
	 */
	@Getter
	private final ThreadFactory sequentialThreadFactory;
	/**
	 * The default pool all pooled listeners are executed in
	 */
	@Getter
	private final Executor executorPool;

	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		for (ListenerExecutor executor : listenerExecutors) {
			executor.handleEvent(event);
		}
	}

	/**
	 * Alias of {@link #appendListenerPooled(org.pircbotx.hooks.Listener) }
	 *
	 * @param listener
	 */
	@Override
	public void addListener(Listener listener) {
		addListenerPooled(listener);
	}

	/**
	 * Add listener to be executed in the "bot/network loop"
	 *
	 * @see SequentialListenerManager
	 */
	public SequentialListenerManager addListenerInline(Listener listener) {
		addListenerExecutor(listener, new InlineListenerExecutor(this, listener));
		return this;
	}

	/**
	 * Add listener to be executed in the main executor pool
	 *
	 * @see SequentialListenerManager
	 */
	public SequentialListenerManager addListenerPooled(Listener listener) {
		addListenerExecutor(listener, new PooledListenerExecutor(this, listener, executorPool));
		return this;
	}

	/**
	 * Add listener to be executed in the supplied executor pool
	 *
	 * @see SequentialListenerManager
	 */
	public SequentialListenerManager addListenerPooled(Listener listener, Executor suppliedPool) {
		addListenerExecutor(listener, new PooledListenerExecutor(this, listener, suppliedPool));
		return this;
	}

	/**
	 * Add listener to be executed in its sequential single thread
	 *
	 * @see SequentialListenerManager
	 */
	public SequentialListenerManager addListenerSequential(Listener listener) {
		addListenerExecutor(listener, new SequentialListenerExecutor(this, listener));
		return this;
	}

	/**
	 * Add a listener to be executed by the supplied executor
	 */
	public SequentialListenerManager addListenerExecutor(Listener listener, ListenerExecutor executor) {
		if (listeners.contains(listener))
			throw new IllegalArgumentException("Cannot add listener twice " + listener);
		listeners.add(listener);
		listenerExecutors.add(executor);
		return this;
	}

	/**
	 * Add a listener at the given index to be executed by the supplied executor
	 */
	public SequentialListenerManager addListenerExecutor(int index, Listener listener, ListenerExecutor executor) {
		if (listeners.contains(listener))
			throw new IllegalArgumentException("Cannot add listener twice " + listener);
		listeners.add(index, listener);
		listenerExecutors.add(index, executor);
		return this;
	}

	/**
	 * Replace the executor for the supplied listener
	 */
	public SequentialListenerManager updateExecutor(Listener listener, ListenerExecutor executor) {
		if (!listenerExists(listener))
			throw new RuntimeException("Listener " + listener + " does not exist in this manager");

		int index = listeners.indexOf(listener);
		listenerExecutors.set(index, executor);
		return this;
	}

	/**
	 * Replace all executors with the inline executor
	 */
	public SequentialListenerManager updateExecutorAllInline() {
		for (int i = 0; i < listeners.size(); i++) {
			Listener curListener = listeners.get(i);
			listenerExecutors.set(i, new InlineListenerExecutor(this, curListener));
		}
		return this;
	}

	@Override
	public boolean removeListener(Listener listener) {
		if (!listeners.contains(listener))
			return false;

		int index = listeners.indexOf(listener);
		listeners.remove(index);
		listenerExecutors.remove(index);
		return true;
	}

	public static interface ListenerExecutor extends Closeable {
		public void handleEvent(Event event);
	}

	@RequiredArgsConstructor
	public static class InlineListenerExecutor implements ListenerExecutor {
		protected final AbstractListenerManager listenerManager;
		protected final Listener wrappedListener;

		@Override
		public void handleEvent(Event event) {
			listenerManager.executeListener(wrappedListener, event);
		}

		public void close() throws IOException {
			//Nothing to do, listener runs in bots thread
		}
	}

	@RequiredArgsConstructor
	public static class PooledListenerExecutor implements ListenerExecutor {
		protected final AbstractListenerManager listenerManager;
		protected final Listener wrappedListener;
		protected final Executor executor;

		@Override
		public void handleEvent(Event event) {
			executor.execute(new ExecuteListenerRunnable(listenerManager, wrappedListener, event));
		}

		public void close() throws IOException {
			//TODO: Blocking close or listener tracking
			if (executor instanceof ExecutorService)
				((ExecutorService) executor).shutdown();
		}
	}

	public static class SequentialListenerExecutor extends PooledListenerExecutor {
		public SequentialListenerExecutor(SequentialListenerManager listenerManager, Listener wrappedListener) {
			super(listenerManager, wrappedListener, Executors.newSingleThreadExecutor(listenerManager.getSequentialThreadFactory()));
		}
	}

	@Override
	public boolean listenerExists(Listener listener) {
		return listeners.contains(listener);
	}

	@Override
	public ImmutableSet<Listener> getListeners() {
		return ImmutableSet.copyOf(listeners);
	}

	@Override
	public void shutdown(PircBotX bot) {
		//TODO: Active listener tracking
		if (executorPool instanceof ExecutorService)
			((ExecutorService) executorPool).shutdown();
	}

	/**
	 * Create with 	 <code>
	 * executorPool = Executors.newCachedThreadPool();
	 * sequentialThreadFactory = Executors.defaultThreadFactory();
	 * </code>
	 *
	 * @return
	 */
	public static SequentialListenerManager newDefault() {
		return builder().build();
	}

	//Defaults for magic lombok builder
	public static class SequentialListenerManagerBuilder {
		public SequentialListenerManagerBuilder() {
			executorPool = Executors.newCachedThreadPool();
			sequentialThreadFactory = Executors.defaultThreadFactory();
		}
	}
}
