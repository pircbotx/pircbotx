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
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericUserEvent;

/**
 * Called when the mode of a user is set.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserModeEvent<B extends PircBotX> extends Event<B> implements GenericUserEvent<B> {
	@Getter(onMethod = @_({@Override}))
	protected final User user;
	protected final User recipient;
	protected final String mode;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param user The user that set the mode.
	 * @param recipient The user that the mode operation applies to.
	 * @param mode The mode that has been set.
	 */
	public UserModeEvent(@NonNull B bot, @NonNull User user, @NonNull User recipient, @NonNull String mode) {
		super(bot);
		this.user = user;
		this.recipient = recipient;
		this.mode = mode;
	}

	/**
	 * Respond with a private message to the source user
	 * @param response The response to send
	 */
	@Override
	public void respond(@Nullable String response) {
		getUser().send().message(response);
	}
}
