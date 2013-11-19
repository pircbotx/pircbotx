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

import com.google.common.base.CharMatcher;
import java.util.ArrayList;
import java.util.List;
import org.pircbotx.hooks.Event;
import org.slf4j.MDC;

/**
 * Useful utilities for internal PircBotX use. Users should not use this class directly
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public final class Utils {
	//Do not create instances of this
	private Utils() {
	}

	@SuppressWarnings("unchecked")
	public static void dispatchEvent(PircBotX bot, Event event) {
		bot.getConfiguration().getListenerManager().dispatchEvent(event);
	}
	
	/**
	 * Try to parse int string, returning -1 if it fails.
	 * @param longString
	 * @return The string as an int or -1
	 */
	public static int tryParseInt(String intString, int defaultValue) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Try to parse long string, returning -1 if it fails.
	 * @param longString
	 * @return The string as a long or -1
	 */
	public static long tryParseLong(String longString, int defaultValue) {
		try {
			return Long.parseLong(longString);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static <V> V tryGetIndex(List<V> list, int index, V defaultValue) {
		if (index < list.size())
			return list.get(index);
		else
			return defaultValue;
	}

	public static void addBotToMDC(PircBotX bot) {
		Configuration<PircBotX> configuration = bot.getConfiguration();
		MDC.put("pircbotx.id", String.valueOf(bot.getBotId()));
		MDC.put("pircbotx.server", configuration.getServerHostname());
		MDC.put("pircbotx.port", String.valueOf(configuration.getServerPort()));
	}
	
	/**
	 * Sends a raw line to the server. Needed so {@link PircBotX#sendRawLineToServer(java.lang.String) }
	 * can stay protected but still be callable from the org.pircbotx.output package
	 * @param bot The bot that sends the raw line
	 * @param rawLine The raw line to send
	 */
	public static void sendRawLineToServer(PircBotX bot, String rawLine) {
		bot.sendRawLineToServer(rawLine);
	}

	/**
	 * Tokenize IRC raw input into it's components, keeping the
	 * 'sender' and 'message' fields intact.
	 * @param input A string in the format [:]item [item] ... [:item [item] ...]
	 * @return List of strings.
	 */
	public static List<String> tokenizeLine(String input) {
		List<String> stringParts = new ArrayList<String>();
		if (input == null || input.length() == 0)
			return stringParts;

		//Heavily optimized version string split by space with all characters after :
		//added as a single entry. Under benchmarks, its faster than StringTokenizer,
		//String.split, toCharArray, and charAt
		String trimmedInput = CharMatcher.WHITESPACE.trimFrom(input);
		int pos = 0, end;
		while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
			stringParts.add(trimmedInput.substring(pos, end));
			pos = end + 1;
			if (trimmedInput.charAt(pos) == ':') {
				stringParts.add(trimmedInput.substring(pos + 1));
				return stringParts;
			}
		}
		//No more spaces, add last part of line
		stringParts.add(trimmedInput.substring(pos));
		return stringParts;
	}
}
