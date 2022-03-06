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

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.exception.NotReadyException;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.snapshot.ChannelSnapshot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a Channel that we're joined to.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = {"name", "bot"})
@Slf4j
@Getter
@Setter(AccessLevel.PROTECTED)
public class Channel implements Comparable<Channel> {
	/**
	 * The name of the channel. Will never change
	 */
	protected final String name;
	/**
	 * Unique UUID for this channel <i>instance</i>
	 */
	protected final UUID channelId;
	/**
	 * The bot that this channel came from
	 */
	protected final PircBotX bot;
	//Output is lazily created since it might not ever be used
	@Getter(AccessLevel.NONE)
	protected final AtomicSafeInitializer<OutputChannel> output = new AtomicSafeInitializer<OutputChannel>() {
		@Override
		protected OutputChannel initialize() {
			return bot.getConfiguration().getBotFactory().createOutputChannel(bot, Channel.this);
		}
	};
	@Setter(AccessLevel.NONE)
	protected String mode = "";
	/**
	 * The current channel topic
	 */
	protected String topic = "";
	/**
	 * Timestamp of when the topic was created. Defaults to 0
	 */
	protected long topicTimestamp;
	/**
	 * Timestamp of when channel was created. Defaults to 0
	 */
	protected long createTimestamp;
	/**
	 * The user who set the topic. Default is blank
	 */
	protected UserHostmask topicSetter;
	/**
	 * Moderated (+m) status
	 */
	protected boolean moderated = false;
	/**
	 * No external messages (+n) status
	 */
	protected boolean noExternalMessages = false;
	/**
	 * Invite only (+i) status
	 */
	protected boolean inviteOnly = false;
	/**
	 * Secret (+s) status
	 */
	protected boolean secret = false;
	/**
	 * Private (+p) status
	 */
	protected boolean channelPrivate = false;
	@Getter(AccessLevel.NONE)
	protected boolean topicProtection = false;
	/**
	 * Channel limit (+l #)
	 */
	protected int channelLimit = -1;
	/**
	 * Channel key (+k)
	 */
	protected String channelKey = null;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected CountDownLatch modeChangeLatch = null;
	@Getter(AccessLevel.NONE)
	protected final Object modeChangeLock = new Object();

	protected Channel(PircBotX bot, String name) {
		this.bot = bot;
		this.name = name;
		this.channelId = UUID.randomUUID();
	}

	/**
	 * Used by ChannelSnapshot
	 *
	 * @param channel
	 */
	protected Channel(Channel channel) {
		this.bot = channel.bot;
		this.name = channel.name;
		this.channelId = channel.channelId;
	}

	protected UserChannelDao<User, Channel> getDao() {
		return bot.getUserChannelDao();
	}

	/**
	 * Send a line to the channel.
	 *
	 * @return A {@link OutputChannel} for this channel
	 */
	public OutputChannel send() {
		try {
			return output.get();
		} catch (ConcurrentException ex) {
			throw new RuntimeException("Could not generate OutputChannel for " + getName(), ex);
		}
	}

	protected void parseMode(String rawMode) {
		synchronized (modeChangeLock) {
			if (rawMode.contains(" ") || (mode != null && mode.contains(" "))) {
				//Mode contains arguments which are impossible to parse.
				//Could be a ban command (we shouldn't use this), channel key (should, but where), etc
				//Need to ask server
				if (mode == null)
					log.trace("Unknown args in channel {} mode '{}', waiting on server to respond with mode", name, rawMode);
				else {
					log.trace("Unknown args in channel {} mode '{}', getting fresh mode", name, rawMode);
					mode = null;
					modeChangeLatch = new CountDownLatch(1); 
					
					send().getMode();
				}
			} else {
				//Parse mode by switching between removing and adding by the existance of a + or - sign
				if (mode == null) {
					log.error("Channel mode for {} was null - initializing to empty", this.name);
					mode = "";
				}
					
				
				boolean adding = true;
				for (char curChar : rawMode.toCharArray())
					if (curChar == '-')
						adding = false;
					else if (curChar == '+')
						adding = true;
					else if (adding)
						mode = mode + curChar;
					else
						mode = mode.replace(Character.toString(curChar), "");
			}
		}
	}

	/**
	 * Gets the channel mode. If mode is simple (no arguments), this will return
	 * immediately. If its not (mode with arguments, eg channel key), then asks
	 * the server for the correct mode, waiting until it gets a response
	 * <p>
	 * <b>WARNING:</b> Because of the last checking, a threaded listener manager
	 * like {@link ThreadedListenerManager} is required. Using a single threaded
	 * listener manager like
	 * {@link org.pircbotx.hooks.managers.GenericListenerManager} will mean this
	 * method <i>never returns</i>!
	 *
	 * @return A known good mode, either immediately or soon.
	 */
	public String getMode() throws NotReadyException {
		synchronized (modeChangeLock) {
			if (mode != null)
				return mode;
			else
				throw new NotReadyException("Mode not ready yet");
		}
	}
	
	public String getMode(String defaultValue)  {
		try {
			return getMode();
		} catch (NotReadyException e) {
			return defaultValue;
		}
	}

	public boolean containsMode(char modeLetter) throws NotReadyException {
		final String mode = getMode();
		if (mode.isEmpty())
			return false;
		
		String modeLetters = StringUtils.split(mode, ' ')[0];
		return StringUtils.contains(modeLetters, modeLetter);
	}
	
	public boolean containsMode(char modeLetter, boolean defaultValue) {
		try {
			final String mode = getMode();
			if (mode.isEmpty())
				return false;
			
			String modeLetters = StringUtils.split(mode, ' ')[0];
			return StringUtils.contains(modeLetters, modeLetter);
		} catch (NotReadyException e) {
			return defaultValue;
		}
	}

	/**
	 * Check if the channel has topic protection (+t) set.
	 *
	 * @return True if +t
	 */
	public boolean hasTopicProtection() {
		return topicProtection;
	}

	/**
	 * Get all levels the user holds in this channel.
	 *
	 * @param user The user to get the levels of
	 * @return An <b>immutable copy</b> of the levels the user holds
	 */
	public ImmutableSortedSet<UserLevel> getUserLevels(User user) {
		return getDao().getLevels(this, user);
	}

	/**
	 * Get all users that don't have any special status in this channel. This
	 * means that they aren't ops, have voice, superops, halops, or owners in
	 * this channel
	 *
	 * @return An <b>immutable copy</b> of normal users
	 */
	public ImmutableSortedSet<User> getNormalUsers() {
		return getDao().getNormalUsers(this);
	}

	/**
	 * Get all opped users in this channel.
	 *
	 * @return An <b>immutable copy</b> of opped users
	 */
	public ImmutableSortedSet<User> getOps() {
		return getDao().getUsers(this, UserLevel.OP);
	}

	/**
	 * Get all voiced users in this channel.
	 *
	 * @return An <b>immutable copy</b> of voiced users
	 */
	public ImmutableSortedSet<User> getVoices() {
		return getDao().getUsers(this, UserLevel.VOICE);
	}

	/**
	 * Get all users with Owner status in this channel.
	 *
	 * @return An <b>immutable copy</b> of users with Owner status
	 */
	public ImmutableSortedSet<User> getOwners() {
		return getDao().getUsers(this, UserLevel.OWNER);
	}

	/**
	 * Get all users with Half Operator status in this channel.
	 *
	 * @return An <b>immutable copy</b> of users with Half Operator status
	 */
	public ImmutableSortedSet<User> getHalfOps() {
		return getDao().getUsers(this, UserLevel.HALFOP);
	}

	/**
	 * Get all users with Super Operator status in this channel.
	 *
	 * @return An <b>immutable copy</b> of users with Super Operator status
	 */
	public ImmutableSortedSet<User> getSuperOps() {
		return getDao().getUsers(this, UserLevel.SUPEROP);
	}

	/**
	 * Sets the mode of the channel. If there is a getMode() waiting on this,
	 * fire it.
	 *
	 * @param mode
	 */
	protected void setMode(String mode, ImmutableList<String> modeParsed) {
		synchronized (modeChangeLock) {
			this.mode = mode;

			//Parse out mode
			PeekingIterator<String> params = Iterators.peekingIterator(modeParsed.iterator());

			//Process modes letter by letter, grabbing paramaters as needed
			boolean adding = true;
			String modeLetters = params.next();
			for (int i = 0; i < modeLetters.length(); i++) {
				char curModeChar = modeLetters.charAt(i);
				if (curModeChar == '+')
					adding = true;
				else if (curModeChar == '-')
					adding = false;
				else {
					ChannelModeHandler modeHandler = bot.getConfiguration().getChannelModeHandlers().get(curModeChar);
					if (modeHandler != null)
						modeHandler.handleMode(bot, this, null, null, params, adding, false);
				}
			}

			if (modeChangeLatch != null)
				modeChangeLatch.countDown();
		}
	}

	/**
	 * Get all users in this channel. }
	 *
	 * @return An <i>Unmodifiable</i> Set of users in this channel
	 */
	public ImmutableSortedSet<User> getUsers() {
		return getDao().getUsers(this);
	}

	/**
	 * Get all the user's nicks in this channel
	 *
	 * @return An <i>Unmodifiable</i> Set of user's nicks as String in this
	 * channel
	 */
	public ImmutableSortedSet<String> getUsersNicks() {
		ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.naturalOrder();
		for (User curUser : getDao().getUsers(this))
			builder.add(curUser.getNick());
		return builder.build();
	}

	/**
	 * Get the user that set the topic. As the user may or may not be in the
	 * channel return as a string
	 *
	 * @return The user that set the topic in String format
	 */
	public UserHostmask getTopicSetter() {
		return topicSetter;
	}

	/**
	 * Checks if the given user is an Operator in this channel
	 *
	 * @return True if the user is an Operator, false if not
	 */
	public boolean isOp(User user) {
		return getDao().levelContainsUser(UserLevel.OP, this, user);
	}

	/**
	 * Checks if the given user has Voice in this channel.
	 *
	 * @return True if the user has Voice, false if not
	 */
	public boolean hasVoice(User user) {
		return getDao().levelContainsUser(UserLevel.VOICE, this, user);
	}

	/**
	 * Checks if the given user is a Super Operator in this channel.
	 *
	 * @return True if the user is a Super Operator, false if not
	 */
	public boolean isSuperOp(User user) {
		return getDao().levelContainsUser(UserLevel.SUPEROP, this, user);
	}

	/**
	 * Checks if the given user is an Owner in this channel.
	 *
	 * @return True if the user is an Owner, false if not
	 */
	public boolean isOwner(User user) {
		return getDao().levelContainsUser(UserLevel.OWNER, this, user);
	}

	/**
	 * Checks if the given user is a Half Operator in this channel.
	 *
	 * @return True if the user is a Half Operator, false if not
	 */
	public boolean isHalfOp(User user) {
		return getDao().levelContainsUser(UserLevel.HALFOP, this, user);
	}

	/**
	 * Create an immutable snapshot of this channel.
	 *
	 * @return Immutable Channel copy minus the DAO
	 */
	public ChannelSnapshot createSnapshot() {
		return new ChannelSnapshot(this, mode);
	}

	/**
	 * Compare {@link #getName()} with {@link String#compareToIgnoreCase(java.lang.String)
	 * }. This is useful for sorting lists of Channel objects.
	 *
	 * @param other Other channel to compare to
	 * @return the result of calling compareToIgnoreCase on channel names.
	 */
	public int compareTo(Channel other) {
		return getName().compareToIgnoreCase(other.getName());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PircBotX> T getBot() {
		return (T) bot;
	}
}
