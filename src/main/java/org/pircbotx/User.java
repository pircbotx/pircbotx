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
package org.pircbotx;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * This class is used to represent a user on an IRC server.
 * Instances of this class are returned by the getUsers method
 * in the PircBot class.

 *
 * @since   1.0.0
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
public class User implements Comparable<User> {
	@Setter(AccessLevel.PACKAGE)
	private String nick;
	@Setter(AccessLevel.PACKAGE)
	private String realName = "";
	@Setter(AccessLevel.PACKAGE)
	private String login = "";
	@Setter(AccessLevel.PACKAGE)
	private String hostmask = "";
	@Setter(AccessLevel.PACKAGE)
	private boolean away = false;
	@Setter(AccessLevel.PACKAGE)
	private boolean ircop = false;
	@Setter(AccessLevel.PACKAGE)
	private String server = "";
	@Setter(AccessLevel.PACKAGE)
	private boolean identified = true;
	@Setter(AccessLevel.PACKAGE)
	private int hops = 0;
	private final PircBotX bot;

	public void parseStatus(String channel, String prefix) {
		if(prefix.contains("@"))
			bot.getChannel(channel).addOp(this);
		if(prefix.contains("+"))
			bot.getChannel(channel).addVoice(this);
		setAway(prefix.contains("G")); //Assume here (H) if there is no G
		setIrcop(prefix.contains("*"));
	}

	/**
	 * Get all channels this user is a part of
	 * @return All channels this user is a part of
	 */
	public Collection<Channel> getChannels() {
		return bot.getChannels(this);
	}

	/**
	 * Returns the result of calling the compareTo method on lowercased
	 * nicks. This is useful for sorting lists of User objects.
	 *
	 * @return the result of calling compareTo on lowercased nicks.
	 */
	@Override
	public int compareTo(User other) {
		return other.getNick().compareToIgnoreCase(getNick());
	}

	/**
	 * The exact server that this user is joined to
	 * @return The address of the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * The number of hops it takes to this user
	 * @return the hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Checks if the user is an operator in the specified channel
	 * @return True if they are an op, false if not
	 */
	public boolean isOp(Channel chan) {
		return chan.isOp(this);
	}

/*
	 * Attempts to give Operator status to this user in the given channel. Simply
	 * calls {@link PircBotX#op(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to Op
	 */
	public void op(Channel chan) {
		bot.op(chan, this);
	}

	/**
	 * Attempts to remove Operator status from this user in the given channel. 
	 * Simply calls {@link PircBotX#deOp(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to Voice
	 */
	public void deOp(Channel chan) {
		bot.deOp(chan, this);
	}
	
	/**
	 * Checks if the user has voice status in the given channel
	 * @return True if the user has voice, false if not
	 */
	public boolean hasVoice(Channel chan) {
		return chan.hasVoice(this);
	}

	/**
	 * Attempts to give Voice status to this user in the given channel. Simply 
	 * calls {@link PircBotX#voice(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param chan The channel to give voice in
	 */
	public void voice(Channel chan) {
		bot.voice(chan, this);
	}

	/**
	 * Attempts to remove Voice status from this user in the given channel. Simply
	 * calls {@link PircBotX#deVoice(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param chan The channel to give voice in
	 */
	public void deVoice(Channel chan) {
		bot.deVoice(chan, this);
	}
}
