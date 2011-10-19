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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

/**
 * Represents a Channel that we're joined to. Contains all the available 
 * information about the channel, as well as delegate methods for useful functions
 * like {@link #op(org.pircbotx.User) giving op} or 
 * {@link #voice(org.pircbotx.User) voice} status.
 * @author  Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@ToString(exclude = {"ops", "voices"}, doNotUseGetters = true)
@EqualsAndHashCode(of = {"name", "bot"})
@Setter(AccessLevel.PACKAGE)
public class Channel {
	private final String name;
	private String mode = "";
	private String topic = "";
	private long topicTimestamp;
	private long createTimestamp;
	private String topicSetter = "";
	protected final PircBotX bot;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected boolean modeStale = false;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected CountDownLatch modeLatch = null;
	/**
	 * Set of half op users in this channel
	 */
	protected final Set<User> halfOps = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Set of super ops users in this channel
	 */
	protected final Set<User> superOps = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Set of opped users in this channel
	 */
	protected final Set<User> ops = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Set of voiced users in this channel
	 */
	protected final Set<User> voices = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Set of super owner users in this channel
	 */
	protected final Set<User> owners = Collections.synchronizedSet(new HashSet<User>());

	protected Channel(PircBotX bot, String name) {
		this.bot = bot;
		this.name = name;
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
	 * manager like {@link GenericListenerManager} will mean this method <i>never returns</i>!
	 * @return A known good mode, either immediatly or soon. 
	 */
	public String getMode() {
		if (!modeStale)
			return mode;

		//Mode is stale, get new mode from server
		try {
			bot.sendRawLine("MODE " + getName());
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

	public boolean isInviteOnly() {
		return modeExists('i');
	}

	/**
	 * Check if channel is moderated (+m)
	 * @return True if set, false if not
	 */
	public boolean isModerated() {
		return modeExists('m');
	}

	/**
	 * Check if channel will not accept external messages (+n)
	 * @return True if set, false if not
	 */
	public boolean isNoExternalMessages() {
		return modeExists('n');
	}

	/**
	 * Check if channel is secret (+s)
	 * @return True if set, false if not
	 */
	public boolean isSecret() {
		return modeExists('s');
	}

	/**
	 * Check if the channel has topic protection (+t) set
	 * @return True if its set, false if not
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
			return modeParts[modeParts.length];

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
		try {
			return Integer.parseInt(getModeArgument('l'));
		} catch (NumberFormatException e) {
			//Can't parse it or null, return -1
			return -1;
		}
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
	 * Get all users that don't have any special status in this channel. This means
	 * that they aren't ops, have voice, superops, halops, or owners in this channel
	 * @return An <i>unmodifiable</i> Set (IE snapshot)  of non-special users in the channel
	 */
	public Set<User> getNormalUsers() {
		//Build set
		Set<User> normalUsers = new HashSet(bot.getUsers(this));
		normalUsers.removeAll(ops);
		normalUsers.removeAll(voices);
		normalUsers.removeAll(halfOps);
		normalUsers.removeAll(superOps);
		normalUsers.removeAll(owners);
		return Collections.unmodifiableSet(normalUsers);
	}

	/**
	 * Gets all opped users in this channel. 
	 * Be careful when storing the result from this method as it may be out of date 
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of opped users
	 */
	public Set<User> getOps() {
		return Collections.unmodifiableSet(ops);
	}

	/**
	 * Gets all voiced users in this channel. 
	 * Be careful when storing the result from this method as it may be out of date 
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of opped users
	 */
	public Set<User> getVoices() {
		return Collections.unmodifiableSet(voices);
	}

	/**
	 * Gets all users with Owner status in this channel. 
	 * Be careful when storing the result from this method as it may be out of date 
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of users with Owner status
	 */
	public Set<User> getOwners() {
		return Collections.unmodifiableSet(owners);
	}

	/**
	 * Gets all users with Half Operator status in this channel. 
	 * Be careful when storing the result from this method as it may be out of date 
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of users with Half Operator status
	 */
	public Set<User> getHalfOps() {
		return Collections.unmodifiableSet(halfOps);
	}

	/**
	 * Gets all users with Super Operator status in this channel. 
	 * Be careful when storing the result from this method as it may be out of date 
	 * by the time you use it again
	 * @return An <i>unmodifiable</i> Set (IE snapshot) of users with Super Operator status
	 */
	public Set<User> getSuperOps() {
		return Collections.unmodifiableSet(superOps);
	}

	/**
	 * Sets the mode of the channel. If there is a getMode() waiting on this,
	 * fire it. 
	 * @param mode 
	 */
	void setMode(String mode) {
		this.mode = mode;
		if (modeLatch != null && modeLatch.getCount() == 1)
			modeLatch.countDown();
	}

	/**
	 * Get all users in this channel. Simply calls {@link PircBotX#getUsers(org.pircbotx.Channel) }
	 * @return An <i>Unmodifiable</i> Set of users in this channel
	 */
	public Set<User> getUsers() {
		return bot.getUsers(this);
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
		return ops.contains(user);
	}

	/**
	 * Attempts to give Operator status to the given user in this channel. Simply 
	 * calls {@link PircBotX#op(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to Op
	 */
	public void op(User user) {
		bot.op(this, user);
	}

	/**
	 * Attempts to remove Operator status from the given user in this channel. 
	 * Simply calls {@link PircBotX#deOp(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to remove Operator status from
	 */
	public void deOp(User user) {
		bot.deOp(this, user);
	}

	/**
	 * Checks if the given user has Voice in this channel
	 * @return True if the user has Voice, false if not
	 */
	public boolean hasVoice(User user) {
		return voices.contains(user);
	}

	/**
	 * Attempts to give Voice status to the given user in this channel. Simply 
	 * calls {@link PircBotX#voice(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to voice
	 */
	public void voice(User user) {
		bot.voice(this, user);
	}

	/**
	 * Attempts to remove Voice status from the given user in this channel. Simply
	 * calls {@link PircBotX#deVoice(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to remove Voice from
	 */
	public void deVoice(User user) {
		bot.deVoice(this, user);
	}

	/**
	 * Attempts to give Super Operator status to the given user in this channel. Simply 
	 * calls {@link PircBotX#superOp(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to give Super Operator status
	 */
	public void superOp(User user) {
		bot.superOp(this, user);
	}

	/**
	 * Checks if the given user is a Super Operator in this channel
	 * @return True if the user is a Super Operator, false if not
	 */
	public boolean isSuperOp(User user) {
		return superOps.contains(user);
	}

	/**
	 * Attempts to remove Super Operator status from the given user in this channel. 
	 * Simply calls {@link PircBotX#deSuperOp(org.pircbotx.Channel, org.pircbotx.User)  }
	 * @param user The user to attempt to remove Super Operator status from
	 */
	public void deSuperOp(User user) {
		bot.deSuperOp(this, user);
	}

	/**
	 * Attempts to give Owner status to the given user in this channel. Simply 
	 * calls {@link PircBotX#owner(org.pircbotx.Channel, org.pircbotx.User) }
	 * @param user The user to attempt to give Owner status to
	 */
	public void owner(User user) {
		bot.owner(this, user);
	}

	/**
	 * Checks if the given user is an Owner in this channel
	 * @return True if the user is an Owner, false if not
	 */
	public boolean isOwner(User user) {
		return owners.contains(user);
	}

	/**
	 * Attempts to remove Owner status from the given user in this channel. 
	 * Simply calls {@link PircBotX#deOwner(org.pircbotx.Channel, org.pircbotx.User)  }
	 * @param user The user to attempt to remove Owner status from
	 */
	public void deOwner(User user) {
		bot.deOwner(this, user);
	}

	/**
	 * Attempts to give Half Operator status to the given user in this channel. Simply 
	 * calls {@link PircBotX#halfOp(org.pircbotx.Channel, org.pircbotx.User)  }
	 * @param user The user to attempt to give Half Operator status to
	 */
	public void halfOp(User user) {
		bot.halfOp(this, user);
	}

	/**
	 * Checks if the given user is a Half Operator in this channel
	 * @return True if the user is a Half Operator, false if not
	 */
	public boolean isHalfOp(User user) {
		return halfOps.contains(user);
	}

	/**
	 * Attempts to remove Half Operator status from the given user in this channel. 
	 * Simply calls {@link PircBotX#deHalfOp(org.pircbotx.Channel, org.pircbotx.User)  }
	 * @param user The user to attempt to remove Half Operator status from
	 */
	public void deHalfOp(User user) {
		bot.deHalfOp(this, user);
	}

	/**
	 * Removes user from op and voice lists
	 * @param user
	 */
	protected void removeUser(User user) {
		ops.remove(user);
		voices.remove(user);
		superOps.remove(user);
		halfOps.remove(user);
		owners.remove(user);
	}
}
