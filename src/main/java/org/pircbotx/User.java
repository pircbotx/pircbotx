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
package org.pircbotx;

import org.pircbotx.snapshot.UserSnapshot;
import com.google.common.collect.ImmutableSortedSet;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

/**
 * Represents a User on the server.
 *
 * @since PircBot 1.0.0
 */
@Getter
@ToString(callSuper = true)
@Setter(AccessLevel.PROTECTED)
public class User extends UserHostmask {
	private final UUID userId;
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
	 * The exact server that this user is joined to.
	 *
	 * @return The address of the server
	 */
	private String server = "";
	/**
	 * The number of hops it takes to this user.
	 */
	private int hops = 0;

	protected User(UserHostmask hostmask) {
		super(hostmask);
		userId = UUID.randomUUID();
	}
	
	/**
	 * Needed for UserSnapshot, 2nd parameter to prevent potential bugs from overloading
	 * @return 
	 */
	protected User(User user, boolean unused) {
		super(user);
		userId = user.userId;
	}
	
	protected UserChannelDao<User, Channel> getDao() {
		return bot.getUserChannelDao();
	}

	/**
	 * Query the user with WHOIS to determine if they are verified *EXPENSIVE*.
	 * This is intended to be a quick utility method, if you need more specific
	 * info from the Whois then its recommended to listen for or use
	 * {@link WaitForQueue }
	 *
	 * @return True if the user is verified
	 */
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
	 *
	 * @param channel The channel to get the levels from
	 * @return An <b>immutable copy</b> of the levels this user holds
	 */
	public ImmutableSortedSet<UserLevel> getUserLevels(Channel channel) {
		return getDao().getLevels(channel, this);
	}

	/**
	 * Get all channels this user is a part of.
	 *
	 * @return All channels this user is a part of
	 */
	public ImmutableSortedSet<Channel> getChannels() {
		return getDao().getChannels(this);
	}

	/**
	 * Get all channels user has Operator status in. Be careful when storing the
	 * result from this method as it may be out of date by the time you use it
	 * again
	 *
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsOpIn() {
		return getDao().getChannels(this, UserLevel.OP);
	}

	/**
	 * Get all channels user has Voice status in. Be careful when storing the
	 * result from this method as it may be out of date by the time you use it
	 * again
	 *
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Voice status in
	 */
	public ImmutableSortedSet<Channel> getChannelsVoiceIn() {
		return getDao().getChannels(this, UserLevel.VOICE);
	}

	/**
	 * Get all channels user has Owner status in. Be careful when storing the
	 * result from this method as it may be out of date by the time you use it
	 * again
	 *
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Owner status in
	 */
	public ImmutableSortedSet<Channel> getChannelsOwnerIn() {
		return getDao().getChannels(this, UserLevel.OWNER);
	}

	/**
	 * Get all channels user has Half Operator status in. Be careful when
	 * storing the result from this method as it may be out of date by the time
	 * you use it again
	 *
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Half Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsHalfOpIn() {
		return getDao().getChannels(this, UserLevel.HALFOP);
	}

	/**
	 * Get all channels user has Super Operator status in.
	 *
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Super Operator status in
	 */
	public ImmutableSortedSet<Channel> getChannelsSuperOpIn() {
		return getDao().getChannels(this, UserLevel.SUPEROP);
	}

	public boolean isAway() {
		return awayMessage != null;
	}

	@Override
	public boolean equals(Object user) {
		return super.equals(user);
	}

	/**
	 * Hash code generated from UUID
	 */
	@Override
	public int hashCode() {
		return userId.hashCode();
	}
}
