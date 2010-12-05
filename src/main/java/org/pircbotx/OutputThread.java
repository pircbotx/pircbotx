/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx;

import java.io.BufferedWriter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Thread which is responsible for sending messages to the IRC server.
 * Messages are obtained from the outgoing message queue and sent
 * immediately if possible.  If there is a flood of messages, then to
 * avoid getting kicked from a channel, we put a small delay between
 * each one.
 *
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class OutputThread implements Runnable {
	protected PircBotX _bot = null;
	protected LinkedBlockingQueue<String> _queue = new LinkedBlockingQueue<String>();
	protected final BufferedWriter _bwriter;
	public static ThreadFactory threadFactory = new ThreadFactory() {
		public Thread newThread(Runnable r, PircBotX bot) {
			return new Thread(r, bot.getServer() + "-" + bot.getNick() + "-OutputThread");
		}
	};

	/**
	 * Constructs an OutputThread for the underlying PircBotX.  All messages
	 * sent to the IRC server are sent by this OutputThread to avoid hammering
	 * the server.  Messages are sent immediately if possible.  If there are
	 * multiple messages queued, then there is a delay imposed.
	 *
	 * @param bot The underlying PircBotX instance.
	 */
	public OutputThread(PircBotX bot, BufferedWriter bwriter) {
		_bot = bot;
		_bwriter = bwriter;
	}

	/**
	 * A static method to write a line to a BufferedOutputStream and then pass
	 * the line to the log method of the supplied PircBotX instance.
	 *
	 * @param bot The underlying PircBotX instance.
	 * @param out The BufferedOutputStream to write to.
	 * @param line The line to be written. "\r\n" is appended to the end.
	 * @param encoding The charset to use when encoing this string into a
	 *                 byte array.
	 */
	public void sendRawLineNow(String line) {
		if (line.length() > _bot.getMaxLineLength() - 2)
			line = line.substring(0, _bot.getMaxLineLength() - 2);
		synchronized (_bwriter) {
			try {
				_bwriter.write(line + "\r\n");
				_bwriter.flush();
				_bot.log(">>>" + line);
			} catch (Exception e) {
				// Silent response - just lose the line.
			}
		}
	}

	public void send(String message) {
		_queue.add(message);
	}

	public int getQueueSize() {
		return _queue.size();
	}

	/**
	 * This method starts the Thread consuming from the outgoing message
	 * Queue and sending lines to the server.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				//Small delay to prevent spamming of the channel
				Thread.sleep(_bot.getMessageDelay());

				String line = _queue.take();
				if (line != null)
					_bot.sendRawLine(line);
			}
		} catch (InterruptedException e) {
			// Just let the method return naturally...
		}
	}
}
