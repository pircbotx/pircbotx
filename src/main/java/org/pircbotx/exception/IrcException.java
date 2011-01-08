/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.exception;

/**
 * An IrcException class.
 *
 * @since   0.9
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class IrcException extends Exception {
	/**
	 * Constructs a new IrcException.
	 *
	 * @param e The error message to report.
	 */
	public IrcException(String e) {
		super(e);
	}
}
