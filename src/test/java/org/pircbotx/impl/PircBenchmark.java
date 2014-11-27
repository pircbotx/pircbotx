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
package org.pircbotx.impl;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.pircbotx.Configuration;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;
import org.testng.collections.Lists;

/**
 *
 * @author Leon Blakey
 */
public class PircBenchmark {
	protected final static int MAX_USERS = 200;
	protected final static int MAX_CHANNELS = 20;
	protected final static int MAX_ITERATIONS = 5;
	protected static String[][] responseGroups;
	protected static InputParser inputParser;

	public static void main(String[] args) throws Exception {
		//Change logging to no-op
//		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//		JoranConfigurator configurator = new JoranConfigurator();
//		configurator.setContext(context);
//		context.reset();
//		configurator.doConfigure(PircBenchmark.class.getResource("/logback-nop.xml"));
//		StatusPrinter.printInCaseOfErrorsOrWarnings(context);

		if (args.length != 1) {
			System.err.println("Must specify thread count:");
			System.err.println(" -1: GenericListenerManager");
			System.err.println(" 0: ThreadedListenerManager (default threads)");
			System.err.println(" 1+: ThreadedListenerManager (specified threads)");
			return;
		}
		int threadCount = Integer.parseInt(args[0]);

		//Init
		String[][] responseTemplateGroups = new String[][]{
			new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} ?jmeter ${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} ?jmeter ${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :\u0001ACTION ${thisNick}\u0001"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter NOTICE ${channel} :${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :\u0001ACTION ${thisNick}\u0001"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +o ${thisNick}",
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -o ${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +v ${thisNick}",
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -v ${thisNick}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter KICK ${channel} ${targetNick}: ${thisNick}",
				":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +b ${thisNick}!*@*",
				":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -b ${thisNick}!*@*"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter PART ${channel}",
				":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"
			}, new String[]{
				":${thisNick}!~jmeter@bots.jmeter QUIT :${thisNick}",
				":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"
			},};

		Runtime runtime = Runtime.getRuntime();
		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));

		int responseCount = 1 + MAX_USERS * MAX_CHANNELS * MAX_ITERATIONS * responseTemplateGroups.length;
		System.out.println("Building " + responseCount + " response groups from " + responseTemplateGroups.length + " template groups");

		//Generate users up to MAX_USERS, eg user1, user2, user3..
		//Then Generate channels up to MAX_CHANNELS
		//Then duplicate response group MAX_ITERATIONS times
		//Then replace each ${} template with its corresponding value
		//Shuffle entire array, excluding 0th stop for the setup
		responseGroups = new String[responseCount][];
		int responseGroupsCounter = 1; //Give space for the "setup" response group
		SecureRandom sortRandom = new SecureRandom();
		String[] searchList = new String[]{"${thisNick}", "${targetNick}", "${channel}"};
		for (int userNum = 0; userNum < MAX_USERS; userNum++) {
			shuffleArrayExceptFirst(responseTemplateGroups, sortRandom);
			for (int channelNum = 0; channelNum < MAX_CHANNELS; channelNum++) {
				String[] replaceList = new String[]{"pirc" + userNum, "user" + userNum, "##benchmarking" + channelNum};
				for (int iterationNum = 0; iterationNum < MAX_ITERATIONS; iterationNum++)
					//Parse template
					for (int templateNum = 0, templateSize = responseTemplateGroups.length; templateNum < templateSize; templateNum++) {
						String[] templateGroup = responseTemplateGroups[templateNum];
						String[] responseGroup = new String[templateGroup.length];
						int responseCounter = 0;
						for (int templateLineNum = 0, templateLineSize = templateGroup.length; templateLineNum < templateLineSize; templateLineNum++)
							responseGroup[responseCounter++] = StringUtils.replaceEachRepeatedly(templateGroup[templateLineNum], searchList, replaceList);
						responseGroups[responseGroupsCounter++] = responseGroup;
					}
			}
		}

		System.out.println("Sorting");
		shuffleArrayExceptFirst(responseGroups, sortRandom);

		//PircBotX needs to know beforehand what users are in the channel
		List<String> initLines = Lists.newArrayList(MAX_USERS + MAX_CHANNELS);
		for (int userNum = 0; userNum < MAX_USERS; userNum++) {
			for (int channelNum = 0; channelNum < MAX_CHANNELS; channelNum++) {
				String channelName = "##benchmarking" + channelNum;
				initLines.add(":BenchUser!BenchLogin@BenchHostmask JOIN " + channelName);
				initLines.add(":pirc" + userNum + "!pirclogin@pirchostmask JOIN " + channelName);
				initLines.add(":user" + userNum + "!userlogin@userhostmask JOIN " + channelName);
			}
		}
		responseGroups[0] = initLines.toArray(new String[MAX_USERS + MAX_CHANNELS]);

		//Init other objects
		StopWatch stopWatch = new StopWatch();
		ListenerManager listenerManager;
		if (threadCount == -1)
			listenerManager = new GenericListenerManager();
		else if (threadCount == 0)
			listenerManager = new ThreadedListenerManager(Executors.newCachedThreadPool());
		else
			listenerManager = new ThreadedListenerManager(Executors.newFixedThreadPool(threadCount));
		PircBotX bot = new BenchPircBotX(new Configuration.Builder()
				.addServer("example.com")
				.setName("BenchUser")
				.setLogin("BenchLogin")
				.setListenerManager(listenerManager)
				.addListener(new PircBotXJMeter())
				.buildConfiguration());
		inputParser = bot.getInputParser();

		System.out.println("Waiting 5 seconds");
		Thread.sleep(5000);

		System.out.println("Executing");
		int counter = run(stopWatch);

		System.out.println("Parsed " + counter + " lines in " + stopWatch.toString());
		System.out.println("Average parse speed: " + ((float) counter / (stopWatch.getTime() / 1000)) + " per second");

		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));

		//Kill the listener manager so the JVM can shutdown
		if (listenerManager instanceof ThreadedListenerManager)
			((ThreadedListenerManager) listenerManager).shutdown();
	}

	/**
	 * Copied from Collections.shuffle and Collections.swap, this is actually
	 * what they do, just on Collections instead of arrays
	 *
	 * @param array
	 * @return
	 */
	private static final void shuffleArrayExceptFirst(String[][] array, Random rnd) {
		for (int i = array.length; i > 1; i--) {
			//Don't allow 0 to be swapped
			if (i == 0)
				continue;
			int j;
			while ((j = rnd.nextInt(i)) == 0);

			String[] tmp = array[i - 1];
			array[i - 1] = array[j];
			array[j] = tmp;
		}
	}

	private static final int run(StopWatch stopWatch) throws IOException, IrcException {
		int counter = 0;
		stopWatch.start();
		for (String[] curGroup : responseGroups) {
			int size = curGroup.length;
			counter += size;
			for (int i = 0; i < size; i++)
				inputParser.handleLine(curGroup[i]);
		}

		stopWatch.stop();
		return counter;
	}

	static final class BenchPircBotX extends PircBotX {
		public BenchPircBotX(Configuration configuration) {
			super(configuration);
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		protected void sendRawLineToServer(String line) {
			//Do nothing
		}
	}

	static class PircBotXJMeter extends ListenerAdapter {
		@Override
		public void onGenericMessage(GenericMessageEvent event) throws Exception {
			event.respond(event.getMessage().trim());
		}

		@Override
		public void onGenericChannelMode(GenericChannelModeEvent event) throws Exception {
			event.respond(event.getUser().getNick());
		}

		@Override
		public void onGenericUserMode(GenericUserModeEvent event) throws Exception {
			event.respond(event.getUser().getNick());
		}

		@Override
		public void onKick(KickEvent event) throws Exception {
			event.respond(event.getReason().trim());
		}

		@Override
		public void onQuit(QuitEvent event) throws Exception {
			//Send dummy line
			event.getBot().sendRaw().rawLine(event.getReason().trim());
		}

		@Override
		public void onJoin(JoinEvent event) throws Exception {
			event.respond(event.getUser().getNick());
		}

		@Override
		public void onPart(PartEvent event) throws Exception {
			event.respond(event.getUser().getNick());
		}

		@Override
		public void onUnknown(UnknownEvent event) throws Exception {
			System.out.println("Unknown line: " + event.getLine());
		}

		@Override
		public void onSocketConnect(SocketConnectEvent event) throws Exception {
			System.out.println("Connected to socket");
		}

		@Override
		public void onConnect(ConnectEvent event) throws Exception {
			System.out.println("Connected to server");
		}

		@Override
		public void onDisconnect(DisconnectEvent event) throws Exception {
			System.out.println("Disconnected from server");
		}
	}
}
