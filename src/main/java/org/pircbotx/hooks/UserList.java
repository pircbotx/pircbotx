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
package org.pircbotx.hooks;

import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 * This method is called when we receive a user list from the server
 * after joining a channel.
 *  <p>
 * Shortly after joining a channel, the IRC server sends a list of all
 * users in that channel. The PircBotX collects this information and
 * calls this method as soon as it has the full list.
 *  <p>
 * To obtain the nick of each user in the channel, call the getNick()
 * method on each User object in the array.
 *  <p>
 * At a later time, you may call the getUsers method to obtain an
 * up to date list of the users in the channel.
 *  <p>
 * The implementation of this method in the PircBotX abstract class
 * performs no actions and may be overridden as required.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see User
 */
public class UserList {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link UserList} for an explanation on use 
	 * @see UserList 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		/**
		 * Simple Listener for UserList Events. See {@link UserList} for a complete description on when
		 * this is called.
		 * @param channel The channel that the user list is from.
		 * @param users An <b>immutable</b> Set of Users belonging to this channel.
		 * @see UserList
		 * @see SimpleListener
		 */
		public void onUserList(Channel channel, Set<User> users);
	}

	/**
	 * Listener that receives an event. See {@link UserList} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see UserList 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		/**
		 * Listener for UserList Events. See {@link UserList} for a complete description on when
		 * this is called.
		 * @see UserList
		 * @see Listener
		 */
		public void onUserList(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link UserList} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see UserList 
	 * @see Listener
	 */
	public static class Event extends BaseEvent {
		protected final Channel channel;
		protected final Set<User> users;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param channel The channel that the user list is from.
		 * @param users An <b>immutable</b> Set of Users belonging to this channel.
		 */
		public <T extends PircBotX> Event(T bot, Channel channel, Set<User> users) {
			super(bot);
			this.channel = channel;
			this.users = users;
		}

		public Channel getChannel() {
			return channel;
		}

		public Set<User> getUsers() {
			return users;
		}
	}
}
