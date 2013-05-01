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

import lombok.RequiredArgsConstructor;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class OutputUser {
	protected final OutputIRC sendIRC;

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void invite(User target, String channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null user");
		sendIRC.invite(target.getNick(), channel);
	}

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the invite to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void invite(User target, Channel channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null user");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null channel");
		sendIRC.invite(target.getNick(), channel.getName());
	}

	/**
	 * Send a notice to the user. See {@link #sendNotice(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the notice to
	 * @param notice The notice to send
	 */
	public void notice(User target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null user");
		sendIRC.notice(target.getNick(), notice);
	}

	/**
	 * Send an action to the user. See {@link #sendAction(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the action to
	 * @param action The action message to send
	 */
	public void action(User target, String action) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null user");
		sendIRC.action(target.getNick(), action);
	}

	/**
	 * Send a private message to a user. See {@link #sendMessage(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the message to
	 * @param message The message to send
	 */
	public void message(User target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null user");
		sendIRC.message(target.getNick(), message);
	}

	/**
	 * Send a CTCP command to the user. See {@link #sendCTCPCommand(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param command The CTCP command to send
	 */
	public void ctcpCommand(User target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null user");
		sendIRC.ctcpCommand(target.getNick(), command);
	}

	/**
	 * Send a CTCP Response to the user. See {@link #sendCTCPResponse(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP Response to
	 * @param message The response to send
	 */
	public void ctcpResponse(User target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP response to null user");
		sendIRC.ctcpResponse(target.getNick(), message);
	}
}
