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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class IdentServerTest {
	protected IdentServer identServer;
	protected final String entryUserName = "WorkingIrcUser";
	protected final InetAddress entryLocalAddress;

	public IdentServerTest() throws UnknownHostException {
		entryLocalAddress = InetAddress.getByName("127.23.32.32");
	}

	@BeforeMethod
	public void setup() throws UnknownHostException {
		//Set local address to get around build server restrictions
		IdentServer.startServer(Charset.defaultCharset(), InetAddress.getLoopbackAddress(), 0);
		identServer = IdentServer.getServer();
	}

	@AfterMethod
	public void cleanup() throws IOException {
		IdentServer.stopServer();
		identServer = null;
	}

	@Test
	public void IdentSuccessTest() throws IOException {
		String response = executeIdentServer(6667, entryLocalAddress, 55321, 6667, 55321);
		assertEquals(response, 55321 + ", " + 6667 + " : USERID : UNIX : " + entryUserName);
	}

	@Test
	public void IdentFailInvalidAddressTest() throws IOException, UnknownHostException {
		String response = executeIdentServer(6667, InetAddress.getByName("127.55.55.55"), 55321, 6667, 55321);
		assertEquals(response, 55321 + ", " + 6667 + " : ERROR : NO-USER");
	}

	@Test
	public void IdentFailInvalidRemotePortTest() throws IOException, UnknownHostException {
		String response = executeIdentServer(6667, entryLocalAddress, 55321, 9999, 55321);
		assertEquals(response, 55321 + ", " + 9999 + " : ERROR : NO-USER");
	}

	@Test
	public void IdentFailInvalidLocalPortTest() throws IOException, UnknownHostException {
		String response = executeIdentServer(6667, entryLocalAddress, 55321, 6667, 65535);
		assertEquals(response, 65535 + ", " + 6667 + " : ERROR : NO-USER");
	}

	public String executeIdentServer(int entryRemotePort, InetAddress actualLocalAddress, int entryLocalPort, int sentRemotePort, int sentLocalPort) throws IOException {
		//Pretend there's a bot connected to localhost
		identServer.addIdentEntry(entryLocalAddress, entryRemotePort, entryLocalPort, entryUserName);

		//Send an ident reqeust
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), IdentServer.getServer().getPort(), actualLocalAddress, 42121);
		OutputStreamWriter socketWriter = new OutputStreamWriter(socket.getOutputStream());
		socketWriter.write(sentLocalPort + ", " + sentRemotePort + "\r\n");
		socketWriter.flush();

		//Just grab the response
		BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = socketReader.readLine();
		log.info("Line from server: " + line);
		assertNull(socketReader.readLine(), "Server sent more than 1 line");
		socket.close();
		return line;
	}
}
