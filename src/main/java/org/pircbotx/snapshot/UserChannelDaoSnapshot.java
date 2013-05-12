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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.snapshot;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import java.util.EnumMap;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.UserChannelMap;
import org.pircbotx.UserLevel;

/**
 *
 * @author Leon
 */
public class UserChannelDaoSnapshot extends UserChannelDao<UserSnapshot, ChannelSnapshot> {
	public UserChannelDaoSnapshot(PircBotX bot, UserChannelMapSnapshot mainMap, EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMap, ImmutableBiMap<String, UserSnapshot> userNickMap, ImmutableBiMap<String, ChannelSnapshot> channelNameMap, ImmutableSet<UserSnapshot> privateUsers) {
		super(bot, null, mainMap, levelsMap, userNickMap, channelNameMap, privateUsers);
	}

	@Override
	public UserSnapshot getUser(String nick) {
		UserSnapshot user = userNickMap.get(nick);
		if (user == null)
			throw new RuntimeException("User " + nick + " does not exist");
		return user;
	}

	@Override
	public ChannelSnapshot getChannel(String name) {
		ChannelSnapshot channel = channelNameMap.get(name);
		if (channel == null)
			throw new RuntimeException("Channel " + channel + " does not exist");
		return channel;
	}

	@Override
	protected void removeUserFromChannel(UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	protected void removeUser(UserSnapshot user) {
		SnapshotUtils.fail();
	}

	@Override
	protected void renameUser(UserSnapshot user, String newNick) {
		SnapshotUtils.fail();
	}

	@Override
	protected void removeChannel(ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	protected void addUserToChannel(UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	protected void addUserToPrivate(UserSnapshot user) {
		SnapshotUtils.fail();
	}

	@Override
	protected void addUserToLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	protected void removeUserFromLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	public void close() {
		SnapshotUtils.fail();
	}
}
