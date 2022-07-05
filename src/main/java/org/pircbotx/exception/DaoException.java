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

import lombok.Getter;

/**
 *
 */
public class DaoException extends RuntimeException {
	protected static final long serialVersionUID = 1L;
	@Getter
	protected final Reason reason;

	public DaoException(Reason reason, String message) {
		this(reason, message, null);
	}

	public DaoException(Reason reason, String message, Exception e) {
		super(reason + ": " + message, e);
		this.reason = reason;
	}

	public enum Reason {
		UNKNOWN_CHANNEL,
		UNKNOWN_USER,
		UNKNOWN_USER_HOSTMASK,
	}
}
