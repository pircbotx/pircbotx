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

import com.google.common.collect.ImmutableSet;
import java.util.concurrent.CountDownLatch;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.snapshot.ChannelSnapshot;

/**
 * Represents a Channel that we're joined to. 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@ToString(doNotUseGetters = true, exclude = {"outputCreated", "outputCreatedLock"})
@EqualsAndHashCode(of = {"name", "bot"})
@Slf4j
public class Channel {
	@Getter
	private final String name;
	@Getter(AccessLevel.PROTECTED)
	protected final UserChannelDao dao;
	@Getter
	protected final PircBotX bot;
	private String mode = "";
	@Setter(AccessLevel.PROTECTED)
	@Getter
	private String topic = "";
	@Setter(AccessLevel.PROTECTED)
	@Getter
	private long topicTimestamp;
	@Setter(AccessLevel.PROTECTED)
	@Getter
	private long createTimestamp;
	@Setter(AccessLevel.PROTECTED)
	private String topicSetter = "";
	protected boolean modeStale = false;
	protected CountDownLatch modeLatch = null;
	//Output is lazily created since it might not ever be used
	protected OutputChannel output = null;
	protected volatile boolean outputCreated = false;
	protected final Object outputCreatedLock = new Object[0];
	
	protected Channel(PircBotX bot, UserChannelDao dao, String name) {
		this.bot = bot;
		this.dao = dao;
		this.name = name;
	}
	
	/**
	 * Send a line to the channel
	 * @return A {@link OutputChannel} for this channel
	 */
	public OutputChannel send() {
		if(!outputCreated) {
			synchronized(outputCreatedLock) {
				this.output = bot.getConfiguration().getBotFactory().createOutputChannel(bot, this);
				this.outputCreated = true;
			}
		}
		return output;
	}

	protected void parseMode(String rawMode) {
		if (rawMode.contains(" ")) {
			//Mode contains arguments which are impossible to parse.
			//Could be a ban command (we shouldn't use this), channel key (should, but where), etc
			//Need to ask server
			modeStale = true;
			return;
		}

		//Parse mode by switching between removing and adding by the existance of a + or - sign
		boolean adding = true;
		for (char curChar : rawMode.toCharArray())
			if (curChar == '-')
				adding = false;
			else if (curChar == '+')
				adding = true;
			else if (adding)
				mode = curChar + mode;
			else
				mode = mode.replace(Character.toString(curChar), "");
	}

	/**
	 * Gets the channel mode. If mode is simple (no arguments), this will return immediately.
	 * If its not (mode with arguments, eg channel key), then asks the server for the
	 * correct mode, waiting until it gets a response
	 * <p>
	 * <b>WARNING:</b> Because of the last checking, a threaded listener manager like
	 * {@link ThreadedListenerManager} is required. Using a single threaded listener
	 * manager like {@link org.pircbotx.hooks.managers.GenericListenerManager} will 
	 * mean this method <i>never returns</i>!
	 * @return A known good mode, either immediately or soon.
	 */
	public String getMode() {
		if (!modeStale)
			return mode;

		//Mode is stale, get new mode from server
		try {
			bot.sendRaw().rawLine("MODE " + getName());
			if (modeLatch == null || modeLatch.getCount() == 0)
				modeLatch = new CountDownLatch(1);
			//Wait for setMode to be called
			modeLatch.await();
			//Mode is no longer stale since we have a good mode
			modeStale = false;
			//We have known good mode from server, now return
			return mode;
		} catch (InterruptedException e) {
			throw new RuntimeException("Waiting for mode response interrupted", e);
		}
	}

	protected boolean modeExists(char modeChar) {
		//Can't exist if there's nothing there
		if (getMode().isEmpty())
			return false;

		return getMode().split(" ")[0].contains("" + modeChar);
	}

	/**
	 * Check if channel is invite only (+i)
	 * @return True if +i
	 */
	public boolean isInviteOnly() {
		return modeExists('i');
	}

	/**
	 * Check if channel is moderated (+m)
	 * @return True if +m
	 */
	public boolean isModerated() {
		return modeExists('m');
	}

	/**
	 * Check if channel will not accept external messages (+n)
	 * @return True if +n
	 */
	public boolean isNoExternalMessages() {
		return modeExists('n');
	}

	/**
	 * Check if channel is secret (+s)
	 * @return True if +s
	 */
	public boolean isSecret() {
		return modeExists('s');
	}

	/**
	 * Check if the channel has topic protection (+t) set
	 * @return True if +t	
 */
	public boolean hasTopicProtection() {
		return modeExists('t');
	}

	protected String getModeArgument(char modeChar) {
		String cleanMode = (getMode().startsWith("+") || getMode().startsWith("-")) ? getMode().substring(1) : getMode();
		String[] modeParts = cleanMode.split(" ");

		//Make sure it exists
		if (!modeExists(modeChar))
			return null;

		//If its the fist one, use that
		if (cleanMode.startsWith("" + modeChar))
			return modeParts[1];

		//If its the last one, use that
		if (modeParts[0].endsWith("" + modeChar))
			return modeParts[modeParts.length - 1];

		//Its in the middle. Go through the modes and move which position we think the argument is
		int argCounter = 0;
		for (char curMode : getMode().split(" ")[0].toCharArray())
			if (curMode == modeChar)
				argCounter++;

		//If arg counter is 0, then the mode arg found
		if (argCounter == 0)
			throw new RuntimeException("Arg wasn't found yet it go to the loop");

		//Assume the argument here is the correct one
		return modeParts[argCounter];
	}

	/**
	 * Get the channel limit if it exists
	 * <p>
	 * <b>Note:</b> The returned value is the best effort guess of what the channel
	 * limit is. Unknown modes and their arguments may make the returned value wrong.
	 * Whether the returned value is null is not affected by this issue
	 * @return If its set, the best effort guess of what the channel limit is.
	 * If its not set, returns -1.
	 */
	public int getChannelLimit() {
		return Utils.tryParseInt(getModeArgument('l'), -1);
	}

	/**
	 * Get the channel key if it exists
	 * <p>
	 * <b>Note:</b> The returned value is the best effort guess of what the channel
	 * key is. Unknown modes and their arguments may make the returned value wrong.
	 * Whether the returned value is null is not affected by this issue
	 * @return If its set, the best effort guess of what the channel key is.
	 * If its not set, null.
	 */
	public String getChannelKey() {
		return getModeArgument('k');
	}
	
	/**
	 * Get all levels the user holds in this channel.
	 * @param user The user to get the levels of
	 * @return An <b>immutable copy</b> of the levels the user holds
	 */
	public ImmutableSet<UserLevel> getUserLevels(User user) {
		return getDao().getLevels(this, user);
	}

	/**
	 * Get all users that don't have any special status in this channel. This means
	 * that they aren't ops, have voice, superops, halops, or owners in this channel
	 * @return An <b>immutable copy</b> of normal users
	 */
	public ImmutableSet<User> getNormalUsers() {
		return getDao().getNormalUsers(this);
	}

	/**
	 * Get all opped users in this channel.
	 * @return An <b>immutable copy</b> of opped users
	 */
	public ImmutableSet<User> getOps() {
		return getDao().getUsers(this, UserLevel.OP);
	}

	/**
	 * Get all voiced users in this channel.
	 * @return An <b>immutable copy</b> of voiced users
	 */
	public ImmutableSet<User> getVoices() {
		return getDao().getUsers(this, UserLevel.VOICE);
	}

	/**
	 * Get all users with Owner status in this channel.
	 * @return An <b>immutable copy</b> of users with Owner status
	 */
	public ImmutableSet<User> getOwners() {
		return getDao().getUsers(this, UserLevel.OWNER);
	}

	/**
	 * Get all users with Half Operator status in this channel.
	 * @return An <b>immutable copy</b> of users with Half Operator status
	 */
	public ImmutableSet<User> getHalfOps() {
		return getDao().getUsers(this, UserLevel.HALFOP);
	}

	/**
	 * Get all users with Super Operator status in this channel.
	 * @return An <b>immutable copy</b> of users with Super Operator status
	 */
	public ImmutableSet<User> getSuperOps() {
		return getDao().getUsers(this, UserLevel.SUPEROP);
	}

	/**
	 * Sets the mode of the channel. If there is a getMode() waiting on this,
	 * fire it.
	 * @param mode
	 */
	void setMode(String mode) {
		this.mode = mode;
		this.modeStale = false;
		if (modeLatch != null && modeLatch.getCount() == 1)
			modeLatch.countDown();
	}

	/**
	 * Get all users in this channel. Simply calls {@link PircBotX#getUsers(org.pircbotx.Channel) }
	 * @return An <i>Unmodifiable</i> Set of users in this channel
	 */
	public ImmutableSet<User> getUsers() {
		return getDao().getUsers(this);
	}

	/**
	 * Get the user that set the topic. As the user may or may not be in the
	 * channel return as a string
	 * @return The user that set the topic in String format
	 */
	public String getTopicSetter() {
		return topicSetter;
	}

	/**
	 * Checks if the given user is an Operator in this channel
	 * @return True if the user is an Operator, false if not
	 */
	public boolean isOp(User user) {
		return getDao().levelContainsUser(UserLevel.OP, this, user);
	}

	/**
	 * Checks if the given user has Voice in this channel
	 * @return True if the user has Voice, false if not
	 */
	public boolean hasVoice(User user) {
		return getDao().levelContainsUser(UserLevel.VOICE, this, user);
	}

	/**
	 * Checks if the given user is a Super Operator in this channel
	 * @return True if the user is a Super Operator, false if not
	 */
	public boolean isSuperOp(User user) {
		return getDao().levelContainsUser(UserLevel.SUPEROP, this, user);
	}

	/**
	 * Checks if the given user is an Owner in this channel
	 * @return True if the user is an Owner, false if not
	 */
	public boolean isOwner(User user) {
		return getDao().levelContainsUser(UserLevel.OWNER, this, user);
	}

	/**
	 * Checks if the given user is a Half Operator in this channel
	 * @return True if the user is a Half Operator, false if not
	 */
	public boolean isHalfOp(User user) {
		return getDao().levelContainsUser(UserLevel.HALFOP, this, user);
	}
	
	public ChannelSnapshot createSnapshot() {
		if(modeStale)
			log.warn("Channel {} mode '{}' is stale", getName(), getMode());
		return new ChannelSnapshot(this, mode);
	}
}
