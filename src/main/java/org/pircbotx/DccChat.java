/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This class is used to allow the bot to interact with a DCC Chat session.
 *
 * @since   0.9c
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
public class DccChat {
	private PircBotX _bot;
	private User _source;
	private BufferedReader _reader;
	private BufferedWriter _writer;
	private Socket _socket;
	private boolean _acceptable;
	private long _address = 0;
	private int _port = 0;

	/**
	 * This constructor is used when we are accepting a DCC CHAT request
	 * from somebody. It attempts to connect to the client that issued the
	 * request.
	 *
	 * @param bot An instance of the underlying PircBotX.
	 * @param sourceNick The nick of the sender.
	 * @param address The address to connect to.
	 * @param port The port number to connect to.
	 *
	 * @throws IOException If the connection cannot be made.
	 */
	DccChat(PircBotX bot, User source, long address, int port) {
		_bot = bot;
		_address = address;
		_port = port;
		_source = source;
		_acceptable = true;
	}

	/**
	 * This constructor is used after we have issued a DCC CHAT request to
	 * somebody. If the client accepts the chat request, then the socket we
	 * obtain is passed to this constructor.
	 *
	 * @param bot An instance of the underlying PircBotX.
	 * @param sourceNick The nick of the user we are sending the request to.
	 * @param socket The socket which will be used for the DCC CHAT session.
	 *
	 * @throws IOException If the socket cannot be read from.
	 */
	DccChat(PircBotX bot, User source, Socket socket) throws IOException {
		_bot = bot;
		_source = source;
		_socket = socket;
		_reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		_writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
		_acceptable = false;
	}

	/**
	 * Accept this DccChat connection.
	 *
	 * @since 1.2.0
	 *
	 */
	public synchronized void accept() throws IOException {
		if (_acceptable) {
			_acceptable = false;
			int[] ip = _bot.longToIp(_address);
			String ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
			_socket = new Socket(ipStr, _port);
			_reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
		}
	}

	/**
	 * Reads the next line of text from the client at the other end of our DCC Chat
	 * connection.  This method blocks until something can be returned.
	 * If the connection has closed, null is returned.
	 *
	 * @return The next line of text from the client.  Returns null if the
	 *          connection has closed normally.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public String readLine() throws IOException {
		if (_acceptable)
			throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
		return _reader.readLine();
	}

	/**
	 * Sends a line of text to the client at the other end of our DCC Chat
	 * connection.
	 *
	 * @param line The line of text to be sent.  This should not include
	 *             linefeed characters.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void sendLine(String line) throws IOException {
		if (_acceptable)
			throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
		// No need for synchronization here really...
		_writer.write(line + "\r\n");
		_writer.flush();
	}

	/**
	 * Closes the DCC Chat connection.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void close() throws IOException {
		if (_acceptable)
			throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
		_socket.close();
	}

	public User getSource() {
		return _source;
	}

	/**
	 * Returns the BufferedReader used by this DCC Chat.
	 *
	 * @return the BufferedReader used by this DCC Chat.
	 */
	public BufferedReader getBufferedReader() {
		return _reader;
	}

	/**
	 * Returns the BufferedReader used by this DCC Chat.
	 *
	 * @return the BufferedReader used by this DCC Chat.
	 */
	public BufferedWriter getBufferedWriter() {
		return _writer;
	}

	/**
	 * Returns the raw Socket used by this DCC Chat.
	 *
	 * @return the raw Socket used by this DCC Chat.
	 */
	public Socket getSocket() {
		return _socket;
	}

	/**
	 * Returns the address of the sender as a long.
	 *
	 * @return the address of the sender as a long.
	 */
	public long getNumericalAddress() {
		return _address;
	}
}
