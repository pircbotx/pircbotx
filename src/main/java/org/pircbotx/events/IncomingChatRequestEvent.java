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
package org.pircbotx.events;

import org.pircbotx.DccChat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;

/**
 * This method will be called whenever a DCC Chat request is received.
 * This means that a client has requested to chat to us directly rather
 * than via the IRC server. This is useful for sending many lines of text
 * to and from the bot without having to worry about flooding the server
 * or any operators of the server being able to "spy" on what is being
 * said. This abstract implementation performs no action, which means
 * that all DCC CHAT requests will be ignored by default.
 *  <p>
 * If you wish to accept the connection, then you may override this
 * method and call the accept() method on the DccChat object, which
 * connects to the sender of the chat request and allows lines to be
 * sent to and from the bot.
 *  <p>
 * Your bot must be able to connect directly to the user that sent the
 * request.
 *  <p>
 * Example:
 * <pre>
 *     DccChat chat = event.getChat();
 *     try {
 *         // Accept all chat, whoever it's from.
 *         chat.accept();
 *         chat.sendLine("Hello");
 *         String response = chat.readLine();
 *         chat.close();
 *     }
 *     catch (IOException e) {}
 * </pre>
 *
 * Each time this method is called, it is called from within a new Thread
 * so that multiple DCC CHAT sessions can run concurrently.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see DccChat
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IncomingChatRequestEvent extends BaseEvent {
	protected final DccChat chat;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param chat A DccChat object that represents the incoming chat request.
	 */
	public <T extends PircBotX> IncomingChatRequestEvent(T bot, DccChat chat) {
		super(bot);
		this.chat = chat;
	}
}
