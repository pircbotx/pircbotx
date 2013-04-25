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
import java.net.InetAddress;
import java.net.Socket;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class ReceiveChat extends SendChat {
	protected final InetAddress address;
	protected final int port;
	protected boolean accepted;

	public void accept() throws IOException {
		init(new Socket(address, port));
		accepted = true;
	}

	protected void failIfUnaccepted() {
		if (!accepted)
			throw new RuntimeException("Must accept the chat request first");
	}

	@Override
	public String readLine() throws IOException {
		failIfUnaccepted();
		return super.readLine();
	}

	@Override
	public void sendLine(String line) throws IOException {
		failIfUnaccepted();
		super.sendLine(line);
	}

	@Override
	public BufferedReader getBufferedReader() {
		failIfUnaccepted();
		return super.getBufferedReader();
	}

	@Override
	public BufferedWriter getBufferedWriter() {
		failIfUnaccepted();
		return super.getBufferedWriter();
	}

	@Override
	public Socket getSocket() {
		failIfUnaccepted();
		return super.getSocket();
	}
}
