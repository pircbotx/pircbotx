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

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import com.google.common.collect.ImmutableMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * This event is dispatched whenever we receive a line from the server that
 * PircBotX has not been programmed to recognize.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnknownEvent extends Event {
	/**
	 * The target of the event
	 */
	@Getter
	protected final String target;
	/**
	 * The nickname (if any) of the originating user
	 */
	@Getter
	protected final String nick;
	/**
	 * The IRC command that was issued
	 */
	@Getter
	protected final String command;
	/**
	 * The raw line that was received from the server.
	 */
	@Getter
	protected final String line;
	/**
	 * The parsed line
	 */
	@Getter
	protected final List<String> parsedLine;
	/**
	 * The IRCv3 tags (if any)
	 */
	@Getter
	protected final ImmutableMap<String, String> tags;


	public UnknownEvent(PircBotX bot, String target, String nick, String command, @NonNull String line, List<String> parsedLine, ImmutableMap<String, String> tags) {
		super(bot);
		this.target = target;
		this.nick = nick;
		this.command = command;
		this.line = line;
		this.parsedLine = parsedLine;
		this.tags = tags;
	}

	/**
	 * Responds by sending a <b>raw line</b> to the server.
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getBot().sendRaw().rawLine(response);
	}
	
	public void respondWith(String fullLine) {
        	getBot().sendIRC().message(target, fullLine);
    	}
}
