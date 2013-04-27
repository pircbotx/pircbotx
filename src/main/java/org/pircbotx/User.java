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
package org.pircbotx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

/**
 * Represents a User on the server. Contains all the available information about
 * the user as well as some useful delegate methods.
 * status.
 * @since PircBot 1.0.0
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by Leon Blakey <lord.quackstar at gmail.com> in <a href="http://pircbotx.googlecode.com">PircBotX</a>
 */
@Data
@EqualsAndHashCode(of = {"uuid", "bot"})
@Setter(AccessLevel.PROTECTED)
public class User implements Comparable<User> {
	private String nick;
	private String realName = "";
	private String login = "";
	private String hostmask = "";
	private boolean away = false;
	private boolean ircop = false;
	private String server = "";
	private int hops = 0;
	protected final PircBotX bot;
	protected final UserChannelDao dao;
	@Getter(AccessLevel.NONE)
	protected final UUID uuid = UUID.randomUUID();

	protected User(PircBotX bot, UserChannelDao dao, String nick) {
		this.bot = bot;
		this.dao = dao;
		this.nick = nick;
	}

	/**
	 * Query the user with WHOIS to determine if they are verified *EXPENSIVE*.
	 * This is intended to be a quick utility method, if you need more specific
	 * info from the Whois then its recommended to listen for or use
	 * {@link PircBotX#waitFor(java.lang.Class) }
	 * @return True if the user is verified
	 */
	public boolean isVerified() {
		try {
			bot.sendRawLine("WHOIS " + getNick() + " " + getNick());
			WaitForQueue waitForQueue = new WaitForQueue(bot);
			while (true) {
				WhoisEvent event = waitForQueue.waitFor(WhoisEvent.class);
				if (!event.getNick().equals(nick))
					continue;

				//Got our event
				waitForQueue.close();
				return event.getRegisteredAs() != null && !event.getRegisteredAs().isEmpty();
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException("Couldn't finish querying user for verified status", ex);
		}
	}

	public UserSnapshot generateSnapshot() {
		return new UserSnapshot(this);
	}

	/**
	 * Get all channels this user is a part of
	 * @return All channels this user is a part of
	 */
	public Set<Channel> getChannels() {
		return dao.getChannels(this);
	}

	/**
	 * Get all channels user has Operator status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Operator status in
	 */
	public Set<Channel> getChannelsOpIn() {
		return dao.getUsersOps(this);
	}

	/**
	 * Get all channels user has Voice status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Voice status in
	 */
	public Set<Channel> getChannelsVoiceIn() {
		return dao.getUsersVoices(this);
	}

	/**
	 * Get all channels user has Owner status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Owner status in
	 */
	public Set<Channel> getChannelsOwnerIn() {
		return dao.getUsersOwners(this);
	}

	/**
	 * Get all channels user has Half Operator status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Half Operator status in
	 */
	public Set<Channel> getChannelsHalfOpIn() {
		return dao.getUsersHalfOps(this);
	}

	/**
	 * Get all channels user has Super Operator status in. Simply calls 
	 * {@link UserChannelDao#getUsersSuperOps(org.pircbotx.User) }
	 * 
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Super Operator status in
	 */
	public Set<Channel> getChannelsSuperOpIn() {
		return dao.getUsersSuperOps(this);
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
	 * Send a message to this user. See {@link PircBotX#sendMessage(org.pircbotx.User, java.lang.String)}
	 * for more information
	 */
	public void sendMessage(String message) {
		getBot().sendMessage(this, message);
	}
}
