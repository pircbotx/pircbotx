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

import javax.annotation.Nullable;
import lombok.AccessLevel;
import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelModeRecipientEvent;

/**
 * Called when a user (possibly us) gets voice status granted in a channel.
 * <p>
 * This is a type of mode change and therefor is also dispatched in a
 * {@link org.pircbotx.hooks.events.ModeEvent}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceEvent extends Event implements GenericChannelModeRecipientEvent {
	/**
	 * The channel in which the mode change took place.
	 */
	@Getter(onMethod = @__({
		@Override}))
	protected final Channel channel;
	/**
	 * The user hostmask that performed the mode change.
	 */
	@Getter(onMethod = @__({
		@Override}))
	protected final UserHostmask userHostmask;
	/**
	 * The user that performed the mode change.
	 */
	@Getter(onMethod = @__({
		@Override,
		@Nullable}))
	protected final User user;
	/**
	 * The user hostmask that got 'voiced'
	 */
	@Getter(onMethod = @__({
		@Override}))
	protected final UserHostmask recipientHostmask;
	/**
	 * The nick of the user that got 'voiced'.
	 */
	@Getter(onMethod = @__({
		@Override,
		@Nullable}))
	protected final User recipient;
	@Getter(AccessLevel.NONE)
	protected final boolean hasVoice;

	public VoiceEvent(PircBotX bot, @NonNull Channel channel, @NonNull UserHostmask userHostmask,
			User user, @NonNull UserHostmask recipientHostmask, User recipient, boolean hasVoice) {
		super(bot);
		this.channel = channel;
		this.userHostmask = userHostmask;
		this.user = user;
		this.recipientHostmask = recipientHostmask;
		this.recipient = recipient;
		this.hasVoice = hasVoice;
	}

	/**
	 * Checks if this is a set or remove voice operation
	 *
	 * @return True if this was set, false if removed
	 * @deprecated Use the better named hasVoice method. Will be removed in
	 * future versions
	 * @see #hasVoice()
	 */
	@Deprecated
	public boolean isVoice() {
		return hasVoice;
	}

	/**
	 * If the voice was given or removed.
	 */
	public boolean hasVoice() {
		return hasVoice;
	}

	@Override
	public void respond(String response) {
		getChannel().send().message(response);
	}
}
