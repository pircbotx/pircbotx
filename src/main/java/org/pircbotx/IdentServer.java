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
package org.pircbotx;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
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
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple IdentServer (also know as "The Identification Protocol"). An ident
 * server provides a means to determine the identity of a user of a particular
 * TCP connection.
 * <p>
 * Most IRC servers attempt to contact the ident server on connecting hosts in
 * order to determine the user's identity. A few IRC servers will not allow you
 * to connect unless this information is provided.
 * <p>
 * So when a PircBotX is run on a machine that does not run an ident server, it
 * may be necessary to provide a "faked" response by starting up its own ident
 * server and sending out apparently correct responses.
 *
 * @since PircBot 0.9c
 */
@Slf4j
public class IdentServer implements Closeable, Runnable {
	protected static final int DEFAULT_PORT = 113;
	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	protected static IdentServer server;
	protected static final Object INSTANCE_CREATE_LOCK = new Object();
	protected final InetAddress localAddress;
	protected final Charset encoding;
	protected final ServerSocket serverSocket;
	protected final List<IdentEntry> identEntries = new ArrayList<IdentEntry>();
	protected Thread runningThread;
	protected int port;

	/**
	 * Start the ident server with the systems default charset.
	 *
	 * @see Charset#defaultCharset()
	 */
	public static void startServer() {
		startServer(Charset.defaultCharset(), null);
	}

	/**
	 * Start the ident server with the specified charset.
	 *
	 * @param encoding The encoding to use for connections
	 */
	@Synchronized("INSTANCE_CREATE_LOCK")
	public static void startServer(Charset encoding, InetAddress localAddress) {
		startServer(encoding, localAddress, DEFAULT_PORT);
	}

	@Synchronized("INSTANCE_CREATE_LOCK")
	protected static void startServer(Charset encoding, InetAddress localAddress, int port) {
		if (server != null)
			throw new RuntimeException("Already created an IdentServer instance");
		server = new IdentServer(encoding, localAddress, port);
		server.start();
	}

	/**
	 * Stop the server and clear pending ident responses.
	 *
	 * @throws IOException
	 */
	@Synchronized("INSTANCE_CREATE_LOCK")
	public static void stopServer() throws IOException {
		if (server == null)
			throw new RuntimeException("Never created an IdentServer");
		server.doClose();
		server = null;
	}

	/**
	 * Create an ident server on port 113 with the specified encoding
	 *
	 * @param encoding Encoding to use for sockets
	 */
	protected IdentServer(Charset encoding, InetAddress localAddress, int port) {
		try {
			this.encoding = encoding;
			this.localAddress = localAddress;
			this.serverSocket = new ServerSocket(port, 50, localAddress);
			this.port = port;
		} catch (Exception e) {
			throw new RuntimeException("Could not create server socket for IdentServer on " + localAddress.toString() + ", port " + port, e);
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
		log.info("IdentServer running on port " + serverSocket.getLocalPort());
		while (!serverSocket.isClosed()) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
				OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), encoding);
				InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

				//Read first line and process
				String line = reader.readLine();
				String response = handleNextConnection(remoteAddress, line);
				if (response != null) {
					writer.write(response);
					writer.flush();
				}
			} catch (Exception e) {
				if (serverSocket.isClosed()) {
					log.debug("Server socket closed, exiting connection loop");
					return;
				} else
					//This is not from the server socket closing
					throw new RuntimeException("Exception encountered when opening user socket", e);
			} finally {
				//Close user socket
				try {
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					throw new RuntimeException("Exception encountered when closing user socket", e);
				}
			}
		}

		//Done with connection loop, can safely close the socket now
		if (!serverSocket.isClosed())
			try {
				close();
			} catch (IOException e) {
				log.error("Cannot close IdentServer socket", e);
			}
	}

	/**
	 * Wait for and process the next connection.
	 *
	 * @throws IOException If any error occurred during reading or writing
	 */
	public String handleNextConnection(InetSocketAddress remoteAddress, String line) throws IOException {
		//Get and validate Ident from server
		if (StringUtils.isBlank(line)) {
			log.error("Ignoring connection from " + remoteAddress + ", received blank line");
			return null;
		}
		String[] parsedLine = StringUtils.split(line, ", ");
		if (parsedLine.length != 2) {
			log.error("Ignoring connection from " + remoteAddress + ", recieved unknown line: " + line);
			return null;
		}
		int localPort = Utils.tryParseInt(parsedLine[0], -1);
		int remotePort = Utils.tryParseInt(parsedLine[1], -1);
		if (localPort == -1 || remotePort == -1) {
			log.error("Ignoring connection from " + remoteAddress + ", recieved unparsable line: " + line);
			return null;
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
			String response = localPort + ", " + remotePort + " : ERROR : NO-USER";
			log.error("Unknown ident " + line + " from " + remoteAddress + ", responding with: " + response);
			return response;
		}

		//Respond to correct ident entry with login
		String response = line + " : USERID : UNIX : " + identEntry.getLogin();
		log.debug("Responded to ident request from " + remoteAddress + " with: " + response);
		return response;
	}

	protected void addIdentEntry(InetAddress remoteAddress, int remotePort, int localPort, String login) {
		synchronized (identEntries) {
			log.debug("Added ident entry for address " + remoteAddress + " on port " + remotePort + " for local port " + localPort + " for " + login);
			identEntries.add(new IdentEntry(remoteAddress, remotePort, localPort, login));
		}
	}

	protected void removeIdentEntry(InetAddress remoteAddress, int remotePort, int localPort, String login) {
		synchronized (identEntries) {
			log.debug("Removed ident entry for address " + remoteAddress + " on port " + remotePort + " for local port " + localPort + " for " + login);
			for (Iterator<IdentEntry> itr = identEntries.iterator(); itr.hasNext();) {
				IdentEntry curEntry = itr.next();
				if (curEntry.getRemoteAddress().equals(remoteAddress) && curEntry.getRemotePort() == remotePort
						&& curEntry.getLocalPort() == localPort && curEntry.getLogin().equals(login))
					itr.remove();
			}
		}
	}

	protected int getPort() {
		Preconditions.checkState(!serverSocket.isClosed(), "Server socket is not open");
		return serverSocket.getLocalPort();
	}

	/**
	 * Calls {@link #stopServer() }
	 * @throws IOException 
	 */
	public void close() throws IOException {
		stopServer();
	}
	
	/**
	 * Close the server socket and clear pending ident responses.
	 *
	 * @throws IOException If an error occured during closing
	 */
	protected void doClose() throws IOException {
		serverSocket.close();
		identEntries.clear();
		log.info("Closed ident server on port " + port + "/" + serverSocket.getLocalPort());
	}

	@Data
	protected static class IdentEntry {
		protected final InetAddress remoteAddress;
		protected final int remotePort;
		protected final int localPort;
		protected final String login;
	}
}
