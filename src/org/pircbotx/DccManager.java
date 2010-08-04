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

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is used to process DCC events from the server.
 *
 * @since   1.2.0
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class DccManager {
	/**
	 * Constructs a DccManager to look after all DCC SEND and CHAT events.
	 *
	 * @param bot The PircBotX whose DCC events this class will handle.
	 */
	DccManager(PircBotX bot) {
		_bot = bot;
	}

	/**
	 * Processes a DCC request.
	 *
	 * @return True if the type of request was handled successfully.
	 */
	boolean processRequest(String nick, String login, String hostname, String request) {
		StringTokenizer tokenizer = new StringTokenizer(request);
		tokenizer.nextToken();
		String type = tokenizer.nextToken();
		String filename = tokenizer.nextToken();

		if (type.equals("SEND")) {
			long address = Long.parseLong(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());
			long size = -1;
			try {
				size = Long.parseLong(tokenizer.nextToken());
			} catch (Exception e) {
				// Stick with the old value.
			}

			DccFileTransfer transfer = new DccFileTransfer(_bot, this, nick, login, hostname, type, filename, address, port, size);
			_bot.onIncomingFileTransfer(transfer);

		} else if (type.equals("RESUME")) {
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = Long.parseLong(tokenizer.nextToken());

			DccFileTransfer transfer = null;
			synchronized (_awaitingResume) {
				for (int i = 0; i < _awaitingResume.size(); i++) {
					transfer = (DccFileTransfer) _awaitingResume.elementAt(i);
					if (transfer.getNick().equals(nick) && transfer.getPort() == port) {
						_awaitingResume.removeElementAt(i);
						break;
					}
				}
			}

			if (transfer != null) {
				transfer.setProgress(progress);
				_bot.sendCTCPCommand(nick, "DCC ACCEPT file.ext " + port + " " + progress);
			}

		} else if (type.equals("ACCEPT")) {
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = Long.parseLong(tokenizer.nextToken());

			DccFileTransfer transfer = null;
			synchronized (_awaitingResume) {
				for (int i = 0; i < _awaitingResume.size(); i++) {
					transfer = (DccFileTransfer) _awaitingResume.elementAt(i);
					if (transfer.getNick().equals(nick) && transfer.getPort() == port) {
						_awaitingResume.removeElementAt(i);
						break;
					}
				}
			}

			if (transfer != null)
				transfer.doReceive(transfer.getFile(), true);

		} else if (type.equals("CHAT")) {
			long address = Long.parseLong(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());

			final DccChat chat = new DccChat(_bot, nick, login, hostname, address, port);

			new Thread() {
				public void run() {
					_bot.onIncomingChatRequest(chat);
				}
			}.start();
		} else
			return false;

		return true;
	}

	/**
	 * Add this DccFileTransfer to the list of those awaiting possible
	 * resuming.
	 *
	 * @param transfer the DccFileTransfer that may be resumed.
	 */
	void addAwaitingResume(DccFileTransfer transfer) {
		synchronized (_awaitingResume) {
			_awaitingResume.addElement(transfer);
		}
	}

	/**
	 * Remove this transfer from the list of those awaiting resuming.
	 */
	void removeAwaitingResume(DccFileTransfer transfer) {
		_awaitingResume.removeElement(transfer);
	}
	private PircBotX _bot;
	private Vector _awaitingResume = new Vector();
}
