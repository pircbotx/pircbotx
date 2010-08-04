/*
 * Copyright (C) 2009-2010 Leon Blakey
 *
 * Quackedbot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quackedbot  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.io.BufferedReader;
import java.io.InterruptedIOException;
import java.net.Socket;


/**
 * A Thread which reads lines from the IRC server.  It then
 * passes these lines to the PircBotX without changing them.
 * This running Thread also detects disconnection from the server
 * and is thus used by the OutputThread to send lines to the server.
 *
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class InputThread implements Runnable {
	private final PircBotX _bot;
	private Socket _socket;
	private BufferedReader _breader = null;
	private boolean _isConnected = true;
	public static final int MAX_LINE_LENGTH = 512;
	public static ThreadFactory threadFactory = new ThreadFactory() {
		public Thread newThread(Runnable r, PircBotX bot) {
			return new Thread(r, bot.getServer() + "-" + bot.getNick() + "-InputThread");
		}
	};

	/**
	 * The InputThread reads lines from the IRC server and allows the
	 * PircBotX to handle them.
	 *
	 * @param bot An instance of the underlying PircBotX.
	 * @param breader The BufferedReader that reads lines from the server.
	 * @param bwriter The BufferedWriter that sends lines to the server.
	 */
	InputThread(PircBotX bot, Socket socket, BufferedReader breader) {
		_bot = bot;
		_socket = socket;
		_breader = breader;
	}

	/**
	 * Returns true if this InputThread is connected to an IRC server.
	 * The result of this method should only act as a rough guide,
	 * as the result may not be valid by the time you act upon it.
	 *
	 * @return True if still connected.
	 */
	boolean isConnected() {
		return _isConnected;
	}

	/**
	 * Called to start this Thread reading lines from the IRC server.
	 * When a line is read, this method calls the handleLine method
	 * in the PircBotX, which may subsequently call an 'onXxx' method
	 * in the PircBotX subclass.  If any subclass of Throwable (i.e.
	 * any Exception or Error) is thrown by your method, then this
	 * method will print the stack trace to the standard output.  It
	 * is probable that the PircBotX may still be functioning normally
	 * after such a problem, but the existence of any uncaught exceptions
	 * in your code is something you should really fix.
	 */
	public void run() {
		while (true)
			try {
				String line = null;
				while ((line = _breader.readLine()) != null)
					_bot.handleLine(line);

				if (line == null)
					// The server must have disconnected us.
					break;
			} catch (InterruptedIOException iioe) {
				// This will happen if we haven't received anything from the server for a while.
				// So we shall send it a ping to check that we are still connected.
				_bot.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
				// Now we go back to listening for stuff from the server...
			} catch (Throwable t) {
				//Either readLine or handleLine encountered an error, but don't care
			}

		//Disconnected at this point
		try {
			_socket.close();
		} catch (Exception e) {
			// Just assume the socket was already closed.
		}

		//Now that the socket is definatly closed, call event and log
		_bot.onDisconnect();
		_bot.log("*** Disconnected.");
	}
}
