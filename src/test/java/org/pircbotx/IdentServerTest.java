/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterTest;

/**
 *
 * @author Leon
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
	public void setup() {
		IdentServer.startServer();
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
		Socket socket = new Socket(InetAddress.getLoopbackAddress(), IdentServer.PORT, actualLocalAddress, 42121);
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
