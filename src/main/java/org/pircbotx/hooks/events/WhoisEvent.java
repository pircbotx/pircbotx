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

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when we receive a completed Whois request. Note this is completely
 * independent of User and Channel objects since a user might not be connected to
 * us directly
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
//TODO: Add tests
@EqualsAndHashCode(callSuper = true)
@Getter
public class WhoisEvent<B extends PircBotX> extends Event<B> {
	protected final String nick;
	protected final String login;
	protected final String hostname;
	protected final String realname;
	protected final ImmutableList<String> channels;
	protected final String server;
	protected final String serverInfo;
	protected final long idleSeconds;
	protected final long signOnTime;
	protected final String registeredAs;

	public WhoisEvent(@NonNull B bot, @NonNull Builder<B> builder) {
		super(bot);
		this.nick = builder.getNick();
		this.login = builder.getLogin();
		this.hostname = builder.getHostname();
		this.realname = builder.getRealname();
		this.channels = builder.getChannels();
		this.server = builder.getServer();
		this.serverInfo = builder.getServerInfo();
		this.idleSeconds = builder.getIdleSeconds();
		this.signOnTime = builder.getSignOnTime();
		this.registeredAs = builder.getRegisteredAs();
	}

	@Override
	public void respond(@Nullable String response) {
		getBot().sendIRC().message(getNick(), response);
	}

	@Data
	@NoArgsConstructor
	public static class Builder<B extends PircBotX> {
		@NonNull
		protected String nick;
		@NonNull
		protected String login;
		@NonNull
		protected String hostname;
		@NonNull
		protected String realname;
		@NonNull
		protected ImmutableList<String> channels;
		@NonNull
		protected String server;
		@NonNull
		protected String serverInfo;
		protected long idleSeconds;
		protected long signOnTime;
		@NonNull
		protected String registeredAs;

		public WhoisEvent<B> generateEvent(@NonNull B bot) {
			return new WhoisEvent<B>(bot, this);
		}
	}
}
