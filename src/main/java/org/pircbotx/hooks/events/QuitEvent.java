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
import org.pircbotx.hooks.types.GenericSnapshotEvent;
import org.pircbotx.hooks.types.GenericUserEvent;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

/**
 * This event is dispatched whenever someone (possibly us) quits from the
 * server. We will only observe this if the user was in one of the channels to
 * which we are connected.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuitEvent extends Event implements GenericUserEvent, GenericSnapshotEvent {
	@Getter(onMethod = @_(
			@Override))
	protected final UserChannelDaoSnapshot userChannelDaoSnapshot;
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * Snapshot of the user as of before the user quit.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserSnapshot user;
	/**
	 * The reason the user quit from the server.
	 */
	protected final String reason;

	public QuitEvent(PircBotX bot, UserChannelDaoSnapshot userChannelDaoSnapshot,
			@NonNull UserHostmask userHostmask, UserSnapshot user, @NonNull String reason) {
		super(bot);
		this.userChannelDaoSnapshot = userChannelDaoSnapshot;
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
	 * Does NOT respond! This will throw an
	 * {@link UnsupportedOperationException} since we can't respond to a user
	 * that just quit
	 *
	 * @param response The response to send
	 */
	@Override
	@Deprecated
	public void respond(String response) {
		throw new UnsupportedOperationException("Attempting to respond to a user that quit");
	}
}
