/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Synchronized;
import org.pircbotx.hooks.events.UserListEvent;

/**
 *
 * @author Leon
 */
public class UserChannelDao {
	@Setter(AccessLevel.PROTECTED)
	protected PircBotX bot;
	protected final Object accessLock = new Object();
	protected final ChannelToUserMap mainMap = new ChannelToUserMap();
	protected final ChannelToUserMap opsMap = new ChannelToUserMap();
	protected final ChannelToUserMap voiceMap = new ChannelToUserMap();
	protected final ChannelToUserMap superOpsMap = new ChannelToUserMap();
	protected final ChannelToUserMap halfOpsMap = new ChannelToUserMap();
	protected final ChannelToUserMap ownersMap = new ChannelToUserMap();
	protected final HashBiMap<String, User> userNickMap = HashBiMap.create();
	protected final HashBiMap<String, Channel> channelNameMap = HashBiMap.create();
	protected final Set<User> privateUsers = new HashSet();

	@Synchronized("accessLock")
	public void init(PircBotX bot) {
		if (bot != null)
			throw new RuntimeException("Already inited UserChannelDao with " + bot);
		this.bot = bot;
	}

	@Synchronized("accessLock")
	public User getUser(String nick) {
		if (nick == null)
			throw new NullPointerException("Can't get a null user");
		User user = userNickMap.get(nick);
		if (user != null)
			return user;

		//Create new user
		user = new User(bot, this, nick);
		userNickMap.put(nick, user);
		return user;
	}

	@Synchronized("accessLock")
	public boolean userExists(User user) {
		return userNickMap.containsValue(user);
	}

	/**
	 * Get all user's in the channel. There are some important things to note about this method:
	 * <ul>
	 * <li>This method may not return a full list of users if you call it
	 * before the complete nick list has arrived from the IRC server.</li>
	 * <li>If you wish to find out which users are in a channel as soon
	 * as you join it, then you should listen for a {@link UserListEvent}
	 * instead of calling this method, as the {@link UserListEvent} is only
	 * dispatched as soon as the full user list has been received.</li>
	 * <li>This method will return immediately, as it does not require any
	 * interaction with the IRC server.</li>
	 * </ul>
	 *
	 * @since PircBot 1.0.0
	 *
	 * @param chan The channel object to search in
	 * @return A Set of all user's in the channel
	 *
	 * @see UserListEvent
	 */
	@Synchronized("accessLock")
	public Set<User> getAllUsers() {
		return ImmutableSet.copyOf(userNickMap.values());
	}

	@Synchronized("accessLock")
	protected void addUserToChannel(User user, Channel channel) {
		mainMap.addUserToChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void addUserToPrivate(User user) {
		privateUsers.add(user);
	}

	protected void addUserToOps(User user, Channel channel) {
		opsMap.addUserToChannel(user, channel);
	}

	protected void addUserToVoices(User user, Channel channel) {
		voiceMap.addUserToChannel(user, channel);
	}

	protected void addUserToHalfOps(User user, Channel channel) {
		halfOpsMap.addUserToChannel(user, channel);
	}

	protected void addUserToSuperOps(User user, Channel channel) {
		superOpsMap.addUserToChannel(user, channel);
	}

	protected void addUserToOwners(User user, Channel channel) {
		ownersMap.addUserToChannel(user, channel);
	}

	protected void removeUserFromOps(User user, Channel channel) {
		opsMap.removeUserFromChannel(user, channel);
	}

	protected void removeUserFromVoices(User user, Channel channel) {
		voiceMap.removeUserFromChannel(user, channel);
	}

	protected void removeUserFromHalfOps(User user, Channel channel) {
		halfOpsMap.removeUserFromChannel(user, channel);
	}

	protected void removeUserFromSuperOps(User user, Channel channel) {
		superOpsMap.removeUserFromChannel(user, channel);
	}

	protected void removeUserFromOwners(User user, Channel channel) {
		ownersMap.removeUserFromChannel(user, channel);
	}
	
	public Set<User> getChannelOps(Channel channel) {
		return ImmutableSet.copyOf(opsMap.getUsers(channel));
	}
	
	public Set<User> getChannelVoices(Channel channel) {
		return ImmutableSet.copyOf(voiceMap.getUsers(channel));
	}
	
	public Set<User> getChannelHalfOps(Channel channel) {
		return ImmutableSet.copyOf(halfOpsMap.getUsers(channel));
	}
	
	public Set<User> getChannelSuperOps(Channel channel) {
		return ImmutableSet.copyOf(superOpsMap.getUsers(channel));
	}
	
	public Set<User> getChannelOwners(Channel channel) {
		return ImmutableSet.copyOf(ownersMap.getUsers(channel));
	}
	
	public Set<Channel> getUsersOps(User user) {
		return ImmutableSet.copyOf(opsMap.getChannels(user));
	}
	
	public Set<Channel> getUsersVoices(User user) {
		return ImmutableSet.copyOf(voiceMap.getChannels(user));
	}
	
	public Set<Channel> getUsersHalfOps(User user) {
		return ImmutableSet.copyOf(halfOpsMap.getChannels(user));
	}
	
	public Set<Channel> getUsersSuperOps(User user) {
		return ImmutableSet.copyOf(superOpsMap.getChannels(user));
	}
	
	public Set<Channel> getUsersOwners(User user) {
		return ImmutableSet.copyOf(ownersMap.getChannels(user));
	}

	@Synchronized("accessLock")
	protected void removeUserFromChannel(User user, Channel channel) {
		mainMap.removeUserFromChannel(user, channel);
		opsMap.removeUserFromChannel(user, channel);
		voiceMap.removeUserFromChannel(user, channel);
		halfOpsMap.removeUserFromChannel(user, channel);
		superOpsMap.removeUserFromChannel(user, channel);
		ownersMap.removeUserFromChannel(user, channel);
	}

	@Synchronized("accessLock")
	protected void removeUser(User user) {
		mainMap.removeUser(user);
		opsMap.removeUser(user);
		voiceMap.removeUser(user);
		halfOpsMap.removeUser(user);
		superOpsMap.removeUser(user);
		ownersMap.removeUser(user);

		//Remove remaining locations
		userNickMap.inverse().remove(user);
		privateUsers.remove(user);
	}

	@Synchronized("accessLock")
	protected void renameUser(User user, String newNick) {
		user.setNick(newNick);
		userNickMap.inverse().put(user, newNick);
	}

	@Synchronized("accessLock")
	public Channel getChannel(String name) {
		if (name == null)
			throw new NullPointerException("Can't get a null channel");
		Channel chan = channelNameMap.get(name);
		if (chan != null)
			return chan;

		//Channel does not exist, create one
		chan = new Channel(bot, name);
		channelNameMap.put(name, chan);
		return chan;
	}

	/**
	 * Check if the bot is currently in the given channel.
	 * @param name A channel name as a string
	 * @return True if we are still connected to the channel, false if not
	 */
	@Synchronized
	public boolean channelExists(String name) {
		return channelNameMap.containsKey(name);
	}

	@Synchronized("accessLock")
	public Set<User> getUsers(Channel channel) {
		return ImmutableSet.copyOf(mainMap.getUsers(channel));
	}

	public Set<Channel> getAllChannels() {
		return ImmutableSet.copyOf(channelNameMap.values());
	}

	@Synchronized("accessLock")
	public Set<Channel> getChannels(User user) {
		return ImmutableSet.copyOf(mainMap.getChannels(user));
	}

	@Synchronized("accessLock")
	protected void removeChannel(Channel channel) {
		mainMap.removeChannel(channel);
		opsMap.removeChannel(channel);
		voiceMap.removeChannel(channel);
		halfOpsMap.removeChannel(channel);
		superOpsMap.removeChannel(channel);
		ownersMap.removeChannel(channel);

		//Remove remaining locations
		channelNameMap.remove(channel.getName());
	}

	@Synchronized("accessLock")
	protected void reset() {
		bot = null;
		mainMap.clear();
		opsMap.clear();
		voiceMap.clear();
		halfOpsMap.clear();
		superOpsMap.clear();
		ownersMap.clear();
		channelNameMap.clear();
		privateUsers.clear();
		userNickMap.clear();
	}

	protected static class ChannelToUserMap {
		protected final HashMultimap<Channel, User> channelToUserMap = HashMultimap.create();
		protected final HashMultimap<User, Channel> userToChannelMap = HashMultimap.create();

		public void addUserToChannel(User user, Channel channel) {
			userToChannelMap.put(user, channel);
			channelToUserMap.put(channel, user);
		}

		public void removeUserFromChannel(User user, Channel channel) {
			if (userToChannelMap.get(user).size() == 1)
				//Remove user completely, that was the only channel they were in
				userToChannelMap.removeAll(user);
			else
				//Just remove the relationship
				userToChannelMap.remove(user, channel);
			//Only need to remove the user since empty channels do exist
			channelToUserMap.remove(channel, user);
		}

		public void removeUser(User user) {
			//Remove from all mappings
			for (Channel curChannel : userToChannelMap.removeAll(user))
				channelToUserMap.remove(curChannel, user);
		}

		public void removeChannel(Channel channel) {
			//Remove from all mappings
			for (User curUser : channelToUserMap.removeAll(channel))
				userToChannelMap.remove(channel, curUser);
		}

		public Set<User> getUsers(Channel channel) {
			return ImmutableSet.copyOf(channelToUserMap.get(channel));
		}

		public Set<Channel> getChannels(User user) {
			return ImmutableSet.copyOf(userToChannelMap.get(user));
		}

		public boolean containsEntry(User user, Channel channel) {
			return channelToUserMap.containsEntry(channel, user)
					&& userToChannelMap.containsEntry(user, channel);
		}

		public void clear() {
			userToChannelMap.clear();
			channelToUserMap.clear();
		}
	}
}
