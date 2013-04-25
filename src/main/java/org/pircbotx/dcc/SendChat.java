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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.User;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class SendChat {
	@Getter
	protected User user;
	@Getter
	protected BufferedReader bufferedReader;
	@Getter
	protected BufferedWriter bufferedWriter;
	@Getter
	protected Socket socket;
	protected Boolean inited = false;
	protected final Object initedLock = new Object();

	protected SendChat init(Socket socket) throws IOException {
		if (inited)
			synchronized (initedLock) {
				if (inited)
					throw new RuntimeException("Already inited Chat");
			}
		this.socket = socket;
		this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		return this;
	}

	/**
	 * Reads the next line of text from the client at the other end of our DCC Chat
	 * connection. This method blocks until something can be returned.
	 * If the connection has closed, null is returned.
	 *
	 * @return The next line of text from the client. Returns null if the
	 * connection has closed normally.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public String readLine() throws IOException {
		return bufferedReader.readLine();
	}

	/**
	 * Sends a line of text to the client at the other end of our DCC Chat
	 * connection.
	 *
	 * @param line The line of text to be sent. This should not include
	 * linefeed characters.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void sendLine(String line) throws IOException {
		synchronized (bufferedWriter) {
			bufferedWriter.write(line + "\r\n");
			bufferedWriter.flush();
		}
	}

	/**
	 * Closes the DCC Chat connection.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void close() throws IOException {
		socket.close();
	}
}
