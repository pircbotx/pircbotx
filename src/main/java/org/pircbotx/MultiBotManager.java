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
package org.pircbotx;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.SocketFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Manager that provides an easy way to create bots on many different servers
 * with the same or close to the same information. All important setup methods
 * have been mirrored here. For documentation, see their equivalent PircBotX
 * methods.
 * <p>
 * <b>Note:</b> Setting any value after connectAll() is invoked will NOT update
 * all existing bots. You will need to loop over the bots and call the set methods
 * manually
 * <p/>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
public class MultiBotManager {
	protected final Map<PircBotX, BotBuilder> bots = new HashMap();

	/**
	 * Create a bot using the specified hostname, port, password, and socketfactory
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param password The password to use to join the server.
	 * @param socketFactory The factory to use for creating sockets, including secure sockets
	 */
	public BotBuilder createBot(Configuration config) {
		BotBuilder builder = new BotBuilder(config);
		bots.put(new PircBotX(config), builder);
		return builder;
	}

	/**
	 * Connect all bots to their respective hosts and channels
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public void connectAll() throws IOException, IrcException, NickAlreadyInUseException {
		for (Map.Entry<PircBotX, BotBuilder> curEntry : bots.entrySet()) {
			PircBotX bot = curEntry.getKey();
			BotBuilder builder = curEntry.getValue();
			bot.connect();

			//Join channels
			for (Map.Entry<String, String> curChannel : builder.getChannels().entrySet())
				bot.joinChannel(curChannel.getKey(), curChannel.getValue());
		}
	}

	/**
	 * Disconnect all bots from their respective severs cleanly.
	 */
	public void disconnectAll() {
		for (PircBotX bot : bots.keySet())
			if (bot.isConnected())
				bot.disconnect();
	}

	/**
	 * Get all the bots that this MultiBotManager is managing. Do not save this
	 * anywhere as it will be out of date when a new bot is created
	 * @return An <i>unmodifiable</i> Set of bots that are being managed
	 */
	public Set<PircBotX> getBots() {
		return Collections.unmodifiableSet(bots.keySet());
	}

	@RequiredArgsConstructor
	public static class BotBuilder {
		@Getter
		protected final Configuration config;
		@Getter(AccessLevel.PROTECTED)
		protected final Map<String, String> channels = new HashMap();

		public BotBuilder addChannel(String channelName) {
			channels.put(channelName, "");
			return this;
		}

		public BotBuilder addChannel(String channelName, String key) {
			channels.put(channelName, key);
			return this;
		}
	}
}
