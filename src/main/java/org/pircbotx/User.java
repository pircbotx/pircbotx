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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString(exclude = {"outputCreated", "outputCreatedLock"})
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
	@Getter(AccessLevel.PROTECTED)
	protected final UserChannelDao dao;
	@Getter(AccessLevel.NONE)
	protected final UUID uuid = UUID.randomUUID();
	//Output is lazily created since it might not ever be used
	protected OutputUser output = null;
	protected volatile boolean outputCreated = false;
	protected final Object outputCreatedLock = new Object[0];

	protected User(PircBotX bot, UserChannelDao dao, String nick) {
		this.bot = bot;
		this.dao = dao;
		this.nick = nick;
	}
	
	/**
	 * Send a line to the user
	 * @return A {@link OutputUser} for this user
	 */
	public OutputUser send() {
		if(!outputCreated) {
			synchronized(outputCreatedLock) {
				this.output = bot.getConfiguration().getBotFactory().createOutputUser(bot, this);
				this.outputCreated = true;
			}
		}
		return output;
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
			bot.sendRaw().rawLine("WHOIS " + getNick() + " " + getNick());
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

	public UserSnapshot createSnapshot() {
		return new UserSnapshot(this);
	}
	
	/**
	 * Get all the levels this user holds in the channel
	 * @param channel The channel to get the levels from
	 * @return An <b>immutable copy</b> of the levels this user holds
	 */
	public ImmutableSet<UserLevel> getUserLevels(Channel channel) {
		return getDao().getLevels(channel, this);
	}

	/**
	 * Get all channels this user is a part of
	 * @return All channels this user is a part of
	 */
	public ImmutableSet<Channel> getChannels() {
		return getDao().getChannels(this);
	}

	/**
	 * Get all channels user has Operator status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Operator status in
	 */
	public ImmutableSet<Channel> getChannelsOpIn() {
		return getDao().getChannels(this, UserLevel.OP);
	}

	/**
	 * Get all channels user has Voice status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Voice status in
	 */
	public ImmutableSet<Channel> getChannelsVoiceIn() {
		return getDao().getChannels(this, UserLevel.VOICE);
	}

	/**
	 * Get all channels user has Owner status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Owner status in
	 */
	public ImmutableSet<Channel> getChannelsOwnerIn() {
		return getDao().getChannels(this, UserLevel.OWNER);
	}

	/**
	 * Get all channels user has Half Operator status in
	 * Be careful when storing the result from this method as it may be out of date
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Half Operator status in
	 */
	public ImmutableSet<Channel> getChannelsHalfOpIn() {
		return getDao().getChannels(this, UserLevel.HALFOP);
	}

	/**
	 * Get all channels user has Super Operator status in. Simply calls 
	 * {@link UserChannelDao#getUsersSuperOps(org.pircbotx.User) }
	 * 
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of all channels Get all
	 * channels user has Super Operator status in
	 */
	public ImmutableSet<Channel> getChannelsSuperOpIn() {
		return getDao().getChannels(this, UserLevel.SUPEROP);
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
}
