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

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.SocketFactory;
import lombok.Data;
import lombok.experimental.Accessors;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.EnableCapHandler;
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
	protected final Map<String, String> autoJoinChannels;
	//Bot classes
	protected final ListenerManager<? extends PircBotX> listenerManager;
	protected final boolean capEnabled;
	protected final List<CapHandler> capHandlers;

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
		this.listenerManager = builder.getListenerManager();
		this.autoJoinChannels = builder.getAutoJoinChannels();
		this.capEnabled = builder.isCapEnabled();
		this.capHandlers = builder.getCapHandlers();
		this.shutdownHookEnabled = builder.isShutdownHookEnabled();
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
		//Bot classes
		protected ListenerManager<? extends PircBotX> listenerManager = null;
		protected boolean capEnabled = false;
		protected final List<CapHandler> capHandlers = new ArrayList() {
			{
				add(new EnableCapHandler("multi-prefix", true));
			}
		};

		public Builder addCapHandler(CapHandler handler) {
			getCapHandlers().add(handler);
			return this;
		}

		public Builder addListener(Listener listener) {
			getListenerManager().addListener(listener);
			return this;
		}

		public Builder addAutoJoinChannel(String channel) {
			getAutoJoinChannels().put(channel, null);
			return this;
		}

		public Builder addAutoJoinChannel(String channel, String key) {
			getAutoJoinChannels().put(channel, null);
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
}
