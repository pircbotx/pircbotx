/*
 * Copyright (C) 2009-2010 Leon Blakey
 *
 * Quackedbot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quackedbot  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

/**
 * This class is used to represent a user on an IRC server.
 * Instances of this class are returned by the getUsers method
 * in the PircBot class.

 *
 * @since   1.0.0
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class User implements Comparable<User> {
	/**
	 * The user represented by this object's nickname on the server.
	 */
	private String _nick;
	/**
	 * The real name of this user represented by this object on the server
	 */
	private String _realname = "";
	/**
	 * The login of the user represented by this object on the server
	 */
	private String _login = "";
	/**
	 * The hostmask of the user represented by this object on this server
	 */
	private String _hostmask = "";
	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 */
	private boolean _op = false;
	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 */
	private boolean _voice = false;
	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 */
	private boolean _away = false;
	/**
	 * Weather or not the user represented by this object is a zombie in the channel
	 * this object was fetched from
	 */
	private boolean _zombie = false;
	/**
	 * Weather or not the user represented by this object is deaf in the channel
	 * this object was fetched from
	 */
	private boolean _deaf = false;
	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 */
	private boolean _ircop = false;
	/**
	 * The server that the user represented by this object is joined to
	 */
	private String _server = "";
	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 */
	private boolean _identified = true;
	/**
	 * The number of hops it takes to the user represented by this object
	 */
	private int _hops = 0;

	User(String nick) {
		_nick = nick;
	}

	public void parseStatus(String prefix) {
		setOp(prefix.contains("@"));
		setVoice(prefix.contains("+"));
		setZombie(prefix.contains("+"));
		setDeaf(prefix.contains("d"));
		setZombie(prefix.contains("+"));
		setAway(prefix.contains("G")); //Assume here (H) if there is no G
		setIrcop(prefix.contains("*"));
	}

	/**
	 * Returns the nick of the user complete with their prefix if they
	 * have one, e.g. "@Dave".
	 *
	 * @return The user's prefix and nick.
	 */
	@Override
	public String toString() {
		String prefix = "";
		if (isOp())
			prefix = prefix + "@";
		if (hasVoice())
			prefix = prefix + "+";
		return prefix + getNick();
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
		return _nick;
	}

	/**
	 * The user represented by this object's nickname on the server.
	 * @param nick the _nick to set
	 */
	void setNick(String nick) {
		_nick = nick;
	}

	/**
	 * The real name of this user represented by this object on the server
	 * @return the _realname
	 */
	public String getRealname() {
		return _realname;
	}

	/**
	 * The real name of this user represented by this object on the server
	 * @param realname the _realname to set
	 */
	void setRealname(String realname) {
		_realname = realname;
	}

	/**
	 * The hostmask of the user represented by this object on this server
	 * @return the _hostmask
	 */
	public String getHostmask() {
		return _hostmask;
	}

	/**
	 * The hostmask of the user represented by this object on this server
	 * @param hostmask the _hostmask to set
	 */
	void setHostmask(String hostmask) {
		_hostmask = hostmask;
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @return the _op
	 */
	public boolean isOp() {
		return _op;
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @param op the _op to set
	 */
	public void setOp(boolean op) {
		_op = op;
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @return the _voice
	 */
	public boolean hasVoice() {
		return _voice;
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @param voice the _voice to set
	 */
	public void setVoice(boolean voice) {
		_voice = voice;
	}

	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 * @return the _away
	 */
	public boolean isAway() {
		return _away;
	}

	/**
	 * Weather or not the user represented by this object is away in the channel
	 * this object was fetched from
	 * @param away the _away to set
	 */
	public void setAway(boolean away) {
		_away = away;
	}

	/**
	 * Weather or not the user represented by this object is a zombie in the channel
	 * this object was fetched from
	 * @return the _zombie
	 */
	public boolean isZombie() {
		return _zombie;
	}

	/**
	 * Weather or not the user represented by this object is a zombie in the channel
	 * this object was fetched from
	 * @param zombie the _zombie to set
	 */
	public void setZombie(boolean zombie) {
		_zombie = zombie;
	}

	/**
	 * Weather or not the user represented by this object is deaf in the channel
	 * this object was fetched from
	 * @return the _deaf
	 */
	public boolean isDeaf() {
		return _deaf;
	}

	/**
	 * Weather or not the user represented by this object is deaf in the channel
	 * this object was fetched from
	 * @param deaf the _deaf to set
	 */
	public void setDeaf(boolean deaf) {
		_deaf = deaf;
	}

	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 * @return the _ircop
	 */
	public boolean isIrcop() {
		return _ircop;
	}

	/**
	 * Weather or not the user represented by this object is an IRCop on the server
	 * @param ircop the _ircop to set
	 */
	public void setIrcop(boolean ircop) {
		_ircop = ircop;
	}

	/**
	 * The server that the user represented by this object is joined to
	 * @return the _server
	 */
	public String getServer() {
		return _server;
	}

	/**
	 * The server that the user represented by this object is joined to
	 * @param server the _server to set
	 */
	void setServer(String server) {
		_server = server;
	}

	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 * @return the _identified
	 */
	public boolean isIdentified() {
		return _identified;
	}

	/**
	 * Weather or not the user represented by this object is identified. Note that
	 * on some server's this is not 100% reliable
	 * @param identified the _identified to set
	 */
	void setIdentified(boolean identified) {
		_identified = identified;
	}

	/**
	 * The number of hops it takes to the user represented by this object
	 * @return the hops
	 */
	public int getHops() {
		return _hops;
	}

	/**
	 * The number of hops it takes to the user represented by this object
	 * @param hops the hops to set
	 */
	public void setHops(int hops) {
		_hops = hops;
	}

	/**
	 * The login of the user represented by this object on the server
	 * @return the _login
	 */
	public String getLogin() {
		return _login;
	}

	/**
	 * The login of the user represented by this object on the server
	 * @param login the _login to set
	 */
	void setLogin(String login) {
		this._login = login;
	}
}
