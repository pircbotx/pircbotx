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
import java.io.InterruptedIOException;
import java.net.Socket;

/**
 * A Thread which reads lines from the IRC server.  It then
 * passes these lines to the PircBotX without changing them.
 * This running Thread also detects disconnection from the server
 * and is thus used by the OutputThread to send lines to the server.
 *
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
public class InputThread extends Thread {
    private final PircBotX bot;
    private Socket socket;
    private BufferedReader breader = null;
    private boolean isConnected = true;
    public static final int MAX_LINE_LENGTH = 512;

    /**
     * The InputThread reads lines from the IRC server and allows the
     * PircBotX to handle them.
     *
     * @param bot An instance of the underlying PircBotX.
     * @param socket Socket that represents the connection
     * @param breader The BufferedReader that reads lines from the server.
     */
    protected InputThread(PircBotX bot, Socket socket, BufferedReader breader) {
	this.bot = bot;
	this.socket = socket;
	this.breader = breader;
    }

    /**
     * Returns true if this InputThread is connected to an IRC server.
     * The result of this method should only act as a rough guide,
     * as the result may not be valid by the time you act upon it.
     *
     * @return True if still connected.
     */
    boolean isConnected() {
	return isConnected;
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
    @Override
    public void run() {
	while (true) {
	    //Get line from the server
	    String line = null;
	    try {
		line = breader.readLine();
	    } catch (InterruptedIOException iioe) {
		// This will happen if we haven't received anything from the server for a while.
		// So we shall send it a ping to check that we are still connected.
		bot.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
		// Now we go back to listening for stuff from the server...
		continue;
	    } catch (Exception e) {
		//Something is wrong. Assume its bad and begin disconnect
		bot.logException(e);
		line = null;
	    }

	    //End the loop if the line is null
	    if (line == null)
		break;

	    //Start acting the line
	    try {
		bot.handleLine(line);
	    } catch (Exception e) {
		//Exception in client code. Just log and continue
		bot.logException(e);
	    }
	}

	//Disconnected at this point, close the socket
	try {
	    socket.close();
	} catch (Exception e) {
	    //There's not much we can do here besides log that we couldn't properly close
	    bot.logException(e);
	}

	//Now that the socket is definatly closed call event, log, and kill the OutputThread
	isConnected = false;
	bot.shutdown();
    }
}
