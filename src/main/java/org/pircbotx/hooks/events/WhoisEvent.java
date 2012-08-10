/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when we receive a completed Whois request. Note this is completely
 * independent of User and Channel objects since a user might not be connected to
 * us directly
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@EqualsAndHashCode(callSuper = true)
public class WhoisEvent<T extends PircBotX> extends Event<T> {
	@Delegate(types=WhoisEventBuilderIncludes.class)
	protected WhoisEventBuilder builder;

	public WhoisEvent(T bot, WhoisEventBuilder builder) {
		super(bot);
		this.builder = builder;
	}
	
	@Override
	public void respond(String response) {
		bot.sendMessage(getNick(), response);
	}
	
	public List<String> getChannels() {
		//Project Lombok doesn't like Delagates with generics
		return builder.getChannels();
	}
	
	@Data
	public static class WhoisEventBuilder<J extends PircBotX> {
		protected String nick;
		protected String login;
		protected String hostname;
		protected String realname;
		protected List<String> channels;
		protected String server;
		protected String serverInfo;
		
		public List<String> getChannels() {
			return Collections.unmodifiableList(channels);
		}
		
		public WhoisEvent generateEvent(J bot) {
			return new WhoisEvent(bot, this);
		}
	}
	
	protected static interface WhoisEventBuilderIncludes {
		public String getNick();
		public String getLogin();
		public String getHostname();
		public String getRealname();
		public String getServer();
		public String getServerInfo();
		@Override
		public String toString();
	}
}
