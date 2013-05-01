/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
	public void sendCTCPResponse(User target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP response to null user");
		sendIRC.ctcpResponse(target.getNick(), message);
	}
}
