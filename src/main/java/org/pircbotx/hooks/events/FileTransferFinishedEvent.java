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
package org.pircbotx.hooks.events;

import org.pircbotx.DccFileTransfer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;

/**
 * This event gets dispatched when a DccFileTransfer has finished.
 * If there was a problem, the Exception will say what went wrong.
 * If the file was sent successfully, the Exception will be null.
 *  <p>
 * Both incoming and outgoing file transfers are passed to this event.
 * You can determine the type by calling the isIncoming or isOutgoing
 * methods on the DccFileTransfer object.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see DccFileTransfer
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTransferFinishedEvent extends Event {
	protected final DccFileTransfer transfer;
	protected final Exception exception;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param transfer The DccFileTransfer that has finished.
	 * @param e null if the file was transfered successfully, otherwise this
	 *          will report what went wrong.
	 */
	public <T extends PircBotX> FileTransferFinishedEvent(T bot, DccFileTransfer transfer, Exception e) {
		super(bot);
		this.transfer = transfer;
		this.exception = e;
	}
}
