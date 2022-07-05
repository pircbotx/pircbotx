/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.events;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;


/**
 * Contains the userlist responded from a WHO query
 * 
 * This is very close to UserListEvent, but where UserListEvent is bound to a channel WhoEvent just refers to the query with WHO
 * 
 * @see UserListEvent
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class WhoEvent extends Event {

	protected final String query;
	
	protected final ImmutableList<User> users;
	
	public WhoEvent(PircBotX bot, String query, ImmutableList<User> users) {
		super(bot);
		this.query = query;
		this.users = users;
	}
	

	@Override
	public void respond(String response) {
		bot.sendRaw().rawLine(response);		
	}
}
