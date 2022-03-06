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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PartEvent;

/**
 * Send lines to a channel.
 */
@RequiredArgsConstructor
public class OutputChannel implements GenericChannelUserOutput {
	@NonNull
	protected final PircBotX bot;
	@NonNull
	protected final Channel channel;

	/**
	 * Parts a channel.
	 */
	public void part() {
		bot.sendRaw().rawLine("PART " + channel.getName());
	}

	/**
	 * Parts a channel, giving a reason.
	 *
	 * @param reason The reason for parting the channel.
	 */
	public void part(String reason) {
		bot.sendRaw().rawLine("PART " + channel.getName() + " :" + reason);
	}

	/**
	 * Send a message to the channel.
	 *
	 * @param message The message to send
	 */
	public void message(String message) {
		bot.sendIRC().message(channel.getName(), message);
	}

	/**
	 * Send a message to the given user in the given channel in this format:
	 * <code>user: message</code>. Very useful for responding directly to a
	 * command
	 *
	 * @param user The user to recieve the message in the channel
	 * @param message The message to send
	 */
	public void message(UserHostmask user, String message) {
		if (user == null)
			throw new IllegalArgumentException("Can't send message to null user");
		message(user.getNick() + ": " + message);
	}

	/**
	 * Send an action to the channel.
	 *
	 * @param action The action message to send
	 */
	public void action(String action) {
		bot.sendIRC().action(channel.getName(), action);
	}

	/**
	 * Send a notice to the channel.
	 *
	 * @param notice The notice to send
	 */
	public void notice(String notice) {
		bot.sendIRC().notice(channel.getName(), notice);
	}

	/**
	 * Send an invite for this channel to another channel.
	 *
	 * @param otherChannel The channel you are inviting the user to join.
	 * @see OutputIRC#invite(java.lang.String, java.lang.String)
	 */
	public void invite(Channel otherChannel) {
		if (otherChannel == null)
			throw new IllegalArgumentException("Can't send invite to null invite channel");
		bot.sendIRC().invite(otherChannel.getName(), channel.getName());
	}

	/**
	 * Send an invite from this channel to a user
	 *
	 * @param user
	 * @see OutputIRC#invite(java.lang.String, java.lang.String)
	 */
	public void invite(@NonNull UserHostmask user) {
		bot.sendIRC().invite(user.getNick(), channel.getName());
	}

	/**
	 * Send an invite from this channel to a channel or user.
	 *
	 * @param target
	 * @see OutputIRC#invite(java.lang.String, java.lang.String)
	 */
	public void invite(@NonNull String target) {
		bot.sendIRC().invite(target, channel.getName());
	}

	/**
	 * Send a CTCP command to the channel. } for more information
	 *
	 * @param command The CTCP command to send
	 */
	public void ctcpCommand(String command) {
		bot.sendIRC().ctcpCommand(channel.getName(), command);
	}

	/**
	 * Part and rejoin specified channel. Useful for obtaining auto privileges
	 * after identifying
	 */
	public void cycle() {
		cycle("");
	}

	/**
	 * Part and rejoin specified channel using channel key. Useful for obtaining
	 * auto privileges after identifying
	 *
	 * @param key The key to use when rejoining the channel
	 */
	public void cycle(final String key) {
		final String channelName = channel.getName();
		//As we might not immediatly part and you can't join a channel that your
		//already joined to, wait for the PART event before rejoining
		bot.getConfiguration().getListenerManager().addListener(new ListenerAdapter() {
			@Override
			public void onPart(PartEvent event) throws Exception {
				//Make sure this bot is us to prevent nasty errors in multi bot sitations
				if (event.getBot() == bot) {
					bot.sendIRC().joinChannel(channelName, key);
					//Self destrust, this listener has no more porpose
					bot.getConfiguration().getListenerManager().removeListener(this);
				}
			}
		});
		part();
	}

	public void who() {
		bot.sendRaw().rawLine("WHO " + channel.getName());
	}

	public void getMode() {
		bot.sendRaw().rawLine("MODE " + channel.getName());
	}

	/**
	 * Set the mode of a channel. This method attempts to set the mode of a
	 * channel. This may require the bot to have operator status on the channel.
	 * For example, if the bot has operator status, we can grant operator status
	 * to "Dave" on the #cs channel by calling setMode("#cs", "+o Dave"); An
	 * alternative way of doing this would be to use the op method.
	 *
	 * @param mode The new mode to apply to the channel. This may include zero
	 * or more arguments if necessary.
	 *
	 * @see #op(org.pircbotx.UserHostmask)
	 */
	public void setMode(String mode) {
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		bot.sendIRC().mode(channel.getName(), mode);
	}

	/**
	 * Set a mode for the channel with arguments. Nicer way to pass arguments
	 * than with string concatenation. See {@link #setMode(java.lang.String)
	 * }
	 * for more information
	 *
	 * @param mode The new mode to apply to the channel. This may include zero
	 * or more arguments if necessary.
	 * @param args Arguments to be passed to the mode. All will be converted to
	 * a string using {@link Object#toString() } and added together with a
	 * single space separating them
	 */
	public void setMode(String mode, Object... args) {
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		if (args == null)
			throw new IllegalArgumentException("Can't set mode arguments to null");
		setMode(mode + " " + StringUtils.join(args, " "));
	}

	/**
	 * Set a mode for a user. See {@link #setMode(java.lang.String)
	 * }
	 *
	 * @param mode The new mode to apply to the channel.
	 * @param user The user to perform the mode change on
	 * @see #setMode(java.lang.String)
	 */
	public void setMode(String mode, UserHostmask user) {
		if (mode == null)
			throw new IllegalArgumentException("Can't set user mode on channel to null");
		if (user == null)
			throw new IllegalArgumentException("Can't set user mode on null user");
		setMode(mode + " " + user.getNick());
	}

	/**
	 * Attempt to set the channel limit (+l) to specified value. May require
	 * operator privileges in the channel
	 *
	 * @param limit The maximum amount of people that can be in the channel
	 */
	public void setChannelLimit(int limit) {
		setMode("+l", limit);
	}

	/**
	 * Attempt to remove the channel limit (-l) on the specified channel. May
	 * require operator privileges in the channel
	 */
	public void removeChannelLimit() {
		setMode("-l");
	}

	/**
	 * Sets the channel key (+k) or password to get into the channel. May
	 * require operator privileges in the channel
	 *
	 * @param key The secret key to use
	 */
	public void setChannelKey(String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't set channel key to null");
		setMode("+k", key);
	}

	/**
	 * Removes the channel key (-k) or password to get into the channel. May
	 * require operator privileges in the channel
	 *
	 * @param key The secret key to remove. If this is not known a blank key or
	 * asterisk might work
	 */
	public void removeChannelKey(String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't remove channel key with null key");
		setMode("-k", key);
	}

	/**
	 * Set the channel as invite only (+i). May require operator privileges in
	 * the channel
	 */
	public void setInviteOnly() {
		setMode("+i");
	}

	/**
	 * Removes invite only (-i) status from the channel. May require operator
	 * privileges in the channel
	 */
	public void removeInviteOnly() {
		setMode("-i");
	}

	/**
	 * Set the channel as moderated (+m). May require operator privileges in the
	 * channel
	 */
	public void setModerated() {
		setMode("+m");
	}

	/**
	 * Removes moderated (-m) status from the channel. May require operator
	 * privileges in the channel
	 */
	public void removeModerated() {
		setMode("-m");
	}

	/**
	 * Prevent external messages from appearing in the channel (+n). May require
	 * operator privileges in the channel
	 */
	public void setNoExternalMessages() {
		setMode("+n");
	}

	/**
	 * Allow external messages to appear in the channel (+n). May require
	 * operator privileges in the channel
	 */
	public void removeNoExternalMessages() {
		setMode("-n");
	}

	/**
	 * Set the channel as secret (+s). May require operator privileges in the
	 * channel
	 */
	public void setSecret() {
		setMode("+s");
	}

	/**
	 * Removes secret (-s) status from the channel. May require operator
	 * privileges in the channel
	 */
	public void removeSecret() {
		setMode("-s");
	}

	/**
	 * Prevent non-operator users from changing the channel topic (+t). May
	 * require operator privileges in the channel
	 */
	public void setTopicProtection() {
		setMode("+t");
	}

	/**
	 * Allow non-operator users to change the channel topic (-t). May require
	 * operator privileges in the channel
	 */
	public void removeTopicProtection() {
		setMode("-t");
	}

	/**
	 * Set the channel as private (+p). May require operator privileges in the
	 * channel
	 */
	public void setChannelPrivate() {
		setMode("+p");
	}

	/**
	 * Removes private (-p) status from the channel. May require operator
	 * privileges in the channel
	 */
	public void removeChannelPrivate() {
		setMode("-p");
	}

	/**
	 * Bans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net". This may be used in conjunction with the kick
	 * method to permanently remove a user from a channel. Successful use of
	 * this method may require the bot to have operator status itself.
	 *
	 * @param hostmask A hostmask representing the user we're banning.
	 */
	public void ban(String hostmask) {
		if (hostmask == null)
			throw new IllegalArgumentException("Can't set ban on null hostmask");
		bot.sendRaw().rawLine("MODE " + channel.getName() + " +b " + hostmask);
	}

	/**
	 * Unbans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net". Successful use of this method may require the bot
	 * to have operator status itself.
	 *
	 * @param hostmask A hostmask representing the user we're unbanning.
	 */
	public void unBan(String hostmask) {
		if (hostmask == null)
			throw new IllegalArgumentException("Can't remove ban on null hostmask");
		bot.sendRaw().rawLine("MODE " + channel.getName() + " -b " + hostmask);
	}

	/**
	 * Grants operator privileges to a user on a channel. Successful use of this
	 * method may require the bot to have operator status itself.
	 *
	 * @param user The user we are opping.
	 */
	public void op(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set op on null user");
		setMode("+o " + user.getNick());
	}

	/**
	 * Removes operator privileges from a user on a channel. Successful use of
	 * this method may require the bot to have operator status itself.
	 *
	 * @param user The user we are deopping.
	 */
	public void deOp(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove op on null user");
		setMode("-o " + user.getNick());
	}

	/**
	 * Grants voice privileges to a user on a channel. Successful use of this
	 * method may require the bot to have operator status itself.
	 *
	 * @param user The user we are voicing.
	 */
	public void voice(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set voice on null user");
		setMode("+v " + user.getNick());
	}

	/**
	 * Removes voice privileges from a user on a channel. Successful use of this
	 * method may require the bot to have operator status itself.
	 *
	 * @param user The user we are devoicing.
	 */
	public void deVoice(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove voice on null user");
		setMode("-v " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel. Successful use of this
	 * method may require the bot to have operator or halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void halfOp(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set halfop on null user");
		setMode("+h " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel. Successful use of this
	 * method may require the bot to have operator or halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void deHalfOp(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove halfop on null user");
		setMode("-h " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel. Successful use of this
	 * method may require the bot to have owner status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void owner(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set owner on null user");
		setMode("+q " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel. Successful use of this
	 * method may require the bot to have owner status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void deOwner(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove owner on null user");
		setMode("-q " + user.getNick());
	}

	/**
	 * Grants superOp privileges to a user on a channel. Successful use of this
	 * method may require the bot to have owner or superOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void superOp(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set super op on null user");
		setMode("+a " + user.getNick());
	}

	/**
	 * Removes superOp privileges to a user on a channel. Successful use of this
	 * method may require the bot to have owner or superOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even
	 * use it to mean something else!
	 *
	 * @param user
	 */
	public void deSuperOp(UserHostmask user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove super op on null user");
		setMode("-a " + user.getNick());
	}

	/**
	 * Set the topic for a channel. This method attempts to set the topic of a
	 * channel. This may require the bot to have operator status if the topic is
	 * protected.
	 *
	 * @param topic The new topic for the channel.
	 *
	 */
	public void setTopic(String topic) {
		if (topic == null)
			throw new IllegalArgumentException("Can't set topic to null");
		bot.sendRaw().rawLine("TOPIC " + channel.getName() + " :" + topic);
	}

	/**
	 * Kicks a user from a channel. This method attempts to kick a user from a
	 * channel and may require the bot to have operator status in the channel.
	 *
	 * @param user The user to kick.
	 */
	public void kick(UserHostmask user) {
		kick(user, "");
	}

	/**
	 * Kicks a user from a channel, giving a reason. This method attempts to
	 * kick a user from a channel and may require the bot to have operator
	 * status in the channel.
	 *
	 * @param user The user to kick.
	 * @param reason A description of the reason for kicking a user.
	 */
	public void kick(UserHostmask user, String reason) {
		if (user == null)
			throw new IllegalArgumentException("Can't kick null user");
		bot.sendRaw().rawLine("KICK " + channel.getName() + " " + user.getNick() + " :" + reason);
	}
}
