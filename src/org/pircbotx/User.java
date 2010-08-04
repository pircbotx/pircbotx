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
 *  <p>
 * Note that this class no longer implements the Comparable interface
 * for Java 1.1 compatibility reasons.
 *
 * @since   1.0.0
 * @author  Origionally by Paul James Mutton,
 *          <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 *          <p/>Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class User implements Comparable<User> {
	private String _nick;
	private String _lowerNick;
	private String _name;
	private String _hostmask;
	private boolean _op;
	private boolean _voice;

	User(String nick, String name, String hostmask) {
		_nick = nick;
		_lowerNick = nick.toLowerCase();
		_hostmask = hostmask;
		_name = name;
	}

	User(String nick, String name, String hostmask, String prefix) {
		_nick = nick;
		_lowerNick = nick.toLowerCase();
		_hostmask = hostmask;
		_name = name;
		if(prefix.contains("@"))
			_op = true;
		if(prefix.contains("+"))
			_voice = true;
	}

	/**
	 * Constructs a User object with a known prefix and nick.
	 *
	 * @param prefix The status of the user, for example, "@".
	 * @param nick The nick of the user.
	 */
	User(String nick, String name, String hostmask, boolean op, boolean voice) {
		_nick = nick;
		_lowerNick = nick.toLowerCase();
		_hostmask = hostmask;
		_name = name;
		_op = op;
		_voice = voice;
	}

	/**
	 * Returns whether or not the user represented by this object is an
	 * operator. If the User object has been obtained from a list of users
	 * in a channel, then this will reflect the user's operator status in
	 * that channel.
	 *
	 * @return true if the user is an operator in the channel.
	 */
	public boolean isOp() {
		return _op;
	}

	public void setOp(boolean op) {
		_op = op;
	}

	/**
	 * Returns whether or not the user represented by this object has
	 * voice. If the User object has been obtained from a list of users
	 * in a channel, then this will reflect the user's voice status in
	 * that channel.
	 *
	 * @return true if the user has voice in the channel.
	 */
	public boolean hasVoice() {
		return _voice;
	}

	public void setVoice(boolean voice) {
		_voice = voice;
	}

	public User parsePrefix(String prefix) {

		return this;
	}

	/**
	 * Returns the nick of the user.
	 *
	 * @return The user's nick.
	 */
	public String getNick() {
		return _nick;
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
		if(_op)
			prefix = prefix+"@";
		if(_voice)
			prefix = prefix+"+";
		return prefix + this.getNick();
	}

	/**
	 * Returns true if the nick represented by this User object is the same
	 * as the argument. A case insensitive comparison is made.
	 *
	 * @return true if the nicks are identical (case insensitive).
	 */
	public boolean equals(String nick) {
		return nick.toLowerCase().equals(_lowerNick);
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
			return other._lowerNick.equals(_lowerNick);
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
		return _lowerNick.hashCode();
	}

	/**
	 * Returns the result of calling the compareTo method on lowercased
	 * nicks. This is useful for sorting lists of User objects.
	 *
	 * @return the result of calling compareTo on lowercased nicks.
	 */
	@Override
	public int compareTo(User other) {
		return other._lowerNick.compareTo(_lowerNick);
	}

	public void setNick(String nick) {
		_nick = nick;
		_lowerNick = nick.toLowerCase();
	}

	/**
	 * Gets the Name of the user
	 *
	 * @return User's name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Get the HostMask of the user
	 *
	 * @return User's hostmask
	 */
	public String getHostmask() {
		return _hostmask;
	}
}