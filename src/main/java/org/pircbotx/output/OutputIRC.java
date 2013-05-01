/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.output;

import lombok.RequiredArgsConstructor;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.CoreHooks;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class OutputIRC {
	protected final PircBotX bot;
	protected final OutputRaw sendRaw;

	/**
	 * Quits from the IRC server.
	 * Providing we are actually connected to an IRC server, a {@link DisconnectEvent}
	 * will be dispatched as soon as the IRC server disconnects us.
	 */
	public void quitServer() {
		quitServer("");
	}

	/**
	 * Quits from the IRC server with a reason.
	 * Providing we are actually connected to an IRC server, a {@link DisconnectEvent}
	 * will be dispatched as soon as the IRC server disconnects us.
	 *
	 * @param reason The reason for quitting the server.
	 */
	public void quitServer(String reason) {
		sendRaw.rawLine("QUIT :" + reason);
	}

	/**
	 * Sends a CTCP command to a channel or user. (Client to client protocol).
	 * Examples of such commands are "PING <number>", "FINGER", "VERSION", etc.
	 * For example, if you wish to request the version of a user called "Dave",
	 * then you would call
	 * <code>sendCTCPCommand("Dave", "VERSION");</code>.
	 * The type of response to such commands is largely dependant on the target
	 * client software.
	 *
	 * @since PircBot 0.9.5
	 *
	 * @param target The name of the channel or user to send the CTCP message to.
	 * @param command The CTCP command to send.
	 */
	public void ctcpCommand(String target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null target");
		sendRaw.rawLineSplit("PRIVMSG " + target + " :\u0001", command, "\u0001");
	}

	/**
	 * Send a CTCP response to the target channel or user. Note that the
	 * {@link CoreHooks} class already handles responding to the most common CTCP
	 * commands. Only respond to other commands that aren't implemented
	 * @param target The target of the response
	 * @param message The message to send
	 */
	public void ctcpResponse(String target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP response to null target");
		sendRaw.rawLine("NOTICE " + target + " :\u0001" + message + "\u0001");
	}

	/**
	 * Sends a message to a channel or a private message to a user. These
	 * messages are added to the outgoing message queue and sent at the
	 * earliest possible opportunity.
	 * <p>
	 * Some examples: -
	 * <pre>    // Send the message "Hello!" to the channel #cs.
	 *    sendMessage("#cs", "Hello!");
	 *
	 *    // Send a private message to Paul that says "Hi".
	 *    sendMessage("Paul", "Hi");</pre>
	 *
	 * You may optionally apply colours, boldness, underlining, etc to
	 * the message by using the
	 * <code>Colors</code> class.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param message The message to send.
	 *
	 * @see Colors
	 */
	public void message(String target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null target");
		sendRaw.rawLineSplit("PRIVMSG " + target + " :", message);
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
		if (target == null)
			throw new IllegalArgumentException("Can't send action to null target");
		ctcpCommand(target, "ACTION " + action);
	}

	/**
	 * Sends a notice to the channel or to a user.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param notice The notice to send.
	 */
	public void notice(String target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null target");
		sendRaw.rawLineSplit("NOTICE " + target + " :", notice);
	}

	/**
	 * Attempt to change the current nick (nickname) of the bot when it
	 * is connected to an IRC server.
	 * After confirmation of a successful nick change, the getNick method
	 * will return the new nick.
	 *
	 * @param newNick The new nick to use.
	 */
	public void changeNick(String newNick) {
		if (newNick == null)
			throw new IllegalArgumentException("Can't change to null nick");
		sendRaw.rawLine("NICK " + newNick);
	}

	/**
	 * Sends an invitation to join a channel. Some channels can be marked
	 * as "invite-only", so it may be useful to allow a bot to invite people
	 * into it.
	 *
	 * @param nick The nick of the user to invite
	 * @param channel The channel you are inviting the user to join.
	 *
	 */
	public void invite(String nick, String channel) {
		if (nick == null)
			throw new IllegalArgumentException("Can't send invite to null nick");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null channel");
		sendRaw.rawLine("INVITE " + nick + " :" + channel);
	}
	
	/**
	 * Issues a request for a list of all channels on the IRC server.
	 * When the PircBotX receives information for each channel, a {@link ChannelInfoEvent}
	 * will be dispatched which you will need to listen for if you want to do
	 * anything useful.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels() {
		listChannels(null);
	}

	/**
	 * Issues a request for a list of all channels on the IRC server.
	 * When the PircBotX receives information for each channel, a {@link ChannelInfoEvent}
	 * will be dispatched which you will need to listen for if you want to do
	 * anything useful
	 * <p>
	 * Some IRC servers support certain parameters for LIST requests.
	 * One example is a parameter of ">10" to list only those channels
	 * that have more than 10 users in them. Whether these parameters
	 * are supported or not will depend on the IRC server software.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 * @param parameters The parameters to supply when requesting the
	 * list.
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels(String parameters) {
		if (!bot.getInputParser().isChannelListRunning())
			if (parameters == null)
				sendRaw.rawLine("LIST");
			else
				sendRaw.rawLine("LIST " + parameters);
	}
	
	/**
	 * Identify the bot with NickServ, supplying the appropriate password.
	 * Some IRC Networks (such as freenode) require users to <i>register</i> and
	 * <i>identify</i> with NickServ before they are able to send private messages
	 * to other users, thus reducing the amount of spam. If you are using
	 * an IRC network where this kind of policy is enforced, you will need
	 * to make your bot <i>identify</i> itself to NickServ before you can send
	 * private messages. Assuming you have already registered your bot's
	 * nick with NickServ, this method can be used to <i>identify</i> with
	 * the supplied password. It usually makes sense to identify with NickServ
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
	 * @param password The password which will be used to identify with NickServ.
	 */
	public void identify(final String password) {
		if (password == null)
			throw new IllegalArgumentException("Can't identify with null password");
		if (bot.isConnected())
			sendRaw.rawLine("NICKSERV IDENTIFY " + password);
		else
			bot.getConfiguration().getListenerManager().addListener(new ListenerAdapter() {
				@Override
				public void onConnect(ConnectEvent event) throws Exception {
					//Make sure this bot is us to prevent nasty errors in multi bot sitations
					if (event.getBot() == bot) {
						sendRaw.rawLine("NICKSERV IDENTIFY " + password);
						//Self destrust, this listener has no more porpose
						bot.getConfiguration().getListenerManager().removeListener(this);
					}
				}
			});
	}
}
