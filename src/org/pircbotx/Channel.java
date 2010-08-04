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
	private String name;
	private String mode = "";
	private String topic = "";
	private long timestamp;
	private long topicTimestamp;
	private String topicSetter = "";
	private List<User> users = new ArrayList<User>();

	Channel(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	public void setMode(String modes) {
		mode = modes;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return the users
	 */
	public List<User> getAllUsers() {
		return users;
	}

	public User getUser(String nick) {
		for (User curUser : users)
			if (curUser.getNick().toLowerCase().equals(nick))
				return curUser;
		return null;
	}

	public void addUser(User usr) {
		users.add(usr);
	}

	public void removeUser(String nick) {
		for (User curUsr : users)
			if (curUsr.getNick().equalsIgnoreCase(nick)) {
				users.remove(curUsr);
				break;
			}
	}

	public boolean userExists(String nick) {
		for(User curUser : getAllUsers())
			if(curUser.getNick().equals(nick))
				return true;
		return false;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the topicTimestamp
	 */
	public long getTopicTimestamp() {
		return topicTimestamp;
	}

	/**
	 * @param topicTimestamp the topicTimestamp to set
	 */
	public void setTopicTimestamp(long topicTimestamp) {
		this.topicTimestamp = topicTimestamp;
	}

	@Override
	public String toString() {
		return "Name{" + name + "} "
				+ "Mode{" + mode + "} "
				+ "Topic{" + topic + "} "
				+ "Timestamp{" + timestamp + "} "
				+ "Topic Timestamp{" + topicTimestamp + "} "
				+ "Users{" + users + '}';
	}

	/**
	 * @return the topicSetter
	 */ public String getTopicSetter() {
		return topicSetter;
	}

	/**
	 * @param topicSetter the topicSetter to set
	 */ public void setTopicSetter(String topicSetter) {
		this.topicSetter = topicSetter;
	}
}
