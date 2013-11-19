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
package org.pircbotx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple IdentServer (also know as "The Identification Protocol").
 * An ident server provides a means to determine the identity of a
 * user of a particular TCP connection.
 * <p>
 * Most IRC servers attempt to contact the ident server on connecting
 * hosts in order to determine the user's identity. A few IRC servers
 * will not allow you to connect unless this information is provided.
 * <p>
 * So when a PircBotX is run on a machine that does not run an ident server,
 * it may be necessary to provide a "faked" response by starting up its
 * own ident server and sending out apparently correct responses.
 *
 * @since PircBot 0.9c
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Slf4j
public class IdentServer implements Closeable, Runnable {
	protected static final int PORT = 113;
	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	protected static IdentServer server;
	protected static final Object INSTANCE_CREATE_LOCK = new Object();
	protected final Charset encoding;
	protected final ServerSocket serverSocket;
	protected final List<IdentEntry> identEntries = new ArrayList<IdentEntry>();
	protected Thread runningThread;
	
	/**
	 * Start the ident server with the systems default charset.
	 * @see Charset#defaultCharset()
	 */
	public static void startServer() {
		startServer(Charset.defaultCharset());
	}
	
	/**
	 * Start the ident server with the specified charset.
	 * @param encoding The encoding to use for connections
	 */
	@Synchronized("INSTANCE_CREATE_LOCK")
	public static void startServer(Charset encoding) {
		if (server != null)
			throw new RuntimeException("Already created an IdentServer instance");
		server = new IdentServer(encoding);
		server.start();
	}

	/**
	 * Stop the server and clear pending ident responses.
	 * @throws IOException 
	 */
	@Synchronized("INSTANCE_CREATE_LOCK")
	public static void stopServer() throws IOException {
		if (server == null)
			throw new RuntimeException("Never created an IdentServer");
		server.close();
		server = null;
	}

	/**
	 * Create an ident server on port 113 with the specified encoding
	 * @param encoding Encoding to use for sockets
	 */
	protected IdentServer(Charset encoding) {
		try {
			this.encoding = encoding;
			this.serverSocket = new ServerSocket(PORT);
		} catch (Exception e) {
			throw new RuntimeException("Could not create server socket for IdentServer on port " + PORT, e);
		}
	}

	/**
	 * Start the ident server in a new thread.
	 */
	public void start() {
		runningThread = new Thread(this);
		runningThread.setName("IdentServer");
		runningThread.start();
	}

	/**
	 * Waits for a client to connect to the ident server before making an
	 * appropriate response. 
	 */
	public void run() {
		try {
			log.info("IdentServer running on port " + PORT);
			while (true)
				handleNextConnection();
		} catch (Exception e) {
			log.error("Exception encountered when running IdentServer", e);
		} finally {
			try {
				close();
			} catch (IOException e) {
				log.error("Cannot close IdentServer socket", e);
			}
		}
	}

	/**
	 * Wait for and process the next connection.
	 * @throws IOException If any error occurred during reading or writing
	 */
	public void handleNextConnection() throws IOException {
		//Grab next connectin
		Socket socket = serverSocket.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
		InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

		//Get and validate Ident from server
		String line = reader.readLine();
		if (StringUtils.isBlank(line)) {
			log.error("Ignoring connection from " + remoteAddress + ", received blank line");
			socket.close();
			return;
		}
		String[] parsedLine = StringUtils.split(line, ", ");
		if (parsedLine.length != 2) {
			log.error("Ignoring connection from " + remoteAddress + ", recieved unknown line: " + line);
			socket.close();
			return;
		}
		int localPort = Utils.tryParseInt(parsedLine[0], -1);
		int remotePort = Utils.tryParseInt(parsedLine[1], -1);
		if (localPort == -1 || remotePort == -1) {
			log.error("Ignoring connection from " + remoteAddress + ", recieved unparsable line: " + line);
			socket.close();
			return;
		}

		//Grab the IdentEntry for this ident
		log.debug("Received ident request from " + remoteAddress + ": " + line);
		IdentEntry identEntry = null;
		synchronized (identEntries) {
			for (IdentEntry curIdentEntry : identEntries)
				if (curIdentEntry.getRemoteAddress().equals(remoteAddress.getAddress())
						&& curIdentEntry.getRemotePort() == remotePort
						&& curIdentEntry.getLocalPort() == localPort) {
					identEntry = curIdentEntry;
					break;
				}
		}
		if (identEntry == null) {
			String response = localPort + ", " + remotePort + " ERROR : NO-USER";
			log.error("Unknown ident " + line + " from " + remoteAddress + ", responding with: " + response);
			writer.write(line + "\r\n");
			writer.flush();
			socket.close();
			return;
		}
		
		//Respond to correct ident entry with login
		String response = line + " : USERID : UNIX : " + identEntry.getLogin();
		log.debug("Responded to ident request from " + remoteAddress + " with: " + response);
		writer.write(line + "\r\n");
		writer.flush();
		socket.close();
	}

	protected void addIdentEntry(InetAddress remoteAddress, int remotePort, int localPort, String login) {
		synchronized (identEntries) {
			identEntries.add(new IdentEntry(remoteAddress, remotePort, localPort, login));
		}
	}

	/**
	 * Close the server socket and clear pending ident responses.
	 * @throws IOException If an error occured during closing
	 */
	@Synchronized("INSTANCE_CREATE_LOCK")
	public void close() throws IOException {
		serverSocket.close();
		identEntries.clear();
		log.info("Closed ident server on port " + PORT);
	}

	@Data
	protected static class IdentEntry {
		protected final InetAddress remoteAddress;
		protected final int remotePort;
		protected final int localPort;
		protected final String login;
	}
}
