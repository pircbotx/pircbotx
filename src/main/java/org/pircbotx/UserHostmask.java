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
package org.pircbotx;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.output.OutputUser;

/**
 * Represents any hostmask that may or may not be an actual user.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"bot", "nick", "login", "hostname"})
@Getter
@ToString(exclude = {"bot", "output"})
@Slf4j
public class UserHostmask implements Comparable<User> {
	@NonNull
	protected final PircBotX bot;
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
	 * Extban prefix if in format extban:nick or extban:nick!login@hostmask
	 */
	private final String extbanPrefix;
	/**
	 * Current nick of the user (nick!login@hostname).
	 */
	@Setter(AccessLevel.PROTECTED)
	private String nick;
	/**
	 * Login of the user (nick!login@hostname).
	 */
	private String login;
	/**
	 * Hostname of the user (nick!login@hostname).
	 */
	private String hostname;

	protected UserHostmask(PircBotX bot, String rawHostmask) {		
		try {
			Preconditions.checkArgument(StringUtils.isNotBlank(rawHostmask), "Cannot parse blank hostmask");
			

			
			this.bot = bot;
			if (StringUtils.contains(rawHostmask, "!") && StringUtils.contains(rawHostmask, "@")) {
				String[] hostmaskParts = StringUtils.split(rawHostmask, "!@");
				if (hostmaskParts.length >= 3) {
					this.nick = hostmaskParts[0];
					this.login = hostmaskParts[1];
					this.hostname = hostmaskParts[2];
				} else {
					this.nick = rawHostmask;
					this.login = null;
					this.hostname = null;
				}
			} else {
				this.nick = rawHostmask;
				this.login = null;
				this.hostname = null;
			}
			
			if (nick.startsWith(":"))  
				nick = nick.substring(1);
			
			if (nick.contains(":")) {
				String[] nickParts = StringUtils.split(nick, ":");
				this.extbanPrefix = nickParts[0];
				this.nick = nickParts[1];
			} else {
				this.extbanPrefix = null;
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse hostmask " + rawHostmask, e);
		}
	}

	protected UserHostmask(UserHostmask otherHostmask) {
		this.bot = otherHostmask.getBot();
		this.nick = otherHostmask.getNick();
		this.login = otherHostmask.getLogin();
		this.hostname = otherHostmask.getHostname();
		this.extbanPrefix = otherHostmask.getExtbanPrefix();
	}
	
	protected void updateHostmask(@NonNull UserHostmask userHostmask) {
		if (StringUtils.isNotBlank(userHostmask.getHostname()) && !userHostmask.getHostname().equals(getHostname())) {
			log.trace("Updating hostname to {} for user {}!{}@{}", 
					userHostmask.getHostname(),
					getNick(),
					getLogin(),
					getHostname());
			this.hostname = userHostmask.getHostname();
		}
		if (StringUtils.isNotBlank(userHostmask.getLogin()) && !userHostmask.getLogin().equals(getLogin())) {
			log.trace("Updating login to {} for user {}!{}@{}", 
					userHostmask.getLogin(),
					getNick(),
					getLogin(),
					getHostname());
			this.login = userHostmask.getLogin();
		}
	}

	/**
	 * The full hostmask of the user in extban:user!login@hostname format. If
	 * there's no extban prefix extban: is not returned. If there's no login nor
	 * hostmask, meaning this is a service or the IRCd server "user", just the
	 * nick is returned
	 */
	@NonNull
	public String getHostmask() {
		StringBuilder hostmask = new StringBuilder();
		if (StringUtils.isNotBlank(extbanPrefix)) {
			hostmask.append(extbanPrefix).append(':');
		}
		hostmask.append(nick);
		if (StringUtils.isNotBlank(login) || StringUtils.isNotBlank(hostname)) {
			hostmask.append('!').append(login).append('@').append(hostname);
		}
		return hostmask.toString();
	}
	
	/**
	 * Alias of {@link #getLogin() }
	 */
	public String getIdent() {
		return getLogin();
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
	
	@SuppressWarnings("unchecked")
	public <T extends PircBotX> T getBot() {
		return (T) bot;
	}
}
