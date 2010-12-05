/*
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx;

/**
 * The Colors class provides several static fields and methods that you may
 * find useful when writing an IRC Bot.
 *  <p>
 * This class contains constants that are useful for formatting lines
 * sent to IRC servers.  These constants allow you to apply various
 * formatting to the lines, such as colours, boldness, underlining
 * and reverse text.
 *  <p>
 * The class contains static methods to remove colours and formatting
 * from lines of IRC text.
 *  <p>
 * Here are some examples of how to use the contants from within a
 * class that extends PircBot and imports org.jibble.pircbot.*;
 * 
 * <pre> sendMessage("#cs", Colors.BOLD + "A bold hello!");
 *     <b>A bold hello!</b>
 * sendMessage("#cs", Colors.RED + "Red" + Colors.NORMAL + " text");
 *     <font color="red">Red</font> text
 * sendMessage("#cs", Colors.BOLD + Colors.RED + "Bold and red");
 *     <b><font color="red">Bold and red</font></b></pre>
 * 
 * Please note that some IRC channels may be configured to reject any
 * messages that use colours.  Also note that older IRC clients may be
 * unable to correctly display lines that contain colours and other
 * control characters.
 *  <p>
 * Note that this class name has been spelt in the American style in
 * order to remain consistent with the rest of the Java API.
 *
 *
 * @since   0.9.12
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class Colors {
	/**
	 * Removes all previously applied color and formatting attributes.
	 */
	public static final String NORMAL = "\u000f";
	/**
	 * Bold text.
	 */
	public static final String BOLD = "\u0002";
	/**
	 * Underlined text.
	 */
	public static final String UNDERLINE = "\u001f";
	/**
	 * Reversed text (may be rendered as italic text in some clients).
	 */
	public static final String REVERSE = "\u0016";
	/**
	 * White coloured text.
	 */
	public static final String WHITE = "\u000300";
	/**
	 * Black coloured text.
	 */
	public static final String BLACK = "\u000301";
	/**
	 * Dark blue coloured text.
	 */
	public static final String DARK_BLUE = "\u000302";
	/**
	 * Dark green coloured text.
	 */
	public static final String DARK_GREEN = "\u000303";
	/**
	 * Red coloured text.
	 */
	public static final String RED = "\u000304";
	/**
	 * Brown coloured text.
	 */
	public static final String BROWN = "\u000305";
	/**
	 * Purple coloured text.
	 */
	public static final String PURPLE = "\u000306";
	/**
	 * Olive coloured text.
	 */
	public static final String OLIVE = "\u000307";
	/**
	 * Yellow coloured text.
	 */
	public static final String YELLOW = "\u000308";
	/**
	 * Green coloured text.
	 */
	public static final String GREEN = "\u000309";
	/**
	 * Teal coloured text.
	 */
	public static final String TEAL = "\u000310";
	/**
	 * Cyan coloured text.
	 */
	public static final String CYAN = "\u000311";
	/**
	 * Blue coloured text.
	 */
	public static final String BLUE = "\u000312";
	/**
	 * Magenta coloured text.
	 */
	public static final String MAGENTA = "\u000313";
	/**
	 * Dark gray coloured text.
	 */
	public static final String DARK_GRAY = "\u000314";
	/**
	 * Light gray coloured text.
	 */
	public static final String LIGHT_GRAY = "\u000315";

	/**
	 * This class should not be constructed.
	 */
	private Colors() {
	}

	/**
	 * Removes all colours from a line of IRC text.
	 *
	 * @since PircBot 1.2.0
	 *
	 * @param line the input text.
	 *
	 * @return the same text, but with all colours removed.
	 */
	public static String removeColors(String line) {
		int length = line.length();
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		while (i < length) {
			char ch = line.charAt(i);
			if (ch == '\u0003') {
				i++;
				// Skip "x" or "xy" (foreground color).
				if (i < length) {
					ch = line.charAt(i);
					if (Character.isDigit(ch)) {
						i++;
						if (i < length) {
							ch = line.charAt(i);
							if (Character.isDigit(ch))
								i++;
						}
						// Now skip ",x" or ",xy" (background color).
						if (i < length) {
							ch = line.charAt(i);
							if (ch == ',') {
								i++;
								if (i < length) {
									ch = line.charAt(i);
									if (Character.isDigit(ch)) {
										i++;
										if (i < length) {
											ch = line.charAt(i);
											if (Character.isDigit(ch))
												i++;
										}
									} else
										// Keep the comma.
										i--;
								} else
									// Keep the comma.
									i--;
							}
						}
					}
				}
			} else if (ch == '\u000f')
				i++;
			else {
				buffer.append(ch);
				i++;
			}
		}
		return buffer.toString();
	}

	/**
	 * Remove formatting from a line of IRC text.
	 *
	 * @since PircBot 1.2.0
	 *
	 * @param line the input text.
	 *
	 * @return the same text, but without any bold, underlining, reverse, etc.
	 */
	public static String removeFormatting(String line) {
		int length = line.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			char ch = line.charAt(i);
			if (ch == '\u000f' || ch == '\u0002' || ch == '\u001f' || ch == '\u0016') {
				// Don't add this character.
			} else
				buffer.append(ch);
		}
		return buffer.toString();
	}

	/**
	 * Removes all formatting and colours from a line of IRC text.
	 *
	 * @since PircBot 1.2.0
	 *
	 * @param line the input text.
	 *
	 * @return the same text, but without formatting and colour characters.
	 *
	 */
	public static String removeFormattingAndColors(String line) {
		return removeFormatting(removeColors(line));
	}
}
