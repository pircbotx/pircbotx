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

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

/**
 * This event is dispatched whenever we receive a notice.
 *
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {
	/**
	 * The user hostmask that sent the hostmask.
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final UserHostmask userHostmask;
	/**
	 * The user that sent the notice.
	 */
	@Getter(onMethod = @_(
			@Override,
			@Nullable))
	protected final User user;
	/**
	 * The target channel of the notice. A value of <code>null</code> means that
	 * the target is us
	 */
	@Getter(onMethod = @_(
			@Override))
	protected final Channel channel;
	/**
	 * The raw channel name, could be a special mode message eg +#channel that
	 * only goes to voiced users.
	 */
	protected final String channelSource;
	/**
	 * The notice message.
	 */
	protected final String notice;

	public NoticeEvent(PircBotX bot, @NonNull UserHostmask userHostmask, User user, Channel channel, @NonNull String channelSource, @NonNull String notice) {
		super(bot);
		this.user = user;
		this.userHostmask = userHostmask;
		this.channel = channel;
		this.channelSource = channelSource;
		this.notice = notice;
	}

	/**
	 * Returns the notice the user sent
	 *
	 * @return The notice the user sent
	 */
	@Override
	public String getMessage() {
		return notice;
	}

	/**
	 * Respond by sending a message to the channel in
	 * <code>user: message</code>, or if its a private message respond with a
	 * private message to the user format.
	 *
	 * @param response The response to send
	 */
	public void respond(String response) {
		respondWith(response);
	}
	
	@Override
	public void respondWith(String response) {
		if (getChannel() == null)
			getUser().send().message(response);
		else
			getBot().sendIRC().message(channelSource, response);
	}

	/**
	 * Respond with a message to the channel without the prefix
	 *
	 * @param response The response to send
	 */
	public void respondChannel(String response) {
		if (getChannel() == null)
			throw new RuntimeException("Event does not contain a channel");
		getBot().sendIRC().message(channelSource, response);
	}

	/**
	 * Respond with a PM directly to the user
	 *
	 * @param response The response to send
	 */
	public void respondPrivateMessage(String response) {
		getUser().send().message(response);
	}
}
