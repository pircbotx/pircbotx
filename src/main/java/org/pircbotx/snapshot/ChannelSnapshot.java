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

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.UserHostmask;

/**
 *
 */
@Slf4j
public class ChannelSnapshot extends Channel {
	@Setter
	protected UserChannelDaoSnapshot dao;
	@Getter
	protected final Channel generatedFrom;
	@Getter(onMethod = @__(
			@Override))
	protected final String mode;

	public ChannelSnapshot(Channel channel, String mode) {
		super(channel);
		this.generatedFrom = channel;
		this.mode = mode;

		//Clone
		super.setCreateTimestamp(channel.getCreateTimestamp());
		super.setTopic(channel.getTopic());
		super.setTopicSetter(channel.getTopicSetter());
		super.setTopicTimestamp(channel.getTopicTimestamp());
		super.setChannelKey(channel.getChannelKey());
		super.setChannelLimit(channel.getChannelLimit());
		super.setChannelPrivate(channel.isChannelPrivate());
		super.setInviteOnly(channel.isInviteOnly());
		super.setModerated(channel.isModerated());
		super.setNoExternalMessages(channel.isNoExternalMessages());
		super.setSecret(channel.isSecret());
		super.setTopicProtection(channel.hasTopicProtection());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected UserChannelDao<User, Channel> getDao() {
		//Workaround for generics
		return (UserChannelDao<User, Channel>) (Object) dao;
	}

	@Override
	protected void parseMode(String rawMode) {
		SnapshotUtils.fail();
	}

	@Override
	public ChannelSnapshot createSnapshot() {
		throw new UnsupportedOperationException("Attempting to generate channel snapshot from a snapshot");
	}

	@Override
	protected void setTopic(String topic) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setTopicTimestamp(long topicTimestamp) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setCreateTimestamp(long createTimestamp) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setTopicSetter(UserHostmask topicSetter) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setModerated(boolean moderated) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setNoExternalMessages(boolean noExternalMessages) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setInviteOnly(boolean inviteOnly) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setSecret(boolean secret) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setChannelPrivate(boolean channelPrivate) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setTopicProtection(boolean topicProtection) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setChannelLimit(int channelLimit) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setChannelKey(String channelKey) {
		SnapshotUtils.fail();
	}

	@Override
	protected void setMode(String mode, ImmutableList<String> modeParsed) {
		SnapshotUtils.fail();
	}
}
