/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.hooks.events;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.dcc.FileTransfer;
import org.pircbotx.dcc.ReceiveFileTransfer;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.types.GenericDCCEvent;

/**
 * This event is dispatched whenever a DCC SEND request is sent to the PircBotX.
 * This means that a client has requested to send a file to us. By default there
 * are no {@link Listener listeners} for this event, which means that all DCC
 * SEND requests will be ignored by default. If you wish to receive the file,
 * then you must listen for this event and call the receive method on the
 * DccFileTransfer object, which connects to the sender and downloads the file.
 * <p>
 * Example:
 * <pre>
 *     DccFileTransfer transfer = event.getTransfer();
 *     // Use the suggested file name.
 *     File file = transfer.getFile();
 *     // Receive the transfer and save it to the file, allowing resuming.
 *     transfer.receive(file, true);
 * </pre>
 * <p>
 * <b>Warning:</b> Receiving an incoming file transfer will cause a file to be
 * written to disk. Please ensure that you make adequate security checks so that
 * this file does not overwrite anything important!
 * <p>
 * If you allow resuming and the file already partly exists, it will be appended
 * to instead of overwritten. If resuming is not enabled, the file will be
 * overwritten if it already exists.
 *
 * @author Leon Blakey
 * @see FileTransfer
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IncomingFileTransferEvent extends Event implements GenericDCCEvent {
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	protected final String rawFilename;
	protected final String safeFilename;
	@Getter(onMethod = @_(
			@Override))
	protected final InetAddress address;
	@Getter(onMethod = @_(
			@Override))
	protected final int port;
	protected final long filesize;
	@Getter(onMethod = @_(
			@Override))
	protected final String token;
	@Getter(onMethod = @_(
			@Override))
	protected final boolean passive;

	public IncomingFileTransferEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, @NonNull String rawFilename, @NonNull String safeFilename,
			@NonNull InetAddress address, int port, long filesize, String token, boolean passive) {
		super(bot);
		this.user = user;
		this.userHostmask = userHostmask;
		this.rawFilename = rawFilename;
		this.safeFilename = safeFilename;
		this.address = address;
		this.port = port;
		this.filesize = filesize;
		this.token = token;
		this.passive = passive;
	}

	/**
	 * @deprecated Use {@link #getToken() } from {@link GenericDCCEvent}
	 * interface
	 */
	@Deprecated
	public String getTransferToken() {
		return getToken();
	}

	public ReceiveFileTransfer accept(@NonNull File destination) throws IOException {
		return getBot().getDccHandler().acceptFileTransfer(this, destination);
	}

	public ReceiveFileTransfer acceptResume(@NonNull File destination, long startPosition) throws IOException, InterruptedException {
		return getBot().getDccHandler().acceptFileTransferResume(this, destination, startPosition);
	}

	/**
	 * Accept the request and transfer the file now, blocking until finished.
	 *
	 * @param destination
	 * @throws IOException
	 */
	public ReceiveFileTransfer acceptAndTransfer(File destination) throws IOException {
		ReceiveFileTransfer transfer = accept(destination);
		transfer.transfer();
		return transfer;
	}

	/**
	 * Accept the resume request and transfer the file now, blocking until
	 * finished.
	 *
	 * @param destination
	 * @throws IOException
	 */
	public ReceiveFileTransfer acceptResumeAndTransfer(File destination, long startPosition) throws IOException, InterruptedException {
		ReceiveFileTransfer transfer = acceptResume(destination, startPosition);
		transfer.transfer();
		return transfer;
	}

	/**
	 * Respond with a <i>private message</i> to the user that sent the request
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getUser().send().message(response);
	}
}
