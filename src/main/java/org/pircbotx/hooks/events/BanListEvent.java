/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.events;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelEvent;

/**
 *
 * @author Leon
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BanListEvent extends Event implements GenericChannelEvent {
	@Getter(onMethod = @_(
			@Override))
	private final Channel channel;
	private final ImmutableList<Entry> entries;

	public BanListEvent(PircBotX bot, Channel channel, ImmutableList<Entry> entries) {
		super(bot);
		this.channel = channel;
		this.entries = entries;
	}

	/**
	 * Send a message to the channel
	 * @param response 
	 */
	@Override
	public void respond(String response) {
		channel.send().message(response);
	}
	
	@Data
	public static class Entry {
		/**
		 * Extban prefix, from format extban:nick and extban:nick!login@hostmask 
		 * but NOT just extban
		 * @return extban or null
		 */
		private final String extbanPrefix;
		private final UserHostmask recipient;
		private final UserHostmask source;
		private final long time;
	}
}
