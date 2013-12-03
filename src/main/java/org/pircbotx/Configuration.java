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

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.SocketFactory;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.dcc.DccHandler;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.dcc.ReceiveFileTransfer;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.dcc.SendFileTransfer;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.CoreHooks;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.pircbotx.output.OutputCAP;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputDCC;
import org.pircbotx.output.OutputIRC;
import org.pircbotx.output.OutputRaw;
import org.pircbotx.output.OutputUser;

/**
 * Immutable configuration for PircBotX. Use {@link Configuration.Builder} to create
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@ToString(exclude = {"serverPassword", "nickservPassword"})
public class Configuration<B extends PircBotX> {
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
	protected final String realName;
	protected final String channelPrefixes;
	//DCC
	protected final boolean dccFilenameQuotes;
	protected final ImmutableList<Integer> dccPorts;
	protected final InetAddress dccLocalAddress;
	protected final int dccAcceptTimeout;
	protected final int dccResumeAcceptTimeout;
	protected final int dccTransferBufferSize;
	protected final boolean dccPassiveRequest;
	//Connect information
	protected final String serverHostname;
	protected final int serverPort;
	protected final String serverPassword;
	protected final SocketFactory socketFactory;
	protected final InetAddress localAddress;
	protected final Charset encoding;
	protected final Locale locale;
	protected final int socketTimeout;
	protected final int maxLineLength;
	protected final boolean autoSplitMessage;
	protected final boolean autoNickChange;
	protected final long messageDelay;
	protected final boolean shutdownHookEnabled;
	protected final ImmutableMap<String, String> autoJoinChannels;
	protected final boolean identServerEnabled;
	protected final String nickservPassword;
	protected final boolean autoReconnect;
	//Bot classes
	protected final ListenerManager<B> listenerManager;
	protected final boolean capEnabled;
	protected final ImmutableList<CapHandler> capHandlers;
	protected final ImmutableSortedMap<Character, ChannelModeHandler> channelModeHandlers;
	protected final BotFactory botFactory;

	/**
	 * Use {@link Configuration.Builder#build() }.
	 * @param builder 
	 * @see Configuration.Builder#build()
	 */
	protected Configuration(Builder<B> builder) {
		//Check for basics
		if (builder.isWebIrcEnabled()) {
			checkNotNull(builder.getWebIrcAddress(), "Must specify WEBIRC address if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcHostname()), "Must specify WEBIRC hostname if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcUsername()), "Must specify WEBIRC username if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcPassword()), "Must specify WEBIRC password if enabled");
		}
		checkNotNull(builder.getListenerManager());
		checkArgument(StringUtils.isNotBlank(builder.getName()), "Must specify name");
		checkArgument(StringUtils.isNotBlank(builder.getLogin()), "Must specify login");
		checkArgument(StringUtils.isNotBlank(builder.getRealName()), "Must specify realName");
		checkArgument(StringUtils.isNotBlank(builder.getChannelPrefixes()), "Must specify channel prefixes");
		checkArgument(builder.getDccAcceptTimeout() > 0, "dccAcceptTimeout must be positive");
		checkArgument(builder.getDccResumeAcceptTimeout() > 0, "dccResumeAcceptTimeout must be positive");
		checkArgument(builder.getDccTransferBufferSize() > 0, "dccTransferBufferSize must be positive");
		checkArgument(StringUtils.isNotBlank(builder.getServerHostname()), "Must specify server hostname");
		checkArgument(builder.getServerPort() > 0 && builder.getServerPort() <= 65535, "Port must be between 1 and 65535");
		checkNotNull(builder.getSocketFactory(), "Must specify socket factory");
		checkNotNull(builder.getEncoding(), "Must specify encoding");
		checkNotNull(builder.getLocale(), "Must specify locale");
		checkArgument(builder.getSocketTimeout() >= 0, "Socket timeout must be positive");
		checkArgument(builder.getMaxLineLength() > 0, "Max line length must be positive");
		checkArgument(builder.getMessageDelay() >= 0, "Message delay must be positive");
		if (builder.getNickservPassword() != null)
			checkArgument(!builder.getNickservPassword().trim().equals(""), "Nickserv password cannot be empty");
		checkNotNull(builder.getListenerManager(), "Must specify listener manager");
		checkNotNull(builder.getBotFactory(), "Must specify bot factory");

		this.webIrcEnabled = builder.isWebIrcEnabled();
		this.webIrcUsername = builder.getWebIrcUsername();
		this.webIrcHostname = builder.getWebIrcHostname();
		this.webIrcAddress = builder.getWebIrcAddress();
		this.webIrcPassword = builder.getWebIrcPassword();
		this.name = builder.getName();
		this.login = builder.getLogin();
		this.version = builder.getVersion();
		this.finger = builder.getFinger();
		this.realName = builder.getRealName();
		this.channelPrefixes = builder.getChannelPrefixes();
		this.dccFilenameQuotes = builder.isDccFilenameQuotes();
		this.dccPorts = ImmutableList.copyOf(builder.getDccPorts());
		this.dccLocalAddress = builder.getDccLocalAddress();
		this.dccAcceptTimeout = builder.getDccAcceptTimeout();
		this.dccResumeAcceptTimeout = builder.getDccResumeAcceptTimeout();
		this.dccTransferBufferSize = builder.getDccTransferBufferSize();
		this.dccPassiveRequest = builder.isDccPassiveRequest();
		this.serverHostname = builder.getServerHostname();
		this.serverPort = builder.getServerPort();
		this.serverPassword = builder.getServerPassword();
		this.socketFactory = builder.getSocketFactory();
		this.localAddress = builder.getLocalAddress();
		this.encoding = builder.getEncoding();
		this.locale = builder.getLocale();
		this.socketTimeout = builder.getSocketTimeout();
		this.maxLineLength = builder.getMaxLineLength();
		this.autoSplitMessage = builder.isAutoSplitMessage();
		this.autoNickChange = builder.isAutoNickChange();
		this.messageDelay = builder.getMessageDelay();
		this.identServerEnabled = builder.isIdentServerEnabled();
		this.nickservPassword = builder.getNickservPassword();
		this.autoReconnect = builder.isAutoReconnect();
		this.listenerManager = builder.getListenerManager();
		this.autoJoinChannels = ImmutableMap.copyOf(builder.getAutoJoinChannels());
		this.capEnabled = builder.isCapEnabled();
		this.capHandlers = ImmutableList.copyOf(builder.getCapHandlers());
		ImmutableSortedMap.Builder<Character, ChannelModeHandler> channelModeHandlersBuilder = ImmutableSortedMap.naturalOrder();
		for (ChannelModeHandler curHandler : builder.getChannelModeHandlers())
			channelModeHandlersBuilder.put(curHandler.getMode(), curHandler);
		this.channelModeHandlers = channelModeHandlersBuilder.build();
		this.shutdownHookEnabled = builder.isShutdownHookEnabled();
		this.botFactory = builder.getBotFactory();
	}

	@Accessors(chain = true)
	@Data
	public static class Builder<B extends PircBotX> {
		//WebIRC
		/**
		 * Enable or disable sending WEBIRC line on connect
		 */
		protected boolean webIrcEnabled = false;
		/**
		 * Username of WEBIRC connection
		 */
		protected String webIrcUsername = null;
		/**
		 * Hostname of WEBIRC connection
		 */
		protected String webIrcHostname = null;
		/**
		 * IP address of WEBIRC connection
		 */
		protected InetAddress webIrcAddress = null;
		/**
		 * Password of WEBIRC connection
		 */
		protected String webIrcPassword = null;
		//Bot information
		/**
		 * The base name to be used for the IRC connection (nick!login@host)
		 */
		protected String name = "PircBotX";
		/**
		 * The login to be used for the IRC connection (nick!login@host)
		 */
		protected String login = "PircBotX";
		/**
		 * CTCP version response. 
		 */
		protected String version = "PircBotX " + PircBotX.VERSION + ", a fork of PircBot, the Java IRC bot - pircbotx.googlecode.com";
		/**
		 * CTCP finger response
		 */
		protected String finger = "You ought to be arrested for fingering a bot!";
		/**
		 * The realName/fullname used for WHOIS info. Defaults to version
		 */
		protected String realName = version;
		/**
		 * Allowed channel prefix characters. Defaults to <code>#&+!</code>
		 */
		protected String channelPrefixes = "#&+!";
		//DCC
		/**
		 * If true sends filenames in quotes, otherwise uses underscores. Defaults to false
		 */
		protected boolean dccFilenameQuotes = false;
		/**
		 * Ports to allow DCC incoming connections. Recommended to set multiple as
		 * DCC connections will be rejected if no free port can be found
		 */
		protected List<Integer> dccPorts = new ArrayList<Integer>();
		/**
		 * The local address to bind DCC connections to. Defaults to {@link #getLocalAddress() }
		 */
		protected InetAddress dccLocalAddress = null;
		/**
		 * Timeout for user to accept a sent DCC request. Defaults to {@link #getSocketTimeout() }
		 */
		protected int dccAcceptTimeout = -1;
		/**
		 * Timeout for a user to accept a resumed DCC request. Defaults to {@link #getDccResumeAcceptTimeout() }
		 */
		protected int dccResumeAcceptTimeout = -1;
		/**
		 * Size of the DCC file transfer buffer. Defaults to 1024
		 */
		protected int dccTransferBufferSize = 1024;
		/**
		 * Weather to send DCC Passive/reverse requests. Defaults to false
		 */
		protected boolean dccPassiveRequest = false;
		//Connect information
		/**
		 * Hostname of the IRC server
		 */
		protected String serverHostname = null;
		/**
		 * Port number of IRC server. Defaults to 6667
		 */
		protected int serverPort = 6667;
		/**
		 * Password for IRC server
		 */
		protected String serverPassword = null;
		/**
		 * Socket factory for connections. Defaults to {@link SocketFactory#getDefault() }
		 */
		protected SocketFactory socketFactory = SocketFactory.getDefault();
		/**
		 * Address to bind to when connecting to IRC server. 
		 */
		protected InetAddress localAddress = null;
		/**
		 * Charset encoding to use for connection. Defaults to {@link Charset#defaultCharset()}
		 */
		protected Charset encoding = Charset.defaultCharset();
		/**
		 * Locale to use for connection. Defaults to {@link Locale#getDefault() }
		 */
		protected Locale locale = Locale.getDefault();
		/**
		 * Timeout of IRC connection before sending PING. Defaults to 5 minutes
		 */
		protected int socketTimeout = 1000 * 60 * 5;
		/**
		 * Maximum line length of IRC server. Defaults to 512
		 */
		protected int maxLineLength = 512;
		/**
		 * Enable or disable automatic message splitting to fit {@link #getMaxLineLength()}.
		 * Note that messages might be truncated by the IRC server if not set. Defaults
		 * to true
		 */
		protected boolean autoSplitMessage = true;
		/**
		 * Enable or disable automatic nick changing if a nick is in use by adding
		 * a number to the end. If this is false and a nick is already in use, a 
		 * {@link IrcException} will be thrown. Defaults to false. 
		 */
		protected boolean autoNickChange = false;
		/**
		 * Millisecond delay between sending messages with {@link OutputRaw#rawLine(java.lang.String) }.
		 * Defaults to 1000 milliseconds
		 */
		protected long messageDelay = 1000;
		/**
		 * Enable or disable creating a JVM shutdown hook which will properly QUIT
		 * the IRC server and shutdown the bot. Defaults to true
		 */
		protected boolean shutdownHookEnabled = true;
		/**
		 * Map of channels and keys to automatically join upon connecting.
		 */
		protected final Map<String, String> autoJoinChannels = Maps.newHashMap();
		/**
		 * Enable or disable use of an existing {@link IdentServer}. Note that the
		 * IdentServer must be started separately or else an exception will be thrown.
		 * Defaults to false
		 * @see IdentServer
		 */
		protected boolean identServerEnabled = false;
		/**
		 * If set, password to authenticate against NICKSERV
		 */
		protected String nickservPassword;
		/**
		 * Enable or disable automatic reconnecting. Note that you MUST call 
		 * {@link PircBotX#stopBotReconnect() } when you do not want the bot to 
		 * reconnect anymore! Defaults to false
		 */
		protected boolean autoReconnect = false;
		//Bot classes
		/**
		 * The {@link ListenerManager} to use to handle events.
		 */
		protected ListenerManager<B> listenerManager = null;
		/**
		 * Enable or disable CAP handling. Defaults to false
		 */
		protected boolean capEnabled = false;
		/**
		 * Registered {@link CapHandler}'s. 
		 */
		protected final List<CapHandler> capHandlers = new ArrayList<CapHandler>();
		protected final List<ChannelModeHandler> channelModeHandlers = new ArrayList<ChannelModeHandler>();
		/**
		 * The {@link BotFactory} to use
		 */
		protected BotFactory botFactory = new BotFactory();

		/**
		 * Default constructor, adding a multi-prefix {@link EnableCapHandler}
		 */
		public Builder() {
			capHandlers.add(new EnableCapHandler("multi-prefix", true));
			capHandlers.add(new EnableCapHandler("away-notify", true));
			channelModeHandlers.addAll(InputParser.DEFAULT_CHANNEL_MODE_HANDLERS);
		}

		/**
		 * Copy values from an existing Configuration.
		 * @param configuration Configuration<B> to copy values from
		 */
		public Builder(Configuration<B> configuration) {
			this.webIrcEnabled = configuration.isWebIrcEnabled();
			this.webIrcUsername = configuration.getWebIrcUsername();
			this.webIrcHostname = configuration.getWebIrcHostname();
			this.webIrcAddress = configuration.getWebIrcAddress();
			this.webIrcPassword = configuration.getWebIrcPassword();
			this.name = configuration.getName();
			this.login = configuration.getLogin();
			this.version = configuration.getVersion();
			this.finger = configuration.getFinger();
			this.realName = configuration.getRealName();
			this.channelPrefixes = configuration.getChannelPrefixes();
			this.dccFilenameQuotes = configuration.isDccFilenameQuotes();
			this.dccPorts.addAll(configuration.getDccPorts());
			this.dccLocalAddress = configuration.getDccLocalAddress();
			this.dccAcceptTimeout = configuration.getDccAcceptTimeout();
			this.dccResumeAcceptTimeout = configuration.getDccResumeAcceptTimeout();
			this.dccTransferBufferSize = configuration.getDccTransferBufferSize();
			this.dccPassiveRequest = configuration.isDccPassiveRequest();
			this.serverHostname = configuration.getServerHostname();
			this.serverPort = configuration.getServerPort();
			this.serverPassword = configuration.getServerPassword();
			this.socketFactory = configuration.getSocketFactory();
			this.localAddress = configuration.getLocalAddress();
			this.encoding = configuration.getEncoding();
			this.locale = configuration.getLocale();
			this.socketTimeout = configuration.getSocketTimeout();
			this.maxLineLength = configuration.getMaxLineLength();
			this.autoSplitMessage = configuration.isAutoSplitMessage();
			this.autoNickChange = configuration.isAutoNickChange();
			this.messageDelay = configuration.getMessageDelay();
			this.listenerManager = configuration.getListenerManager();
			this.nickservPassword = configuration.getNickservPassword();
			this.autoReconnect = configuration.isAutoReconnect();
			this.autoJoinChannels.putAll(configuration.getAutoJoinChannels());
			this.identServerEnabled = configuration.isIdentServerEnabled();
			this.capEnabled = configuration.isCapEnabled();
			this.capHandlers.addAll(configuration.getCapHandlers());
			this.channelModeHandlers.addAll(configuration.getChannelModeHandlers().values());
			this.shutdownHookEnabled = configuration.isShutdownHookEnabled();
			this.botFactory = configuration.getBotFactory();
		}

		/**
		 * Copy values from another builder. 
		 * @param otherBuilder<B> 
		 */
		public Builder(Builder<B> otherBuilder) {
			this.webIrcEnabled = otherBuilder.isWebIrcEnabled();
			this.webIrcUsername = otherBuilder.getWebIrcUsername();
			this.webIrcHostname = otherBuilder.getWebIrcHostname();
			this.webIrcAddress = otherBuilder.getWebIrcAddress();
			this.webIrcPassword = otherBuilder.getWebIrcPassword();
			this.name = otherBuilder.getName();
			this.login = otherBuilder.getLogin();
			this.version = otherBuilder.getVersion();
			this.finger = otherBuilder.getFinger();
			this.realName = otherBuilder.getRealName();
			this.channelPrefixes = otherBuilder.getChannelPrefixes();
			this.dccFilenameQuotes = otherBuilder.isDccFilenameQuotes();
			this.dccPorts.addAll(otherBuilder.getDccPorts());
			this.dccLocalAddress = otherBuilder.getDccLocalAddress();
			this.dccAcceptTimeout = otherBuilder.getDccAcceptTimeout();
			this.dccResumeAcceptTimeout = otherBuilder.getDccResumeAcceptTimeout();
			this.dccTransferBufferSize = otherBuilder.getDccTransferBufferSize();
			this.dccPassiveRequest = otherBuilder.isDccPassiveRequest();
			this.serverHostname = otherBuilder.getServerHostname();
			this.serverPort = otherBuilder.getServerPort();
			this.serverPassword = otherBuilder.getServerPassword();
			this.socketFactory = otherBuilder.getSocketFactory();
			this.localAddress = otherBuilder.getLocalAddress();
			this.encoding = otherBuilder.getEncoding();
			this.locale = otherBuilder.getLocale();
			this.socketTimeout = otherBuilder.getSocketTimeout();
			this.maxLineLength = otherBuilder.getMaxLineLength();
			this.autoSplitMessage = otherBuilder.isAutoSplitMessage();
			this.autoNickChange = otherBuilder.isAutoNickChange();
			this.messageDelay = otherBuilder.getMessageDelay();
			this.listenerManager = otherBuilder.getListenerManager();
			this.nickservPassword = otherBuilder.getNickservPassword();
			this.autoReconnect = otherBuilder.isAutoReconnect();
			this.autoJoinChannels.putAll(otherBuilder.getAutoJoinChannels());
			this.identServerEnabled = otherBuilder.isIdentServerEnabled();
			this.capEnabled = otherBuilder.isCapEnabled();
			this.capHandlers.addAll(otherBuilder.getCapHandlers());
			this.channelModeHandlers.addAll(otherBuilder.getChannelModeHandlers());
			this.shutdownHookEnabled = otherBuilder.isShutdownHookEnabled();
			this.botFactory = otherBuilder.getBotFactory();
		}

		/**
		 * The local address to bind DCC connections to. Defaults to {@link #getLocalAddress() }
		 */
		public InetAddress getDccLocalAddress() {
			return (dccLocalAddress != null) ? dccLocalAddress : localAddress;
		}

		/**
		 * Timeout for user to accept a sent DCC request. Defaults to {@link #getSocketTimeout() }
		 */
		public int getDccAcceptTimeout() {
			return (dccAcceptTimeout != -1) ? dccAcceptTimeout : socketTimeout;
		}

		/**
		 * Timeout for a user to accept a resumed DCC request. Defaults to {@link #getDccResumeAcceptTimeout() }
		 */
		public int getDccResumeAcceptTimeout() {
			return (dccResumeAcceptTimeout != -1) ? dccResumeAcceptTimeout : getDccAcceptTimeout();
		}

		/**
		 * Utility method for <code>{@link #getCapHandlers()}.add(handler)</code>
		 * @param handler
		 * @return 
		 */
		public Builder<B> addCapHandler(CapHandler handler) {
			getCapHandlers().add(handler);
			return this;
		}

		/**
		 * Utility method for <code>{@link #getListenerManager().add(listener)</code>
		 * @param listener
		 * @return 
		 */
		public Builder<B> addListener(Listener<B> listener) {
			getListenerManager().addListener(listener);
			return this;
		}

		/**
		 * Utility method for <code>{@link #getAutoJoinChannels().put(channel, "")</code>
		 * @param channel
		 * @return 
		 */
		public Builder<B> addAutoJoinChannel(String channel) {
			getAutoJoinChannels().put(channel, "");
			return this;
		}

		/**
		 * Utility method for <code>{@link #getAutoJoinChannels().put(channel, key)</code>
		 * @param channel
		 * @return 
		 */
		public Builder<B> addAutoJoinChannel(String channel, String key) {
			getAutoJoinChannels().put(channel, key);
			return this;
		}

		/**
		 * Utility method to set server hostname and port
		 * @param hostname
		 * @param port
		 * @return 
		 */
		public Builder<B> setServer(String hostname, int port) {
			return setServerHostname(hostname)
					.setServerPort(port);
		}

		/**
		 * Utility method to set server hostname, port, and password
		 * @param hostname
		 * @param port
		 * @return 
		 */
		public Builder<B> setServer(String hostname, int port, String password) {
			return setServer(hostname, port).setServerPassword(password);
		}

		/**
		 * Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are added
		 * when this method is called. If you do not want this, remove CoreHooks with
		 * {@link ListenerManager#removeListener(org.pircbotx.hooks.Listener) }
		 * @param listenerManager The listener manager
		 */
		@SuppressWarnings("unchecked")
		public Builder<B> setListenerManager(ListenerManager<? extends B> listenerManager) {
			this.listenerManager = (ListenerManager<B>) listenerManager;
			for (Listener<B> curListener : this.listenerManager.getListeners())
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
		public ListenerManager<B> getListenerManager() {
			if (listenerManager == null)
				setListenerManager(new ThreadedListenerManager<B>());
			return listenerManager;
		}

		/**
		 * Build a new configuration from this Builder
		 * @return 
		 */
		public Configuration<B> buildConfiguration() {
			return new Configuration<B>(this);
		}

		/**
		 * Create a <b>new</b> builder with the specified hostname then build 
		 * a configuration. Useful for template builders
		 * @param hostname
		 * @return 
		 */
		public Configuration<B> buildForServer(String hostname) {
			return new Builder<B>(this)
					.setServerHostname(serverHostname)
					.buildConfiguration();
		}

		/**
		 * Create a <b>new</b> builder with the specified hostname and port then build 
		 * a configuration. Useful for template builders
		 * @param hostname
		 * @return 
		 */
		public Configuration<B> buildForServer(String hostname, int port) {
			return new Builder<B>(this)
					.setServerHostname(serverHostname)
					.setServerPort(serverPort)
					.buildConfiguration();
		}

		/**
		 * Create a <b>new</b> builder with the specified hostname, port, and password
		 * then build a configuration. Useful for template builders
		 * @param hostname
		 * @return 
		 */
		public Configuration<B> buildForServer(String hostname, int port, String password) {
			return new Builder<B>(this)
					.setServerHostname(serverHostname)
					.setServerPort(serverPort)
					.setServerPassword(serverPassword)
					.buildConfiguration();
		}
	}

	/**
	 * Factory for various bot classes. 
	 */
	public static class BotFactory {
		public UserChannelDao createUserChannelDao(PircBotX bot) {
			return new UserChannelDao(bot, bot.getConfiguration().getBotFactory());
		}

		public OutputRaw createOutputRaw(PircBotX bot) {
			return new OutputRaw(bot);
		}

		public OutputCAP createOutputCAP(PircBotX bot) {
			return new OutputCAP(bot);
		}

		public OutputIRC createOutputIRC(PircBotX bot) {
			return new OutputIRC(bot);
		}

		public OutputDCC createOutputDCC(PircBotX bot) {
			return new OutputDCC(bot);
		}

		public OutputChannel createOutputChannel(PircBotX bot, Channel channel) {
			return new OutputChannel(bot, channel);
		}

		public OutputUser createOutputUser(PircBotX bot, User user) {
			return new OutputUser(bot, user);
		}

		public InputParser createInputParser(PircBotX bot) {
			return new InputParser(bot);
		}

		public DccHandler createDccHandler(PircBotX bot) {
			return new DccHandler(bot);
		}

		public SendChat createSendChat(PircBotX bot, User user, Socket socket) throws IOException {
			return new SendChat(user, socket, bot.getConfiguration().getEncoding());
		}

		public ReceiveChat createReceiveChat(PircBotX bot, User user, Socket socket) throws IOException {
			return new ReceiveChat(user, socket, bot.getConfiguration().getEncoding());
		}

		public SendFileTransfer createSendFileTransfer(PircBotX bot, Socket socket, User user, File file, long startPosition) {
			return new SendFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
		}

		public ReceiveFileTransfer createReceiveFileTransfer(PircBotX bot, Socket socket, User user, File file, long startPosition) {
			return new ReceiveFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
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
