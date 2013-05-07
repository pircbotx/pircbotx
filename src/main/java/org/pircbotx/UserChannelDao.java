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

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.io.Closeable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Synchronized;
import org.pircbotx.hooks.events.UserListEvent;

/**
 * Stores and maintains relationships between users and channels. This class should
 * not be directly, it is meant to be the internal storage engine.
 * @see User
 * @see Channel
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class UserChannelDao implements Closeable {
	protected final PircBotX bot;
	protected final Configuration.BotFactory botFactory;
	protected final Object accessLock = new Object();
	protected final UserChannelMap mainMap = new UserChannelMap();
	protected final EnumMap<UserLevel, UserChannelMap> levelsMap = Maps.newEnumMap(UserLevel.class);
	protected final HashBiMap<String, User> userNickMap = HashBiMap.create();
	protected final HashBiMap<String, Channel> channelNameMap = HashBiMap.create();
	protected final Set<User> privateUsers = new HashSet();

	public UserChannelDao(PircBotX bot, Configuration.BotFactory botFactory) {
		this.bot = bot;
		this.botFactory = botFactory;

		//Initialize levels map with a UserChannelMap for each level
		for (UserLevel level : UserLevel.values())
			levelsMap.put(level, new UserChannelMap());
	}

	@Synchronized("accessLock")
	public User getUser(String nick) {
		if (nick == null)
			throw new NullPointerException("Can't get a null user");
		User user = userNickMap.get(nick);
		if (user != null)
			return user;

		//Create new user
		user = botFactory.createUser(bot, nick);
		userNickMap.put(nick, user);
		return user;
	}

	@Synchronized("accessLock")
	public boolean userExists(String nick) {
		return userNickMap.containsKey(nick);
	}

	/**
	 * Get all user's in the channel. There are some important things to note about this method:
	 * <ul>
	 * <li>This method may not return a full list of users if you call it
	 * before the complete nick list has arrived from the IRC server.</li>
	 * <li>If you wish to find out which users are in a channel as soon
	 * as you join it, then you should listen for a {@link UserListEvent}
	 * instead of calling this method, as the {@link UserListEvent} is only
	 * dispatched as soon as the full user list has been received.</li>
	 * <li>This method will return immediately, as it does not require any
	 * interaction with the IRC server.</li>
	 * </ul>
	 *
	 * @since PircBot 1.0.0
	 *
	 * @param chan The channel object to search in
	 * @return A Set of all user's in the channel
	 *
	 * @see UserListEvent
	 */
	@Synchronized("accessLock")
	public ImmutableSet<User> getAllUsers() {
		return ImmutableSet.copyOf(userNickMap.values());
	}

	@Synchronized("accessLock")
	protected void addUserToChannel(User user, Channel channel) {
		mainMap.addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void addUserToPrivate(User user) {
		privateUsers.add(user);
	}

	@Synchronized("accessLock")
	protected void addUserToLevel(UserLevel level, User user, Channel channel) {
		levelsMap.get(level).addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void removeUserFromLevel(UserLevel level, User user, Channel channel) {
		levelsMap.get(level).removeUserFromChannel(user, channel);
	}

	@Synchronized("accessLock")
	public ImmutableSet<User> getNormalUsers(Channel channel) {
		Set<User> remainingUsers = new HashSet(mainMap.getUsers(channel));
		for(UserChannelMap curLevelMap : levelsMap.values())
			remainingUsers.removeAll(curLevelMap.getUsers(channel));
		return ImmutableSet.copyOf(remainingUsers);
	}

	@Synchronized("accessLock")
	public ImmutableSet<User> getUsers(Channel channel, UserLevel level) {
		return levelsMap.get(level).getUsers(channel);
	}
	
	public ImmutableSet<UserLevel> getLevels(Channel channel, User user) {
		ImmutableSet.Builder<UserLevel> builder = ImmutableSet.builder();
		for(Map.Entry<UserLevel, UserChannelMap> curEntry : levelsMap.entrySet())
			if(curEntry.getValue().containsEntry(user, channel))
				builder.add(curEntry.getKey());
		return builder.build();
	}

	@Synchronized("accessLock")
	public ImmutableSet<Channel> getNormalUserChannels(User user) {
		Set<Channel> remainingChannels = new HashSet(mainMap.getChannels(user));
		for(UserChannelMap curLevelMap : levelsMap.values())
			remainingChannels.removeAll(curLevelMap.getChannels(user));
		return ImmutableSet.copyOf(remainingChannels);
	}
	
	@Synchronized("accessLock")
	public ImmutableSet<Channel> getChannels(User user, UserLevel level) {
		return levelsMap.get(level).getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeUserFromChannel(User user, Channel channel) {
		mainMap.removeUserFromChannel(user, channel);
		for(UserChannelMap curLevelMap : levelsMap.values())
			curLevelMap.removeUserFromChannel(user, channel);

		if (!privateUsers.contains(user) && !mainMap.containsUser(user))
			//Completely remove user
			userNickMap.inverse().remove(user);
	}

	@Synchronized("accessLock")
	protected void removeUser(User user) {
		mainMap.removeUser(user);
		for(UserChannelMap curLevelMap : levelsMap.values())
			curLevelMap.removeUser(user);

		//Remove remaining locations
		userNickMap.inverse().remove(user);
		privateUsers.remove(user);
	}
	
	protected boolean levelContainsUser(UserLevel level, Channel channel, User user) {
		return levelsMap.get(level).containsEntry(user, channel);
	}

	@Synchronized("accessLock")
	protected void renameUser(User user, String newNick) {
		user.setNick(newNick);
		userNickMap.inverse().put(user, newNick);
	}

	@Synchronized("accessLock")
	public Channel getChannel(String name) {
		if (name == null)
			throw new NullPointerException("Can't get a null channel");
		Channel chan = channelNameMap.get(name);
		if (chan != null)
			return chan;

		//Channel does not exist, create one
		chan = botFactory.createChannel(bot, name);
		channelNameMap.put(name, chan);
		return chan;
	}

	/**
	 * Check if the bot is currently in the given channel.
	 * @param name A channel name as a string
	 * @return True if we are still connected to the channel, false if not
	 */
	@Synchronized
	public boolean channelExists(String name) {
		return channelNameMap.containsKey(name);
	}

	@Synchronized("accessLock")
	public ImmutableSet<User> getUsers(Channel channel) {
		return mainMap.getUsers(channel);
	}

	@Synchronized("accessLock")
	public ImmutableSet<Channel> getAllChannels() {
		return ImmutableSet.copyOf(channelNameMap.values());
	}

	@Synchronized("accessLock")
	public ImmutableSet<Channel> getChannels(User user) {
		return mainMap.getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeChannel(Channel channel) {
		mainMap.removeChannel(channel);
		for(UserChannelMap curLevelMap : levelsMap.values())
			curLevelMap.removeChannel(channel);

		//Remove remaining locations
		channelNameMap.remove(channel.getName());
	}

	@Synchronized("accessLock")
	public void close() {
		mainMap.clear();
		for(UserChannelMap curLevelMap : levelsMap.values())
			curLevelMap.clear();
		channelNameMap.clear();
		privateUsers.clear();
		userNickMap.clear();
	}
}
