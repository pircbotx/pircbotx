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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Closeable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.exception.DaoException;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserChannelMapSnapshot;
import org.pircbotx.snapshot.UserSnapshot;
import org.pircbotx.tools.ConcurrentEnumMap;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Model that creates and tracks Users and Channel and maintains relationships.
 * This includes channel users, channel op/voice/etc users, private messages,
 * etc
 * <p>
 * All methods will throw a {@link NullPointerException} when any argument is
 * null
 *
 * @see User
 * @see Channel
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChannelDao<U extends User, C extends Channel> implements Closeable {
	protected final PircBotX bot;
	protected final Configuration.BotFactory botFactory;
	protected final Locale locale;
	protected final Object accessLock = new Object();
	protected final UserChannelMap<U, C> mainMap;
	protected final Map<UserLevel, UserChannelMap<U, C>> levelsMap;
	protected final Map<String, U> userNickMap;
	protected final Map<String, C> channelNameMap;

	
    private final ReentrantReadWriteLock reentlock = new ReentrantReadWriteLock();
    private final Lock rL = reentlock.readLock();
    private final Lock wL = reentlock.writeLock();

	protected UserChannelDao(PircBotX bot, Configuration.BotFactory botFactory) {
		this.bot = bot;
		this.botFactory = botFactory;
		this.locale = bot.getConfiguration().getLocale();
		this.mainMap = new UserChannelMap<U, C>();
		
		userNickMap = new HashMap<>();
		channelNameMap = new HashMap<>();		

		//Initialize levels map with a UserChannelMap for each level		
		this.levelsMap = new ConcurrentEnumMap<>(UserLevel.class);
		for (UserLevel level : UserLevel.values())
			levelsMap.put(level, new UserChannelMap<U, C>());
	}

	/**
	 * Lookup user by nick, throwing a {@link DaoException} if not found
	 *
	 * @param nick The nick of the user
	 * @return Known active {@link User}
	 * @throws DaoException If user does not exist, exception will contain
	 * {@link org.pircbotx.exception.DaoException.Reason#UNKNOWN_USER} and the
	 * nick that doesn't exist
	 */	
	public U getUser(@NonNull String nick) throws DaoException {
		checkArgument(StringUtils.isNotBlank(nick), "Cannot get a blank user");
		
		String nickLowercase = nick.toLowerCase(locale);
		
		rL.lock();
		try {			
			U user = userNickMap.get(nickLowercase);
			if (user != null)
				return user;
	
			//Does not exist
			throw new DaoException(DaoException.Reason.UNKNOWN_USER, nick);
		} finally {
			rL.unlock();
		}
	}

	/**
	 * Lookup user by UserHostmask, throwing a {@link DaoException} if not found
	 *
	 * @param userHostmask The hostmask of the user
	 * @return Known active {@link User}
	 * @throws DaoException If user does not exist, exception will contain
	 * {@link org.pircbotx.exception.DaoException.Reason#UNKNOWN_USER_HOSTMASK},
	 * hostmask, and wrapped exception with nick
	 */
	public U getUser(@NonNull UserHostmask userHostmask) {
		rL.lock();
		try {
			//Rarely we don't get the full hostmask
			//eg, the server setting your usermode when you connect to the server
			if (userHostmask.getNick() == null)
				return getUser(userHostmask.getHostmask());
			return getUser(userHostmask.getNick());
		} catch (Exception e) {
			//Does not exist, wrap with detail about hostmask
			throw new DaoException(DaoException.Reason.UNKNOWN_USER_HOSTMASK, userHostmask.toString(), e);
		} finally {
			rL.unlock();
		}
	}

	/**
	 * Create a user from a hostmask, internally called when a valid, real user
	 * contacts us
	 *
	 * @param userHostmask The hostmask of the user
	 * @return Active {@link User} that was created
	 */
	@SuppressWarnings("unchecked")
	public U createUser(@NonNull UserHostmask userHostmask) {
		String nickLowercase = userHostmask.getNick().toLowerCase(locale);
		
		wL.lock();
		try {
			if (containsUser(userHostmask))
				throw new RuntimeException("Cannot create a user from hostmask that already exists: " + userHostmask);
			U user = (U) botFactory.createUser(userHostmask);
			userNickMap.put(nickLowercase, user);
			return user;
		} finally {
			wL.unlock();
		}			
	}


	/**
	 * Check if user exists by nick
	 *
	 * @param nick Nick of user
	 * @return True if user exists
	 */
	public boolean containsUser(@NonNull String nick) {
		String nickLowercase = nick.toLowerCase(locale);
		
		rL.lock();
		try {					
			return userNickMap.containsKey(nickLowercase);
		} finally {
			rL.unlock();
		}			
			
	}

	/**
	 * Check if user exists by hostmask
	 *
	 * @param hostmask Hostmask of user
	 * @return True if user exists
	 */
	public boolean containsUser(@NonNull UserHostmask hostmask) {
		rL.lock();
		try {				
			return containsUser(hostmask.getNick());
		} finally {
			rL.unlock();
		}				
	}

	/**
	 * Get all currently known users, except from just joined channels where the
	 * WHO response hasn't finished (listen for {@link UserListEvent} instead)
	 *
	 * @return An immutable set of the currently known users
	 * @see UserListEvent
	 */
	public ImmutableSortedSet<U> getAllUsers() {
		rL.lock();
		try {		
			return ImmutableSortedSet.copyOf(userNickMap.values());
		} finally {
			rL.unlock();
		}			
	}

	
	protected void addUserToChannel(@NonNull U user, @NonNull C channel) {
		wL.lock();
		try {
			if (!containsUser(user)) {
				String nickLowercase = user.getNick().toLowerCase(locale);
				userNickMap.put(nickLowercase, user);
			}
			
			mainMap.addUserToChannel(user, channel);
		} finally {
			wL.unlock();
		}			
	}


	
	protected void addUserToLevel(@NonNull UserLevel level, @NonNull U user, @NonNull C channel) {
		wL.lock();
		try {		
			levelsMap.get(level).addUserToChannel(user, channel);
		} finally {
			wL.unlock();
		}				
	}

	
	protected void removeUserFromLevel(@NonNull UserLevel level, @NonNull U user, @NonNull C channel) {
		wL.lock();
		try {		
			levelsMap.get(level).removeUserFromChannel(user, channel);
		} finally {
			wL.unlock();
		}							
	}

	/**
	 * Gets all currently known users in a channel who do not hold a UserLevel
	 * (op/voice/etc). A {@link UserListEvent} for the channel must of been
	 * dispatched before this method will return complete results
	 *
	 * @param channel Known channel
	 * @return An immutable sorted set of Users
	 */
	
	public ImmutableSortedSet<U> getNormalUsers(@NonNull C channel) {
		rL.lock();
		try {				
			Set<U> remainingUsers = new HashSet<U>(mainMap.getUsers(channel));
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				remainingUsers.removeAll(curLevelMap.getUsers(channel));
			return ImmutableSortedSet.copyOf(remainingUsers);
		} finally {
			rL.unlock();
		}			
	}

	/**
	 * Gets all currently known users in a channel that hold the specified
	 * UserLevel. A {@link UserListEvent} for the channel must of been
	 * dispatched before this method will return complete results
	 *
	 * @param channel Known channel
	 * @param level Level users must hold
	 * @return An immutable sorted set of Users
	 */
	
	public ImmutableSortedSet<U> getUsers(@NonNull C channel, @NonNull UserLevel level) {
		rL.lock();
		try {						
			return levelsMap.get(level).getUsers(channel);
		} finally {
			rL.unlock();
		}				
	}

	/**
	 * Gets all currently known levels (op/voice/etc) a user holds in the
	 * channel. A {@link UserListEvent} for the channel must of been dispatched
	 * before this method will return complete results
	 *
	 * @param channel Known channel
	 * @param user Known user
	 * @return An immutable sorted set of UserLevels
	 */
	
	public ImmutableSortedSet<UserLevel> getLevels(@NonNull C channel, @NonNull U user) {
		rL.lock();
		try {						
			ImmutableSortedSet.Builder<UserLevel> builder = ImmutableSortedSet.naturalOrder();
			for (Map.Entry<UserLevel, UserChannelMap<U, C>> curEntry : levelsMap.entrySet())
				if (curEntry.getValue().containsEntry(user, channel))
					builder.add(curEntry.getKey());
			return builder.build();
		} finally {
			rL.unlock();
		}				
			
	}

	/**
	 * Gets all currently known channels the user is a part of as a normal user.
	 * A {@link UserListEvent} for all channels must of been dispatched before
	 * this method will return complete results
	 *
	 * @param user Known user
	 * @return An immutable sorted set of Channels
	 */
	
	public ImmutableSortedSet<C> getNormalUserChannels(@NonNull U user) {
		rL.lock();
		try {						
			Set<C> remainingChannels = new HashSet<C>(mainMap.getChannels(user));
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				remainingChannels.removeAll(curLevelMap.getChannels(user));
			return ImmutableSortedSet.copyOf(remainingChannels);
		} finally {
			rL.unlock();
		}			
	}

	/**
	 * Gets all currently known channels the user is a part of with the
	 * specified level. A {@link UserListEvent} for all channels must of been
	 * dispatched before this method will return complete results
	 *
	 * @param user Known user
	 * @return An immutable sorted set of Channels
	 */
	
	public ImmutableSortedSet<C> getChannels(@NonNull U user, @NonNull UserLevel level) {
		rL.lock();
		try {				
			return levelsMap.get(level).getChannels(user);
		} finally {
			rL.unlock();
		}				
	}

	
	protected void removeUserFromChannel(@NonNull U user, @NonNull C channel) {
		String nickLowercase = user.getNick().toLowerCase(locale);
		
		wL.lock();
		try {						
			mainMap.removeUserFromChannel(user, channel);
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				curLevelMap.removeUserFromChannel(user, channel);
	
			if (!mainMap.containsUser(user))
				//Completely remove user
				userNickMap.remove(nickLowercase);
		} finally {
			wL.unlock();
		}						
	}

	
	protected void removeUser(@NonNull U user) {
		String nickLowercase = user.getNick().toLowerCase(locale);
		
		wL.lock();
		try {							
			mainMap.removeUser(user);
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				curLevelMap.removeUser(user);
	
			//Remove remaining locations
			userNickMap.remove(nickLowercase);
		} finally {
			wL.unlock();
		}			
	}

	
	protected boolean levelContainsUser(@NonNull UserLevel level, @NonNull C channel, @NonNull U user) {
		rL.lock();
		try {		
			return levelsMap.get(level).containsEntry(user, channel);
		} finally {
			rL.unlock();
		}						
	}

	
	protected void renameUser(@NonNull U user, @NonNull String newNick) {
		String oldNick = user.getNick();
		String oldNickLowercase = oldNick.toLowerCase(locale);
		String newNickLowercase = newNick.toLowerCase(locale);
		
		wL.lock();
		try {		
				
			user.setNick(newNick);
			userNickMap.remove(oldNickLowercase);
			userNickMap.put(newNickLowercase, user);
		} finally {
			wL.unlock();
		}			
	}

	/**
	 * Lookup channel by name, throwing a {@link DaoException} if not found
	 *
	 * @param name Name of channel (eg #pircbotx)
	 * @return A known channel
	 */
	public C getChannel(@NonNull String name) throws DaoException {
		checkArgument(StringUtils.isNotBlank(name), "Cannot get a blank channel");
	
		String nameLowercase = name.toLowerCase(locale);
		rL.lock();
		try {					
			C chan = channelNameMap.get(nameLowercase);
			if (chan != null)
				return chan;
	
			//This could potentially be a mode message, strip off prefixes till we get a channel
			String modePrefixes = bot.getConfiguration().getUserLevelPrefixes();
			if (modePrefixes.contains(Character.toString(name.charAt(0)))) {
				String nameTrimmed = nameLowercase;
				do {
					nameTrimmed = nameTrimmed.substring(1);
					chan = channelNameMap.get(nameTrimmed);
					if (chan != null)
						return chan;
				} while (modePrefixes.contains(Character.toString(nameTrimmed.charAt(0))));
			}
	
			//Channel does not exist
			throw new DaoException(DaoException.Reason.UNKNOWN_CHANNEL, name);
		} finally {
			rL.unlock();
		}			
			
	}

	/**
	 * Creates a known channel, internally called when we join a channel
	 *
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public C createChannel(@NonNull String name) {
		
		String nameLowercase = name.toLowerCase(locale);
		
		wL.lock();
		try {		
			C chan = (C) botFactory.createChannel(bot, name);
			channelNameMap.put(nameLowercase, chan);
			return chan;
		} finally {
			wL.unlock();
		}				
	}


	/**
	 * Check if we are currently in the given channel
	 *
	 * @param name Channel name (eg #pircbotx)
	 * @return True if we are still connected to the channel
	 */
	public boolean containsChannel(@NonNull String name) {
		
		String nameLowercase = name.toLowerCase(locale);
		
		rL.lock();
		try {		
			if (channelNameMap.containsKey(nameLowercase))
				return true;
	
			//This could potentially be a mode message, strip off prefixes till we get a channel
			String modePrefixes = bot.getConfiguration().getUserLevelPrefixes();
			if (modePrefixes.contains(Character.toString(name.charAt(0)))) {
				String nameTrimmed = nameLowercase;
				do {
					nameTrimmed = nameTrimmed.substring(1);
					if (channelNameMap.containsKey(nameTrimmed))
						return true;
				} while (modePrefixes.contains(Character.toString(nameTrimmed.charAt(0))));
			}
	
			//Nope, doesn't exist
			return false;
		} finally {
			rL.unlock();
		}				
			
	}

	/**
	 * Get all currently known users in a channel
	 *
	 * @param channel Known channel
	 * @return An immutable set of users
	 */
	
	public ImmutableSortedSet<U> getUsers(@NonNull C channel) {
		rL.lock();
		try {		
			return mainMap.getUsers(channel);
		} finally {
			rL.unlock();
		}				
	}

	/**
	 * Get all currently joined channels
	 *
	 * @return An immutable set of channels
	 */
	public ImmutableSortedSet<C> getAllChannels() {
		rL.lock();
		try {		
			return ImmutableSortedSet.copyOf(channelNameMap.values());
		} finally {
			rL.unlock();
		}				
	}

	/**
	 * Get <i>channels we're joined to</i> that the user is joined to as well
	 *
	 * @param user A known user
	 * @return An immutable set of channels
	 */
	
	public ImmutableSortedSet<C> getChannels(@NonNull U user) {
		rL.lock();
		try {		
			return mainMap.getChannels(user);
		} finally {
			rL.unlock();
		}
	}

	
	protected void removeChannel(@NonNull C channel) {
		wL.lock();
		try {		
			mainMap.removeChannel(channel);
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				curLevelMap.removeChannel(channel);
	
			//Remove remaining locations
			channelNameMap.remove(channel.getName());
		} finally {
			wL.unlock();
		}
	}

	/**
	 * Gets the bots own user object.
	 *
	 * @return The user object representing this bot
	 */
	public User getUserBot() {		
		return getUser(bot.getNick());
	}

	/**
	 * Clears all internal maps
	 */
	
	public void close() {
		wL.lock();
		try {		
			mainMap.clear();
			for (UserChannelMap<U, C> curLevelMap : levelsMap.values())
				curLevelMap.clear();
			channelNameMap.clear();
			userNickMap.clear();
		} finally {
			wL.unlock();
		}
	}

	/**
	 * Create an immutable snapshot (copy) of all of contained Users, Channels,
	 * and mappings, VERY EXPENSIVE.
	 *
	 * @return Copy of entire model
	 */
	
	public UserChannelDaoSnapshot createSnapshot() {
		rL.lock();
		try {		
			//Create snapshots of all users and channels
			Map<U, UserSnapshot> userSnapshotMap = Maps.newHashMapWithExpectedSize(userNickMap.size());
			for (U curUser : userNickMap.values())
				userSnapshotMap.put(curUser, curUser.createSnapshot());
			Map<C, ChannelSnapshot> channelSnapshotMap = Maps.newHashMapWithExpectedSize(channelNameMap.size());
			for (C curChannel : channelNameMap.values())
				channelSnapshotMap.put(curChannel, curChannel.createSnapshot());
	
			//Make snapshots of the relationship maps using the above user and channel snapshots
			UserChannelMapSnapshot mainMapSnapshot = mainMap.createSnapshot(userSnapshotMap, channelSnapshotMap);
			EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMapSnapshot = new EnumMap<>(UserLevel.class);
			for (Map.Entry<UserLevel, UserChannelMap<U, C>> curLevel : levelsMap.entrySet())
				levelsMapSnapshot.put(curLevel.getKey(), curLevel.getValue().createSnapshot(userSnapshotMap, channelSnapshotMap));
			ImmutableBiMap.Builder<String, UserSnapshot> userNickMapSnapshotBuilder = ImmutableBiMap.builder();
			for (Map.Entry<String, U> curNickEntry : userNickMap.entrySet())
				userNickMapSnapshotBuilder.put(curNickEntry.getKey(), userSnapshotMap.get(curNickEntry.getValue()));
			ImmutableBiMap.Builder<String, ChannelSnapshot> channelNameMapSnapshotBuilder = ImmutableBiMap.builder();
			for (Map.Entry<String, C> curName : channelNameMap.entrySet())
				channelNameMapSnapshotBuilder.put(curName.getKey(), channelSnapshotMap.get(curName.getValue()));

	
			//Finally can create the snapshot object
			UserChannelDaoSnapshot daoSnapshot = new UserChannelDaoSnapshot(bot,
					locale,
					mainMapSnapshot,
					levelsMapSnapshot,
					userNickMapSnapshotBuilder.build(),
					channelNameMapSnapshotBuilder.build()
					);
	
			//Tell UserSnapshots and ChannelSnapshots what the new backing dao is
			for (UserSnapshot curUserSnapshot : userSnapshotMap.values())
				curUserSnapshot.setDao(daoSnapshot);
			for (ChannelSnapshot curChannelSnapshot : channelSnapshotMap.values())
				curChannelSnapshot.setDao(daoSnapshot);
	
			//Finally
			return daoSnapshot;
		} finally {
			rL.unlock();
		}
			
	}
}
