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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author Leon
 */
public class Benchmark {
	protected final static int MAX_USERS = 200;
	protected final static int MAX_CHANNELS = 20;
	protected final static int MAX_ITERATIONS = 50;
	protected static int classCount = 0; // keep track of classes created
	protected int botNumber;
	protected int lastItem = -1;
	protected List<String> responseItems = new ArrayList();
	protected String thisNick;
	protected StringBuilder requestData;
	protected int requestDataLength;
	protected CountDownLatch latch;
	protected String responseLine;

	static {
	}

	public Benchmark() throws Exception {
		botNumber = classCount++;

		//Init
		List<List<String>> responseTemplateGroups = new ArrayList();
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} ?jmeter ${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} ?jmeter ${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${channel} :\u0001ACTION ${thisNick}\u0001"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter NOTICE ${channel} :${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PRIVMSG ${targetNick} :\u0001ACTION ${thisNick}\u0001"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +o ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -o ${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +v ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -v ${thisNick}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter KICK ${channel} ${targetNick}: ${thisNick}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter MODE ${channel} +b ${thisNick}!*@*", ":${thisNick}!~jmeter@bots.jmeter MODE ${channel} -b ${thisNick}!*@*"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter PART ${channel}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"));
		responseTemplateGroups.add(Arrays.asList(":${thisNick}!~jmeter@bots.jmeter QUIT :${thisNick}", ":${thisNick}!~jmeter@bots.jmeter JOIN :${channel}"));

		Runtime runtime = Runtime.getRuntime();
		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));

		System.out.println("Building responses");
		List<String[]> responseGroups = new ArrayList(MAX_USERS * MAX_CHANNELS * MAX_ITERATIONS * responseTemplateGroups.size());
		SecureRandom sortRandom = new SecureRandom();
		String[] searchList = new String[]{"${thisNick}", "${channel}"};
		for (int userNum = 0; userNum < MAX_USERS; userNum++) {
			Collections.shuffle(responseTemplateGroups, sortRandom);
			for (int channelNum = 0; channelNum < MAX_CHANNELS; channelNum++) {
				String[] replaceList = new String[]{"umark" + userNum, "#cbench" + channelNum};
				for (int iterationNum = 0; iterationNum < MAX_ITERATIONS; iterationNum++) {
					//Parse template
					for (List<String> curTemplateGroup : responseTemplateGroups) {
						String[] responseGroup = new String[curTemplateGroup.size()];
						int responseCounter = 0;
						for (String curTemplate : curTemplateGroup)
							responseGroup[responseCounter++] = StringUtils.replaceEachRepeatedly(curTemplate, searchList, replaceList);
						responseGroups.add(responseGroup);
					}
				}
			}
		}

		System.out.println("Sorting");
		Collections.shuffle(responseGroups);

		//Init other objects
		StopWatch stopWatch = new StopWatch();
		PircBotX bot = new PircBotX();
		int counter = 0;

		System.out.println("Waiting 5 seconds");
		Thread.sleep(5000);

		stopWatch.start();
		for (String[] curGroup : responseGroups) {
			int size = curGroup.length;
			counter += size;
			for (int i = 0; i < size; i++)
				bot.handleLine(curGroup[i]);
		}
		stopWatch.stop();

		System.out.println("Parsed " + counter + " enteries in " + stopWatch.toString());

		System.out.println("Memory usage: " + (runtime.totalMemory() / 1024));
	}

	public static void main(String[] args) throws Exception {
		new Benchmark();
	}
}
