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

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.impl.PircBotXJMeter;

/**
 *
 * @author Leon
 */
public class Benchmark {
	protected final static int MAX_USERS = 200;
	protected final static int MAX_CHANNELS = 20;
	protected final static int MAX_ITERATIONS = 50;
	protected static String[][] responseGroups;
	protected static PircBotX bot;

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Must specify thread count:");
			System.err.println(" -1: GenericListenerManager");
			System.err.println(" 0: ThreadedListenerManager (default threads)");
			System.err.println(" 1+: ThreadedListenerManager (specified threads)");
			return;
		}
		int threadCount = Integer.parseInt(args[0]);

		//Init
		String[][] responseTemplateGroups = new String[13][];
		responseTemplateGroups[0] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} ?jmeter ${thisNick}"};
		responseTemplateGroups[1] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} ?jmeter ${thisNick}"};
		responseTemplateGroups[2] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :${thisNick}"};
		responseTemplateGroups[3] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :\u0001ACTION ${thisNick}\u0001"};
		responseTemplateGroups[4] = new String[]{":${thisNick}!~jmeter@bots.jmeter NOTICE ${channel} :${thisNick}"};
		responseTemplateGroups[5] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :${thisNick}"};
		responseTemplateGroups[6] = new String[]{":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :\u0001ACTION ${thisNick}\u0001"};
		responseTemplateGroups[7] = new String[]{":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +o ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -o ${thisNick}"};
		responseTemplateGroups[8] = new String[]{":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +v ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -v ${thisNick}"};
		responseTemplateGroups[9] = new String[]{":${thisNick}!~jmeter@bots.jmeter KICK ${channel} ${targetNick}: ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"};
		responseTemplateGroups[10] = new String[]{":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +b ${thisNick}!*@*", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -b ${thisNick}!*@*"};
		responseTemplateGroups[11] = new String[]{":${thisNick}!~jmeter@bots.jmeter PART ${channel}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"};
		responseTemplateGroups[12] = new String[]{":${thisNick}!~jmeter@bots.jmeter QUIT :${thisNick}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"};

		Runtime runtime = Runtime.getRuntime();
		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));

		System.out.println("Building responses");
		responseGroups = new String[MAX_USERS * MAX_CHANNELS * MAX_ITERATIONS * responseTemplateGroups.length][];
		int responseGroupsCounter = 0;
		SecureRandom sortRandom = new SecureRandom();
		String[] searchList = new String[]{"${thisNick}", "${channel}"};
		for (int userNum = 0; userNum < MAX_USERS; userNum++) {
			shuffleArray(responseTemplateGroups, sortRandom);
			for (int channelNum = 0; channelNum < MAX_CHANNELS; channelNum++) {
				String[] replaceList = new String[]{"umark" + userNum, "#cbench" + channelNum};
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
		shuffleArray(responseGroups, sortRandom);

		//Init other objects
		StopWatch stopWatch = new StopWatch();
		bot = new PircBotX();
		if (threadCount == -1)
			bot.setListenerManager(new GenericListenerManager());
		else if (threadCount == 0)
			bot.setListenerManager(new ThreadedListenerManager(Executors.newCachedThreadPool()));
		else
			bot.setListenerManager(new ThreadedListenerManager(Executors.newFixedThreadPool(threadCount)));
		bot.getListenerManager().addListener(new PircBotXJMeter());

		System.out.println("Waiting 5 seconds");
		Thread.sleep(5000);

		System.out.println("Executing with " + responseGroups.length + " response groups");
		int counter = run(stopWatch);

		System.out.println("Parsed " + counter + " enteries in " + stopWatch.toString());
		System.out.println("Average parse speed: " + ((float) counter / (stopWatch.getTime() / 1000)) + " per second");

		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));

		//Kill the listener manager so the JVM can shutdown
		((ThreadedListenerManager) bot.getListenerManager()).shutdown();
	}

	/**
	 * Copied from Collections.shuffle and Collections.swap, this is actually what 
	 * they do, just on Collections instead of arrays
	 * @param array
	 * @return 
	 */
	private static final void shuffleArray(String[][] array, Random rnd) {
		for (int i = array.length; i > 1; i--) {
			int j = rnd.nextInt(i);
			String[] tmp = array[i - 1];
			array[i - 1] = array[j];
			array[j] = tmp;
		}
	}

	private static final int run(StopWatch stopWatch) throws IOException {
		int counter = 0;
		stopWatch.start();
		for (String[] curGroup : responseGroups) {
			int size = curGroup.length;
			counter += size;
			for (int i = 0; i < size; i++)
				bot.handleLine(curGroup[i]);
		}

		stopWatch.stop();
		return counter;
	}
}
