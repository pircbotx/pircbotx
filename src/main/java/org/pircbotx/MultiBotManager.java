/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import static com.google.common.util.concurrent.Service.State;
import static com.google.common.base.Preconditions.*;
import com.google.common.collect.ImmutableSortedSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.exception.IrcException;
import org.pircbotx.output.OutputIRC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager that makes connecting and running multiple bots an easy, painless
 * process. 
 * <p>
 * Lifecycle:
 * <ol><li>When created, any added bots or configurations are queued</li>
 * <li>When {@link #start()} is called, all queued bots are connected. Any bots 
 * added after this point are automatically connected</li>
 * <li>When {@link #stop()} is called, {@link OutputIRC#quitServer()} is called 
 * on all bots. No more bots can be added, the Manager is finished. Note that 
 * an optional {@link #stopAndWait() } method is provided to block until all bots
 * shutdown
 * </ol>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Slf4j
public class MultiBotManager<B extends PircBotX> {
	protected static final AtomicInteger MANAGER_COUNT = new AtomicInteger();
	protected final int managerNumber;
	protected final LinkedHashMap<B, ListenableFuture<Void>> runningBots = new LinkedHashMap<B, ListenableFuture<Void>>();
	protected final BiMap<B, Integer> runningBotsNumbers = HashBiMap.create();
	protected final Object runningBotsLock = new Object[0];
	protected final ListeningExecutorService botPool;
	//Code for starting
	protected List<B> startQueue = new ArrayList<B>();
	protected State state = State.NEW;
	protected final Object stateLock = new Object[0];

	/**
	 * Create MultiBotManager with a cached thread pool.
	 */
	public MultiBotManager() {
		managerNumber = MANAGER_COUNT.getAndIncrement();
		ThreadPoolExecutor defaultPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		defaultPool.allowCoreThreadTimeOut(true);
		this.botPool = MoreExecutors.listeningDecorator(defaultPool);
	}

	/**
	 * Create MultiBotManager with the specified thread pool. 
	 * @param botPool A provided thread pool. 
	 */
	public MultiBotManager(ExecutorService botPool) {
		checkNotNull(botPool, "Bot pool cannot be null");
		this.botPool = MoreExecutors.listeningDecorator(botPool);
		this.managerNumber = MANAGER_COUNT.getAndIncrement();
	}

	/**
	 * Adds a managed bot using the specified configuration.
	 * @param config A configuration to pass to the created bot
	 */
	@Synchronized("stateLock")
	public void addBot(Configuration<PircBotX> config) {
		checkNotNull(config, "Configuration cannot be null");
		//Since creating a bot is expensive, verify the state first
		if (state != State.NEW && state != State.RUNNING)
			throw new RuntimeException("MultiBotManager is not running. State: " + state);
		addBot((B)new PircBotX(config));
	}

	/**
	 * Adds a bot to be managed.
	 * @param bot An existing <b>unconnected</b> bot
	 */
	@Synchronized("stateLock")
	public void addBot(B bot) {
		checkNotNull(bot, "Bot cannot be null");
		checkArgument(!bot.isConnected(), "Bot must not already be connected");
		if (state == State.NEW) {
			log.debug("Not started yet, add to queue");
			startQueue.add(bot);
		} else if (state == State.RUNNING) {
			log.debug("Already running, start bot immediately");
			startBot(bot);
		} else
			throw new RuntimeException("MultiBotManager is not running. State: " + state);
	}

	/**
	 * Start the manager, connecting all queued bots.
	 */
	public void start() {
		synchronized (stateLock) {
			if (state != State.NEW)
				throw new RuntimeException("MultiBotManager has already been started. State: " + state);
			state = State.STARTING;
		}

		for (B bot : startQueue)
			startBot(bot);
		startQueue.clear();

		synchronized (stateLock) {
			state = State.RUNNING;
		}
	}

	protected ListenableFuture<Void> startBot(final B bot) {
		checkNotNull(bot, "Bot cannot be null");
		ListenableFuture<Void> future = botPool.submit(new BotRunner(bot));
		synchronized (runningBotsLock) {
			runningBots.put(bot, future);
			runningBotsNumbers.put(bot, bot.getBotId());
		}
		Futures.addCallback(future, new BotFutureCallback(bot));
		return future;
	}

	/**
	 * Disconnect all bots from their respective severs cleanly.
	 */
	public void stop() {
		synchronized (stateLock) {
			if (state != State.RUNNING)
				throw new RuntimeException("MultiBotManager cannot be stopped again or before starting. State: " + state);
			state = State.STOPPING;
		}

		for (B bot : runningBots.keySet())
			if (bot.isConnected())
				bot.sendIRC().quitServer();

		botPool.shutdown();
	}

	/**
	 * {@link #stop()} and wait for all bots to disconnect.
	 * @throws InterruptedException If this is interrupted while waiting
	 */
	public void stopAndWait() throws InterruptedException {
		stop();
		
		Joiner commaJoiner = Joiner.on(", ");
		do
			synchronized (runningBotsLock) {
				log.debug("Waiting 5 seconds for bot(s) [{}] to terminate ", commaJoiner.join(runningBots.values()));
			}
		while (!botPool.awaitTermination(5, TimeUnit.SECONDS));
	}

	/**
	 * Get all the bots that this MultiBotManager is managing.
	 * @return An <i>immutable copy</i> of bots that are being managed
	 */
	@Synchronized("runningBotsLock")
	public ImmutableSortedSet<B> getBots() {
		return ImmutableSortedSet.copyOf(runningBots.keySet());
	}

	/**
	 * Lookup a managed bot by id.
	 * @param id The id of the bot
	 * @return A bot that has the specified id or null
	 */
	@Synchronized("runningBotsLock")
	public B getBotById(int id) {
		return runningBotsNumbers.inverse().get(id);
	}

	@RequiredArgsConstructor
	protected class BotRunner implements Callable<Void> {
		@NonNull
		protected final B bot;

		public Void call() throws IOException, IrcException {
			Thread.currentThread().setName("botPool" + managerNumber + "-bot" + bot.getBotId());
			bot.connect();
			return null;
		}
	}

	@RequiredArgsConstructor
	protected class BotFutureCallback implements FutureCallback<Void> {
		protected final Logger log = LoggerFactory.getLogger(getClass());
		@NonNull
		protected final B bot;

		public void onSuccess(Void result) {
			log.debug("Bot #" + bot.getBotId() + " finished");
			remove();
		}

		public void onFailure(Throwable t) {
			log.error("Bot exited with Exception", t);
			remove();
		}

		protected void remove() {
			synchronized (runningBotsLock) {
				runningBots.remove(bot);
				runningBotsNumbers.remove(bot);

				//Change state to TERMINATED if this is the last but to be removed during shutdown
				if (runningBots.isEmpty() && state == State.STOPPING)
						synchronized (stateLock) {
							if (state == State.STOPPING)
								state = State.TERMINATED;
						}
			}
		}
	}
}
