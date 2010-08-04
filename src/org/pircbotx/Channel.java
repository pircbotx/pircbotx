/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.util.ArrayList;
import java.util.List;

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
	private List<User>_users = new ArrayList<User>();

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
			if ("--".contains(Character.toString(curChar))) {
				adding = false;
			} else if (curChar == '+') {
				adding = true;
			} else if (adding) {
				_mode = _mode + curChar;
			} else {
				_mode = _mode.replace(Character.toString(curChar), "");
			}
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
	public List<User> getAllUsers() {
		return _users;
	}

	/**
	 * This will get the user object associated with this channel. If none exists,
	 * one is created. Existence should only be determined by {@link #userExists(java.lang.String) }
	 * @param nick
	 * @return
	 */
	public User getUser(String nick) {
		for (User curUser : _users)
			if (curUser.getNick().toLowerCase().equals(nick))
				return curUser;
		//User does not exist, create one
		User usr = new User(nick);
		_users.add(usr);
		return usr;
	}

	public void addUser(User usr) {
		_users.add(usr);
	}

	public void removeUser(String nick) {
		for (User curUsr : _users)
			if (curUsr.getNick().equalsIgnoreCase(nick)) {
				_users.remove(curUsr);
				break;
			}
	}

	public boolean userExists(String nick) {
		for (User curUser : getAllUsers())
			if (curUser.getNick().equals(nick))
				return true;
		return false;
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
	 */ public String getTopicSetter() {
		return _topicSetter;
	}

	/**
	 * @param topicSetter the _topicSetter to set
	 */ public void setTopicSetter(String topicSetter) {
		this._topicSetter = topicSetter;
	}
}
