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
package org.pircbotx;

import java.util.Collection;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a User on the server. Contains all the available information about
 * the user as well as some useful delegate methods like 
 * {@link #op(org.pircbotx.Channel) giving op} or {@link #voice(org.pircbotx.Channel) voice}
 * status.
 * @since   PircBot 1.0.0
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(of = {"uuid", "bot"})
@Setter(AccessLevel.PACKAGE)
public class User implements Comparable<User> {
	private String nick;
	private String realName = "";
	private String login = "";
	private String hostmask = "";
	private boolean away = false;
	private boolean ircop = false;
	private String server = "";
	private boolean identified = true;
	private int hops = 0;
	private final PircBotX bot;
	@Getter(AccessLevel.NONE)
	protected final UUID uuid = UUID.randomUUID();

	User(PircBotX bot, String nick) {
		this.bot = bot;
		this.nick = nick;
	}
	
	public void parseStatus(Channel chan, String prefix) {
		if(prefix.contains("@"))
			chan.ops.add(this);
		if(prefix.contains("+"))
			chan.voices.add(this);
		if(prefix.contains("%"))
			chan.halfOps.add(this);
		if(prefix.contains("~"))
			chan.owners.add(this);
		if(prefix.contains("&"))
			chan.superOps.add(this);
		setAway(prefix.contains("G")); //Assume here (H) if there is no G
		setIrcop(prefix.contains("*"));
	}
	
	void setNick(String nick) {
		//Replace nick in nick map
		synchronized(bot.userNickMap) {
			bot.userNickMap.remove(this.nick);
			bot.userNickMap.put(nick, this);
			this.nick = nick;
		}
	}

	/**
	 * Get all channels this user is a part of
	 * @return All channels this user is a part of
	 */
	public Collection<Channel> getChannels() {
		return bot.getChannels(this);
	}

	/**
	 * Returns the result of calling the compareTo method on lowercased
	 * nicks. This is useful for sorting lists of User objects.
	 *
	 * @return the result of calling compareTo on lowercased nicks.
	 */
	@Override
	public int compareTo(User other) {
		return other.getNick().compareToIgnoreCase(getNick());
	}

	/**
	 * The exact server that this user is joined to
	 * @return The address of the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * The number of hops it takes to this user
	 * @return the hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Checks if the user is an operator in the specified channel
	 * @return True if they are an op, false if not
	 */
	public boolean isOp(Channel chan) {
		return chan.isOp(this);
	}
	
	/**
	 * Checks if the user has voice status in the given channel
	 * @return True if the user has voice, false if not
	 */
	public boolean hasVoice(Channel chan) {
		return chan.hasVoice(this);
	}
}
