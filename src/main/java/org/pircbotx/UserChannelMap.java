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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * A many to many map of users to channels.
 */
class UserChannelMap {
	protected final Multimap<Channel, User> channelToUserMap;
	protected final Multimap<User, Channel> userToChannelMap;

	/**
	 * Create with HashMultimaps
	 */
	public UserChannelMap() {
		channelToUserMap = HashMultimap.create();
		userToChannelMap = HashMultimap.create();
	}

	/**
	 * Create as an <b>immutable copy</b> of the other map
	 * @param otherMap Another map to copy
	 */
	public UserChannelMap(UserChannelMap otherMap) {
		this.channelToUserMap = ImmutableMultimap.copyOf(otherMap.channelToUserMap);
		this.userToChannelMap = ImmutableMultimap.copyOf(otherMap.userToChannelMap);
	}

	public void addUserToChannel(User user, Channel channel) {
		userToChannelMap.put(user, channel);
		channelToUserMap.put(channel, user);
	}

	public void removeUserFromChannel(User user, Channel channel) {
		userToChannelMap.remove(user, channel);
		channelToUserMap.remove(channel, user);
	}

	public void removeUser(User user) {
		//Remove the user from each channel
		for (Channel curChannel : userToChannelMap.removeAll(user))
			channelToUserMap.remove(curChannel, user);
	}

	public void removeChannel(Channel channel) {
		//Remove the channel from each user
		for (User curUser : channelToUserMap.removeAll(channel))
			//This will automatically remove the user if they have no more channels
			userToChannelMap.remove(curUser, channel);
	}

	public ImmutableSet<User> getUsers(Channel channel) {
		return ImmutableSet.copyOf(channelToUserMap.get(channel));
	}

	public ImmutableSet<Channel> getChannels(User user) {
		return ImmutableSet.copyOf(userToChannelMap.get(user));
	}

	public boolean containsEntry(User user, Channel channel) {
		return channelToUserMap.containsEntry(channel, user) && userToChannelMap.containsEntry(user, channel);
	}

	public boolean containsUser(User user) {
		boolean channelToUserContains = channelToUserMap.containsValue(user);
		boolean userToChannelContains = userToChannelMap.containsKey(user);
		if (channelToUserContains != userToChannelContains)
			throw new RuntimeException("Map inconsistent! User: " + user + " | channelToUserMap: " + channelToUserContains + " | userToChannelMap: " + userToChannelContains);
		return channelToUserContains;
	}

	public void clear() {
		userToChannelMap.clear();
		channelToUserMap.clear();
	}
	
}
