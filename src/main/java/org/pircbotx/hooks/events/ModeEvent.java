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

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelUserEvent;

/**
 * Used when the mode of a channel is set.
 * <p>
 * You may find it more convenient to decode the meaning of the mode string by
 * using instead {@link OpEvent}, {@link VoiceEvent},
 * {@link SetChannelKeyEvent}, {@link RemoveChannelKeyEvent},
 * {@link SetChannelLimitEvent}, {@link RemoveChannelLimitEvent},
 * {@link SetChannelBanEvent} or {@link RemoveChannelBanEvent} as appropriate.
 * <p>
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModeEvent extends Event implements GenericChannelUserEvent {
	/**
	 * The channel that the mode operation applies to.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final Channel channel;
	/**
	 * The user that set the mode.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final UserHostmask userHostmask;
	/**
	 * The user that set the mode.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	/**
	 * The mode that has been set.
	 */
	protected final String mode;
	protected final ImmutableList<String> modeParsed;

	public ModeEvent(PircBotX bot, @NonNull Channel channel, UserHostmask userHostmask,
			User user, @NonNull String mode, @NonNull ImmutableList<String> modeParsed) {
		super(bot);
		this.channel = channel;
		this.userHostmask = userHostmask;
		this.user = user;
		this.mode = mode;
		this.modeParsed = modeParsed;
	}

	/**
	 * Respond by send a message in the channel to the user that set the mode in
	 * <code>user: message</code> format
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getChannel().send().message(getUser(), response);
	}
}
