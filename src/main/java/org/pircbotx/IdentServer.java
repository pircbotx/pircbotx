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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple IdentServer (also know as "The Identification Protocol").
 * An ident server provides a means to determine the identity of a
 * user of a particular TCP connection.
 *  <p>
 * Most IRC servers attempt to contact the ident server on connecting
 * hosts in order to determine the user's identity.  A few IRC servers
 * will not allow you to connect unless this information is provided.
 *  <p>
 * So when a PircBotX is run on a machine that does not run an ident server,
 * it may be necessary to provide a "faked" response by starting up its
 * own ident server and sending out apparently correct responses.
 *  <p>
 * An instance of this class can be used to start up an ident server
 * only if it is possible to do so.  Reasons for not being able to do
 * so are if there is already an ident server running on port 113, or
 * if you are running as an unprivileged user who is unable to create
 * a server socket on that port number.
 *
 * @since   PircBot 0.9c
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by Leon Blakey <lord.quackstar at gmail.com> in <a href="http://pircbotx.googlecode.com">PircBotX</a>
 */
public class IdentServer extends Thread {
	private PircBotX bot;
	private String login;
	private ServerSocket ss = null;

	/**
	 * Constructs and starts an instance of an IdentServer that will
	 * respond to a client with the provided login.  Rather than calling
	 * this constructor explicitly from your code, it is recommended that
	 * you use the startIdentServer method in the PircBotX class.
	 *  <p>
	 * The ident server will wait for up to 60 seconds before shutting
	 * down.  Otherwise, it will shut down as soon as it has responded
	 * to an ident request.
	 *
	 * @param bot The PircBotX instance that will be used to log to.
	 * @param login The login that the ident server will respond with.
	 */
	IdentServer(PircBotX bot, String login) {
		this.bot = bot;
		this.login = login;

		try {
			ss = new ServerSocket(113);
			ss.setSoTimeout(60000);
		} catch (Exception e) {
			bot.log("*** Could not start the ident server on port 113.");
			return;
		}

		bot.log("*** Ident server running on port 113 for the next 60 seconds...");
		this.setName(this.getClass() + "-Thread");
		this.start();
	}

	/**
	 * Waits for a client to connect to the ident server before making an
	 * appropriate response.  Note that this method is started by the class
	 * constructor.
	 */
	public void run() {
		try {
			Socket socket = ss.accept();
			socket.setSoTimeout(60000);

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			String line = reader.readLine();
			if (line != null) {
				bot.log("*** Ident request received: " + line);
				line = line + " : USERID : UNIX : " + login;
				writer.write(line + "\r\n");
				writer.flush();
				bot.log("*** Ident reply sent: " + line);
				writer.close();
			}
		} catch (Exception e) {
			// We're not really concerned with what went wrong, are we?
		}

		try {
			ss.close();
		} catch (Exception e) {
			// Doesn't really matter...
		}

		bot.log("*** The Ident server has been shut down.");
	}
}
