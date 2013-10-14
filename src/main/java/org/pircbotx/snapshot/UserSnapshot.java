/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.snapshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;

/**
 * A snapshot of a user in time. Useful to get information before a user leaves
 * a channel or server. Any attempts to modify data throw an exception
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
//Only use super implementation which uses UIDs
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, of = {})
public class UserSnapshot extends User {
	@Getter
	protected final User generatedFrom;
	@Setter
	protected UserChannelDaoSnapshot dao;
	
	public UserSnapshot(User user) {
		super(user.getBot(), null, user.getNick());
		generatedFrom = user;

		//Clone fields
		super.setAwayMessage(user.getAwayMessage());
		super.setHops(user.getHops());
		super.setHostmask(user.getHostmask());
		super.setIrcop(user.isIrcop());
		super.setLogin(user.getLogin());
		super.setRealName(user.getRealName());
		super.setServer(user.getServer());
	}
	
	@Override
	protected UserChannelDao<User, Channel> getDao() {
		//Workaround for generics
		return (UserChannelDao<User, Channel>) (Object) dao;
	}

	@Override
	public UserSnapshot createSnapshot() {
		throw new UnsupportedOperationException("Attempting to generate user snapshot from a snapshot");
	}

	@Override
	protected void setAwayMessage(String away) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setHops(int hops) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setHostmask(String hostmask) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setIrcop(boolean ircop) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setLogin(String login) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setNick(String nick) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setRealName(String realName) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	protected void setServer(String server) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}
}
