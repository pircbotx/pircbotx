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
package org.pircbotx.output;

import com.google.common.base.Joiner;
import java.net.InetAddress;
import lombok.RequiredArgsConstructor;
import org.pircbotx.PircBotX;
import org.pircbotx.dcc.DccHandler;

/**
 * Implementation of the DCC protocol. <b>NOTE: This class will not handle the
 * actual chat or sending of files. Use the dcc methods in {@link OutputUser}
 * </b>
 */
@RequiredArgsConstructor
public class OutputDCC {
	protected static final Joiner SPACE_JOINER = Joiner.on(' ');
	protected final PircBotX bot;

	public void dcc(String target, String service, Object... parameters) {
		bot.sendIRC().ctcpCommand(target, SPACE_JOINER.join("DCC", service, parameters));
	}

	public void fileRequest(String target, String filename, InetAddress senderAddress, int senderPort, long filesize) {
		dcc(target, "SEND", filename, DccHandler.addressToInteger(senderAddress), senderPort, filesize);
	}

	public void fileResumeRequest(String target, String filename, int senderPort, long position) {
		dcc(target, "RESUME", filename, senderPort, position);
	}

	public void fileResumeAccept(String target, String filename, int senderPort, long position) {
		dcc(target, "ACCEPT", filename, senderPort, position);
	}

	public void filePassiveRequest(String target, String filename, InetAddress senderAddress, long filesize, String transferToken) {
		dcc(target, "SEND", filename, DccHandler.addressToInteger(senderAddress), 0, filesize, transferToken);
	}

	public void filePassiveAccept(String target, String filename, InetAddress receiverAddress, int receiverPort, long filesize, String transferToken) {
		dcc(target, "SEND", filename, DccHandler.addressToInteger(receiverAddress), receiverPort, filesize, transferToken);
	}

	public void filePassiveResumeRequest(String target, String filename, long position, String transferToken) {
		dcc(target, "RESUME", filename, 0, position, transferToken);
	}

	public void filePassiveResumeAccept(String target, String filename, long position, String transferToken) {
		dcc(target, "ACCEPT", filename, 0, position, transferToken);
	}

	public void chatRequest(String target, InetAddress address, int port) {
		dcc(target, "CHAT", "chat", DccHandler.addressToInteger(address), port);
	}

	public void chatPassiveRequest(String target, InetAddress address, String chatToken) {
		dcc(target, "CHAT", "chat", DccHandler.addressToInteger(address), 0, chatToken);
	}

	public void chatPassiveAccept(String target, InetAddress address, int port, String chatToken) {
		dcc(target, "CHAT", "chat", DccHandler.addressToInteger(address), port, chatToken);
	}
}
