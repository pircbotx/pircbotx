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
package org.pircbotx.dcc;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.DccException;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class DccHandler {
	protected final PircBotX bot;
	protected List<ReceiveFileTransfer> recieveFileTransfers = Collections.synchronizedList(new ArrayList<ReceiveFileTransfer>());
	protected List<ReceiveFileTransfer> chats = Collections.synchronizedList(new ArrayList<ReceiveFileTransfer>());
	@Getter
	@Setter
	protected boolean useQuotes = false;

	public boolean processDcc(User user, String line) {
		return true;
	}

	/**
	 * Send the specified file to the user
	 * @see #sendFileTransfers
	 */
	public void sendFile(User receiver, File source, int timeout) throws IOException, DccException {
		//Make the filename safe to send
		String safeFilename = source.getName();
		if (safeFilename.contains(" "))
			if (useQuotes)
				safeFilename = "\"" + safeFilename + "\"";
			else
				safeFilename = safeFilename.replace(" ", "_");

		SendFileTransfer fileTransfer = sendFileRequest(receiver, safeFilename, timeout);
		fileTransfer.sendFile(source);
	}

	/**
	 * Send a file request, returning a ready SendFileTransfer upon success
	 * @param receiver The receiver 
	 * @param safeFilename The filename to send. Must have no spaces or be in quotes
	 * @param timeout The timeout value. 0 means infinite.  120000 is recommended
	 * @return
	 * @throws IOException
	 * @throws DccException 
	 */
	public SendFileTransfer sendFileRequest(User receiver, String safeFilename, int timeout) throws IOException, DccException {
		if (safeFilename == null)
			throw new IllegalArgumentException("Can't send a null file");
		if (safeFilename.contains(" ") && !(safeFilename.startsWith("\"") && safeFilename.endsWith("\"")))
			throw new IllegalArgumentException("Filenames with spaces must be in quotes");
		if (receiver == null)
			throw new IllegalArgumentException("Can't send file to null user");
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout " + timeout + " can't be negative");

		//Try to get the user to connect to us
		ServerSocket serverSocket = createServerSocket();
		String ipNum = DccHandler.addressToInteger(serverSocket.getInetAddress());
		bot.sendCTCPCommand(receiver, "DCC SEND " + safeFilename + " " + ipNum + " " + serverSocket.getLocalPort() + " " + safeFilename.length());

		//Wait for the user to connect
		serverSocket.setSoTimeout(timeout);
		Socket userSocket = serverSocket.accept();
		serverSocket.close();

		return new SendFileTransfer(bot, receiver, safeFilename, userSocket);
	}

	protected ServerSocket createServerSocket() throws IOException, DccException {
		ServerSocket ss = null;
		List<Integer> ports = bot.getDccPorts();
		if (ports.isEmpty())
			// Use any free port.
			ss = new ServerSocket(0);
		else {
			for (int currentPort : ports)
				try {
					ss = new ServerSocket(currentPort);
					// Found a port number we could use.
					break;
				} catch (Exception e) {
					// Do nothing; go round and try another port.
				}
			if (ss == null)
				// No ports could be used.
				throw new DccException("All ports returned by getDccPorts() " + ports.toString() + "are in use.");
		}
		return ss;
	}

	public void close() {
	}

	public static String addressToInteger(InetAddress address) {
		return new BigInteger(1, address.getAddress()).toString();
	}

	public static InetAddress integerToAddress(String rawInteger) {
		//Convert the rawInteger into something usable
		BigInteger bigIp = new BigInteger(rawInteger);
		byte[] addressBytes = bigIp.toByteArray();

		//If there aren't enough bytes, pad with 0 byte
		if (addressBytes.length == 5)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 5);
		else if (addressBytes.length < 4) {
			byte[] newAddressBytes = new byte[4];
			newAddressBytes[3] = addressBytes[0];
			newAddressBytes[2] = (addressBytes.length > 1) ? addressBytes[1] : (byte) 0;
			newAddressBytes[1] = (addressBytes.length > 2) ? addressBytes[2] : (byte) 0;
			newAddressBytes[0] = (addressBytes.length > 3) ? addressBytes[3] : (byte) 0;
			addressBytes = newAddressBytes;
		} else if (addressBytes.length == 17)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 17);
		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException ex) {
			throw new RuntimeException("Can't get InetAdrress version of int IP address " + rawInteger + " (bytes: " + Arrays.toString(addressBytes) + ")", ex);
		}
	}
}
