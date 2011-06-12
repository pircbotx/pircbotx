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
@ToString(exclude = {"ops", "voices"})
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
	@Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
	protected boolean modeStale = false;
	@Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
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
	
	Channel(PircBotX bot, String name) {
		this.bot = bot;
		this.name = name;
	}
	
	public void parseMode(String rawMode) {
		if(rawMode.contains(" ")) {
			//Mode contains arguments which are impossible to parse. 
			//Could be a ban command (we shouldn't use this), channel key (should, but where), etc
			//Need to ask server
			modeStale = true;
			return;
		}
		
		//Parse mode by switching between removing and adding by the existance of a + or - sign
		boolean adding = true;
		for (char curChar : rawMode.toCharArray()) {
			if (curChar == '-')
				adding = false;
			else if (curChar == '+')
				adding = true;
			else if (adding)
				//Add to beginning since end may contan arguments
				mode = curChar + mode;
			else
				mode = mode.replace(Character.toString(curChar), "");
		}
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
		if(!modeStale)
			return mode;
		
		//Mode is stale, get new mode from server
		try {
			bot.sendRawLine("MODE " + getName());
			if(modeLatch == null || modeLatch.getCount() == 0)
				modeLatch = new CountDownLatch(1);
			//Wait for setMode to be called
			modeLatch.await();
			//We have known good mode from server, now return
			return mode;
		} catch (InterruptedException e) {
			throw new RuntimeException("Waiting for mode response interrupted", e);
		}
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
	 * Sets the mode of the channel. If there is a getMode() waiting on this,
	 * fire it. 
	 * @param mode 
	 */
	void setMode(String mode) {
		this.mode = mode;
		if(modeLatch != null && modeLatch.getCount() == 1)
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
	 * Checks if the given use is an Op or not in this channel
	 * @return True if the user is an op, false if not
	 */
	public boolean isOp(User user) {
		return ops.contains(user);
	}

	/*
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
	 * Checks if the given use has Voice or not in this channel
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
