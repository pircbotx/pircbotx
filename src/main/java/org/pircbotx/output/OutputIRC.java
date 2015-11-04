/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.CoreHooks;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import static com.google.common.base.Preconditions.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Implements the basic IRC protocol.
 *
 * @author Leon Blakey
 */
@RequiredArgsConstructor
public class OutputIRC {
	@NonNull
	protected final PircBotX bot;

	/**
	 * Joins a channel.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 */
	public void joinChannel(String channel) {
		checkArgument(StringUtils.isNotBlank(channel), "Channel '%s' is blank", channel);
		bot.sendRaw().rawLine("JOIN " + channel);
	}

	/**
	 * Joins a channel with a key.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 * @param key The key that will be used to join the channel.
	 */
	public void joinChannel(String channel, String key) {
		checkArgument(StringUtils.isNotBlank(channel), "Channel '%s' is blank", channel);
		checkNotNull(key, "Key for channel %s cannot be null", channel);
		joinChannel(channel + " " + key);
	}

	/**
	 * Quits from the IRC server. Providing we are actually connected to an IRC
	 * server, a {@link DisconnectEvent} will be dispatched as soon as the IRC
	 * server disconnects us.
	 */
	public void quitServer() {
		quitServer("");
	}

	/**
	 * Quits from the IRC server with a reason. Providing we are actually
	 * connected to an IRC server, a {@link DisconnectEvent} will be dispatched
	 * as soon as the IRC server disconnects us.
	 *
	 * @param reason The reason for quitting the server.
	 */
	public void quitServer(String reason) {
		checkNotNull(reason, "Reason cannot be null");
		bot.sendRaw().rawLineNow("QUIT :" + reason);
	}

	/**
	 * Sends a CTCP command to a channel or user. (Client to client protocol).
	 * Examples of such commands are "PING <number>", "FINGER", "VERSION", etc.
	 * For example, if you wish to request the version of a user called "Dave",
	 * then you would call <code>sendCTCPCommand("Dave", "VERSION");</code>. The
	 * type of response to such commands is largely dependant on the target
	 * client software.
	 *
	 * @since PircBot 0.9.5
	 *
	 * @param target The name of the channel or user to send the CTCP message
	 * to.
	 * @param command The CTCP command to send.
	 */
	public void ctcpCommand(String target, String command) {
		checkArgument(StringUtils.isNotBlank(target), "Target '%s' is blank", target, command);
		checkArgument(StringUtils.isNotBlank(command), "CTCP command '%s' is blank", command, target);
		bot.sendRaw().rawLineSplit("PRIVMSG " + target + " :\u0001", command, "\u0001");
	}

	/**
	 * Send a CTCP response to the target channel or user. Note that the
	 * {@link CoreHooks} class already handles responding to the most common
	 * CTCP commands. Only respond to other commands that aren't implemented
	 *
	 * @param target The target of the response
	 * @param message The message to send
	 */
	public void ctcpResponse(String target, String message) {
		checkArgument(StringUtils.isNotBlank(target), "Target '%s' is blank", target);
		bot.sendRaw().rawLine("NOTICE " + target + " :\u0001" + message + "\u0001");
	}

	/**
	 * Sends a message to a channel or a private message to a user. These
	 * messages are added to the outgoing message queue and sent at the earliest
	 * possible opportunity.
	 * <p>
	 * Some examples: -
	 * <pre>    // Send the message "Hello!" to the channel #cs.
	 *    sendMessage("#cs", "Hello!");
	 *
	 *    // Send a private message to Paul that says "Hi".
	 *    sendMessage("Paul", "Hi");</pre>
	 *
	 * You may optionally apply colours, boldness, underlining, etc to the
	 * message by using the <code>Colors</code> class.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param message The message to send.
	 *
	 * @see Colors
	 */
	public void message(String target, String message) {
		checkArgument(StringUtils.isNotBlank(target), "Target '%s' is blank", target);
		bot.sendRaw().rawLineSplit("PRIVMSG " + target + " :", message);
	}

	/**
	 * Sends an action to the channel or to a user.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param action The action to send.
	 *
	 * @see Colors
	 */
	public void action(String target, String action) {
		checkArgument(StringUtils.isNotBlank(target), "Target '%s' is blank", target);
		ctcpCommand(target, "ACTION " + action);
	}

	/**
	 * Sends a notice to the channel or to a user.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param notice The notice to send.
	 */
	public void notice(String target, String notice) {
		checkArgument(StringUtils.isNotBlank(target), "Target '%s' is blank", target);
		bot.sendRaw().rawLineSplit("NOTICE " + target + " :", notice);
	}

	/**
	 * Attempt to change the current nick (nickname) of the bot when it is
	 * connected to an IRC server. After confirmation of a successful nick
	 * change, the getNick method will return the new nick.
	 *
	 * @param newNick The new nick to use.
	 */
	public void changeNick(String newNick) {
		checkArgument(StringUtils.isNotBlank(newNick), "Nick '%s' is blank", newNick);
		bot.sendRaw().rawLine("NICK " + newNick);
	}

	/**
	 * Sends an invitation to join a channel. Some channels can be marked as
	 * "invite-only", so it may be useful to allow a bot to invite people into
	 * it.
	 *
	 * @param target The nick or channel to invite
	 * @param channel The channel you are inviting them to join.
	 *
	 */
	public void invite(String target, String channel) {
		checkArgument(StringUtils.isNotBlank(target), "Nick '%s' is blank", target);
		checkArgument(StringUtils.isNotBlank(channel), "Channel '%s' is blank", channel);
		bot.sendRaw().rawLine("INVITE " + target + " :" + channel);
	}

	/**
	 * Issues a request for a list of all channels on the IRC server. When the
	 * PircBotX receives information for each channel, a
	 * {@link ChannelInfoEvent} will be dispatched which you will need to listen
	 * for if you want to do anything useful.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels() {
		listChannels("");
	}

	/**
	 * Issues a request for a list of all channels on the IRC server. When the
	 * PircBotX receives information for each channel, a
	 * {@link ChannelInfoEvent} will be dispatched which you will need to listen
	 * for if you want to do anything useful
	 * <p>
	 * Some IRC servers support certain parameters for LIST requests. One
	 * example is a parameter of ">10" to list only those channels that have
	 * more than 10 users in them. Whether these parameters are supported or not
	 * will depend on the IRC server software.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 *
	 * @param parameters The parameters to supply when requesting the list.
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels(String parameters) {
		checkNotNull(parameters, "Parameters cannot be null");
		if (!bot.getInputParser().isChannelListRunning())
			bot.sendRaw().rawLine("LIST " + parameters);
	}

	/**
	 * Identify the bot with NickServ, supplying the appropriate password. Some
	 * IRC Networks (such as freenode) require users to <i>register</i> and
	 * <i>identify</i> with NickServ before they are able to send private
	 * messages to other users, thus reducing the amount of spam. If you are
	 * using an IRC network where this kind of policy is enforced, you will need
	 * to make your bot <i>identify</i> itself to NickServ before you can send
	 * private messages. Assuming you have already registered your bot's nick
	 * with NickServ, this method can be used to <i>identify</i> with the
	 * supplied password. It usually makes sense to identify with NickServ
	 * immediately after connecting to a server.
	 * <p>
	 * This method issues a raw NICKSERV command to the server, and is therefore
	 * safer than the alternative approach of sending a private message to
	 * NickServ. The latter approach is considered dangerous, as it may cause
	 * you to inadvertently transmit your password to an untrusted party if you
	 * connect to a network which does not run a NickServ service and where the
	 * untrusted party has assumed the nick "NickServ". However, if your IRC
	 * network is only compatible with the private message approach, you may
	 * typically identify like so:
	 * <pre>sendMessage("NickServ", "identify PASSWORD");</pre>
	 * <p>
	 * Note that this method will add a temporary listener for ConnectEvent if
	 * the bot is not logged in yet. If the bot is logged in the command is sent
	 * immediately to the server
	 *
	 * @param password The password which will be used to identify with
	 * NickServ.
	 */
	public void identify(final String password) {
		checkArgument(StringUtils.isNotBlank(password), "Password '%s' is blank", password);
		bot.sendRaw().rawLine("NICKSERV IDENTIFY " + password);
	}

	public void mode(String target, String mode) {
		bot.sendRaw().rawLine("MODE " + target + " " + mode);
	}

	/**
	 * Send "WHOIS target"
	 *
	 * @param target
	 */
	public void whois(String target) {
		bot.sendRaw().rawLine("WHOIS " + target);
	}

	/**
	 * Send "WHOIS target target" for more detail
	 *
	 * @param target
	 */
	public void whoisDetail(String target) {
		bot.sendRaw().rawLine("WHOIS " + target + " " + target);
	}
}
