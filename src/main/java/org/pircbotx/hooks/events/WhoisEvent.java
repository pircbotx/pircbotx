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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when we receive a completed Whois request. Note this is completely
 * independent of User and Channel objects since a user might not be connected
 * to us directly
 *
 * @author Leon Blakey
 */
@Builder(builderClassName = "Builder", buildMethodName = "generateEvent")
@EqualsAndHashCode(callSuper = true)
@Getter
public class WhoisEvent extends Event {
	@NonNull
	protected final String nick;
	@NonNull
	protected final String login;
	@NonNull
	protected final String hostname;
	@NonNull
	protected final String realname;
	@NonNull
	protected final ImmutableList<String> channels;
	@NonNull
	protected final String server;
	@NonNull
	protected final String serverInfo;
	protected final long idleSeconds;
	protected final long signOnTime;
	/**
	 * If registered, the users nickserv account or if the server doesn't send
	 * the account an empty string, or if the user isn't registered at all,
	 * null.
	 */
	protected final String registeredAs;
	protected final boolean exists;
	protected final String awayMessage;

	WhoisEvent(PircBotX bot, @NonNull Builder builder) {
		super(bot);
		this.nick = builder.nick;
		this.login = builder.login;
		this.hostname = builder.hostname;
		this.realname = builder.realname;
		this.channels = builder.channels;
		this.server = builder.server;
		this.serverInfo = builder.serverInfo;
		this.idleSeconds = builder.idleSeconds;
		this.signOnTime = builder.signOnTime;
		this.registeredAs = builder.registeredAs;
		this.exists = builder.exists;
		this.awayMessage = builder.awayMessage;
	}

	public static Builder builder() {
		return new Builder().channels(ImmutableList.<String>of());
	}

	/**
	 * Check if user is registered
	 *
	 * @return True if user is registered
	 * @see #getRegisteredAs()
	 */
	public boolean isRegistered() {
		return registeredAs != null;
	}

	@Override
	public void respond(String response) {
		getBot().sendIRC().message(getNick(), response);
	}

	/**
	 * Internal class to allow data to be collected over multiple lines
	 */
	public static class Builder {
		public WhoisEvent generateEvent(PircBotX bot) {
			return new WhoisEvent(bot, this);
		}
	}
}
