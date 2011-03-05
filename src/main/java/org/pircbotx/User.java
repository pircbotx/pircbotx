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
import java.util.Set;

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
public class User implements Comparable<User> {
	/**
	 * The user represented by this object's nickname on the server.
	 */
	private String nick;
	/**
	 * The real name of this user represented by this object on the server
	 */
	private String realName = "";
	/**
	 * The login of the user represented by this object on the server
	 */
	private String login = "";
	/**
	 * The hostmask of the user represented by this object on this server
	 */
	private String hostmask = "";
	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 */
	private boolean away = false;
	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 */
	private boolean ircop = false;
	/**
	 * The server that the user represented by this object is joined to
	 */
	private String server = "";
	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 */
	private boolean identified = true;
	/**
	 * The number of hops it takes to the user represented by this object
	 */
	private int hops = 0;
	private final PircBotX bot;

	public User(PircBotX bot, String nick) {
		this.nick = nick;
		this.bot = bot;
	}

	public void parseStatus(String channel, String prefix) {
		setOp(bot.getChannel(channel), prefix.contains("@"));
		setVoice(bot.getChannel(channel), prefix.contains("+"));
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
	 *
	 * @return The user's prefix and nick.
	 */
	@Override
	public String toString() {
		return getNick();
	}

	/**
	 * Returns true if the nick represented by this User object is the same
	 * as the argument. A case insensitive comparison is made.
	 *
	 * @return true if the nicks are identical (case insensitive).
	 */
	public boolean equals(String nick) {
		return nick.equalsIgnoreCase(nick);
	}

	/**
	 * Returns true if the nick represented by this User object is the same
	 * as the nick of the User object given as an argument.
	 * A case insensitive comparison is made.
	 *
	 * @return true if o is a User object with a matching lowercase nick.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User other = (User) o;
			return other.getNick().equalsIgnoreCase(getNick());
		}
		return false;
	}

	/**
	 * Returns the hash code of this User object.
	 *
	 * @return the hash code of the User object.
	 */
	@Override
	public int hashCode() {
		return getNick().toLowerCase().hashCode();
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
	 * The user represented by this object's nickname on the server.
	 * @return the _nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * The user represented by this object's nickname on the server.
	 * @param nick the _nick to set
	 */
	void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * The real name of this user represented by this object on the server
	 * @return the _realname
	 */
	public String getRealname() {
		return realName;
	}

	/**
	 * The real name of this user represented by this object on the server
	 * @param realname the _realname to set
	 */
	void setRealname(String realname) {
		realName = realname;
	}

	/**
	 * The hostmask of the user represented by this object on this server
	 * @return the _hostmask
	 */
	public String getHostmask() {
		return hostmask;
	}

	/**
	 * The hostmask of the user represented by this object on this server
	 * @param hostmask the _hostmask to set
	 */
	void setHostmask(String hostmask) {
		this.hostmask = hostmask;
	}

	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 * @return the _away
	 */
	public boolean isAway() {
		return away;
	}

	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 * @param away the _away to set
	 */
	public void setAway(boolean away) {
		this.away = away;
	}

	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 * @return the _ircop
	 */
	public boolean isIrcop() {
		return ircop;
	}

	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 * @param ircop the _ircop to set
	 */
	public void setIrcop(boolean ircop) {
		this.ircop = ircop;
	}

	/**
	 * The server that the user represented by this object is joined to
	 * @return the _server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * The server that the user represented by this object is joined to
	 * @param server the _server to set
	 */
	void setServer(String server) {
		this.server = server;
	}

	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 * @return the _identified
	 */
	public boolean isIdentified() {
		return identified;
	}

	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 * @param identified the _identified to set
	 */
	void setIdentified(boolean identified) {
		this.identified = identified;
	}

	/**
	 * The number of hops it takes to the user represented by this object
	 * @return the hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * The number of hops it takes to the user represented by this object
	 * @param hops the hops to set
	 */
	public void setHops(int hops) {
		this.hops = hops;
	}

	/**
	 * The login of the user represented by this object on the server
	 * @return the _login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * The login of the user represented by this object on the server
	 * @param login the _login to set
	 */
	void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @return the _op
	 */
	public boolean isOp(String chan) {
		return bot.getChannel(chan).isOp(this);
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @param op the _op to set
	 */
	public void setOp(Channel chan, boolean op) {
		chan.setOp(this, op);
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @return the _voice
	 */
	public boolean hasVoice(String chan) {
		return bot.getChannel(chan).hasVoice(this);
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @param voice the _voice to set
	 */
	public void setVoice(Channel chan, boolean voice) {
		chan.setVoice(this, voice);
	}
}
