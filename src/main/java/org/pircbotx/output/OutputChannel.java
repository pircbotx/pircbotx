/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.output;

import lombok.RequiredArgsConstructor;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PartEvent;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class OutputChannel {
	protected final OutputRaw sendRaw;
	protected final OutputIRC sendIRC;

	/**
	 * Joins a channel.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 */
	public void join(String channel) {
		if (channel == null)
			throw new IllegalArgumentException("Can't join a null channel");
		sendRaw.rawLine("JOIN " + channel);
	}

	/**
	 * Joins a channel with a key.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 * @param key The key that will be used to join the channel.
	 */
	public void join(String channel, String key) {
		if (channel == null)
			throw new IllegalArgumentException("Can't join a null channel");
		if (key == null)
			throw new IllegalArgumentException("Can't channel " + channel + " with null key");
		join(channel + " " + key);
	}

	/**
	 * Parts a channel.
	 *
	 * @param channel The name of the channel to leave.
	 */
	public void part(Channel channel) {
		if (channel == null)
			throw new IllegalArgumentException("Can't part a null channel");
		sendRaw.rawLine("PART " + channel.getName());
	}

	/**
	 * Parts a channel, giving a reason.
	 *
	 * @param channel The name of the channel to leave.
	 * @param reason The reason for parting the channel.
	 */
	public void part(Channel channel, String reason) {
		if (channel == null)
			throw new IllegalArgumentException("Can't part a null channel");
		sendRaw.rawLine("PART " + channel.getName() + " :" + reason);
	}

	/**
	 * Send a message to the channel. See {@link #sendMessage(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the message to
	 * @param message The message to send
	 */
	public void message(Channel target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		sendIRC.message(target.getName(), message);
	}

	/**
	 * Send a message to the given user in the given channel in this format:
	 * <code>user: message</code>. Very useful for responding directly to a command
	 * @param chan The channel to send the message to
	 * @param user The user to recieve the message in the channel
	 * @param message The message to send
	 */
	public void message(Channel chan, User user, String message) {
		if (chan == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		if (user == null)
			throw new IllegalArgumentException("Can't send message to null user");
		message(chan, user.getNick() + ": " + message);
	}

	/**
	 * Send an action to the channel. See {@link #sendAction(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the action to
	 * @param action The action message to send
	 */
	public void action(Channel target, String action) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		sendIRC.action(target.getName(), action);
	}

	/**
	 * Send a notice to the channel. See {@link #sendNotice(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the notice to
	 * @param notice The notice to send
	 */
	public void notice(Channel target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null channel");
		sendIRC.notice(target.getName(), notice);
	}

	/**
	 * Send an invite to the channel. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the invite to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void invite(Channel target, Channel channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null target channel");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null invite channel");
		sendIRC.invite(target.getName(), channel.getName());
	}

	/**
	 * Send a CTCP command to the channel. See {@link #sendCTCPCommand(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the CTCP command to
	 * @param command The CTCP command to send
	 */
	public void ctcpCommand(Channel target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null channel");
		sendIRC.ctcpCommand(target.getName(), command);
	}
	
	/**
	 * Part and rejoin specified channel. Useful for obtaining auto privileges
	 * after identifying
	 * @param chan The channel to part and join from. Note that the object will
	 * be invalid after this method executes and a new one will be created
	 */
	public void cycle(final Channel chan) {
		cycle(chan, "");
	}

	/**
	 * Part and rejoin specified channel using channel key. Useful for obtaining
	 * auto privileges after identifying
	 * @param chan The channel to part and join from. Note that the object will
	 * be invalid after this method executes and a new one will be created
	 * @param key The key to use when rejoining the channel
	 */
	public void cycle(final Channel chan, final String key) {
		part(chan);
		//As we might not immediatly part and you can't join a channel that your
		//already joined to, wait for the PART event before rejoining
		chan.getBot().getConfiguration().getListenerManager().addListener(new ListenerAdapter() {
			@Override
			public void onPart(PartEvent event) throws Exception {
				//Make sure this bot is us to prevent nasty errors in multi bot sitations
				if (event.getBot() == chan.getBot()) {
					join(chan.getName(), key);
					//Self destrust, this listener has no more porpose
					chan.getBot().getConfiguration().getListenerManager().removeListener(this);
				}
			}
		});
	}

	/**
	 * Set the mode of a channel.
	 * This method attempts to set the mode of a channel. This
	 * may require the bot to have operator status on the channel.
	 * For example, if the bot has operator status, we can grant
	 * operator status to "Dave" on the #cs channel
	 * by calling setMode("#cs", "+o Dave");
	 * An alternative way of doing this would be to use the op method.
	 *
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel. This may include
	 * zero or more arguments if necessary.
	 *
	 * @see #op(org.pircbotx.Channel, org.pircbotx.User)
	 */
	public void setMode(Channel chan, String mode) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set mode on null channel");
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		sendRaw.rawLine("MODE " + chan.getName() + " " + mode);
	}

	/**
	 * Set a mode for the channel with arguments. Nicer way to pass arguments than
	 * with string concatenation. See {@link #setMode(org.pircbotx.Channel, java.lang.String) }
	 * for more information
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel. This may include
	 * zero or more arguments if necessary.
	 * @param args Arguments to be passed to the mode. All will be converted to
	 * a string using {@link Object#toString() } and added together
	 * with a single space separating them
	 */
	public void setMode(Channel chan, String mode, Object... args) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set mode on null channel");
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		if (args == null)
			throw new IllegalArgumentException("Can't set mode arguments to null");
		//Build arg string
		StringBuilder argBuilder = new StringBuilder(" ");
		for (Object curArg : args)
			argBuilder.append(curArg.toString()).append(" ");
		setMode(chan, mode + argBuilder.toString());
	}

	/**
	 * Set a mode for a user. See {@link #setMode(org.pircbotx.Channel, java.lang.String) }
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel.
	 * @param user The user to perform the mode change on
	 * @see #setMode(org.pircbotx.Channel, java.lang.String)
	 */
	public void setMode(Channel chan, String mode, User user) {
		if (mode == null)
			throw new IllegalArgumentException("Can't set user mode on channel to null");
		if (user == null)
			throw new IllegalArgumentException("Can't set user mode on null user");
		setMode(chan, mode + user.getNick());
	}

	/**
	 * Attempt to set the channel limit (+l) to specified value. May require operator
	 * privileges in the channel
	 * @param chan The channel to set the limit on
	 * @param limit The maximum amount of people that can be in the channel
	 */
	public void setChannelLimit(Channel chan, int limit) {
		setMode(chan, "+l", limit);
	}

	/**
	 * Attempt to remove the channel limit (-l) on the specified channel. May require
	 * operator privileges in the channel
	 * @param chan
	 */
	public void removeChannelLimit(Channel chan) {
		setMode(chan, "-l");
	}

	/**
	 * Sets the channel key (+k) or password to get into the channel. May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 * @param key The secret key to use
	 */
	public void setChannelKey(Channel chan, String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't set channel key to null");
		setMode(chan, "+k", key);
	}

	/**
	 * Removes the channel key (-k) or password to get into the channel. May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 * @param key The secret key to remove. If this is not known a blank key or
	 * asterisk might work
	 */
	public void removeChannelKey(Channel chan, String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't remove channel key with null key");
		setMode(chan, "-k", key);
	}

	/**
	 * Set the channel as invite only (+i). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setInviteOnly(Channel chan) {
		setMode(chan, "+i");
	}

	/**
	 * Removes invite only (-i) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeInviteOnly(Channel chan) {
		setMode(chan, "-i");
	}

	/**
	 * Set the channel as moderated (+m). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setModerated(Channel chan) {
		setMode(chan, "+m");
	}

	/**
	 * Removes moderated (-m) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeModerated(Channel chan) {
		setMode(chan, "-m");
	}

	/**
	 * Prevent external messages from appearing in the channel (+n). May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setNoExternalMessages(Channel chan) {
		setMode(chan, "+n");
	}

	/**
	 * Allow external messages to appear in the channel (+n). May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeNoExternalMessages(Channel chan) {
		setMode(chan, "-n");
	}

	/**
	 * Set the channel as secret (+s). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setSecret(Channel chan) {
		setMode(chan, "+s");
	}

	/**
	 * Removes secret (-s) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeSecret(Channel chan) {
		setMode(chan, "-s");
	}

	/**
	 * Prevent non-operator users from changing the channel topic (+t). May
	 * require operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setTopicProtection(Channel chan) {
		setMode(chan, "+t");
	}

	/**
	 * Allow non-operator users to change the channel topic (-t). May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeTopicProtection(Channel chan) {
		setMode(chan, "-t");
	}

	/**
	 * Bans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net". This may be used in conjunction with the
	 * kick method to permanently remove a user from a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param channel The channel to ban the user from.
	 * @param hostmask A hostmask representing the user we're banning.
	 */
	public void ban(Channel channel, String hostmask) {
		if (channel == null)
			throw new IllegalArgumentException("Can't set ban on null channel");
		if (hostmask == null)
			throw new IllegalArgumentException("Can't set ban on null hostmask");
		sendRaw.rawLine("MODE " + channel.getName() + " +b " + hostmask);
	}

	/**
	 * Unbans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net".
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param channel The channel to unban the user from.
	 * @param hostmask A hostmask representing the user we're unbanning.
	 */
	public void unBan(Channel channel, String hostmask) {
		if (channel == null)
			throw new IllegalArgumentException("Can't remove ban on null channel");
		if (hostmask == null)
			throw new IllegalArgumentException("Can't remove ban on null hostmask");
		sendRaw.rawLine("MODE " + channel.getName() + " -b " + hostmask);
	}

	/**
	 * Grants operator privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're opping the user on.
	 * @param user The user we are opping.
	 */
	public void op(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set op on null user");
		setMode(chan, "+o " + user.getNick());
	}

	/**
	 * Removes operator privileges from a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're deopping the user on.
	 * @param user The user we are deopping.
	 */
	public void deOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove op on null user");
		setMode(chan, "-o " + user.getNick());
	}

	/**
	 * Grants voice privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're voicing the user on.
	 * @param user The user we are voicing.
	 */
	public void voice(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set voice on null user");
		setMode(chan, "+v " + user.getNick());
	}

	/**
	 * Removes voice privileges from a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're devoicing the user on.
	 * @param user The user we are devoicing.
	 */
	public void deVoice(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove voice on null user");
		setMode(chan, "-v " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator or
	 * halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void halfOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set halfop on null user");
		setMode(chan, "+h " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator or
	 * halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deHalfOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove halfop on null user");
		setMode(chan, "-h " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void owner(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set owner on null user");
		setMode(chan, "+q " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deOwner(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove owner on null user");
		setMode(chan, "-q " + user.getNick());
	}

	/**
	 * Grants superOp privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner or superOp
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void superOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set super op on null user");
		setMode(chan, "+a " + user.getNick());
	}

	/**
	 * Removes superOp privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner or superOp
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deSuperOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove super op on null user");
		setMode(chan, "-a " + user.getNick());
	}

	/**
	 * Set the topic for a channel.
	 * This method attempts to set the topic of a channel. This
	 * may require the bot to have operator status if the topic
	 * is protected.
	 *
	 * @param chan The channel on which to perform the mode change.
	 * @param topic The new topic for the channel.
	 *
	 */
	public void setTopic(Channel chan, String topic) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set topic on null channel");
		if (topic == null)
			throw new IllegalArgumentException("Can't set topic to null");
		sendRaw.rawLine("TOPIC " + chan.getName() + " :" + topic);
	}

	/**
	 * Kicks a user from a channel.
	 * This method attempts to kick a user from a channel and
	 * may require the bot to have operator status in the channel.
	 *
	 * @param chan The channel to kick the user from.
	 * @param user The user to kick.
	 */
	public void kick(Channel chan, User user) {
		kick(chan, user, "");
	}

	/**
	 * Kicks a user from a channel, giving a reason.
	 * This method attempts to kick a user from a channel and
	 * may require the bot to have operator status in the channel.
	 *
	 * @param chan The channel to kick the user from.
	 * @param user The user to kick.
	 * @param reason A description of the reason for kicking a user.
	 */
	public void kick(Channel chan, User user, String reason) {
		if (chan == null)
			throw new IllegalArgumentException("Can't kick on null channel");
		if (user == null)
			throw new IllegalArgumentException("Can't kick null user");
		sendRaw.rawLine("KICK " + chan.getName() + " " + user.getNick() + " :" + reason);
	}
}
