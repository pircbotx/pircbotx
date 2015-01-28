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
package org.pircbotx.hooks.events;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import org.pircbotx.PircBotX;
import org.pircbotx.ChannelListEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.pircbotx.hooks.Event;
import org.pircbotx.output.OutputIRC;

/**
 * After calling the listChannels() method in PircBotX, the server will start to
 * send us information about each channel on the server. You may listen for this
 * event in order to receive the information about each channel as soon as it is
 * received.
 * <p>
 * Note that certain channels, such as those marked as hidden, may not appear in
 * channel listings.
 *
 * @author Leon Blakey
 * @see OutputIRC#listChannels()
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChannelInfoEvent extends Event {
	/**
	 * The results of the channel list.
	 */
	protected final ImmutableList<ChannelListEntry> list;

	public ChannelInfoEvent(PircBotX bot, @NonNull ImmutableList<ChannelListEntry> list) {
		super(bot);
		this.list = list;
	}

	/**
	 * Respond by sending a <b>raw line</b> to the server.
	 *
	 * @param response The response to send
	 */
	@Override
	public void respond(String response) {
		getBot().sendRaw().rawLine(response);
	}
}
