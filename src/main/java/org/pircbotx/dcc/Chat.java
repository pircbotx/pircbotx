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

import static com.google.common.base.Preconditions.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.User;
import org.pircbotx.exception.DccException;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Generic DCC chat handling class that represents an active dcc chat.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Slf4j
public class Chat {
	protected static final Marker INPUT_CHAT_MARKER = MarkerFactory.getMarker("pircbotx.dccChat.input");
	protected static final Marker OUTPUT_CHAT_MARKER = MarkerFactory.getMarker("pircbotx.dccChat.output");
	@Getter
	protected User user;
	@Getter
	protected BufferedReader bufferedReader;
	@Getter
	protected BufferedWriter bufferedWriter;
	@Getter
	protected Socket socket;
	@Getter
	protected boolean finished;

	protected Chat(User user, Socket socket, Charset encoding) throws IOException {
		checkNotNull(user, "User cannot be null");
		checkNotNull(socket, "Socket cannot be null");
		checkNotNull(encoding, "Encoding cannot be null");
		this.user = user;
		this.socket = socket;
		this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
		this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
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
		if (finished)
			throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
		String line = bufferedReader.readLine();
		log.info(INPUT_CHAT_MARKER, "<<<" + line);
		return line;
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
		checkNotNull(line, "Line cannot be null");
		if (finished)
			throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
		synchronized (bufferedWriter) {
			log.info(OUTPUT_CHAT_MARKER, ">>>" + line);
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
		if (finished)
			throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
		finished = true;
		socket.close();
	}
}
