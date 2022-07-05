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
package org.pircbotx.snapshot;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Locale;
import org.pircbotx.PircBotX;
import org.pircbotx.UserChannelDao;
import org.pircbotx.UserChannelMap;
import org.pircbotx.UserHostmask;
import org.pircbotx.UserLevel;

/**
 *
 */
public class UserChannelDaoSnapshot extends UserChannelDao<UserSnapshot, ChannelSnapshot> {
	protected final String botNick;

	public UserChannelDaoSnapshot(PircBotX bot, Locale locale, UserChannelMapSnapshot mainMap, EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMap, ImmutableMap<String, UserSnapshot> userNickMap, ImmutableMap<String, ChannelSnapshot> channelNameMap) {
		super(bot, null, locale, mainMap, levelsMap, userNickMap, channelNameMap);
		botNick = bot.getNick();
	}

	@Override
	public UserChannelDaoSnapshot createSnapshot() {
		throw new UnsupportedOperationException("Attempting to generate UserChannelDao snapshot from a snapshot");
	}

	@Override
	public ChannelSnapshot createChannel(String name) {
		return SnapshotUtils.fail();
	}

	@Override
	public UserSnapshot createUser(UserHostmask userHostmask) {
		return SnapshotUtils.fail();
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
	protected void addUserToLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	protected void removeUserFromLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
		SnapshotUtils.fail();
	}

	@Override
	public UserSnapshot getUserBot() {
		return getUser(botNick);
	}

	@Override
	public void close() {
		SnapshotUtils.fail();
	}
}
