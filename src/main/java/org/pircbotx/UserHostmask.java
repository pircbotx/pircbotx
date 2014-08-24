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
package org.pircbotx;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.output.OutputUser;

/**
 * Represents any hostmask that may or may not be an actual user.
 *
 * @author Leon
 */
@AllArgsConstructor
@EqualsAndHashCode(of = {"bot", "nick", "login", "hostname"})
@Data
public class UserHostmask implements Comparable<User> {
	@NonNull
	private final PircBotX bot;
	/**
	 * Lazily created output since it might not ever be used
	 */
	@Getter(AccessLevel.NONE)
	protected final AtomicSafeInitializer<OutputUser> output = new AtomicSafeInitializer<OutputUser>() {
		@Override
		protected OutputUser initialize() {
			return bot.getConfiguration().getBotFactory().createOutputUser(bot, UserHostmask.this);
		}
	};
	/**
	 * Hostmask of the user (The entire user!login@hostname).
	 */
	@NonNull
	private String hostmask;
	/**
	 * Current nick of the user (nick!login@hostname).
	 */
	private String nick;
	/**
	 * Login of the user (nick!login@hostname).
	 */
	private final String login;
	/**
	 * Hostname of the user (nick!login@hostname).
	 */
	private final String hostname;

	public UserHostmask(PircBotX bot, String rawHostmask) {
		this.bot = bot;
		if(StringUtils.containsAny(rawHostmask, "!@")) {
			this.hostmask = rawHostmask;
			String[] hostmaskParts = StringUtils.split(rawHostmask, "!@");
			this.nick = hostmaskParts[0];
			this.login = hostmaskParts[1];
			this.hostname = hostmaskParts[2];
		} else {
			this.hostmask = rawHostmask;
			this.nick = rawHostmask;
			this.login = null;
			this.hostname = null;
		}
	}
	
	public UserHostmask(UserHostmask otherHostmask) {
		this.bot = otherHostmask.getBot();
		this.hostmask = otherHostmask.getHostmask();
		this.nick = otherHostmask.getNick();
		this.login = otherHostmask.getLogin();
		this.hostname = otherHostmask.getHostname();
	}

	public UserHostmask(PircBotX bot, String nick, String login, String hostname) {
		this.bot = bot;
		this.nick = nick;
		this.login = login;
		this.hostname = hostname;
		this.hostmask = nick + ":" + login + "@" + hostname;
	}

	/**
	 * Send a line to the user.
	 *
	 * @return A {@link OutputUser} for this user
	 */
	public OutputUser send() {
		try {
			return output.get();
		} catch (ConcurrentException ex) {
			throw new RuntimeException("Could not generate OutputChannel for " + getNick(), ex);
		}
	}
	
	@Override
	public String toString() {
		return hostmask;
	}

	/**
	 * Compare {@link #getNick()} with {@link String#compareToIgnoreCase(java.lang.String)
	 * }. This is useful for sorting lists of User objects.
	 *
	 * @param other Other user to compare to
	 * @return the result of calling compareToIgnoreCase user nicks.
	 */
	@Override
	public int compareTo(User other) {
		return getNick().compareToIgnoreCase(other.getNick());
	}
}
