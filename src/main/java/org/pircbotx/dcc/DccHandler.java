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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
import static com.google.common.base.Preconditions.*;
import java.nio.charset.Charset;
import lombok.NonNull;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
@Slf4j
public class DccHandler implements Closeable {
	protected static final Random tokenRandom = new SecureRandom();
	@NonNull
	protected final Configuration configuration;
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final ListenerManager listenerManager;
	@NonNull
	protected final OutputDCC sendDCC;
	@Getter(AccessLevel.PROTECTED)
	protected final Map<PendingRecieveFileTransfer, CountDownLatch> pendingReceiveTransfers = new HashMap();
	@Getter(AccessLevel.PROTECTED)
	protected final List<PendingSendFileTransfer> pendingSendTransfers = new ArrayList();
	@Getter(AccessLevel.PROTECTED)
	protected final Map<PendingSendFileTransferPassive, CountDownLatch> pendingSendPassiveTransfers = new HashMap();
	protected boolean shuttingDown = false;

	public boolean processDcc(final User user, String request) throws IOException {
		List<String> requestParts = tokenizeDccRequest(request);
		String type = requestParts.get(1);
		if (type.equals("SEND")) {
			//Someone is trying to send a file to us
			//Example: DCC SEND <filename> <ip> <port> <file size> <transferToken> (note File size is optional)
			String rawFilename = requestParts.get(2);
			final String safeFilename = (rawFilename.startsWith("\"") && rawFilename.endsWith("\""))
					? rawFilename.substring(1, rawFilename.length() - 1) : rawFilename;
			InetAddress address = integerToAddress(requestParts.get(3));
			int port = Integer.parseInt(requestParts.get(4));
			long size = Integer.parseInt(Utils.tryGetIndex(requestParts, 5, "-1"));
			String transferToken = Utils.tryGetIndex(requestParts, 6, null);

			if (transferToken != null)
				//Check if this is an acknowledgement of a passive dcc file request
				synchronized (pendingSendPassiveTransfers) {
					Iterator<Map.Entry<PendingSendFileTransferPassive, CountDownLatch>> pendingItr = pendingSendPassiveTransfers.entrySet().iterator();
					while (pendingItr.hasNext()) {
						Map.Entry<PendingSendFileTransferPassive, CountDownLatch> curEntry = pendingItr.next();
						PendingSendFileTransferPassive transfer = curEntry.getKey();
						if (transfer.getUser() == user && transfer.getFilename().equals(rawFilename)
								&& transfer.getTransferToken().equals(transferToken)) {
							transfer.setReceiverAddress(address);
							transfer.setReceiverPort(port);
							log.debug("Passive send file transfer of file {} to user {} accepted at address {} and port {}", 
									transfer.getFilename(), transfer.getUser().getNick(), address, port);
							curEntry.getValue().countDown();
							pendingItr.remove();
							return true;
						}
					}
				}

			//Nope, this is a new transfer
			if (port == 0 || transferToken != null)
				//User is trying to use reverse DCC
				listenerManager.dispatchEvent(new IncomingFileTransferEvent(bot, user, rawFilename, safeFilename, address, port, size, transferToken, true));
			else
				listenerManager.dispatchEvent(new IncomingFileTransferEvent(bot, user, rawFilename, safeFilename, address, port, size, transferToken, false));
		} else if (type.equals("RESUME")) {
			//Someone is trying to resume sending a file to us
			//Example: DCC RESUME <filename> 0 <position> <token>
			//Reply with: DCC ACCEPT <filename> 0 <position> <token>
			String filename = requestParts.get(2);
			int port = Integer.parseInt(requestParts.get(3));
			long position = Integer.parseInt(requestParts.get(4));

			if (port == 0) {
				//Passive transfer
				String transferToken = requestParts.get(5);
				synchronized (pendingSendPassiveTransfers) {
					Iterator<Map.Entry<PendingSendFileTransferPassive, CountDownLatch>> pendingItr = pendingSendPassiveTransfers.entrySet().iterator();
					while (pendingItr.hasNext()) {
						Map.Entry<PendingSendFileTransferPassive, CountDownLatch> curEntry = pendingItr.next();
						PendingSendFileTransferPassive transfer = curEntry.getKey();
						if (transfer.getUser() == user && transfer.getFilename().equals(filename)
								&& transfer.getTransferToken().equals(transferToken)) {
							transfer.setStartPosition(position);
							log.debug("Passive send file transfer of file {} to user {} set to position {}",
									transfer.getFilename(), transfer.getUser().getNick(), position);
							return true;
						}
					}
				}
			} else
				synchronized (pendingSendTransfers) {
					Iterator<PendingSendFileTransfer> pendingItr = pendingSendTransfers.iterator();
					while (pendingItr.hasNext()) {
						PendingSendFileTransfer transfer = pendingItr.next();
						if (transfer.getUser() == user && transfer.getFilename().equals(filename)
								&& transfer.getPort() == port) {
							transfer.setPosition(position);
							log.debug("Send file transfer of file {} to user {} set to position {}",
									transfer.getFilename(), transfer.getUser().getNick(), position);
							return true;
						}
					}
				}

			//Haven't returned yet, received an unknown transfer
			throw new DccException(DccException.Reason.UnknownFileTransferResume, user, "Transfer line: " + request);
		} else if (type.equals("ACCEPT")) {
			//Someone is acknowledging a transfer resume
			//Example: DCC ACCEPT <filename> 0 <position> <token> (if 0 exists then its a passive connection)
			String filename = requestParts.get(2);
			int dataPosition = (requestParts.size() == 5) ? 3 : 4;
			long position = Integer.parseInt(requestParts.get(dataPosition));
			String transferToken = requestParts.get(dataPosition + 1);
			synchronized (pendingReceiveTransfers) {
				Iterator<Map.Entry<PendingRecieveFileTransfer, CountDownLatch>> pendingItr = pendingReceiveTransfers.entrySet().iterator();
				while (pendingItr.hasNext()) {
					Map.Entry<PendingRecieveFileTransfer, CountDownLatch> curEntry = pendingItr.next();
					IncomingFileTransferEvent transferEvent = curEntry.getKey().getEvent();
					if (transferEvent.getUser() == user && transferEvent.getRawFilename().equals(filename)
							&& transferEvent.getTransferToken().equals(transferToken)) {
						curEntry.getKey().setPosition(position);
						log.debug("Receive file transfer of file {} to user {} set to position {}", 
								transferEvent.getRawFilename(), transferEvent.getUser().getNick(), position);
						curEntry.getValue().countDown();
						pendingItr.remove();
						return true;
					}
				}
			}
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
		checkNotNull(event, "Event cannot be null");
		return configuration.getBotFactory().createReceiveChat(bot, event.getUser(), new Socket(event.getChatAddress(), event.getChatPort()));
	}

	public ReceiveFileTransfer acceptFileTransfer(IncomingFileTransferEvent event, File destination) throws IOException {
		checkNotNull(event, "Event cannot be null");
		checkNotNull(destination, "Destination file cannot be null");
		return acceptFileTransfer(event, destination, 0);
	}

	public ReceiveFileTransfer acceptFileTransferResume(IncomingFileTransferEvent event, File destination, long startPosition) throws IOException, InterruptedException, DccException {
		checkNotNull(event, "Event cannot be null");
		checkNotNull(destination, "Destination file cannot be null");
		checkArgument(startPosition >= 0, "Start position %s must be positive", startPosition);
		
		//Add to pending map so we can be notified when the user has accepted
		CountDownLatch countdown = new CountDownLatch(1);
		PendingRecieveFileTransfer pendingTransfer = new PendingRecieveFileTransfer(event);
		synchronized (pendingReceiveTransfers) {
			pendingReceiveTransfers.put(pendingTransfer, countdown);
		}

		//Request resume
		if (event.isReverse())
			sendDCC.filePassiveResumeRequest(event.getUser().getNick(), event.getRawFilename(), startPosition, event.getTransferToken());
		else
			sendDCC.fileResumeRequest(event.getUser().getNick(), event.getRawFilename(), event.getPort(), startPosition);
		if (!countdown.await(configuration.getDccResumeAcceptTimeout(), TimeUnit.MILLISECONDS))
			throw new DccException(DccException.Reason.FileTransferResumeTimeout, event.getUser(), "Event: " + event);
		if (shuttingDown)
			throw new DccException(DccException.Reason.FileTransferResumeCancelled, event.getUser(), "Transfer " + event + " canceled due to bot shutting down");

		//User has accepted resume, begin transfer
		if (pendingTransfer.getPosition() != startPosition)
			log.warn("User is resuming transfer at position {} instead of requested position {} for transfer {}. Defaulting to users position",
					pendingTransfer.getPosition(), startPosition, event);
		return acceptFileTransfer(event, destination, pendingTransfer.getPosition());
	}

	protected ReceiveFileTransfer acceptFileTransfer(IncomingFileTransferEvent event, File destination, long startPosition) throws IOException {
		checkNotNull(event, "Event cannot be null");
		checkNotNull(destination, "Destination file cannot be null");
		checkArgument(startPosition >= 0, "Start position %s must be positive", startPosition);
		
		if (event.isReverse()) {
			ServerSocket serverSocket = createServerSocket(event.getUser());
			sendDCC.filePassiveAccept(event.getUser().getNick(), event.getRawFilename(), serverSocket.getInetAddress(), serverSocket.getLocalPort(), event.getFilesize(), event.getTransferToken());
			Socket userSocket = serverSocket.accept();

			//User is connected, begin transfer
			serverSocket.close();
			return configuration.getBotFactory().createReceiveFileTransfer(bot, userSocket, event.getUser(), destination, startPosition);
		} else {
			Socket userSocket = new Socket(event.getAddress(), event.getPort(), getRealDccAddress(), 0);
			return configuration.getBotFactory().createReceiveFileTransfer(bot, userSocket, event.getUser(), destination, startPosition);
		}
	}

	public SendChat sendChat(User receiver) throws IOException {
		checkNotNull(receiver, "Receiver user cannot be null");
		ServerSocket ss = createServerSocket(receiver);
		ss.setSoTimeout(configuration.getDccAcceptTimeout());

		int serverPort = ss.getLocalPort();
		String ipNum = addressToInteger(ss.getInetAddress());
		receiver.send().ctcpCommand("DCC CHAT chat " + ipNum + " " + serverPort);

		Socket userSocket = ss.accept();
		ss.close();
		return configuration.getBotFactory().createSendChat(bot, receiver, userSocket);
	}

	public SendFileTransfer sendFile(File file, User receiver) throws IOException, DccException, InterruptedException {
		return sendFile(file, receiver, configuration.isDccPassiveRequest());
	}
	
	/**
	 * Send the specified file to the user
	 * @see #sendFileTransfers
	 */
	public SendFileTransfer sendFile(File file, User receiver, boolean passive) throws IOException, DccException, InterruptedException {
		checkNotNull(file, "Source file cannot be null");
		checkNotNull(receiver, "Receiver cannot be null");
		
		//Make the filename safe to send
		String safeFilename = file.getName();
		if (safeFilename.contains(" "))
			if (configuration.isDccFilenameQuotes())
				safeFilename = "\"" + safeFilename + "\"";
			else
				safeFilename = safeFilename.replace(" ", "_");

		if (passive) {
			String transferToken = Integer.toString(tokenRandom.nextInt(20000));
			CountDownLatch countdown = new CountDownLatch(1);
			PendingSendFileTransferPassive pendingPassiveTransfer = new PendingSendFileTransferPassive(receiver, safeFilename, transferToken);
			synchronized (pendingSendTransfers) {
				pendingSendPassiveTransfers.put(pendingPassiveTransfer, countdown);
			}
			sendDCC.filePassiveRequest(receiver.getNick(), safeFilename, getRealDccAddress(), file.length(), transferToken);

			//Wait for user to acknowledge
			if (!countdown.await(configuration.getDccAcceptTimeout(), TimeUnit.MILLISECONDS))
				throw new DccException(DccException.Reason.FileTransferTimeout, receiver, "File: " + file.getAbsolutePath());
			if (shuttingDown)
				throw new DccException(DccException.Reason.FileTransferCancelled, receiver, "Transfer of file " + file.getAbsolutePath() 
						+ " canceled due to bot shutdown");
			Socket transferSocket = new Socket(pendingPassiveTransfer.getReceiverAddress(), pendingPassiveTransfer.getReceiverPort());
			return configuration.getBotFactory().createSendFileTransfer(bot, transferSocket, receiver, file, pendingPassiveTransfer.getStartPosition());
		} else {
			//Try to get the user to connect to us
			final ServerSocket serverSocket = createServerSocket(receiver);
			PendingSendFileTransfer pendingSendFileTransfer = new PendingSendFileTransfer(receiver, safeFilename, serverSocket.getLocalPort());
			synchronized (pendingSendTransfers) {
				pendingSendTransfers.add(pendingSendFileTransfer);
			}
			sendDCC.fileRequest(receiver.getNick(), safeFilename, serverSocket.getInetAddress(), serverSocket.getLocalPort(), file.length());

			//Wait for the user to connect
			Socket userSocket = serverSocket.accept();
			serverSocket.close();
			return configuration.getBotFactory().createSendFileTransfer(bot, userSocket, receiver, file, pendingSendFileTransfer.getPosition());
		}
	}
	
	public InetAddress getRealDccAddress() {
		//Try dccLocalAddress (which tries to default to dccLocalAddress
		InetAddress address = configuration.getDccLocalAddress();
		return (address != null) ? address : bot.getLocalAddress();
	}

	protected ServerSocket createServerSocket(User user) throws IOException, DccException {
		InetAddress address = configuration.getDccLocalAddress();
		if(address == null)
			//Default to bots address
			address = bot.getLocalAddress();
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
				throw new DccException(DccException.Reason.DccPortsInUse, user, "Ports " + configuration.getDccPorts() + " are in use.");
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
		shuttingDown = true;
		log.info("Terminating all transfers waiting to be accepted");
		for (CountDownLatch curCountdown : pendingReceiveTransfers.values())
			curCountdown.countDown();
		for (CountDownLatch curCountdown : pendingSendPassiveTransfers.values())
			curCountdown.countDown();
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
		protected final IncomingFileTransferEvent event;
		protected long position;
	}

	@Data
	protected static class PendingSendFileTransfer {
		protected final User user;
		protected final String filename;
		protected final int port;
		protected long position = 0;
	}

	@Data
	protected static class PendingSendFileTransferPassive {
		protected final User user;
		protected final String filename;
		protected final String transferToken;
		protected long startPosition = 0;
		protected InetAddress receiverAddress;
		protected int receiverPort;
	}
}
