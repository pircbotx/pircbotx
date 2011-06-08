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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import javax.net.SocketFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.managers.ListenerManager;

/**
 * Manager that provides an easy way to create bots on many different servers
 * with the same or close to the same information. All important setup methods
 * have been mirrored here. For documentation, see their equivalent PircBotX
 * methods.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
public class MultiBotManager {
	protected final Set<BotEntry> bots = new HashSet();
	@Getter(AccessLevel.NONE)
	protected final PircBotX dummyBot;
	protected ListenerManager listenerManager = null;
	protected String name;
	protected boolean verbose;
	protected int socketTimeout;
	protected long messageDelay;
	protected String login;
	protected boolean autoNickChange;
	protected String encoding;
	protected InetAddress dcciNetAddress;
	protected int[] dccports;

	public MultiBotManager(String name) {
		//Create a temp bot and mirror default values
		dummyBot = new PircBotX();
		this.name = name;
		verbose = dummyBot.isVerbose();
		socketTimeout = dummyBot.getSocketTimeout();
		messageDelay = dummyBot.getMessageDelay();
		login = dummyBot.getLogin();
		autoNickChange = dummyBot.isAutoNickChange();
		encoding = dummyBot.getEncoding();
		dcciNetAddress = dummyBot.getDccInetAddress();
		dccports = dummyBot.getDccPorts();
	}

	/**
	 * Create a bot using the specified hostname, 6667 for port, and no password or socketfactory
	 * @param hostname The hostname of the server to connect to.
	 */
	public void createBot(String hostname) {
		createBot(hostname, 6667, null, null);
	}
	
	/**
	 * Attempt to connect to the specified IRC server and port number.
	 * The onConnect method is called upon success.
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 */
	public void createBot(String hostname, int port) throws IOException, IrcException, NickAlreadyInUseException {
		createBot(hostname, port, null, null);
	}

	/**
	 * Create a bot using the specified hostname, port and socketfactory with no password
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param socketFactory The factory to use for creating sockets, including secure sockets
	 */
	public void createBot(String hostname, int port, SocketFactory socketFactory) {
		createBot(hostname, port, null, socketFactory);
	}

	/**
	 * Create a bot using the specified hostname, port, password, and socketfactory
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param password The password to use to join the server.
	 * @param socketFactory The factory to use for creating sockets, including secure sockets
	 */
	public void createBot(String hostname, int port, String password, SocketFactory socketFactory) {
		//Create bot with all of the global settings
		PircBotX bot = new PircBotX();
		bot.setListenerManager(listenerManager);
		bot.setName(name);
		bot.setVerbose(verbose);
		bot.setSocketTimeout(socketTimeout);
		bot.setMessageDelay(messageDelay);
		bot.setLogin(login);
		bot.setAutoNickChange(autoNickChange);
		try {
			bot.setEncoding(encoding);
		} catch (UnsupportedEncodingException ex) {
			//Should of been caught with setEncoding, wrap with RuntimeException
			throw new RuntimeException("Creating bot with encoding " + encoding + " failed", ex);
		}
		bot.setDccInetAddress(dcciNetAddress);
		bot.setDccPorts(dccports);

		//Add to bot set
		bots.add(new BotEntry(bot, hostname, port, password, socketFactory));
	}

	/**
	 * Connect all bots to their respective hosts and channels
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public void connectAll() throws IOException, IrcException, NickAlreadyInUseException {
		for (BotEntry curEntry : bots) {
			PircBotX bot = curEntry.getBot();
			bot.connect(curEntry.getHostname(), curEntry.getPort(), curEntry.getPassword(), bot.getSocketFactory());
		}
	}
	
	public Set<PircBotX> getBots() {
		Set<PircBotX> actualBots = new HashSet();
		for(BotEntry curEntry : bots)
			actualBots.add(curEntry.getBot());
		return actualBots;
	}

	public void setEncoding(String encoding) throws UnsupportedEncodingException {
		//Test if exception is thrown when setting encoding
		dummyBot.setEncoding(encoding);

		//Good, set value
		this.encoding = encoding;
	}

	@Data
	protected class BotEntry {
		protected final PircBotX bot;
		protected final String hostname;
		protected final int port;
		protected final String password;
		protected final SocketFactory socketFactory;
	}
}
