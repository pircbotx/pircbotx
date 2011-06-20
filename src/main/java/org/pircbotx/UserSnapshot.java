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

import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * A snapshot of a user in time. Useful to get information before a user leaves
 * a channel or server.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
//Only use super implementation which uses UIDs
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper=true, of={})
@Setter(AccessLevel.NONE)
public class UserSnapshot extends User {
	protected Set<Channel> channels;
	protected Set<Channel> channelsOpIn;
	protected Set<Channel> channelsVoiceIn;
	protected Set<Channel> channelsOwnerIn;
	protected Set<Channel> channelsSuperOpIn;
	protected Set<Channel> channelsHalfOpIn;
	protected final User generatedFrom;
	
	public UserSnapshot(User user) {
		super(user.getBot(), user.getNick());
		generatedFrom = user;
		
		//Clone fields
		super.setAway(user.isAway());
		super.setHops(user.getHops());
		super.setHostmask(user.getHostmask());
		super.setIdentified(user.isIdentified());
		super.setIrcop(user.isIrcop());
		super.setLogin(user.getLogin());
		super.setRealName(user.getRealName());
		super.setServer(user.getServer());

		//Store channels
		channels = user.getChannels();
		channelsOpIn = user.getChannelsOpIn();
		channelsVoiceIn = user.getChannelsVoiceIn();
		channelsSuperOpIn = user.getChannelsSuperOpIn();
		channelsHalfOpIn = user.getChannelsHalfOpIn();
	}

	@Override
	public void parseStatus(Channel chan, String prefix) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setAway(boolean away) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setHops(int hops) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setHostmask(String hostmask) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setIdentified(boolean identified) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setIrcop(boolean ircop) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setLogin(String login) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setNick(String nick) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setRealName(String realName) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setServer(String server) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}
}
