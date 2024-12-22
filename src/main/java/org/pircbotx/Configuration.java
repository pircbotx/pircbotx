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
package org.pircbotx;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.SocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.dcc.DccHandler;
import org.pircbotx.dcc.DccHandler.PendingFileTransfer;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.dcc.ReceiveFileTransfer;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.dcc.SendFileTransfer;
import org.pircbotx.delay.Delay;
import org.pircbotx.delay.StaticDelay;
import org.pircbotx.delay.StaticReadonlyDelay;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Immutable configuration for PircBotX created from
 * {@link Configuration.Builder}
 */
@Data
@ToString(exclude = {"serverPassword", "nickservPassword", "nickservCustomMessage"})
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
	protected final String realName;
	protected final String channelPrefixes;
	protected final String userLevelPrefixes;
	protected final boolean snapshotsEnabled;
	//DCC
	protected final boolean dccFilenameQuotes;
	protected final ImmutableList<Integer> dccPorts;
	protected final InetAddress dccLocalAddress;
	protected final InetAddress dccPublicAddress;
	protected final int dccAcceptTimeout;
	protected final int dccResumeAcceptTimeout;
	protected final boolean dccPassiveRequest;
	//Connect information
	protected final ImmutableList<ServerEntry> servers;
	protected final String serverPassword;
	protected final SocketFactory socketFactory;
	protected final InetAddress localAddress;
	protected final Charset encoding;
	protected final Locale locale;
	protected final int socketConnectTimeout;
	protected final int socketTimeout;
	protected final int maxLineLength;
	protected final boolean autoSplitMessage;
	protected final boolean autoNickChange;
	protected final Delay messageDelay;
	protected final boolean shutdownHookEnabled;
	protected final ImmutableMap<String, String> autoJoinChannels;
	protected final boolean onJoinWhoEnabled;
	protected final boolean onJoinModeEnabled;
	protected final boolean identServerEnabled;
	protected final String nickservPassword;
	protected final String nickservOnSuccess;
	protected final String nickservNick;
	protected final String nickservCustomMessage;
	protected final boolean nickservDelayJoin;
	protected final boolean userModeHideRealHost;
	protected final boolean autoReconnect;
	protected final Delay autoReconnectDelay;
	protected final int autoReconnectAttempts;
	//Bot classes
	protected final ListenerManager listenerManager;
	protected final boolean capEnabled;
	protected final ImmutableList<CapHandler> capHandlers;
	protected final ImmutableSortedMap<Character, ChannelModeHandler> channelModeHandlers;
	protected final BotFactory botFactory;

	/**
	 * Use {@link Configuration.Builder#buildConfiguration() }.
	 *
	 * @param builder
	 * @see Configuration.Builder#buildConfiguration()
	 */
	protected Configuration(Builder builder) {
		//Check for basics
		if (builder.isWebIrcEnabled()) {
			checkNotNull(builder.getWebIrcAddress(), "Must specify WEBIRC address if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcHostname()), "Must specify WEBIRC hostname if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcUsername()), "Must specify WEBIRC username if enabled");
			checkArgument(StringUtils.isNotBlank(builder.getWebIrcPassword()), "Must specify WEBIRC password if enabled");
		}
		checkArgument(StringUtils.isNotBlank(builder.getName()), "Must specify name");
		checkArgument(StringUtils.isNotBlank(builder.getLogin()), "Must specify login");
		checkArgument(StringUtils.isNotBlank(builder.getVersion()), "Must specify version");
		checkArgument(StringUtils.isNotBlank(builder.getFinger()), "Must specify finger");
		checkArgument(StringUtils.isNotBlank(builder.getRealName()), "Must specify realName");
		checkArgument(StringUtils.isNotBlank(builder.getChannelPrefixes()), "Must specify channel prefixes");
		checkNotNull(StringUtils.isNotBlank(builder.getUserLevelPrefixes()), "Channel mode message prefixes cannot be null");
		checkNotNull(builder.getDccPorts(), "DCC ports list cannot be null");
		checkArgument(builder.getDccAcceptTimeout() > 0, "dccAcceptTimeout must be positive");
		checkArgument(builder.getDccResumeAcceptTimeout() > 0, "dccResumeAcceptTimeout must be positive");
		checkNotNull(builder.getServers(), "Servers list cannot be null");
		checkArgument(!builder.getServers().isEmpty(), "Must specify servers to connect to");
		for (ServerEntry serverEntry : builder.getServers()) {
			checkArgument(StringUtils.isNotBlank(serverEntry.getHostname()), "Must specify server hostname");
			checkArgument(serverEntry.getPort() > 0 && serverEntry.getPort() <= 65535, "Port must be between 1 and 65535");
		}
		checkNotNull(builder.getSocketFactory(), "Socket factory cannot be null");
		checkNotNull(builder.getEncoding(), "Encoding cannot be null");
		checkNotNull(builder.getLocale(), "Locale cannot be null");
		checkArgument(builder.getSocketConnectTimeout() > 0, "Socket connect timeout must greater than 0");
		checkArgument(builder.getSocketTimeout() > 0, "Socket timeout must greater than 0");
		checkArgument(builder.getMaxLineLength() > 0, "Max line length must be positive");
		checkNotNull(builder.getMessageDelay(), "Message delay cannot be null");
		checkNotNull(builder.getAutoJoinChannels(), "Auto join channels map cannot be null");
		for (Map.Entry<String, String> curEntry : builder.getAutoJoinChannels().entrySet())
			if (StringUtils.isBlank(curEntry.getKey()))
				throw new RuntimeException("Channel must not be blank");
		if (builder.getNickservPassword() != null)
			checkArgument(StringUtils.isNotBlank(builder.getNickservPassword()), "Nickserv password cannot be empty");
		if (builder.getNickservCustomMessage() != null)
			checkArgument(StringUtils.isNotBlank(builder.getNickservCustomMessage()), "Nickserv custom message cannot be empty");
		checkArgument(StringUtils.isNotBlank(builder.getNickservOnSuccess()), "Nickserv on success cannot be blank");
		checkArgument(StringUtils.isNotBlank(builder.getNickservNick()), "Nickserv nick cannot be blank");
		checkArgument(builder.getAutoReconnectAttempts() > 0, "setAutoReconnectAttempts must be greater than 0");
		checkNotNull(builder.getAutoReconnectDelay(), "setAutoReconnectDelay cannot be null");
		checkNotNull(builder.getListenerManager(), "Must specify listener manager");
		checkNotNull(builder.getCapHandlers(), "Cap handlers list cannot be null");
		checkNotNull(builder.getChannelModeHandlers(), "Channel mode handlers list cannot be null");
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
		this.channelPrefixes = builder.getChannelPrefixes().trim();
		this.userLevelPrefixes = builder.getUserLevelPrefixes().trim();
		this.snapshotsEnabled = builder.isSnapshotsEnabled();
		this.dccFilenameQuotes = builder.isDccFilenameQuotes();
		this.dccPorts = ImmutableList.copyOf(builder.getDccPorts());
		this.dccLocalAddress = builder.getDccLocalAddress();
		this.dccPublicAddress = builder.getDccPublicAddress();
		this.dccAcceptTimeout = builder.getDccAcceptTimeout();
		this.dccResumeAcceptTimeout = builder.getDccResumeAcceptTimeout();
		this.dccPassiveRequest = builder.isDccPassiveRequest();
		this.servers = ImmutableList.copyOf(builder.getServers());
		this.serverPassword = builder.getServerPassword();
		this.socketFactory = builder.getSocketFactory();
		this.localAddress = builder.getLocalAddress();
		this.encoding = builder.getEncoding();
		this.locale = builder.getLocale();
		this.socketConnectTimeout = builder.getSocketConnectTimeout();
		this.socketTimeout = builder.getSocketTimeout();
		this.maxLineLength = builder.getMaxLineLength();
		this.autoSplitMessage = builder.isAutoSplitMessage();
		this.autoNickChange = builder.isAutoNickChange();
		this.messageDelay = builder.getMessageDelay();
		this.identServerEnabled = builder.isIdentServerEnabled();
		this.nickservPassword = builder.getNickservPassword();
		this.nickservOnSuccess = builder.getNickservOnSuccess();
		this.nickservNick = builder.getNickservNick();
		this.nickservCustomMessage = builder.getNickservCustomMessage();
		this.nickservDelayJoin = builder.isNickservDelayJoin();
		this.userModeHideRealHost = builder.isUserModeHideRealHost();
		this.autoReconnect = builder.isAutoReconnect();
		this.autoReconnectDelay = builder.getAutoReconnectDelay();
		this.autoReconnectAttempts = builder.getAutoReconnectAttempts();
		this.listenerManager = builder.getListenerManager();
		this.autoJoinChannels = ImmutableMap.copyOf(builder.getAutoJoinChannels());
		this.onJoinWhoEnabled = builder.isOnJoinWhoEnabled();
		this.onJoinModeEnabled = builder.isOnJoinModeEnabled();
		this.capEnabled = builder.isCapEnabled();
		this.capHandlers = ImmutableList.copyOf(builder.getCapHandlers());
		ImmutableSortedMap.Builder<Character, ChannelModeHandler> channelModeHandlersBuilder = ImmutableSortedMap.naturalOrder();
		for (ChannelModeHandler curHandler : builder.getChannelModeHandlers())
			channelModeHandlersBuilder.put(curHandler.getMode(), curHandler);
		this.channelModeHandlers = channelModeHandlersBuilder.build();
		this.shutdownHookEnabled = builder.isShutdownHookEnabled();
		this.botFactory = builder.getBotFactory();
	}

	@SuppressWarnings("unchecked")
	public <M extends ListenerManager> M getListenerManager() {
		return (M) listenerManager;
	}

	/**
	 * Builder to create an immutable {@link Configuration}.
	 */
	@Accessors(chain = true)
	@Data
	public static class Builder {
		//WebIRC
		/**
		 * Enable or disable sending WEBIRC line on connect, default disabled
		 */
		protected boolean webIrcEnabled = false;
		/**
		 * Username of WEBIRC connection, must not be blank if WEBIRC is enabled
		 */
		protected String webIrcUsername = null;
		/**
		 * Hostname of WEBIRC connection, must not be blank if WEBIRC is enabled
		 */
		protected String webIrcHostname = null;
		/**
		 * IP address of WEBIRC connection, must be set if WEBIRC is enabled
		 */
		protected InetAddress webIrcAddress = null;
		/**
		 * Password of WEBIRC connection, must not be blank if WEBIRC is enabled
		 */
		protected String webIrcPassword = null;
		//Bot information
		/**
		 * The nick to be used for the IRC connection (nick!login@host), must
		 * not be blank
		 */
		protected String name;
		/**
		 * The login to be used for the IRC connection (nick!login@host),
		 * default PircBotX
		 */
		protected String login = "PircBotX";
		/**
		 * CTCP version response.
		 */
		protected String version = "PircBotX " + PircBotX.VERSION + " Java IRC bot - github.com/pircbotx/pircbotx";
		/**
		 * CTCP finger response
		 */
		protected String finger = "You ought to be arrested for fingering a bot!";
		/**
		 * The realName/fullname used for WHOIS info, defaults to version
		 */
		protected String realName = version;
		/**
		 * Allowed channel prefix characters, default <code>#&+!</code>
		 */
		protected String channelPrefixes = "#&+!";
		/**
		 * Supported channel prefixes that restrict a sent message to users with
		 * this mode, eg <code>PRIVMSG +#channel :hello</code> will only send a
		 * message to voiced or higher users, default <code>+@%&~!</code>
		 */
		protected String userLevelPrefixes = UserLevel.getSymbols() + "!";
		/**
		 * Enable creation of snapshots, default true. In bulk datasets or very
		 * lower power devices, creating snapshots can be a relatively expensive
		 * operation for every
		 * {@link org.pircbotx.hooks.types.GenericSnapshotEvent} (eg PartEvent,
		 * QuitEvent) since the entire UserChannelDao with all of its users and
		 * channels is cloned. This can optionally disabled by setting this to
		 * false, however this makes all
		 * {@link org.pircbotx.hooks.types.GenericSnapshotEvent#getUserChannelDaoSnapshot()}
		 * calls return null.
		 * <p>
		 * In regular usage disabling snapshots is not necessary because there
		 * relatively few user QUITs and PARTs per second.
		 */
		protected boolean snapshotsEnabled = true;
		//DCC
		/**
		 * If true sends filenames in quotes, otherwise uses underscores,
		 * default enabled.
		 */
		protected boolean dccFilenameQuotes = false;
		/**
		 * Ports to allow DCC incoming connections, recommended to set multiple
		 * as DCC connections will be rejected if no free port can be found
		 */
		protected List<Integer> dccPorts = new ArrayList<>();
		/**
		 * The local address to bind DCC connections to, defaults to null (which
		 * will be figured out at runtime)
		 */
		protected InetAddress dccLocalAddress = null;
		/**
		 * The public address advertised to other users, defaults to null (which
		 * will be figured out at runtime)
		 */
		protected InetAddress dccPublicAddress = null;
		/**
		 * Timeout for user to accept a sent DCC request, defaults to {@link #getSocketTimeout()
		 * }
		 */
		protected int dccAcceptTimeout = -1;
		/**
		 * Timeout for a user to accept a resumed DCC request, defaults to {@link #getDccResumeAcceptTimeout()
		 * }
		 */
		protected int dccResumeAcceptTimeout = -1;
		/**
		 * Send DCC requests as passive/reverse requests if not specified
		 * otherwise, default false
		 */
		protected boolean dccPassiveRequest = false;
		//Connect information
		/**
		 * List of servers to connect to, easily add with the addServer methods
		 */
		protected List<ServerEntry> servers = new LinkedList<>();
		/**
		 * Password for IRC server, default null
		 */
		protected String serverPassword = null;
		/**
		 * Socket factory for connections, defaults to {@link SocketFactory#getDefault()
		 * }
		 */
		protected SocketFactory socketFactory = SocketFactory.getDefault();
		/**
		 * Address to bind to when connecting to IRC server, default null
		 */
		protected InetAddress localAddress = null;
		/**
		 * Charset encoding to use for connection, defaults to
		 * {@link Charset#defaultCharset()}
		 */
		protected Charset encoding = Charset.defaultCharset();
		/**
		 * Locale to use for connection, defaults to {@link Locale#getDefault()
		 * }
		 */
		protected Locale locale = Locale.getDefault();
		/**
		 * Milliseconds to wait to connect to an IRC server address before
		 * trying the next address, default {@link #getSocketTimeout() }
		 */
		protected int socketConnectTimeout = -1;
		/**
		 * Milliseconds to wait with no data from the IRC server before sending
		 * a PING request to check if the socket is still alive, default 5
		 * minutes (1000x60x5=300,000 milliseconds)
		 */
		protected int socketTimeout = 1000 * 60 * 5;
		/**
		 * Maximum line length of IRC server, defaults 512 characters
		 */
		protected int maxLineLength = 512;
		/**
		 * Enable or disable automatic message splitting to fit
		 * {@link #getMaxLineLength()} to prevent the IRC server from possibly
		 * truncating or rejecting the line, default true.
		 */
		protected boolean autoSplitMessage = true;
		/**
		 * Enable or disable automatic nick changing if a nick is in use by
		 * adding a number to the end, default false which will throw a
		 * {@link IrcException} if the nick is already in use on the server
		 */
		protected boolean autoNickChange = false;
		/**
		 * Millisecond delay between sending messages, default 1000 milliseconds
		 * 
		 * For backwards compatibility the type is {@link Delay}, BUT it should be a StaticReadOnlyDelay, since 
		 * it can't be changed after initialization.
		 */
		protected Delay messageDelay = new StaticReadonlyDelay( 1000 );
		/**
		 * Enable or disable creating a JVM shutdown hook which will properly
		 * QUIT the IRC server and shutdown the bot, default true
		 */
		protected boolean shutdownHookEnabled = true;
		/**
		 * Map of channels and keys to automatically join upon connecting.
		 */
		protected final Map<String, String> autoJoinChannels = new HashMap<>();
		/**
		 * Enable or disable sending "WHO #channel" upon joining a channel and
		 * rely only on the NAMES response
		 */
		protected boolean onJoinWhoEnabled = true;
		/**
		 * Enable or disable sending "MODE #channel" upon joining a channel.
		 */
		protected boolean onJoinModeEnabled = true;
		/**
		 * Enable or disable use of an existing {@link IdentServer}, default
		 * false. Note that the IdentServer must be started separately or else
		 * an exception will be thrown
		 *
		 * @see IdentServer
		 */
		protected boolean identServerEnabled = false;
		/**
		 * Password to authenticate against NICKSERV, default null (will not try
		 * to identify)
		 */
		protected String nickservPassword = null;
		/**
		 * Case-insensitive message a user with 
		 * {@link #setNickservNick(java.lang.String) } in its hostmask will
		 * always contain when we have successfully identified, defaults to "you
		 * are now" which which matches all of the following known server
		 * responses:
		 * <ul>
		 * <li>ircd-seven (freenode) - You are now identified for PircBotX</li>
		 * <li>Unreal (swiftirc) - Password accepted - you are now
		 * recognized.</li>
		 * <li>InspIRCd (mozilla) - You are now logged in as PircBotX</li>
		 * </ul>
		 *
		 * @see PircBotX#isNickservIdentified()
		 * @see #setNickservNick(java.lang.String)
		 */
		protected String nickservOnSuccess = "you are now";
		/**
		 * The nick of the nickserv service account, default "nickserv".
		 *
		 * @see PircBotX#isNickservIdentified()
		 */
		protected String nickservNick = "nickserv";
		/**
		 * Some irc servers require a custom identify string.
		 * eg: Quakenet: <code>PRIVMSG Q@CServe.quakenet.org :AUTH USER PASS</code>
		 * default = null
		 */
		protected String nickservCustomMessage = null;
		/**
		 * Delay joining channels until were identified to nickserv, default
		 * false
		 */
		protected boolean nickservDelayJoin = false;
		/**
		 * Sets mode +x on the bot, to hide the real hostname, default = false
		 */
		protected boolean userModeHideRealHost = false;
		/**
		 * Enable or disable automatic reconnecting, default false. Note that
		 * you MUST call {@link PircBotX#stopBotReconnect() } when you do not
		 * want the bot to reconnect anymore!
		 */
		protected boolean autoReconnect = false;
		/**
		 * Delay in milliseconds between reconnect attempts, default 0.
		 */
		protected Delay autoReconnectDelay = new StaticDelay(0);
		/**
		 * Number of times to attempt to reconnect, default 5.
		 */
		protected int autoReconnectAttempts = 5;
		//Bot classes
		/**
		 * The {@link ListenerManager} to use to handle events, default
		 * {@link ThreadedListenerManager}.
		 */
		//This is lazy loaded in {@link #getListenerManager()} since creating a thread pool is expensive
		protected ListenerManager listenerManager = null;
		/**
		 * Enable or disable CAP handling, defaults true.
		 */
		protected boolean capEnabled = true;
		/**
		 * IRCv3 CAP features to try to use, default enables multi-prefix and
		 * away-notify but ignoring if the server doesn't support them
		 */
		protected final List<CapHandler> capHandlers = Lists.<CapHandler>newArrayList(
				new EnableCapHandler("multi-prefix", true),
				new EnableCapHandler("away-notify", true)
		);
		/**
		 * Handlers for channel modes, defaults to built-in handlers which cover
		 * basic modes that are generally supported on most IRC servers
		 */
		protected final List<ChannelModeHandler> channelModeHandlers = Lists.newArrayList(InputParser.DEFAULT_CHANNEL_MODE_HANDLERS);
		/**
		 * The {@link BotFactory} to use
		 */
		protected BotFactory botFactory = new BotFactory();

		/**
		 * Create with defaults that work in most situations and IRC servers
		 */
		public Builder() {
		}

		/**
		 * Copy values from an existing Configuration.
		 *
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
			this.realName = configuration.getRealName();
			this.channelPrefixes = configuration.getChannelPrefixes();
			this.userLevelPrefixes = configuration.getUserLevelPrefixes();
			this.snapshotsEnabled = configuration.isSnapshotsEnabled();
			this.dccFilenameQuotes = configuration.isDccFilenameQuotes();
			this.dccPorts.clear();
			this.dccPorts.addAll(configuration.getDccPorts());
			this.dccLocalAddress = configuration.getDccLocalAddress();
			this.dccPublicAddress = configuration.getDccPublicAddress();
			this.dccAcceptTimeout = configuration.getDccAcceptTimeout();
			this.dccResumeAcceptTimeout = configuration.getDccResumeAcceptTimeout();
			this.dccPassiveRequest = configuration.isDccPassiveRequest();
			this.servers.clear();
			this.servers.addAll(configuration.getServers());
			this.serverPassword = configuration.getServerPassword();
			this.socketFactory = configuration.getSocketFactory();
			this.localAddress = configuration.getLocalAddress();
			this.encoding = configuration.getEncoding();
			this.locale = configuration.getLocale();
			this.socketConnectTimeout = configuration.getSocketConnectTimeout();
			this.socketTimeout = configuration.getSocketTimeout();
			this.maxLineLength = configuration.getMaxLineLength();
			this.autoSplitMessage = configuration.isAutoSplitMessage();
			this.autoNickChange = configuration.isAutoNickChange();
			this.messageDelay = configuration.getMessageDelay();
			this.listenerManager = configuration.getListenerManager();
			this.nickservPassword = configuration.getNickservPassword();
			this.nickservOnSuccess = configuration.getNickservOnSuccess();
			this.nickservNick = configuration.getNickservNick();
			this.nickservCustomMessage = configuration.getNickservCustomMessage();
			this.nickservDelayJoin = configuration.isNickservDelayJoin();
			this.userModeHideRealHost = configuration.isUserModeHideRealHost();
			this.autoReconnect = configuration.isAutoReconnect();
			this.autoReconnectDelay = configuration.getAutoReconnectDelay();
			this.autoReconnectAttempts = configuration.getAutoReconnectAttempts();
			this.autoJoinChannels.clear();
			this.autoJoinChannels.putAll(configuration.getAutoJoinChannels());
			this.onJoinWhoEnabled = configuration.isOnJoinWhoEnabled();
			this.onJoinModeEnabled = configuration.isOnJoinModeEnabled();
			this.identServerEnabled = configuration.isIdentServerEnabled();
			this.capEnabled = configuration.isCapEnabled();
			this.capHandlers.clear();
			this.capHandlers.addAll(configuration.getCapHandlers());
			this.channelModeHandlers.clear();
			this.channelModeHandlers.addAll(configuration.getChannelModeHandlers().values());
			this.shutdownHookEnabled = configuration.isShutdownHookEnabled();
			this.botFactory = configuration.getBotFactory();
		}

		/**
		 * Copy values from another builder.
		 *
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
			this.realName = otherBuilder.getRealName();
			this.channelPrefixes = otherBuilder.getChannelPrefixes();
			this.userLevelPrefixes = otherBuilder.getUserLevelPrefixes();
			this.snapshotsEnabled = otherBuilder.isSnapshotsEnabled();
			this.dccFilenameQuotes = otherBuilder.isDccFilenameQuotes();
			this.dccPorts.clear();
			this.dccPorts.addAll(otherBuilder.getDccPorts());
			this.dccLocalAddress = otherBuilder.getDccLocalAddress();
			this.dccPublicAddress = otherBuilder.getDccPublicAddress();
			this.dccAcceptTimeout = otherBuilder.getDccAcceptTimeout();
			this.dccResumeAcceptTimeout = otherBuilder.getDccResumeAcceptTimeout();
			this.dccPassiveRequest = otherBuilder.isDccPassiveRequest();
			this.servers.clear();
			this.servers.addAll(otherBuilder.getServers());
			this.serverPassword = otherBuilder.getServerPassword();
			this.socketFactory = otherBuilder.getSocketFactory();
			this.localAddress = otherBuilder.getLocalAddress();
			this.encoding = otherBuilder.getEncoding();
			this.locale = otherBuilder.getLocale();
			this.socketConnectTimeout = otherBuilder.getSocketConnectTimeout();
			this.socketTimeout = otherBuilder.getSocketTimeout();
			this.maxLineLength = otherBuilder.getMaxLineLength();
			this.autoSplitMessage = otherBuilder.isAutoSplitMessage();
			this.autoNickChange = otherBuilder.isAutoNickChange();
			this.messageDelay = otherBuilder.getMessageDelay();
			this.listenerManager = otherBuilder.getListenerManager();
			this.nickservPassword = otherBuilder.getNickservPassword();
			this.nickservOnSuccess = otherBuilder.getNickservOnSuccess();
			this.nickservNick = otherBuilder.getNickservNick();
			this.nickservCustomMessage = otherBuilder.getNickservCustomMessage();
			this.nickservDelayJoin = otherBuilder.isNickservDelayJoin();
			this.userModeHideRealHost = otherBuilder.isUserModeHideRealHost();
			this.autoReconnect = otherBuilder.isAutoReconnect();
			this.autoReconnectDelay = otherBuilder.getAutoReconnectDelay();
			this.autoReconnectAttempts = otherBuilder.getAutoReconnectAttempts();
			this.autoJoinChannels.putAll(otherBuilder.getAutoJoinChannels());
			this.onJoinWhoEnabled = otherBuilder.isOnJoinWhoEnabled();
			this.onJoinModeEnabled = otherBuilder.isOnJoinModeEnabled();
			this.identServerEnabled = otherBuilder.isIdentServerEnabled();
			this.capEnabled = otherBuilder.isCapEnabled();
			this.capHandlers.clear();
			this.capHandlers.addAll(otherBuilder.getCapHandlers());
			this.channelModeHandlers.clear();
			this.channelModeHandlers.addAll(otherBuilder.getChannelModeHandlers());
			this.shutdownHookEnabled = otherBuilder.isShutdownHookEnabled();
			this.botFactory = otherBuilder.getBotFactory();
		}

		public int getSocketConnectTimeout() {
			return (socketConnectTimeout != -1) ? socketConnectTimeout : socketTimeout;
		}

		/**
		 * Timeout for user to accept a sent DCC request. Defaults to {@link #getSocketTimeout()
		 * }
		 */
		public int getDccAcceptTimeout() {
			return (dccAcceptTimeout != -1) ? dccAcceptTimeout : socketTimeout;
		}

		/**
		 * Timeout for a user to accept a resumed DCC request. Defaults to {@link #getDccResumeAcceptTimeout()
		 * }
		 */
		public int getDccResumeAcceptTimeout() {
			return (dccResumeAcceptTimeout != -1) ? dccResumeAcceptTimeout : getDccAcceptTimeout();
		}

		/**
		 * Add a collection of cap handlers
		 *
		 * @see #getCapHandlers()
		 * @param handlers
		 */
		public Builder addCapHandlers(@NonNull Iterable<CapHandler> handlers) {
			for (CapHandler curHandler : handlers) {
				addCapHandler(curHandler);
			}
			return this;
		}

		/**
		 * Add a cap handler
		 *
		 * @see #getCapHandlers()
		 * @param handler
		 */
		public Builder addCapHandler(CapHandler handler) {
			getCapHandlers().add(handler);
			return this;
		}

		/**
		 * Add a collection of listeners to the current ListenerManager
		 *
		 * @see #getListenerManager()
		 * @param listeners
		 */
		public Builder addListeners(@NonNull Iterable<Listener> listeners) {
			for (Listener curListener : listeners) {
				addListener(curListener);
			}
			return this;
		}

		/**
		 * Add a listener to the current ListenerManager
		 *
		 * @see #getListenerManager()
		 * @param listener
		 */
		public Builder addListener(Listener listener) {
			getListenerManager().addListener(listener);
			return this;
		}

		public Builder addAutoJoinChannels(@NonNull Iterable<String> channels) {
			for (String curChannel : channels) {
				addAutoJoinChannel(curChannel);
			}
			return this;
		}

		/**
		 * Add a channel to join on connect
		 *
		 * @see #getAutoJoinChannels()
		 * @param channel
		 */
		public Builder addAutoJoinChannel(@NonNull String channel) {
			if (StringUtils.isBlank(channel))
				throw new RuntimeException("Channel must not be blank");
			getAutoJoinChannels().put(channel, "");
			return this;
		}

		/**
		 * Utility method for <code>{@link #getAutoJoinChannels()}.put(channel,
		 * key)</code>
		 *
		 * @param channel
		 */
		public Builder addAutoJoinChannel(@NonNull String channel, @NonNull String key) {
			if (StringUtils.isBlank(channel))
				throw new RuntimeException("Channel must not be blank");
			if (StringUtils.isBlank(key))
				throw new RuntimeException("Key must not be blank");
			getAutoJoinChannels().put(channel, key);
			return this;
		}
		
		//TODO: Temporary backwards compatibility
		private void checkSetServerBackwardsCompatible() {
			if(servers.size() >= 2)
				throw new RuntimeException("Cannot combine deprecated setServer and addServer");
		}
		
		/**
		 * @deprecated Use {@link #addServer(java.lang.String)},  
		 * will be removed in future releases
		 */
		@Deprecated
		public Builder setServer(String hostname) {
			checkSetServerBackwardsCompatible();
			servers.clear();
			servers.add(new ServerEntry(hostname, 6667));
			return this;
		}
		
		/**
		 * @deprecated Use {@link #addServer(java.lang.String, int)},  
		 * will be removed in future releases
		 */
		@Deprecated
		public Builder setServer(String hostname, int port) {
			checkSetServerBackwardsCompatible();
			servers.clear();
			servers.add(new ServerEntry(hostname, 6667));
			return this;
		}
		
		/**
		 * @deprecated Use {@link #addServer(java.lang.String)},  
		 * will be removed in future releases
		 */
		@Deprecated
		public Builder setServerHostname(String hostname) {
			checkSetServerBackwardsCompatible();
			if(servers.size() == 1)
				servers.add(new ServerEntry(hostname, servers.remove(0).port));
			else
				servers.add(new ServerEntry(hostname, 6667));
			return this;
		}
		
		/**
		 * @deprecated Use {@link #addServer(java.lang.String, int)},  
		 * will be removed in future releases
		 */
		@Deprecated
		public Builder setServerPort(int port) {
			checkSetServerBackwardsCompatible();
			if(servers.size() == 1)
				servers.add(new ServerEntry(servers.remove(0).hostname, port));
			else
				servers.add(new ServerEntry("unset", port));
			return this;
		}

		public Builder addServer(@NonNull String server) {
			servers.add(new ServerEntry(server, 6667));
			return this;
		}

		public Builder addServer(@NonNull String server, int port) {
			servers.add(new ServerEntry(server, port));
			return this;
		}

		public Builder addServer(@NonNull ServerEntry serverEntry) {
			servers.add(serverEntry);
			return this;
		}

		public Builder addServers(@NonNull Iterable<ServerEntry> serverEnteries) {
			for (ServerEntry curServerEntry : serverEnteries)
				servers.add(curServerEntry);
			return this;
		}

		/**
		 * Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are
		 * added when this method is called. If you do not want this, remove
		 * CoreHooks with
		 * {@link ListenerManager#removeListener(org.pircbotx.hooks.Listener) }
		 *
		 * @param listenerManager The listener manager
		 */
		public Builder setListenerManager(ListenerManager listenerManager) {
			this.listenerManager = listenerManager;
			for (Listener curListener : this.listenerManager.getListeners())
				if (curListener instanceof CoreHooks)
					return this;
			listenerManager.addListener(new CoreHooks());
			return this;
		}

		public void replaceCoreHooksListener(CoreHooks extended) {
			//Find the corehooks impl
			CoreHooks orig = null;
			for (Listener curListener : this.listenerManager.getListeners())
				if (curListener instanceof CoreHooks)
					orig = (CoreHooks) curListener;

			//Swap
			if (orig != null)
				this.listenerManager.removeListener(orig);
			addListener(extended);
		}

		/**
		 * Returns the current ListenerManager in use by this bot. Note that the
		 * default listener manager ({@link ListenerManager}) is lazy loaded
		 * here unless one was already set
		 *
		 * @return Current ListenerManager
		 */
		@SuppressWarnings("unchecked")
		public <M extends ListenerManager> M getListenerManager() {
			if (listenerManager == null)
				setListenerManager(new ThreadedListenerManager());
			return (M) listenerManager;
		}

		/**
		 * Builds a Configuration instance from the information in this builder
		 */
		public Configuration buildConfiguration() {
			return new Configuration(this);
		}

		/**
		 * Create a <i>new</i> builder with the specified hostname then build a
		 * configuration. Useful for template builders
		 *
		 * @param hostname
		 */
		public Configuration buildForServer(String hostname) {
			return new Builder(this)
					.addServer(hostname)
					.buildConfiguration();
		}

		/**
		 * Create a <i>new</i> builder with the specified hostname and port then
		 * build a configuration. Useful for template builders
		 *
		 * @param hostname
		 */
		public Configuration buildForServer(String hostname, int port) {
			return new Builder(this)
					.addServer(hostname, port)
					.buildConfiguration();
		}

		/**
		 * Create a <i>new</i> builder with the specified hostname, port, and
		 * password then build a configuration. Useful for template builders
		 *
		 * @param hostname
		 */
		public Configuration buildForServer(String hostname, int port, String password) {
			return new Builder(this)
					.addServer(hostname, port)
					.setServerPassword(password)
					.buildConfiguration();
		}
	}

	/**
	 * Factory for various internal bot classes.
	 */
	public static class BotFactory {
		//Allow subclasses to use own version of User and Channel
		@SuppressWarnings("rawtypes")
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

		public OutputUser createOutputUser(PircBotX bot, UserHostmask user) {
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

		public SendFileTransfer createSendFileTransfer(PircBotX bot, DccHandler dccHandler,
				PendingFileTransfer pendingFileTransfer, File file) {
			return new SendFileTransfer(bot, dccHandler, pendingFileTransfer, file);
		}

		public ReceiveFileTransfer createReceiveFileTransfer(PircBotX bot, DccHandler dccHandler,
				PendingFileTransfer pendingFileTransfer, File file) {
			return new ReceiveFileTransfer(bot, dccHandler, pendingFileTransfer, file);
		}

		public ServerInfo createServerInfo(PircBotX bot) {
			return new ServerInfo(bot);
		}

		public UserHostmask createUserHostmask(PircBotX bot, String hostmask) {
			return new UserHostmask(bot, hostmask);
		}

		public UserHostmask createUserHostmask(PircBotX bot, String extbanPrefix, String nick, String login, String hostname) {
			return new UserHostmask(bot, extbanPrefix, nick, login, hostname);
		}

		public User createUser(UserHostmask userHostmask) {
			return new User(userHostmask);
		}

		public Channel createChannel(PircBotX bot, String name) {
			return new Channel(bot, name);
		}
	}

	@Data
	public static class ServerEntry {
		@NonNull
		private final String hostname;
		private final int port;

		@Override
		public String toString() {
			return hostname + ":" + port;
		}
	}
}
