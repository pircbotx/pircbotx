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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserChannelDao;

/**
 *
 * @author Leon
 */
@Slf4j
public class ChannelSnapshot extends Channel {
	@Getter(value = AccessLevel.PROTECTED, onMethod = @_(@Override))
	@Setter
	protected UserChannelDaoSnapshot dao;
	@Getter
	protected final Channel generatedFrom;

	public ChannelSnapshot(Channel channel, String mode) {
		super(channel.getBot(), null, channel.getName());
		this.generatedFrom = channel;
		
		//Clone
		super.setCreateTimestamp(channel.getCreateTimestamp());
		super.setTopic(channel.getTopic());
		super.setTopicSetter(channel.getTopicSetter());
		super.setTopicTimestamp(channel.getTopicTimestamp());
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
	protected void setTopicSetter(String topicSetter) {
		SnapshotUtils.fail();
	}
	
	
}
