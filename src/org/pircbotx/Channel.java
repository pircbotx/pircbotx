/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author LordQuackstar
 */
public class Channel {
	private String _name;
	private String _mode = "";
	private String _topic = "";
	private long _timestamp;
	private long _topicTimestamp;
	private String _topicSetter = "";
	private Map<String,User> _users = Collections.synchronizedMap(new HashMap<String,User>());

	Channel(String name) {
		this._name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this._name = name;
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

	public Collection<User> getUsers() {
		return _users.values();
	}

	public User getUser(String nick) {
		return _users.get(nick);
	}

	public void removeUser(String nick) {
		User usr = _users.remove(nick);
		if(usr != null)
			usr.removeChannel(_name);
	}

	public void addUser(User usr) {
		_users.put(usr.getNick(), usr);
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
}
