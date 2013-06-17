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
package org.pircbotx.hooks.events;

import javax.annotation.Nullable;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericUserEvent;

/**
 * This event is dispatched whenever someone (possibly us) changes nick on any
 * of the channels that we are on.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NickChangeEvent<T extends PircBotX> extends Event<T> implements GenericUserEvent<T> {
	protected final String oldNick;
	protected final String newNick;
	@Getter(onMethod = @_(@Override))
	protected final User user;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param oldNick The old nick.
	 * @param newNick The new nick.
	 * @param user The user that changed their nick
	 */
	public NickChangeEvent(T bot, @NonNull String oldNick, @NonNull String newNick, @NonNull User user) {
		super(bot);
		this.oldNick = oldNick;
		this.newNick = newNick;
		this.user = user;
	}

	/**
	 * Respond by sending a <i>private message</i> to the user's new nick
	 * @param response The response to send
	 */
	@Override
	public void respond(@Nullable String response) {
		getUser().send().message(response);
	}
}
