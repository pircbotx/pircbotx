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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when we receive a completed Whois request. Note this is completely
 * independent of User and Channel objects since a user might not be connected to
 * us directly
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
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

	protected WhoisEvent(B bot, Builder<B> builder) {
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
	public void respond(String response) {
		getBot().sendIRC().message(getNick(), response);
	}

	@Data
	public static class Builder<B extends PircBotX> {
		protected String nick;
		protected String login;
		protected String hostname;
		protected String realname;
		protected ImmutableList<String> channels;
		protected String server;
		protected String serverInfo;
		protected long idleSeconds;
		protected long signOnTime;
		protected String registeredAs;

		public WhoisEvent<B> generateEvent(B bot) {
			return new WhoisEvent<B>(bot, this);
		}
	}
}
