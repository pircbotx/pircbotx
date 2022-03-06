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
package org.pircbotx.hooks.events;

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
import org.pircbotx.dcc.FileTransferStatus;
import org.pircbotx.hooks.types.GenericDCCEvent;

/**
 * This event is dispatched whenever a DCC Transfer is completed.
 *
 * @see FileTransfer
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTransferCompleteEvent extends Event implements GenericDCCEvent {
	@Getter(onMethod = @__({
		@Override,
		@Nullable}))
	protected final User user;
	@Getter
	protected final FileTransferStatus transferStatus;
	@Getter
	protected final String fileName;
	@Getter(onMethod = @__({
			@Override}))
	protected final InetAddress address;
	@Getter(onMethod = @__({
			@Override}))
	protected final int port;
	@Getter
	protected final long filesize;
	@Getter(onMethod = @__({
			@Override}))
	protected final boolean passive;
	@Getter
	protected final boolean outbound;

	public FileTransferCompleteEvent(PircBotX bot, @NonNull FileTransferStatus transferStatus, User user,
			@NonNull String fileName, InetAddress address, int port, long filesize, boolean passive, boolean outbound) {
		super(bot);
		this.user = user;
		this.transferStatus = transferStatus;
		this.fileName = fileName;
		this.address = address;
		this.port = port;
		this.filesize = filesize;
		this.passive = passive;
		this.outbound = outbound;
	}

	@Override
	public void respond(String response) {
		getUser().send().message(response);
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public UserHostmask getUserHostmask() {
		return null;
	}

}
