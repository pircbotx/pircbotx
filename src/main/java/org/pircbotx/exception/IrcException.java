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
package org.pircbotx.exception;

import static com.google.common.base.Preconditions.*;

/**
 * A fatal IRC error.
 *
 * @since PircBot 0.9
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 * Leon Blakey <lord.quackstar at gmail.com>
 */
public class IrcException extends Exception {
	/**
	 * Constructs a new IrcException.
	 *
	 * @param detail The error message to report.
	 */
	public IrcException(Reason reason, String detail) {
		super(generateMessage(reason, detail));
		checkNotNull(reason, "Reason cannot be null");
		checkNotNull(detail, "Detail cannot be null");
	}

	protected static String generateMessage(Reason reason, String detail) {
		return reason + ": " + detail;
	}

	public static enum Reason {
		AlreadyConnected,
		CannotLogin,
		ReconnectBeforeConnect,
	}
}
