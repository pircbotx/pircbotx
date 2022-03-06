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
 * General Exception for problems during CAP negotiation
 */
public class CAPException extends RuntimeException {
	protected static final long serialVersionUID = 1L;
	
	public CAPException(Reason reason, String detail) {
		this(reason, detail, null);
	}

	public CAPException(Reason reason, String detail, Throwable cause) {
		super(generateMessage(reason, detail), cause);
		checkNotNull(reason, "Reason cannot be null");
		checkNotNull(detail, "Detail cannot be null");
	}

	protected static String generateMessage(Reason reason, String message) {
		return reason + ": " + message;
	}

	public enum Reason {
		UNSUPPORTED_CAPABILITY,
		SASL_FAILED,
		OTHER
	}
}
