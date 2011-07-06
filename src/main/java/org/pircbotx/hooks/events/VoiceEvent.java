/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.events;

import lombok.AccessLevel;
import org.pircbotx.Channel;
import org.pircbotx.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericUserModeEvent;

/**
 * Called when a user (possibly us) gets voice status granted in a channel.
 *  <p>
 * This is a type of mode change and therefor is also dispatched in a 
 * {@link org.pircbotx.hooks.events.ModeEvent}
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceEvent<T extends PircBotX> extends Event<T> implements GenericUserModeEvent<T> {
	protected final Channel channel;
	protected final User source;
	protected final User recipient;
	@Getter(AccessLevel.NONE)
	protected final boolean hasVoice;

	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 * @param channel The channel in which the mode change took place.
	 * @param source The user that performed the mode change.
	 * @param recipient The nick of the user that got 'voiced'.
	 */
	public VoiceEvent(T bot, Channel channel, User source, User recipient, boolean isVoice) {
		super(bot);
		this.channel = channel;
		this.source = source;
		this.recipient = recipient;
		this.hasVoice = isVoice;
	}
	
	/**
	 * Returns true if this was set, false if removed
	 * @return 
	 * @deprecated Use the better named hasVoice method. Will be removed in future versions
	 * @see #hasVoice() 
	 */
	@Deprecated
	public boolean isVoice() {
		return hasVoice;
	}
	
	public boolean hasVoice() {
		return hasVoice;
	}

	@Override
	public void respond(String response) {
		getBot().sendMessage(getChannel(), getSource(), response);
	}
}
