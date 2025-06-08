/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.events;

import java.io.IOException;
import java.net.InetAddress;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.types.GenericDCCEvent;

/**
 * This event will be dispatched whenever a DCC Chat request is received. This
 * means that a client has requested to chat to us directly rather than via the
 * IRC server. This is useful for sending many lines of text to and from the bot
 * without having to worry about flooding the server or any operators of the
 * server being able to "spy" on what is being said. By default there are no
 * {@link Listener} for this event, which means that all DCC CHAT requests will
 * be ignored by default.
 * <p>
 * If you wish to accept the connection, then you listen for this event and call
 * the {@link #receiveChat() } method, which connects to the sender of the chat
 * request and allows lines to be sent to and from the bot.
 * <p>
 * Your bot must be able to connect directly to the user that sent the request.
 * <p>
 * Example:
 * <pre>
 *     // Accept all chat, whoever it's from.
 *     ReceiveChat chat = event.accept();
 *     chat.sendLine("Hello");
 *     String response = chat.readLine();
 *     chat.close();
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IncomingChatRequestEvent extends Event implements GenericDCCEvent {
	@Getter(onMethod = @__({
		@Override,
		@Nullable}))
	protected final User user;
	@Getter(onMethod = @__(
			@Override))
	protected final UserHostmask userHostmask;
	@Getter(onMethod = @__(
			@Override))
	protected final InetAddress address;
	@Getter(onMethod = @__(
			@Override))
	protected final int port;
	@Getter(onMethod = @__(
			@Override))
	protected final String token;
	@Getter(onMethod = @__(
			@Override))
	protected final boolean passive;

	public IncomingChatRequestEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, @NonNull InetAddress address, int port, String token, boolean passive) {
		super(bot);
		this.user = user;
		this.userHostmask = userHostmask;
		this.address = address;
		this.port = port;
		this.token = token;
		this.passive = passive;
	}
	//Rename the method from accept to receiveChat
	public ReceiveChat receiveChat() throws IOException {
		return getBot().getDccHandler().acceptChatRequest(this);
	}

	/**
	 * @deprecated Use {@link #getAddress() } from {@link GenericDCCEvent}
	 * interface
	 */
	@Deprecated
	public InetAddress getChatAddress() {
		return getAddress();
	}

	/**
	 * @deprecated Use {@link #getPort() } from {@link GenericDCCEvent}
	 * interface
	 */
	@Deprecated
	public int getChatPort() {
		return getPort();
	}

	/**
	 * @deprecated Use {@link #getToken() } from {@link GenericDCCEvent}
	 * interface
	 */
	@Deprecated
	public String getChatToken() {
		return getToken();
	}

	/**
	 * Respond with a <i>private message</i> to the user that sent the request,
	 * <b>not a message over dcc</b> since it might not of been accepted yet
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getUser().send().message(response);
	}
}
