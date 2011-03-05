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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author  Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@ToString(exclude={"op","voice"})
public class Channel {
	private final String name;
	@Setter(AccessLevel.PACKAGE)
	private String mode = "";
	@Setter(AccessLevel.PACKAGE)
	private String topic = "";
	@Setter(AccessLevel.PACKAGE)
	private long topicTimestamp;
	@Setter(AccessLevel.PACKAGE)
	private String topicSetter = "";
	private final PircBotX bot;
	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 */
	@Getter(AccessLevel.NONE)
	private final Set<User> op = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 */
	@Getter(AccessLevel.NONE)
	private final Set<User> voice = Collections.synchronizedSet(new HashSet<User>());

	public void parseMode(String rawMode) {
		//Parse mode by switching between removing and adding by the existance of a + or - sign
		boolean adding = true;
		for (char curChar : rawMode.toCharArray())
			if ("--".contains(Character.toString(curChar)))
				adding = false;
			else if (curChar == '+')
				adding = true;
			else if (adding)
				mode = mode + curChar;
			else
				mode = mode.replace(Character.toString(curChar), "");
	}

	public Set<User> getUsers() {
		return bot.getUsers(name);
	}
	
	/**
	 * Get the user that set the topic. As the user may or may not be in the
	 * channel return as a string
	 * @return The user that set the topic in String format
	 */
	public String getTopicSetter() {
		return topicSetter;
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @return the _op
	 */
	public boolean isOp(User user) {
		return op.contains(user);
	}

	public void op(User user) {
		bot.op(this, user);
	}

	void addOp(User user) {
		op.add(user);
	}

	public void deOp(User user) {
		bot.deOp(this, user);
	}

	void removeOp(User user) {
		op.remove(user);
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @return the _voice
	 */
	public boolean hasVoice(User user) {
		return voice.contains(user);
	}

	public void voice(User user) {
		bot.voice(this, user);
	}

	void addVoice(User user) {
		voice.add(user);
	}

	public void deVoice(User user) {
		bot.deVoice(this, user);
	}

	void removeVoice(User user) {
		voice.remove(user);
	}

	boolean removeUser(User user) {
		return op.remove(user) || voice.remove(user);
	}
}
