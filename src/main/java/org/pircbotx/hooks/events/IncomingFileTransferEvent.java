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
 * This event is dispatched whenever a DCC SEND request is sent to the PircBotX.
 * This means that a client has requested to send a file to us.
 * By default there are no {@link Listener listeners} for this event, which means 
 * that all DCC SEND requests will be ignored by default. If you wish to receive
 * the file, then you must listen for this event and call the receive method
 * on the DccFileTransfer object, which connects to the sender and downloads
 * the file.
 *  <p>
 * Example:
 * <pre>
 *     DccFileTransfer transfer = event.getTransfer();
 *     // Use the suggested file name.
 *     File file = transfer.getFile();
 *     // Receive the transfer and save it to the file, allowing resuming.
 *     transfer.receive(file, true);
 * </pre>
 *  <p>
 * <b>Warning:</b> Receiving an incoming file transfer will cause a file
 * to be written to disk. Please ensure that you make adequate security
 * checks so that this file does not overwrite anything important!
 *  <p>
 * If you allow resuming and the file already partly exists, it will
 * be appended to instead of overwritten.  If resuming is not enabled,
 * the file will be overwritten if it already exists.
 *  <p>
 * You can throttle the speed of the transfer by calling 
 * {@link DccFileTransfer#setPacketDelay(millis)} method on the DccFileTransfer 
 * object, either before you receive the file or at any moment during the transfer.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see DccFileTransfer
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IncomingFileTransferEvent extends Event {
	protected final DccFileTransfer transfer;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param transfer The DcccFileTransfer that you may accept.
	 */
	public <T extends PircBotX> IncomingFileTransferEvent(T bot, DccFileTransfer transfer) {
		super(bot);
		this.transfer = transfer;
	}

	/**
	 * Respond with a <i>private message</i> to the user that sent the request
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		getBot().sendMessage(getTransfer().getUser(), response);
	}
}
