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

package org.pircbotx.hooks.events;

import java.net.InetAddress;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 *
 * @author Leon
 */
public class ConnectAttemptFailedEvent<T extends PircBotX> extends Event<T> {
	protected final InetAddress remoteAddress;
	protected final int remotePort;
	protected final InetAddress localAddress;
	protected final int remainingServers;

	public ConnectAttemptFailedEvent(T bot, @NonNull InetAddress remoteAddress, int remotePort, InetAddress localAddress, int remainingServers) {
		super(bot);
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.localAddress = localAddress;
		this.remainingServers = remainingServers;
	}
	
	/**
	 * Does NOT respond to the server! This will throw an {@link UnsupportedOperationException}
	 * since we can't respond to a server we didn't even connect to
	 * @param response The response to send
	 */
	@Override
	@Deprecated
	public void respond(@Nullable String response) {
		throw new UnsupportedOperationException("Attepting to respond to a disconnected server");
	}
}