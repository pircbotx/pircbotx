/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import lombok.Getter;

/**
 * This class is used to allow the bot to interact with a DCC Chat session.
 *
 * @since   PircBot 0.9c
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
public class DccChat implements Closeable {
    private PircBotX bot;
    @Getter
    private User user;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private boolean acceptable;
    @Getter
    private InetAddress address;
    private int port = 0;

    /**
     * This constructor is used when we are accepting a DCC CHAT request
     * from somebody. It attempts to connect to the client that issued the
     * request.
     *
     * @param bot An instance of the underlying PircBotX.
     * @param source The source user
     * @param address The address to connect to.
     * @param port The port number to connect to.
     *
     * @throws IOException If the connection cannot be made.
     */
    protected DccChat(PircBotX bot, User source, InetAddress address, int port) {
	this.bot = bot;
	this.address = address;
	this.port = port;
	this.user = source;
	acceptable = true;
	bot.getDccManager().addDccChat(this);
    }

    /**
     * This constructor is used after we have issued a DCC CHAT request to
     * somebody. If the client accepts the chat request, then the socket we
     * obtain is passed to this constructor.
     *
     * @param bot An instance of the underlying PircBotX.
     * @param source The user that we are sending the request to
     * @param socket The socket which will be used for the DCC CHAT session.
     *
     * @throws IOException If the socket cannot be read from.
     */
    protected DccChat(PircBotX bot, User source, Socket socket) throws IOException {
	this.bot = bot;
	this.user = source;
	this.socket = socket;
	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	acceptable = false;
	bot.getDccManager().addDccChat(this);
    }

    /**
     * Accept this DccChat connection.
     *
     * @since PircBot 1.2.0
     *
     */
    public synchronized void accept() throws IOException {
	if (!acceptable)
	    throw new IOException("Connection already created");
	acceptable = false;
	socket = new Socket(address, port);
	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
	if (acceptable)
	    throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
	return reader.readLine();
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
	if (acceptable)
	    throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
	// No need for synchronization here really...
	writer.write(line + "\r\n");
	writer.flush();
    }

    /**
     * Closes the DCC Chat connection.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void close() throws IOException {
	if (acceptable)
	    throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
	socket.close();
	bot.getDccManager().removeDccChat(this);
    }

    /**
     * Returns the BufferedReader used by this DCC Chat.
     *
     * @return the BufferedReader used by this DCC Chat.
     */
    public BufferedReader getBufferedReader() {
	return reader;
    }

    /**
     * Returns the BufferedReader used by this DCC Chat.
     *
     * @return the BufferedReader used by this DCC Chat.
     */
    public BufferedWriter getBufferedWriter() {
	return writer;
    }

    /**
     * Returns the raw Socket used by this DCC Chat.
     *
     * @return the raw Socket used by this DCC Chat.
     */
    public Socket getSocket() {
	return socket;
    }
}
