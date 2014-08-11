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

import org.pircbotx.snapshot.UserSnapshot;
import com.google.common.collect.ImmutableSortedSet;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.output.OutputUser;

/**
 * Represents a User on the server. 
 * @since PircBot 1.0.0
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by Leon Blakey <lord.quackstar at gmail.com> in <a href="http://pircbotx.googlecode.com">PircBotX</a>
 */
@Data
@Setter(AccessLevel.PROTECTED)
public class User extends UserHostmask {
	@Getter(AccessLevel.PROTECTED)
	private final UserChannelDao<User, Channel> dao;
	/**
	 * Realname/fullname of the user. Never changes
	 */
	private String realName = "";
	/**
	 * User's away status
	 */
	private String awayMessage = null;
	/**
	 * Users IRCop status
	 */
	private boolean ircop = false;
	/**
	 * The server the user is on.
	 */
	private String server = "";
	/**
	 * Number of hops to reach the user.
	 */
	private int hops = 0;

	@SuppressWarnings("unchecked")
	protected User(PircBotX bot, UserChannelDao<? extends User, ? extends Channel> dao, String hostmask, String nick, String login, String hostname) {
		super(bot, hostname, nick, login, hostmask);
		this.dao = (UserChannelDao<User, Channel>)dao;
	}

	/**
	 * Query the user with WHOIS to determine if they are verified *EXPENSIVE*.
	 * This is intended to be a quick utility method, if you need more specific
	 * info from the Whois then its recommended to listen for or use
	 * {@link PircBotX#waitFor(java.lang.Class) }
	 * @return True if the user is verified
	 */
	@SuppressWarnings("unchecked")
	public boolean isVerified() {
		try {
			send().whoisDetail();
			WaitForQueue waitForQueue = new WaitForQueue(getBot());
			while (true) {
				WhoisEvent event = waitForQueue.waitFor(WhoisEvent.class);
				if (!event.getNick().equals(getNick()))
					continue;

				//Got our event
				waitForQueue.close();
				return event.getRegisteredAs() != null;
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException("Couldn't finish querying user for verified status", ex);
		}
	}

	public UserSnapshot createSnapshot() {
		return new UserSnapshot(this);
	}
	
	/**
	 * Get all the levels this user holds in the channel.
	 * @param channel The channel to get the levels from
	 * @return An <b>immutable copy</b> of the levels this user holds
	 */
	public ImmutableSortedSet<UserLevel> getUserLevels(Channel channel) {
		return getDao().getLevels(channel, this);
	}

	/**
	 * Get all channels this user is a part of.
	 * @return All channels this user is a part of
	 */
	public ImmutableSortedSet<Channel> getChannels() {
		return getDao().getChannels(this);
	}

	/**
	 * Get all channels user has Operator status in.
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsOpIn() {
		return getDao().getChannels(this, UserLevel.OP);
	}

	/**
	 * Get all channels user has Voice status in.
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Voice status in
	 */
	public ImmutableSortedSet<Channel> getChannelsVoiceIn() {
		return getDao().getChannels(this, UserLevel.VOICE);
	}

	/**
	 * Get all channels user has Owner status in.
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Owner status in
	 */
	public ImmutableSortedSet<Channel> getChannelsOwnerIn() {
		return getDao().getChannels(this, UserLevel.OWNER);
	}

	/**
	 * Get all channels user has Half Operator status in.
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Half Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsHalfOpIn() {
		return getDao().getChannels(this, UserLevel.HALFOP);
	}

	/**
	 * Get all channels user has Super Operator status in. Simply calls 
	 * {@link UserChannelDao#getUsersSuperOps(org.pircbotx.User) }
	 * 
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Super Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsSuperOpIn() {
		return getDao().getChannels(this, UserLevel.SUPEROP);
	}

	/**
	 * The exact server that this user is joined to.
	 * @return The address of the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * The number of hops it takes to this user.
	 * @return the hops
	 */
	public int getHops() {
		return hops;
	}
	
	public boolean isAway() {
		return awayMessage != null;
	}
}
