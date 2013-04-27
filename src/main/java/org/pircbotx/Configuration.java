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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.SocketFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.dcc.DccHandler;
import org.pircbotx.hooks.CoreHooks;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

/**
 * Configuration class for PircBotX
 * 
 * Bot information:
 * <ul><li>name - Name of the bot, which will be used as its nick when it
 * tries to join an IRC server.</li>
 * <li>login - Login of the bot</li>
 * <li>version - CTCP version response</li>
 * <li>finger - CTCP finger response</li></ul>
 * 
 * WebIRC:
 * 
 * DCC:
 * <ul><li>dccLocalAddress - Sets the InetAddress to be used when sending DCC chat or file transfers.
 * This can be very useful when you are running a bot on a machine which
 * is behind a firewall and you need to tell receiving clients to connect
 * to a NAT/router, which then forwards the connection.</li>
 * </ul>
 * 
 * Connect information
 * <ul><li>serverHostname - The hostname of the server (eg irc.freenode.net)
 * <li>serverPort - The port of the IRC server (default: 6667)
 * <li>serverPassword - The password of the IRC server
 * <li>messageDelay - number of milliseconds to delay between consecutive
 * messages
 * <li>socketFactory - SocketFactory to use to connect to the IRC server (default:
 * {@link SocketFactory#getDefault() }
 * <li>inetAddress - Local address to use when connecting to the IRC server
 * <li>encoding - The encoding {@link Charset} to use for the connection (default:
 * {@link Charset#defaultCharset()}
 * <li>socketTimeout - Number of milliseconds to wait before the socket times out on read
 * operations. This does not mean the socket is invalid. By default its 5 minutes
 * minutes
 * <li>maxLineLength - Maximum length of any line that is sent. (default: IRC 
 * RFC default (including \r\n) 512 bytes)
 * <li>autoSplitMessage - Enable or disable sendRawLineSplit splitting all lines
 * to maxLineLength (default: true)
 * <li>autoNickChange - Enable or disable changing nick in case it is already 
 * in use on the server by adding numbers until an unused nick is found 
 * 
 * Bot classes:
 * <li>listenerManager - Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are added
 * when this method is called. If you do not want this, remove CoreHooks with
 * {@link ListenerManager#removeListener(org.pircbotx.hooks.Listener) }
 * <li>capEnabled - If true, CAP handling is enabled (default: false)
 * <li>capHandlers - All CAP Handlers (default: a {@link EnableCapHandler}
 * for multi-prefix, ignoring errors)
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
public class Configuration {
	//WebIRC
	protected final boolean webIrcEnabled;
	protected final String webIrcUsername;
	protected final String webIrcHostname;
	protected final InetAddress webIrcAddress;
	protected final String webIrcPassword;
	//Bot information
	protected final String name;
	protected final String login;
	protected final String version;
	protected final String finger;
	protected final String channelPrefixes;
	//DCC
	protected final boolean dccFilenameQuotes;
	protected final ImmutableList<Integer> dccPorts;
	protected final InetAddress dccLocalAddress;
	protected final int dccSocketTimeout;
	protected final int dccTransferBufferSize;
	//Connect information
	protected final String serverHostname;
	protected final int serverPort;
	protected final String serverPassword;
	protected final SocketFactory socketFactory;
	protected final InetAddress localAddress;
	protected final Charset encoding;
	protected final int socketTimeout;
	protected final int maxLineLength;
	protected final boolean autoSplitMessage;
	protected final boolean autoNickChange;
	protected final long messageDelay;
	protected final boolean shutdownHookEnabled;
	protected final ImmutableMap<String, String> autoJoinChannels;
	protected final boolean useIdentServer;
	//Bot classes
	protected final ListenerManager<? extends PircBotX> listenerManager;
	protected final boolean capEnabled;
	protected final List<CapHandler> capHandlers;
	protected final BotFactory botFactory;

	/**
	 * Use {@link Configuration.Builder#build() }
	 * @param builder 
	 * @see Configuration.Builder#build()
	 */
	public Configuration(Builder builder) {
		this.webIrcEnabled = builder.isWebIrcEnabled();
		this.webIrcUsername = builder.getWebIrcUsername();
		this.webIrcHostname = builder.getWebIrcHostname();
		this.webIrcAddress = builder.getWebIrcAddress();
		this.webIrcPassword = builder.getWebIrcPassword();
		this.name = builder.getName();
		this.login = builder.getLogin();
		this.version = builder.getVersion();
		this.finger = builder.getFinger();
		this.channelPrefixes = builder.getChannelPrefixes();
		this.dccFilenameQuotes = builder.isDccFilenameQuotes();
		this.dccPorts = ImmutableList.copyOf(builder.getDccPorts());
		this.dccLocalAddress = builder.getDccLocalAddress();
		this.dccSocketTimeout = builder.getDccSocketTimeout();
		this.dccTransferBufferSize = builder.getDccTransferBufferSize();
		this.serverHostname = builder.getServerHostname();
		this.serverPort = builder.getServerPort();
		this.serverPassword = builder.getServerPassword();
		this.socketFactory = builder.getSocketFactory();
		this.localAddress = builder.getLocalAddress();
		this.encoding = builder.getEncoding();
		this.socketTimeout = builder.getSocketTimeout();
		this.maxLineLength = builder.getMaxLineLength();
		this.autoSplitMessage = builder.isAutoSplitMessage();
		this.autoNickChange = builder.isAutoNickChange();
		this.messageDelay = builder.getMessageDelay();
		this.useIdentServer = builder.isUseIdentServer();
		this.listenerManager = builder.getListenerManager();
		this.autoJoinChannels = ImmutableMap.copyOf(builder.getAutoJoinChannels());
		this.capEnabled = builder.isCapEnabled();
		this.capHandlers = ImmutableList.copyOf(builder.getCapHandlers());
		this.shutdownHookEnabled = builder.isShutdownHookEnabled();
		this.botFactory = builder.getBotFactory();
	}

	@Accessors(chain = true)
	@Data
	public static class Builder {
		//WebIRC
		protected boolean webIrcEnabled = false;
		protected String webIrcUsername = null;
		protected String webIrcHostname = null;
		protected InetAddress webIrcAddress = null;
		protected String webIrcPassword = null;
		//Bot information
		protected String name = "PircBotX";
		protected String login = "PircBotX";
		protected String version = "PircBotX " + PircBotX.VERSION + ", a fork of PircBot, the Java IRC bot - pircbotx.googlecode.com";
		protected String finger = "You ought to be arrested for fingering a bot!";
		protected String channelPrefixes = "#&+!";
		//DCC
		protected boolean dccFilenameQuotes = false;
		protected List<Integer> dccPorts = new ArrayList();
		protected InetAddress dccLocalAddress = null;
		protected int dccSocketTimeout = -1;
		protected int dccTransferBufferSize = 1024;
		//Connect information
		protected String serverHostname = null;
		protected int serverPort = 6667;
		protected String serverPassword = null;
		protected SocketFactory socketFactory = SocketFactory.getDefault();
		protected InetAddress localAddress = null;
		protected Charset encoding = Charset.defaultCharset();
		protected int socketTimeout = 1000 * 60 * 5;
		protected int maxLineLength = 512;
		protected boolean autoSplitMessage = true;
		protected boolean autoNickChange = false;
		protected long messageDelay = 1000;
		protected boolean shutdownHookEnabled = true;
		protected final Map<String, String> autoJoinChannels = new HashMap();
		protected boolean useIdentServer;
		//Bot classes
		protected ListenerManager<? extends PircBotX> listenerManager = null;
		protected boolean capEnabled = false;
		protected final List<CapHandler> capHandlers = new ArrayList();
		protected BotFactory botFactory = new BotFactory();

		public Builder() {
			capHandlers.add(new EnableCapHandler("multi-prefix", true));
		}
		
		/**
		 * Copy values from an existing Configuration
		 * @param configuration Configuration to copy values from
		 */
		public Builder(Configuration configuration) {
			this.webIrcEnabled = configuration.isWebIrcEnabled();
			this.webIrcUsername = configuration.getWebIrcUsername();
			this.webIrcHostname = configuration.getWebIrcHostname();
			this.webIrcAddress = configuration.getWebIrcAddress();
			this.webIrcPassword = configuration.getWebIrcPassword();
			this.name = configuration.getName();
			this.login = configuration.getLogin();
			this.version = configuration.getVersion();
			this.finger = configuration.getFinger();
			this.channelPrefixes = configuration.getChannelPrefixes();
			this.dccFilenameQuotes = configuration.isDccFilenameQuotes();
			this.dccPorts.addAll(configuration.getDccPorts());
			this.dccLocalAddress = configuration.getDccLocalAddress();
			this.dccSocketTimeout = configuration.getDccSocketTimeout();
			this.dccTransferBufferSize = configuration.getDccTransferBufferSize();
			this.serverHostname = configuration.getServerHostname();
			this.serverPort = configuration.getServerPort();
			this.serverPassword = configuration.getServerPassword();
			this.socketFactory = configuration.getSocketFactory();
			this.localAddress = configuration.getLocalAddress();
			this.encoding = configuration.getEncoding();
			this.socketTimeout = configuration.getSocketTimeout();
			this.maxLineLength = configuration.getMaxLineLength();
			this.autoSplitMessage = configuration.isAutoSplitMessage();
			this.autoNickChange = configuration.isAutoNickChange();
			this.messageDelay = configuration.getMessageDelay();
			this.listenerManager = configuration.getListenerManager();
			this.autoJoinChannels.putAll(configuration.getAutoJoinChannels());
			this.capEnabled = configuration.isCapEnabled();
			this.capHandlers.addAll(configuration.getCapHandlers());
			this.shutdownHookEnabled = configuration.isShutdownHookEnabled();
			this.botFactory = configuration.getBotFactory();
		}
		
		/**
		 * Copy values from another builder. 
		 * @param otherBuilder 
		 */
		public Builder(Builder otherBuilder) {
			this.webIrcEnabled = otherBuilder.isWebIrcEnabled();
			this.webIrcUsername = otherBuilder.getWebIrcUsername();
			this.webIrcHostname = otherBuilder.getWebIrcHostname();
			this.webIrcAddress = otherBuilder.getWebIrcAddress();
			this.webIrcPassword = otherBuilder.getWebIrcPassword();
			this.name = otherBuilder.getName();
			this.login = otherBuilder.getLogin();
			this.version = otherBuilder.getVersion();
			this.finger = otherBuilder.getFinger();
			this.channelPrefixes = otherBuilder.getChannelPrefixes();
			this.dccFilenameQuotes = otherBuilder.isDccFilenameQuotes();
			this.dccPorts.addAll(otherBuilder.getDccPorts());
			this.dccLocalAddress = otherBuilder.getDccLocalAddress();
			this.dccSocketTimeout = otherBuilder.getDccSocketTimeout();
			this.dccTransferBufferSize = otherBuilder.getDccTransferBufferSize();
			this.serverHostname = otherBuilder.getServerHostname();
			this.serverPort = otherBuilder.getServerPort();
			this.serverPassword = otherBuilder.getServerPassword();
			this.socketFactory = otherBuilder.getSocketFactory();
			this.localAddress = otherBuilder.getLocalAddress();
			this.encoding = otherBuilder.getEncoding();
			this.socketTimeout = otherBuilder.getSocketTimeout();
			this.maxLineLength = otherBuilder.getMaxLineLength();
			this.autoSplitMessage = otherBuilder.isAutoSplitMessage();
			this.autoNickChange = otherBuilder.isAutoNickChange();
			this.messageDelay = otherBuilder.getMessageDelay();
			this.listenerManager = otherBuilder.getListenerManager();
			this.autoJoinChannels.putAll(otherBuilder.getAutoJoinChannels());
			this.capEnabled = otherBuilder.isCapEnabled();
			this.capHandlers.addAll(otherBuilder.getCapHandlers());
			this.shutdownHookEnabled = otherBuilder.isShutdownHookEnabled();
			this.botFactory = otherBuilder.getBotFactory();
		}

		public InetAddress getDccLocalAddress() {
			return (dccLocalAddress != null) ? dccLocalAddress : localAddress;
		}

		public int getDccSocketTimeout() {
			return (dccSocketTimeout != -1) ? dccSocketTimeout : socketTimeout;
		}

		public Builder addCapHandler(CapHandler handler) {
			getCapHandlers().add(handler);
			return this;
		}

		public Builder addListener(Listener listener) {
			getListenerManager().addListener(listener);
			return this;
		}

		public Builder addAutoJoinChannel(String channel) {
			getAutoJoinChannels().put(channel, "");
			return this;
		}

		public Builder addAutoJoinChannel(String channel, String key) {
			getAutoJoinChannels().put(channel, key);
			return this;
		}

		public Builder setServer(String hostname, int port) {
			return setServerHostname(hostname)
					.setServerPort(port);
		}

		public Builder setServer(String hostname, int port, String password) {
			return setServer(hostname, port).setServerPassword(password);
		}

		/**
		 * Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are added
		 * when this method is called. If you do not want this, remove CoreHooks with
		 * {@link ListenerManager#removeListener(org.pircbotx.hooks.Listener) }
		 * @param listenerManager The listener manager
		 */
		public Builder setListenerManager(ListenerManager<? extends PircBotX> listenerManager) {
			this.listenerManager = listenerManager;
			for (Listener curListener : listenerManager.getListeners())
				if (curListener instanceof CoreHooks)
					return this;
			listenerManager.addListener(new CoreHooks());
			return this;
		}

		/**
		 * Returns the current ListenerManager in use by this bot. Note that the default
		 * listener manager ({@link ListenerManager}) is lazy loaded here unless one
		 * was already set
		 * @return Current ListenerManager
		 */
		public ListenerManager<? extends PircBotX> getListenerManager() {
			if (listenerManager == null) {
				listenerManager = new ThreadedListenerManager();
				listenerManager.addListener(new CoreHooks());
			}
			return listenerManager;
		}

		public Configuration buildConfiguration() {
			return new Configuration(this);
		}
	}

	public static class BotFactory {
		public UserChannelDao createUserChannelDao(PircBotX bot) {
			return new UserChannelDao(bot, bot.getConfiguration().getBotFactory());
		}

		public InputParser createInputParser(PircBotX bot) {
			return new InputParser(bot,
					bot.getConfiguration().getListenerManager(),
					bot.getUserChannelDao(),
					bot.getConfiguration().getChannelPrefixes(),
					bot.getServerInfo(),
					bot.getDccHandler());
		}

		public DccHandler createDccHandler(PircBotX bot) {
			return new DccHandler(bot.getConfiguration(), bot, bot.getConfiguration().getListenerManager());
		}

		public ServerInfo createServerInfo(PircBotX bot) {
			return new ServerInfo(bot);
		}

		public User createUser(PircBotX bot, String nick) {
			return new User(bot, bot.getUserChannelDao(), nick);
		}

		public Channel createChannel(PircBotX bot, String name) {
			return new Channel(bot, bot.getUserChannelDao(), name);
		}
	}
}
