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

import java.io.Closeable;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.Utils;
import org.pircbotx.exception.DccException;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.output.OutputDCC;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
@Slf4j
public class DccHandler implements Closeable {
	protected final Configuration configuration;
	protected final PircBotX bot;
	protected final ListenerManager listenerManager;
	protected final OutputDCC sendDCC;
	@Getter(AccessLevel.PROTECTED)
	protected Map<PendingRecieveFileTransfer, Future> pendingReceiveTransfers = Collections.synchronizedMap(new HashMap());

	public boolean processDcc(final User user, String request) throws IOException {
		List<String> requestParts = tokenizeDccRequest(request);
		String type = requestParts.get(1);
		if (type.equals("SEND")) {
			//Someone is trying to send a file to us
			//Example: DCC SEND <filename> <ip> <port> <file size> <passive(random,optional)> (note File size is optional)
			String filename = requestParts.get(2);
			final String safeFilename = (filename.startsWith("\"") && filename.endsWith("\""))
					? filename.substring(1, filename.length() - 1) : filename;
			InetAddress address = integerToAddress(requestParts.get(3));
			int port = Integer.parseInt(requestParts.get(4));
			long size = Integer.parseInt(Utils.tryGetIndex(requestParts, 5, "-1"));
			String transferToken = Utils.tryGetIndex(requestParts, 6, null);

			if (port == 0 || transferToken != null)
				//User is trying to use reverse DCC
				listenerManager.dispatchEvent(new IncomingFileTransferEvent(bot, user, filename, address, port, size, transferToken, true));
			else
				listenerManager.dispatchEvent(new IncomingFileTransferEvent(bot, user, filename, address, port, size, transferToken, false));
		} else if (type.equals("RESUME")) {
			//Someone is trying to resume sending a file to us
			//Example: DCC RESUME <filename> 0 <position> <token>
			//Reply with: DCC ACCEPT <filename> 0 <position> <token>
			String filename = requestParts.get(2);
			int port = Integer.parseInt(requestParts.get(3));
			long progress = Integer.parseInt(requestParts.get(4));
			String transferToken = requestParts.get(5);

			PendingRecieveFileTransfer transfer = null;
			for (PendingRecieveFileTransfer curTransfer : pendingReceiveTransfers.keySet())
				if (curTransfer.user() == user && curTransfer.token().equals(transferToken)) {
					transfer = curTransfer;
					break;
				}
			if (transfer == null)
				throw new DccException("No Dcc File Transfer to resume recieving (user: " + user.getNick()
						+ ", filename: " + filename + ", position: " + progress + ", token: " + transferToken + ")");
			user.send().ctcpCommand("DCC ACCEPT " + filename + " " + port + " " + progress + " " + transferToken);
		} else if (type.equals("CHAT")) {
			//Someone is trying to chat with us
			//Example: DCC CHAT <protocol> <ip> <port> (protocol should be chat)
			InetAddress address = integerToAddress(requestParts.get(3));
			int port = Integer.parseInt(requestParts.get(4));

			listenerManager.dispatchEvent(new IncomingChatRequestEvent(bot, user, address, port));
		} else
			return false;
		return true;
	}

	public ReceiveChat acceptChatRequest(IncomingChatRequestEvent event) throws IOException {
		return new ReceiveChat(event.getUser(), new Socket(event.getChatAddress(), event.getChatPort()));
	}

	public ReceiveFileTransfer acceptFileTransfer(IncomingFileTransferEvent event) throws IOException {
		if (event.isReverse()) {
			ServerSocket serverSocket = createServerSocket();
			sendDCC.filePassiveAccept(event.getUser().getNick(), event.getFilename(), event.getAddress(), event.getPort(), event.getFilesize(), event.getTransferToken());
			Socket userSocket = serverSocket.accept();

			//User is connected, begin transfer
			serverSocket.close();
			return new ReceiveFileTransfer(configuration, event.getUser(), userSocket, event.getFilesize(), event.getFilename(), 0);
		} else {
			Socket userSocket = new Socket(event.getAddress(), event.getPort(), configuration.getDccLocalAddress(), 0);
			return new ReceiveFileTransfer(configuration, event.getUser(), userSocket, event.getFilesize(), event.getFilename(), 0);
		}
	}

	public SendChat sendChatRequest(User receiver) throws IOException {
		if (receiver == null)
			throw new NullPointerException("Cannot send chat request to null user");
		ServerSocket ss = createServerSocket();
		ss.setSoTimeout(configuration.getDccSocketTimeout());

		int serverPort = ss.getLocalPort();
		String ipNum = addressToInteger(ss.getInetAddress());
		receiver.send().ctcpCommand("DCC CHAT chat " + ipNum + " " + serverPort);

		Socket userSocket = ss.accept();
		ss.close();
		return new SendChat(receiver, userSocket);
	}

	/**
	 * Send the specified file to the user
	 * @see #sendFileTransfers
	 */
	public SendFileTransfer sendFile(File file, User receiver, int timeout) throws IOException, DccException {
		//Make the filename safe to send
		String safeFilename = file.getName();
		if (safeFilename.contains(" "))
			if (configuration.isDccFilenameQuotes())
				safeFilename = "\"" + safeFilename + "\"";
			else
				safeFilename = safeFilename.replace(" ", "_");

		SendFileTransfer fileTransfer = sendFileRequest(safeFilename, receiver, timeout);
		fileTransfer.sendFile(file);
		return fileTransfer;
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
	public SendFileTransfer sendFileRequest(String safeFilename, User receiver, int timeout) throws IOException, DccException {
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
		receiver.send().ctcpCommand("DCC SEND " + safeFilename + " " + ipNum + " " + serverSocket.getLocalPort() + " " + safeFilename.length());

		//Wait for the user to connect
		serverSocket.setSoTimeout(timeout);
		Socket userSocket = serverSocket.accept();
		serverSocket.close();

		return new SendFileTransfer(configuration, receiver, safeFilename, userSocket);
	}

	protected ServerSocket createServerSocket() throws IOException, DccException {
		InetAddress address = configuration.getLocalAddress();
		ServerSocket ss = null;
		if (configuration.getDccPorts().isEmpty())
			// Use any free port.
			ss = new ServerSocket(0, 1, address);
		else {
			for (int currentPort : configuration.getDccPorts())
				try {
					ss = new ServerSocket(currentPort, 1, address);
					// Found a port number we could use.
					break;
				} catch (Exception e) {
					// Do nothing; go round and try another port.
				}
			if (ss == null)
				// No ports could be used.
				throw new DccException("All ports returned by getDccPorts() " + configuration.getDccPorts() + "are in use.");
		}
		return ss;
	}

	protected static List<String> tokenizeDccRequest(String request) {
		int quotesIndexBegin = request.indexOf('"');
		if (quotesIndexBegin == -1)
			//Just use tokenizeLine
			return Utils.tokenizeLine(request);

		//This is a slightly modified version of Utils.tokenizeLine to parse
		//potential quotes in filenames
		int quotesIndexEnd = request.lastIndexOf('"');
		List<String> stringParts = new ArrayList();
		int pos = 0, end;
		while ((end = request.indexOf(' ', pos)) >= 0) {
			if (pos >= quotesIndexBegin && end < quotesIndexEnd) {
				//We've entered the filename. Add and skip
				stringParts.add(request.substring(quotesIndexBegin, quotesIndexEnd + 1));
				pos = quotesIndexEnd + 2;
				continue;
			}
			stringParts.add(request.substring(pos, end));
			pos = end + 1;
			if (request.charAt(pos) == ':') {
				stringParts.add(request.substring(pos + 1));
				return stringParts;
			}
		}
		//No more spaces, add last part of line
		stringParts.add(request.substring(pos));
		return stringParts;
	}

	public void close() {
		//Shutdown open reverse dcc servers
		Iterator<Map.Entry<PendingRecieveFileTransfer, Future>> pendingItr = pendingReceiveTransfers.entrySet().iterator();
		while (pendingItr.hasNext()) {
			Map.Entry<PendingRecieveFileTransfer, Future> curEntry = pendingItr.next();
			PendingRecieveFileTransfer curTransfer = curEntry.getKey();
			Future curFuture = curEntry.getValue();
			if (!curFuture.isDone()) {
				log.debug("Terminating reverse dcc server for transfer " + curTransfer);
				curFuture.cancel(true);
			}
			pendingItr.remove();
		}
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

	@Data
	protected static class PendingRecieveFileTransfer {
		protected final ServerSocket serverSocket;
		protected final IncomingFileTransferEvent event;
	}
}
