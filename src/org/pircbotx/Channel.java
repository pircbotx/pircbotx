/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.util.Collection;
import java.util.HashMap;

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
	private HashMap<String, User> _users = new HashMap<String, User>();

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
	 * @return the users
	 */
	public Collection<User> getAllUsers() {
		return _users.values();
	}

	/**
	 * This will get the user object associated with this channel. If none exists,
	 * one is created. Existence should only be determined by {@link #userExists(java.lang.String) }
	 * @param nick
	 * @return
	 */
	public User getUser(String nick) {
		User usr = _users.get(nick);
		if (usr == null) {
			//User does not exist, create one
			usr = new User(nick);
			_users.put(nick, usr);
		}
		return usr;
	}

	public void removeUser(String nick) {
		_users.remove(nick);
	}

	public boolean userExists(String nick) {
		_users.containsKey(nick);
		return false;
	}

	public void renameUser(String oldNick,String newNick) {
		try {
		User removed = _users.remove(oldNick);
		removed.setNick(newNick);
		_users.put(newNick, removed);
		}
		catch(Exception e) {
			System.err.println("OldNick: "+oldNick+" | NewNick: "+newNick);
		}
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
				+ "Topic Timestamp{" + _topicTimestamp + "} "
				+ "Users{" + _users + '}';
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
