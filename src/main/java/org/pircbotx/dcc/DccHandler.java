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
import java.util.StringTokenizer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.DccException;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class DccHandler {
	public static final int TRANSFER_BUFFER_SIZE = 1024;
	protected int socketTimeout;
	protected final PircBotX bot;
	@Getter(AccessLevel.PROTECTED)
	protected List<PendingRecieveFileTransfer> pendingReceiveTransfers = Collections.synchronizedList(new ArrayList<PendingRecieveFileTransfer>());
	protected List<ReceiveFileTransfer> chats = Collections.synchronizedList(new ArrayList<ReceiveFileTransfer>());
	@Getter
	@Setter
	protected boolean useQuotes = false;

	public boolean processDcc(final User user, String request) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(request);
		//Skip the DCC part of the line
		tokenizer.nextToken();
		String type = tokenizer.nextToken();
		if (type.equals("SEND")) {
			//Someone is trying to send a file to us
			//Example: DCC SEND <filename> <ip> <port> <file size> <passive(random,optional)> (note File size is optional)
			String filename = tokenizer.nextToken();
			InetAddress address = integerToAddress(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());
			long size = -1;
			try {
				if (tokenizer.hasMoreTokens())
					size = Long.parseLong(tokenizer.nextToken());
			} catch (Exception e) {
				// Stick with the old value.
			}
			String transferToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;

			if (port == 0 || transferToken != null) {
				//User is trying to use Passive DCC
				final ServerSocket serverSocket = createServerSocket();
				final PendingRecieveFileTransfer pendingTransfer = new PendingRecieveFileTransfer(filename, address, size, transferToken, serverSocket);
				pendingReceiveTransfers.add(pendingTransfer);
				runReverseDccServer(new Runnable() {
					public void run() {
						Exception exception = null;
						ReceiveFileTransfer transfer = null;
						try {
							Socket userSocket = serverSocket.accept();
							serverSocket.close();
							transfer = new ReceiveFileTransfer(user, userSocket, pendingTransfer.filesize(), pendingTransfer.filename(), pendingTransfer.position());
							//Remove the pending transfer
							pendingReceiveTransfers.remove(pendingTransfer);
						} catch (Exception e) {
							exception = e;
						}
						bot.getListenerManager().dispatchEvent(new IncomingFileTransferEvent(bot, transfer, exception));
					}
				});
			} else {
				//User is using normal DCC, connect to them
				Exception exception = null;
				ReceiveFileTransfer fileTransfer = null;
				try {
					Socket userSocket = new Socket(address, port, bot.getDccInetAddress(), 0);
					fileTransfer = new ReceiveFileTransfer(user, userSocket, size, filename, 0);
				} catch (Exception e) {
					exception = e;
				}
				bot.getListenerManager().dispatchEvent(new IncomingFileTransferEvent(bot, fileTransfer, exception));
			}
		} else if (type.equals("RESUME")) {
			//Someone is trying to resume sending a file to us
			//Example: DCC RESUME <filename> 0 <position> <token>
			//Reply with: DCC ACCEPT <filename> 0 <position> <token>
			String filename = tokenizer.nextToken();
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = -1;
			try {
				if (tokenizer.hasMoreTokens())
					progress = Long.parseLong(tokenizer.nextToken());
			} catch (Exception e) {
				// Stick with the old value.
			}
			String transferToken = tokenizer.nextToken();

			PendingRecieveFileTransfer transfer = null;
			for (PendingRecieveFileTransfer curTransfer : pendingReceiveTransfers)
				if (curTransfer.user() == user && curTransfer.token().equals(transferToken)) {
					transfer = curTransfer;
					break;
				}
			if (transfer == null)
				throw new DccException("No Dcc File Transfer to resume recieving (user: " + user.getNick()
						+ ", filename: " + filename + ", position: " + progress + ", token: " + transferToken + ")");
			bot.sendCTCPCommand(user, "DCC ACCEPT " + filename + " " + port + " " + progress + " " + transferToken);
		} else if (type.equals("CHAT")) {
			//Someone is trying to chat with us
			//Example: DCC CHAT <protocol> <ip> <port> (protocol should be chat)
			InetAddress address = integerToAddress(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());

			bot.getListenerManager().dispatchEvent(new IncomingChatRequestEvent(bot, new ReceiveChat(address, port)));
		} else
			return false;
		return true;
	}
	
	public Chat sendChatRequest(User receiver) throws IOException {
		if(receiver == null)
			throw new NullPointerException("Cannot send chat request to null user");
		ServerSocket ss = createServerSocket();
		ss.setSoTimeout(socketTimeout);
		
		int serverPort = ss.getLocalPort();
		String ipNum = addressToInteger(ss.getInetAddress());
		bot.sendCTCPCommand(receiver, "DCC CHAT chat " + ipNum + " " + serverPort);
		
		Socket userSocket = ss.accept();
		ss.close();
		return new Chat().init(userSocket);
	}

	/**
	 * Send the specified file to the user
	 * @see #sendFileTransfers
	 */
	public SendFileTransfer sendFile(File file, User receiver, int timeout) throws IOException, DccException {
		//Make the filename safe to send
		String safeFilename = file.getName();
		if (safeFilename.contains(" "))
			if (useQuotes)
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

	protected void runReverseDccServer(Runnable run) {
		new Thread(run).start();
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

	@Accessors(fluent = true)
	@Getter
	protected static class PendingRecieveFileTransfer {
		protected String filename;
		protected InetAddress userAddress;
		protected long filesize;
		protected long position;
		protected ServerSocket serverSocket;
		protected String token;
		protected User user;

		public PendingRecieveFileTransfer(String filename, InetAddress userAddress, long filesize, String token, ServerSocket serverSocket) {
			this.filename = filename;
			this.userAddress = userAddress;
			this.filesize = filesize;
			this.token = token;
			this.serverSocket = serverSocket;
		}
	}
}
