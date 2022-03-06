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
package org.pircbotx.hooks.events;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericUserEvent;

/**
 * This event is dispatched whenever someone (possibly us) changes nick on any
 * of the channels that we are on.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NickChangeEvent extends Event implements GenericUserEvent {
	/**
	 * The users old nick.
	 */
	protected final String oldNick;
	/**
	 * The users new nick.
	 */
	protected final String newNick;
	/**
	 * The user that changed their nick.
	 */
	@Getter(onMethod = @__({
			@Override}))
	protected final UserHostmask userHostmask;
	/**
	 * The user that changed their nick.
	 */
	@Getter(onMethod = @__({
			@Override,
			@Nullable}))
	protected final User user;
	/**
	 * The IrcV3 tags
	 */
	protected final ImmutableMap<String, String> tags;

	public NickChangeEvent(PircBotX bot, @NonNull String oldNick, @NonNull String newNick,
			@NonNull UserHostmask userHostmask, User user, ImmutableMap<String, String> tags) {
		super(bot);
		this.oldNick = oldNick;
		this.newNick = newNick;
		this.userHostmask = userHostmask;
		this.user = user;
		this.tags = tags;
	}

	/**
	 * Respond by sending a <i>private message</i> to the user's new nick
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getUser().send().message(response);
	}
}
