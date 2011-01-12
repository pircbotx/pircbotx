/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.GenericListenerManager;
import org.pircbotx.hooks.ListenerManager;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
public class MultiBotManager {
	@Setter(AccessLevel.PRIVATE)
	protected Set<BotEntry> bots = new HashSet<BotEntry>();
	protected PircBotX dummyBot;
	protected ListenerManager listenerManager = new GenericListenerManager();
	protected String name = "PircBotXUser";
	protected boolean verbose;
	protected int socketTimeout;
	protected long messageDelay;
	protected String login;
	protected boolean autoNickChange;
	protected String encoding;
	protected InetAddress dcciNetAddress;
	protected int[] dccports;

	public MultiBotManager() {
		//Create a temp bot and mirror default values
		dummyBot = new PircBotX();
		name = dummyBot.getName();
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
