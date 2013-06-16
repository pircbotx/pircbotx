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
package org.pircbotx.output;

import java.io.File;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.dcc.SendFileTransfer;
import org.pircbotx.exception.DccException;

/**
 * Send lines to a user.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class OutputUser {
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final User user;

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void invite(String channel) {
		bot.sendIRC().invite(user.getNick(), channel);
	}

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the invite to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void invite(Channel channel) {
		bot.sendIRC().invite(user.getNick(), channel.getName());
	}

	/**
	 * Send a notice to the user. See {@link #sendNotice(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the notice to
	 * @param notice The notice to send
	 */
	public void notice(String notice) {
		bot.sendIRC().notice(user.getNick(), notice);
	}

	/**
	 * Send an action to the user. See {@link #sendAction(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the action to
	 * @param action The action message to send
	 */
	public void action(String action) {
		bot.sendIRC().action(user.getNick(), action);
	}

	/**
	 * Send a private message to a user. See {@link #sendMessage(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the message to
	 * @param message The message to send
	 */
	public void message(String message) {
		bot.sendIRC().message(user.getNick(), message);
	}

	/**
	 * Send a CTCP command to the user. See {@link #sendCTCPCommand(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param command The CTCP command to send
	 */
	public void ctcpCommand(String command) {
		bot.sendIRC().ctcpCommand(user.getNick(), command);
	}

	/**
	 * Send a CTCP Response to the user. See {@link #sendCTCPResponse(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP Response to
	 * @param message The response to send
	 */
	public void ctcpResponse(String message) {
		bot.sendIRC().ctcpResponse(user.getNick(), message);
	}
	
	public void mode(String mode) {
		bot.sendIRC().mode(user.getNick(), mode);
	}
	
	public SendFileTransfer dccFile(File file) throws IOException, DccException, InterruptedException {
		return bot.getDccHandler().sendFile(file, user);
	}
	
	public SendFileTransfer dccFile(File file, boolean passive) throws IOException, DccException, InterruptedException {
		return bot.getDccHandler().sendFile(file, user, passive);
	}
	
	public SendChat dccChat() throws IOException, InterruptedException {
		return bot.getDccHandler().sendChat(user);
	}
	
	public SendChat dccChat(boolean passive) throws IOException, InterruptedException {
		return bot.getDccHandler().sendChat(user, passive);
	}
}
