/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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

import static com.google.common.base.Preconditions.*;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.exception.DaoException;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserChannelMapSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

/**
 * User-channel model that tracks all channels, users, users' in channels,
 * users' op level in channels, and private message users not in a channel.
 * <p>
 * All methods will throw a {@link NullPointerException} when any argument is
 * null
 *
 * @see User
 * @see Channel
 * @author Leon Blakey
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChannelDao<U extends User, C extends Channel> implements Closeable {
	protected final PircBotX bot;
	protected final Configuration.BotFactory botFactory;
	protected final Locale locale;
	protected final Object accessLock = new Object();
	protected final UserChannelMap<U, C> mainMap;
	protected final EnumMap<UserLevel, UserChannelMap<U, C>> levelsMap;
	protected final Map<String, U> userNickMap;
	protected final Map<String, C> channelNameMap;
	protected final Map<String, U> privateUsers;
	
	protected UserChannelDao(PircBotX bot, Configuration.BotFactory botFactory) {
		this.bot = bot;
		this.botFactory = botFactory;
		this.locale = bot.getConfiguration().getLocale();
		this.mainMap = new UserChannelMap<U, C>();
		this.userNickMap = Maps.newHashMap();
		this.channelNameMap = Maps.newHashMap();
		this.privateUsers = Maps.newHashMap();

		//Initialize levels map with a UserChannelMap for each level
		this.levelsMap = Maps.newEnumMap(UserLevel.class);
		for (UserLevel level : UserLevel.values())
			levelsMap.put(level, new UserChannelMap<U, C>());
	}

	@Synchronized("accessLock")
	public U getUser(@NonNull String nick) {
		checkArgument(StringUtils.isNotBlank(nick), "Cannot get a blank user");
		U user = userNickMap.get(nick.toLowerCase(locale));
		if (user != null)
			return user;

		//Create new user
		throw new DaoException(DaoException.Reason.UnknownUser, nick);
	}

	@Synchronized("accessLock")
	public U getUser(@NonNull UserHostmask userHostmask) {
		try {
			//Rarely we don't get the full hostmask
			//eg, the server setting your usermode when you connect to the server
			if (userHostmask.getNick() == null)
				return getUser(userHostmask.getHostmask());
			return getUser(userHostmask.getNick());
		} catch (Exception e) {
			throw new DaoException(DaoException.Reason.UnknownUserHostmask, userHostmask.toString(), e);
		}
	}

	@Synchronized("accessLock")
	@SuppressWarnings("unchecked")
	public U createUser(@NonNull UserHostmask userHostmask) {
		if (containsUser(userHostmask))
			throw new RuntimeException("Cannot create a user from hostmask that already exists: " + userHostmask);
		U user = (U) botFactory.createUser(userHostmask);
		userNickMap.put(userHostmask.getNick().toLowerCase(locale), user);
		return user;
	}

	@Synchronized("accessLock")
	@Deprecated
	public boolean userExists(@NonNull String nick) {
		return userNickMap.containsKey(nick.toLowerCase(locale));
	}

	@Synchronized("accessLock")
	public boolean containsUser(@NonNull String nick) {
		String nickLowercase = nick.toLowerCase(locale);
		return userNickMap.containsKey(nickLowercase) || privateUsers.containsKey(nickLowercase);
	}

	@Synchronized("accessLock")
	public boolean containsUser(@NonNull UserHostmask hostmask) {
		//Rarely we don't get the full hostmask
		//eg, the server setting your usermode when you connect to the server
		if (hostmask.getNick() == null)
			return containsUser(hostmask.getHostmask());
		return containsUser(hostmask.getNick());
	}

	/**
	 * Get all user's in the channel. There are some important things to note
	 * about this method:
	 * <ul>
	 * <li>This method may not return a full list of users if you call it before
	 * the complete nick list has arrived from the IRC server.</li>
	 * <li>If you wish to find out which users are in a channel as soon as you
	 * join it, then you should listen for a {@link UserListEvent} instead of
	 * calling this method, as the {@link UserListEvent} is only dispatched as
	 * soon as the full user list has been received.</li>
	 * <li>This method will return immediately, as it does not require any
	 * interaction with the IRC server.</li>
	 * </ul>
	 *
	 * @since PircBot 1.0.0
	 *
	 * @return A Set of all user's in the channel
	 *
	 * @see UserListEvent
	 */
	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getAllUsers() {
		return ImmutableSortedSet.copyOf(userNickMap.values());
	}

	@Synchronized("accessLock")
	protected void addUserToChannel(@NonNull U user, @NonNull C channel) {
		mainMap.addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void addUserToPrivate(@NonNull U user) {
		String nick = user.getNick().toLowerCase(locale);
		privateUsers.put(nick, user);
		if (!userNickMap.containsKey(nick))
			userNickMap.put(nick, user);
	}

	@Synchronized("accessLock")
	protected void addUserToLevel(@NonNull UserLevel level, @NonNull U user, @NonNull C channel) {
		levelsMap.get(level).addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void removeUserFromLevel(@NonNull UserLevel level, @NonNull U user, @NonNull C channel) {
		levelsMap.get(level).removeUserFromChannel(user, channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getNormalUsers(@NonNull C channel) {
		Set<U> remainingUsers = new HashSet<U>(mainMap.getUsers(channel));
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			remainingUsers.removeAll(curLevelMap.getUsers(channel));
		return ImmutableSortedSet.copyOf(remainingUsers);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getUsers(@NonNull C channel, @NonNull UserLevel level) {
		return levelsMap.get(level).getUsers(channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<UserLevel> getLevels(@NonNull C channel, @NonNull U user) {
		ImmutableSortedSet.Builder<UserLevel> builder = ImmutableSortedSet.naturalOrder();
		for (Map.Entry<UserLevel, UserChannelMap<U, C>> curEntry : levelsMap.entrySet())
			if (curEntry.getValue().containsEntry(user, channel))
				builder.add(curEntry.getKey());
		return builder.build();
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getNormalUserChannels(@NonNull U user) {
		Set<C> remainingChannels = new HashSet<C>(mainMap.getChannels(user));
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			remainingChannels.removeAll(curLevelMap.getChannels(user));
		return ImmutableSortedSet.copyOf(remainingChannels);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getChannels(@NonNull U user, @NonNull UserLevel level) {
		return levelsMap.get(level).getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeUserFromChannel(@NonNull U user, @NonNull C channel) {
		mainMap.removeUserFromChannel(user, channel);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeUserFromChannel(user, channel);

		if (!privateUsers.values().contains(user) && !mainMap.containsUser(user))
			//Completely remove user
			userNickMap.remove(user.getNick().toLowerCase(locale));
	}

	@Synchronized("accessLock")
	protected void removeUser(@NonNull U user) {
		mainMap.removeUser(user);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeUser(user);

		//Remove remaining locations
		userNickMap.remove(user.getNick().toLowerCase(locale));
		privateUsers.remove(user.getNick().toLowerCase(locale));
	}

	@Synchronized("accessLock")
	protected boolean levelContainsUser(@NonNull UserLevel level, @NonNull C channel, @NonNull U user) {
		return levelsMap.get(level).containsEntry(user, channel);
	}

	@Synchronized("accessLock")
	protected void renameUser(@NonNull U user, @NonNull String newNick) {
		String oldNick = user.getNick();

		user.setNick(newNick);
		userNickMap.remove(oldNick.toLowerCase(locale));
		userNickMap.put(newNick.toLowerCase(locale), user);
	}

	@Synchronized("accessLock")
	public C getChannel(@NonNull String name) {
		checkArgument(StringUtils.isNotBlank(name), "Cannot get a blank channel");
		C chan = channelNameMap.get(name.toLowerCase(locale));
		if (chan != null)
			return chan;

		//This could potentially be a mode message, strip off prefixes till we get a channel
		String modePrefixes = bot.getConfiguration().getChannelModeMessagePrefixes();
		if (modePrefixes.contains(Character.toString(name.charAt(0)))) {
			String nameTrimmed = name.toLowerCase(locale);
			do {
				nameTrimmed = nameTrimmed.substring(1);
				chan = channelNameMap.get(nameTrimmed);
				if (chan != null)
					return chan;
			} while (modePrefixes.contains(Character.toString(nameTrimmed.charAt(0))));
		}

		//Channel does not exist
		throw new DaoException(DaoException.Reason.UnknownChannel, name);
	}

	@Synchronized("accessLock")
	@SuppressWarnings("unchecked")
	public C createChannel(@NonNull String name) {
		C chan = (C) botFactory.createChannel(bot, name);
		channelNameMap.put(name.toLowerCase(locale), chan);
		return chan;
	}

	/**
	 * Check if the bot is currently in the given channel
	 * @param name
	 * @return
	 * @deprecated Use {@link #containsChannel(java.lang.String) }
	 */
	@Deprecated
	public boolean channelExists(@NonNull String name) {
		return containsChannel(name);
	}
	
	/**
	 * Check if the bot is currently in the given channel.
	 *
	 * @param name A channel name as a string
	 * @return True if we are still connected to the channel, false if not
	 */
	@Synchronized("accessLock")
	public boolean containsChannel(@NonNull String name) {
		if (channelNameMap.containsKey(name.toLowerCase(locale)))
			return true;
		
		//This could potentially be a mode message, strip off prefixes till we get a channel
		String modePrefixes = bot.getConfiguration().getChannelModeMessagePrefixes();
		if (modePrefixes.contains(Character.toString(name.charAt(0)))) {
			String nameTrimmed = name.toLowerCase(locale);
			do {
				nameTrimmed = nameTrimmed.substring(1);
				if (channelNameMap.containsKey(nameTrimmed))
					return true;
			} while (modePrefixes.contains(Character.toString(nameTrimmed.charAt(0))));
		}

		//Nope, doesn't exist
		return false;
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<U> getUsers(@NonNull C channel) {
		return mainMap.getUsers(channel);
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getAllChannels() {
		return ImmutableSortedSet.copyOf(channelNameMap.values());
	}

	@Synchronized("accessLock")
	public ImmutableSortedSet<C> getChannels(@NonNull U user) {
		return mainMap.getChannels(user);
	}

	@Synchronized("accessLock")
	protected void removeChannel(@NonNull C channel) {
		mainMap.removeChannel(channel);
		for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
			curLevelMap.removeChannel(channel);

		//Remove remaining locations
		channelNameMap.remove(channel.getName());
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
		for (Map.Entry<String, U> curNickEntry : userNickMap.entrySet())
			userNickMapSnapshotBuilder.put(curNickEntry.getKey(), userSnapshotMap.get(curNickEntry.getValue()));
		ImmutableBiMap.Builder<String, ChannelSnapshot> channelNameMapSnapshotBuilder = ImmutableBiMap.builder();
		for (Map.Entry<String, C> curName : channelNameMap.entrySet())
			channelNameMapSnapshotBuilder.put(curName.getKey(), channelSnapshotMap.get(curName.getValue()));
		ImmutableBiMap.Builder<String, UserSnapshot> privateUserSnapshotBuilder = ImmutableBiMap.builder();
		for (Map.Entry<String, U> curNickEntry : privateUsers.entrySet())
			privateUserSnapshotBuilder.put(curNickEntry.getKey(), userSnapshotMap.get(curNickEntry.getValue()));

		//Finally can create the snapshot object
		UserChannelDaoSnapshot daoSnapshot = new UserChannelDaoSnapshot(bot,
				locale,
				mainMapSnapshot,
				levelsMapSnapshot,
				userNickMapSnapshotBuilder.build(),
				channelNameMapSnapshotBuilder.build(),
				privateUserSnapshotBuilder.build());

		//Tell UserSnapshots and ChannelSnapshots what the new backing dao is
		for (UserSnapshot curUserSnapshot : userSnapshotMap.values())
			curUserSnapshot.setDao(daoSnapshot);
		for (ChannelSnapshot curChannelSnapshot : channelSnapshotMap.values())
			curChannelSnapshot.setDao(daoSnapshot);

		//Finally
		return daoSnapshot;
	}
}
