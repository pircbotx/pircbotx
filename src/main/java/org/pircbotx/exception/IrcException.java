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
package org.pircbotx.exception;

import static com.google.common.base.Preconditions.*;

/**
 * A fatal IRC error.
 */
public class IrcException extends Exception {
	protected static final long serialVersionUID = 1L;
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

	public enum Reason {
		ALREADY_CONNECTED,
		CANNOT_LOGIN,
	}
}
