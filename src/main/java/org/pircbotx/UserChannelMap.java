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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelMapSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

/**
 * A many to many map of users to channels.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class UserChannelMap<U extends User, C extends Channel> {
	protected final Multimap<U, C> userToChannelMap;
	protected final Multimap<C, U> channelToUserMap;
	
    private final ReentrantReadWriteLock reentlock = new ReentrantReadWriteLock();
    private final Lock rL = reentlock.readLock();
    private final Lock wL = reentlock.writeLock();

	/**
	 * Create with HashMultimaps.
	 */
	public UserChannelMap() {
		channelToUserMap = HashMultimap.create();
		userToChannelMap = HashMultimap.create();		
	}

	public void addUserToChannel(U user, C channel) {
		wL.lock();
		try {
			userToChannelMap.put(user, channel);
			channelToUserMap.put(channel, user);
		} finally {
			wL.unlock();
		}
	}

	public void removeUserFromChannel(U user, C channel) {
		wL.lock();
		try {		
			userToChannelMap.remove(user, channel);
			channelToUserMap.remove(channel, user);
		} finally {
			wL.unlock();
		}			
	}

	public void removeUser(U user) {
		wL.lock();
		try {		
			//Remove the user from each channel
			for (Channel curChannel : userToChannelMap.removeAll(user))
				channelToUserMap.remove(curChannel, user);
		} finally {
			wL.unlock();
		}			
	}

	public void removeChannel(C channel) {
		wL.lock();
		try {		
			//Remove the channel from each user
			for (User curUser : channelToUserMap.removeAll(channel))
				//This will automatically remove the user if they have no more channels
				userToChannelMap.remove(curUser, channel);
		} finally {
			wL.unlock();
		}			
	}

	public ImmutableSortedSet<U> getUsers(C channel) {
		rL.lock();
		try {		
			return ImmutableSortedSet.copyOf(channelToUserMap.get(channel));
		} finally {
			rL.unlock();
		}				
	}

	public ImmutableSortedSet<C> getChannels(U user) {
		rL.lock();
		try {				
			return ImmutableSortedSet.copyOf(userToChannelMap.get(user));
		} finally {
			rL.unlock();
		}							
	}

	public boolean containsEntry(U user, C channel) {
		rL.lock();
		try {					
			boolean channelToUserContains = channelToUserMap.containsEntry(channel, user);
			boolean userToChannelContains = userToChannelMap.containsEntry(user, channel);
			if (channelToUserContains != userToChannelContains)
				throw new RuntimeException("Map inconsistent! User: " + user + " | Channel: " + channel + " | channelToUserMap: " + channelToUserContains + " | userToChannelMap: " + userToChannelContains);
			return channelToUserContains;
		} finally {
			rL.unlock();
		}			
	}

	public boolean containsUser(User user) {
		rL.lock();
		try {		
			boolean channelToUserContains = channelToUserMap.containsValue(user);
			boolean userToChannelContains = userToChannelMap.containsKey(user);
			if (channelToUserContains != userToChannelContains)
				throw new RuntimeException("Map inconsistent! User: " + user + " | channelToUserMap: " + channelToUserContains + " | userToChannelMap: " + userToChannelContains);
			return channelToUserContains;
		} finally {
			rL.unlock();
		}			
	}

	public void clear() {
		wL.lock();
		try {				
			userToChannelMap.clear();
			channelToUserMap.clear();
		} finally {
			wL.unlock();
		}				
	}

	public UserChannelMapSnapshot createSnapshot(Map<U, UserSnapshot> userSnapshots, Map<C, ChannelSnapshot> channelSnapshots) {
		rL.lock();
		try {		
			//Create new multimaps replacing each user and channel with their respective snapshots
			ImmutableMultimap.Builder<UserSnapshot, ChannelSnapshot> userToChannelSnapshotBuilder = ImmutableMultimap.builder();
			for (Map.Entry<U, C> curEntry : userToChannelMap.entries()) {
				//Issue #274: Track down NPE
				UserSnapshot key = userSnapshots.get(curEntry.getKey());
				if (key == null) {
					throw new NullPointerException("Issue #274: No user snapshot for " + curEntry.getKey());
				}
				ChannelSnapshot value = channelSnapshots.get(curEntry.getValue());
				if (value == null) {
					throw new NullPointerException("Issue #274: No channel snapshot for " + curEntry.getValue());
				}
				userToChannelSnapshotBuilder.put(key, value);
			}
			ImmutableMultimap.Builder<ChannelSnapshot, UserSnapshot> channelToUserSnapshotBuilder = ImmutableMultimap.builder();
			for (Map.Entry<C, U> curEntry : channelToUserMap.entries()) {
				ChannelSnapshot key = channelSnapshots.get(curEntry.getKey());
				if (key == null) {
					throw new NullPointerException("Issue #274: No channel snapshot for " + curEntry.getKey());
				}
				UserSnapshot value = userSnapshots.get(curEntry.getValue());
				if (value == null) {
					throw new NullPointerException("Issue #274: No user snapshot for " + curEntry.getValue());
				}
				channelToUserSnapshotBuilder.put(key, value);
			}
	
			//Return a snapshot of the map
			return new UserChannelMapSnapshot(userToChannelSnapshotBuilder.build(), channelToUserSnapshotBuilder.build());
		} finally {
			rL.unlock();
		}			
	}
}
