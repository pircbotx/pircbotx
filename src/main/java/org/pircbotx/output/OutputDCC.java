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
package org.pircbotx.output;

import com.google.common.base.Joiner;
import java.net.InetAddress;
import lombok.RequiredArgsConstructor;
import org.pircbotx.dcc.DccHandler;

/**
 * Implementation of the DCC protocol. <b>NOTE: This class will not handle the actual 
 * chat or sending of files. Use </b>
 * @author Leon
 */
@RequiredArgsConstructor
public class OutputDCC {
	protected final OutputIRC sendIRC;
	protected static final Joiner lineJoiner = Joiner.on(' ');

	public void fileRequest(String target, String filename, InetAddress senderAddress, int senderPort, long filesize) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "SEND", filename, DccHandler.addressToInteger(senderAddress), senderPort, filesize));
	}

	public void fileResumeRequest(String target, String filename, int senderPort, long position) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "RESUME", filename, senderPort, position));
	}

	public void fileResumeAccept(String target, String filename, int senderPort, long position) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "ACCEPT", filename, senderPort, position));
	}

	public void filePassiveRequest(String target, String filename, InetAddress senderAddress, long filesize, String transferToken) {
		sendIRC.ctcpCommand(target, lineJoiner.join(target, "DCC", "SEND", filename, DccHandler.addressToInteger(senderAddress), 0, filesize, transferToken));
	}

	public void filePassiveAccept(String target, String filename, InetAddress receiverAddress, int receiverPort, long filesize, String transferToken) {
		sendIRC.ctcpCommand(target, lineJoiner.join(filename, "DCC", "SEND", receiverAddress, receiverPort, filesize, transferToken));
	}

	public void filePassiveResumeRequest(String target, String filename, long position, String transferToken) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "RESUME", filename, 0, position, transferToken));
	}

	public void filePassiveResumeAccept(String target, String filename, long position, String transferToken) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "ACCEPT", filename, 0, position, transferToken));
	}

	public void chatRequest(String target, InetAddress address, int port) {
		sendIRC.ctcpCommand(target, lineJoiner.join("DCC", "CHAT", "chat", DccHandler.addressToInteger(address), port));
	}
}
