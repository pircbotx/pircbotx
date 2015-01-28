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
package org.pircbotx.hooks.events;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericSnapshotEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

/**
 * This event is dispatched whenever someone (possibly us) parts a channel which
 * we are on.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PartEvent extends Event implements GenericChannelUserEvent, GenericSnapshotEvent {
	@Getter(onMethod = @_(
			@Override))
	protected final UserChannelDaoSnapshot userChannelDaoSnapshot;
	/**
	 * Snapshot of the channel as of before the user parted.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final ChannelSnapshot channel;
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * Snapshot of the user as of before the user parted.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserSnapshot user;
	/**
	 * The reason for leaving the channel.
	 */
	protected final String reason;

	public PartEvent(PircBotX bot, UserChannelDaoSnapshot daoSnapshot, ChannelSnapshot channel,
			@NonNull UserHostmask userHostmask, UserSnapshot user, @NonNull String reason) {
		super(bot);
		this.userChannelDaoSnapshot = daoSnapshot;
		this.channel = channel;
		this.userHostmask = userHostmask;
		this.user = user;
		this.reason = reason;
	}

	/**
	 * @see #getUserChannelDaoSnapshot()
	 * @see GenericSnapshotEvent
	 * @deprecated Use {@link #getUserChannelDaoSnapshot() } from
	 * {@link GenericSnapshotEvent}
	 */
	@Deprecated
	public UserChannelDaoSnapshot getDaoSnapshot() {
		return userChannelDaoSnapshot;
	}

	/**
	 * Respond by sending a message to the channel
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getChannel().send().message(response);
	}
}
