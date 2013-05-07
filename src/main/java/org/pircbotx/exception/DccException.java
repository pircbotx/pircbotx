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
import lombok.Getter;
import org.pircbotx.User;

/**
 * A general exception for DCC errors
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class DccException extends RuntimeException {
	@Getter
	protected final Reason ourReason;
	@Getter
	protected final User user;

	public DccException(Reason reason, User user, String detail, Throwable cause) {
		super(generateMessage(reason, user, detail), cause);
		checkNotNull(reason, "Reason cannot be null");
		checkNotNull(user, "User cannot be null");
		this.ourReason = reason;
		this.user = user;
	}

	public DccException(Reason reason, User user, String detail) {
		this(reason, user, detail, null);
	}

	protected static String generateMessage(Reason reason, User user, String detail) {
		return reason + " from user " + user.getNick() + ": " + detail;
	}

	public static enum Reason {
		UnknownFileTransferResume,
		ChatNotConnected,
		ChatCancelled,
		ChatTimeout,
		FileTransferCancelled,
		FileTransferTimeout,
		FileTransferResumeTimeout,
		FileTransferResumeCancelled,
		DccPortsInUse
	}
}
