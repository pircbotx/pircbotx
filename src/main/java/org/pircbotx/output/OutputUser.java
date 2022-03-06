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
package org.pircbotx.output;

import java.io.File;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.dcc.SendFileTransfer;
import org.pircbotx.exception.DccException;

/**
 * Send lines to a serverUser.
 */
@RequiredArgsConstructor
public class OutputUser implements GenericChannelUserOutput {
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final UserHostmask serverUser;

	/**
	 * Send an invite to the serverUser
	 *
	 * for more information
	 *
	 * @param channel The channel you are inviting the serverUser to join.
	 */
	public void invite(String channel) {
		bot.sendIRC().invite(serverUser.getNick(), channel);
	}

	/**
	 * Send an invite to the serverUser.
	 *
	 * for more information
	 *
	 * @param channel The channel you are inviting the serverUser to join.
	 */
	public void invite(Channel channel) {
		bot.sendIRC().invite(serverUser.getNick(), channel.getName());
	}

	/**
	 * Send a notice to the serverUser. } for more information
	 *
	 * @param notice The notice to send
	 */
	public void notice(String notice) {
		bot.sendIRC().notice(serverUser.getNick(), notice);
	}

	/**
	 * Send an action to the serverUser. } for more information
	 *
	 * @param action The action message to send
	 */
	public void action(String action) {
		bot.sendIRC().action(serverUser.getNick(), action);
	}

	/**
	 * Send a private message to a serverUser. } for more information
	 *
	 * @param message The message to send
	 */
	public void message(String message) {
		bot.sendIRC().message(serverUser.getNick(), message);
	}

	/**
	 * Send a CTCP command to the serverUser. } for more information
	 *
	 * @param command The CTCP command to send
	 */
	public void ctcpCommand(String command) {
		bot.sendIRC().ctcpCommand(serverUser.getNick(), command);
	}

	/**
	 * Send a CTCP Response to the serverUser. } for more information
	 *
	 * @param message The response to send
	 */
	public void ctcpResponse(String message) {
		bot.sendIRC().ctcpResponse(serverUser.getNick(), message);
	}

	/**
	 * Send usermode
	 *
	 * @param mode The modes to change
	 */
	public void mode(String mode) {
		bot.sendIRC().mode(serverUser.getNick(), mode);
	}

	public SendFileTransfer dccFile(File file) throws IOException, DccException, InterruptedException {
		return bot.getDccHandler().sendFile(file, bot.getUserChannelDao().getUser(serverUser));
	}

	public SendFileTransfer dccFile(File file, boolean passive) throws IOException, DccException, InterruptedException {
		return bot.getDccHandler().sendFile(file, bot.getUserChannelDao().getUser(serverUser), passive);
	}

	public SendChat dccChat() throws IOException, InterruptedException {
		return bot.getDccHandler().sendChat(bot.getUserChannelDao().getUser(serverUser));
	}

	public SendChat dccChat(boolean passive) throws IOException, InterruptedException {
		return bot.getDccHandler().sendChat(bot.getUserChannelDao().getUser(serverUser), passive);
	}

	/**
	 * Send "WHOIS nick"
	 */
	public void whois() {
		bot.sendIRC().whois(serverUser.getNick());
	}

	/**
	 * Send "WHOIS nick nick" for more detail
	 */
	public void whoisDetail() {
		bot.sendIRC().whoisDetail(serverUser.getNick());
	}
}
