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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author LordQuackstar
 */
public class Channel {
	private final String _name;
	private String _mode = "";
	private String _topic = "";
	private long _timestamp;
	private long _topicTimestamp;
	private String _topicSetter = "";
	private final PircBotX _bot;
	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 */
	private Set<User> _op = Collections.synchronizedSet(new HashSet<User>());
	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 */
	private Set<User> _voice = Collections.synchronizedSet(new HashSet<User>());

	public Channel(PircBotX bot, String name) {
		_name = name;
		_bot = bot;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return _mode;
	}

	public void parseMode(String rawMode) {
		//Parse mode by switching between removing and adding by the existance of a + or - sign
		boolean adding = true;
		for (char curChar : rawMode.toCharArray())
			if ("--".contains(Character.toString(curChar)))
				adding = false;
			else if (curChar == '+')
				adding = true;
			else if (adding)
				_mode = _mode + curChar;
			else
				_mode = _mode.replace(Character.toString(curChar), "");
	}

	public Set<User> getUsers() {
		return _bot.getUsers(_name);
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return _topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this._topic = topic;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return _timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this._timestamp = timestamp;
	}

	/**
	 * @return the topicTimestamp
	 */
	public long getTopicTimestamp() {
		return _topicTimestamp;
	}

	/**
	 * @param topicTimestamp the topicTimestamp to set
	 */
	public void setTopicTimestamp(long topicTimestamp) {
		this._topicTimestamp = topicTimestamp;
	}

	@Override
	public String toString() {
		return "Name{" + _name + "} "
				+ "Mode{" + _mode + "} "
				+ "Topic{" + _topic + "} "
				+ "Timestamp{" + _timestamp + "} "
				+ "Topic Timestamp{" + _topicTimestamp + "} ";
	}

	/**
	 * @return the _topicSetter
	 */
	public String getTopicSetter() {
		return _topicSetter;
	}

	/**
	 * @param topicSetter the _topicSetter to set
	 */
	public void setTopicSetter(String topicSetter) {
		this._topicSetter = topicSetter;
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @return the _op
	 */
	public boolean isOp(User user) {
		return _op.contains(user);
	}

	/**
	 * Weather or not the user represented by this object is an op in the channel
	 * this object was fetched from
	 * @param op the _op to set
	 */
	public void setOp(User user, boolean op) {
		if (op)
			_op.add(user);
		else
			_op.remove(user);
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @return the _voice
	 */
	public boolean hasVoice(User user) {
		return _voice.contains(user);
	}

	/**
	 * Weather or not the user represented by this object has voice in the channel
	 * this object was fetched from
	 * @param voice the _voice to set
	 */
	public void setVoice(User user, boolean voice) {
		if (voice)
			_voice.add(user);
		else
			_voice.remove(user);
	}

	public boolean removeUser(User user) {
		return _op.remove(user) || _voice.remove(user);
	}
}
