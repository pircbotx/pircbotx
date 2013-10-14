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

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserChannelMapSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

/**
 * Stores and maintains relationships between users and channels. This class should
 * not be directly, it is meant to be the internal storage engine.
 * @see User
 * @see Channel
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChannelDao<U extends User, C extends Channel> implements Closeable {
	protected final PircBotX bot;
	protected final Configuration.BotFactory botFactory;
	protected final Locale locale;
	protected final Object accessLock = new Object();
	protected final UserChannelMap<U, C> mainMap;
	protected final EnumMap<UserLevel, UserChannelMap<U, C>> levelsMap;
	protected final BiMap<String, U> userNickMap;
	protected final BiMap<String, C> channelNameMap;
	protected final Set<U> privateUsers;

	public UserChannelDao(PircBotX bot, Configuration.BotFactory botFactory) {
		this.bot = bot;
		this.botFactory = botFactory;
		this.locale = bot.getConfiguration().getLocale();
		this.mainMap = new UserChannelMap<U, C>();
		this.userNickMap = HashBiMap.create();
		this.channelNameMap = HashBiMap.create();
		this.privateUsers = new HashSet<U>();

		//Initialize levels map with a UserChannelMap for each level
		this.levelsMap = Maps.newEnumMap(UserLevel.class);
		for (UserLevel level : UserLevel.values())
			levelsMap.put(level, new UserChannelMap<U, C>());
	}

	@Synchronized("accessLock")
	public U getUser(String nick) {
		checkArgument(StringUtils.isNotBlank(nick), "Cannot get a blank user");
		U user = userNickMap.get(nick.toLowerCase(locale));
		if (user != null)
			return user;

		//Create new user
		user = (U) botFactory.createUser(bot, nick);
		userNickMap.put(nick.toLowerCase(locale), user);
		return user;
	}

	@Synchronized("accessLock")
	public boolean userExists(String nick) {
		return userNickMap.containsKey(nick.toLowerCase(locale));
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
	public ImmutableSortedSet<U> getAllUsers() {
		return ImmutableSortedSet.copyOf(userNickMap.values());
	}

	@Synchronized("accessLock")
	protected void addUserToChannel(U user, C channel) {
		mainMap.addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void addUserToPrivate(U user) {
		privateUsers.add(user);
	}

	@Synchronized("accessLock")
	protected void addUserToLevel(UserLevel level, U user, C channel) {
		levelsMap.get(level).addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void removeUserFromLevel(UserLevel level, U user, C channel) {
		levelsMap.get(level).removeUserFromChannel(user, channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getNormalUsers(C channel) {
		Set<U> remainingUsers = new HashSet<U>(mainMap.getUsers(channel));
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			remainingUsers.removeAll(curLevelMap.getUsers(channel));
		return ImmutableSortedSet.copyOf(remainingUsers);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getUsers(C channel, UserLevel level) {
		return levelsMap.get(level).getUsers(channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<UserLevel> getLevels(C channel, U user) {
		ImmutableSortedSet.Builder<UserLevel> builder = ImmutableSortedSet.naturalOrder();
		for (Map.Entry<UserLevel, UserChannelMap<U, C>> curEntry : levelsMap.entrySet())
			if (curEntry.getValue().containsEntry(user, channel))
				builder.add(curEntry.getKey());
		return builder.build();
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getNormalUserChannels(U user) {
		Set<C> remainingChannels = new HashSet<C>(mainMap.getChannels(user));
		for (UserChannelMap<U, C>  curLevelMap : levelsMap.values())
			remainingChannels.removeAll(curLevelMap.getChannels(user));
		return ImmutableSortedSet.copyOf(remainingChannels);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getChannels(U user, UserLevel level) {
		return levelsMap.get(level).getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeUserFromChannel(U user, C channel) {
		mainMap.removeUserFromChannel(user, channel);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeUserFromChannel(user, channel);

		if (!privateUsers.contains(user) && !mainMap.containsUser(user))
			//Completely remove user
			userNickMap.inverse().remove(user);
	}

	@Synchronized("accessLock")
	protected void removeUser(U user) {
		mainMap.removeUser(user);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeUser(user);

		//Remove remaining locations
		userNickMap.inverse().remove(user);
		privateUsers.remove(user);
	}

	@Synchronized("accessLock")
	protected boolean levelContainsUser(UserLevel level, C channel, U user) {
		return levelsMap.get(level).containsEntry(user, channel);
	}

	@Synchronized("accessLock")
	protected void renameUser(U user, String newNick) {
		user.setNick(newNick);
		userNickMap.inverse().remove(user);
		userNickMap.put(newNick.toLowerCase(locale), user);
	}

	@Synchronized("accessLock")
	public C getChannel(String name) {
		checkArgument(StringUtils.isNotBlank(name), "Cannot get a blank channel");
		C chan = channelNameMap.get(name.toLowerCase(locale));
		if (chan != null)
			return chan;

		//Channel does not exist, create one
		chan = (C) botFactory.createChannel(bot, name);
		channelNameMap.put(name.toLowerCase(locale), chan);
		return chan;
	}

	/**
	 * Check if the bot is currently in the given channel.
	 * @param name A channel name as a string
	 * @return True if we are still connected to the channel, false if not
	 */
	@Synchronized("accessLock")
	public boolean channelExists(String name) {
		return channelNameMap.containsKey(name.toLowerCase(locale));
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getUsers(C channel) {
		return mainMap.getUsers(channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getAllChannels() {
		return ImmutableSortedSet.copyOf(channelNameMap.values());
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getChannels(U user) {
		return mainMap.getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeChannel(C channel) {
		mainMap.removeChannel(channel);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeChannel(channel);

		//Remove remaining locations
		channelNameMap.inverse().remove(channel);
	}

	@Synchronized("accessLock")
	public void close() {
		mainMap.clear();
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.clear();
		channelNameMap.clear();
		privateUsers.clear();
		userNickMap.clear();
	}

	@Synchronized("accessLock")
	public UserChannelDaoSnapshot createSnapshot() {
		//Create snapshots of all users and channels
		ImmutableMap.Builder<U, UserSnapshot> userSnapshotBuilder = ImmutableMap.builder();
		for (U curUser : userNickMap.values())
			userSnapshotBuilder.put(curUser, curUser.createSnapshot());
		ImmutableMap<U, UserSnapshot> userSnapshotMap = userSnapshotBuilder.build();
		ImmutableMap.Builder<C, ChannelSnapshot> channelSnapshotBuilder = ImmutableMap.builder();
		for (C curChannel : channelNameMap.values())
			channelSnapshotBuilder.put(curChannel, curChannel.createSnapshot());
		ImmutableMap<C, ChannelSnapshot> channelSnapshotMap = channelSnapshotBuilder.build();

		//Make snapshots of the relationship maps using the above user and channel snapshots
		UserChannelMapSnapshot mainMapSnapshot = mainMap.createSnapshot(userSnapshotMap, channelSnapshotMap);
		EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMapSnapshot = Maps.newEnumMap(UserLevel.class);
		for (Map.Entry<UserLevel, UserChannelMap<U, C>> curLevel : levelsMap.entrySet())
			levelsMapSnapshot.put(curLevel.getKey(), curLevel.getValue().createSnapshot(userSnapshotMap, channelSnapshotMap));
		ImmutableBiMap.Builder<String, UserSnapshot> userNickMapSnapshotBuilder = ImmutableBiMap.builder();
		for (Map.Entry<String, U> curNick : userNickMap.entrySet())
			userNickMapSnapshotBuilder.put(curNick.getKey(), curNick.getValue().createSnapshot());
		ImmutableBiMap.Builder<String, ChannelSnapshot> channelNameMapSnapshotBuilder = ImmutableBiMap.builder();
		for (Map.Entry<String, C> curName : channelNameMap.entrySet())
			channelNameMapSnapshotBuilder.put(curName.getKey(), curName.getValue().createSnapshot());
		ImmutableSortedSet.Builder<UserSnapshot> privateUserSnapshotBuilder = ImmutableSortedSet.naturalOrder();
		for (User curUser : privateUsers)
			privateUserSnapshotBuilder.add(curUser.createSnapshot());

		//Finally can create the snapshot object
		UserChannelDaoSnapshot daoSnapshot = new UserChannelDaoSnapshot(bot,
				locale,
				mainMapSnapshot,
				levelsMapSnapshot,
				userNickMapSnapshotBuilder.build(),
				channelNameMapSnapshotBuilder.build(),
				privateUserSnapshotBuilder.build());
		
		//Tell UserSnapshots and ChannelSnapshots what the new backing dao is
		for(UserSnapshot curUserSnapshot : userSnapshotMap.values())
			curUserSnapshot.setDao(daoSnapshot);
		for(ChannelSnapshot curChannelSnapshot : channelSnapshotMap.values())
			curChannelSnapshot.setDao(daoSnapshot);
		
		//Finally
		return daoSnapshot;
	}
}
