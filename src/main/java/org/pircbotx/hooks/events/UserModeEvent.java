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
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericUserModeEvent;

/**
 * Called when the mode of a user is set.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserModeEvent extends Event implements GenericUserModeEvent {
	/**
	 * The user hostmask that set the mode.
	 */
	@Getter(onMethod = @_({
		@Override}))
	protected final User userHostmask;
	/**
	 * The user that set the mode.
	 */
	@Getter(onMethod = @_({
		@Override,
		@Nullable}))
	protected final User user;
	/**
	 * The user hostmask that the mode operation applies to.
	 */
	protected final UserHostmask recipientHostmask;
	/**
	 * The user that the mode operation applies to.
	 */
	@Getter(onMethod = @_({
		@Override,
		@Nullable}))
	protected final User recipient;
	/**
	 * The mode that has been set.
	 */
	protected final String mode;

	public UserModeEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user,
			@NonNull UserHostmask recipientHostmask, User recipient, @NonNull String mode) {
		super(bot);
		this.userHostmask = user;
		this.user = user;
		this.recipientHostmask = recipientHostmask;
		this.recipient = recipient;
		this.mode = mode;
	}

	/**
	 * Respond with a private message to the source user
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getUserHostmask().send().message(response);
	}
}
