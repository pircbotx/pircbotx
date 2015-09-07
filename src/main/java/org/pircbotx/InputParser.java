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
package org.pircbotx;

import org.pircbotx.snapshot.UserSnapshot;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.PeekingIterator;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import static org.pircbotx.ReplyConstants.*;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.BanListEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveModeratedEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelBanEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetModeratedEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Parse received input from IRC server.
 *
 * @author Leon Blakey
 */
@RequiredArgsConstructor
@Slf4j
public class InputParser implements Closeable {
	public static final Marker INPUT_MARKER = MarkerFactory.getMarker("pircbotx.input");
	/**
	 * Codes that say we are connected: Initial connection (001-4), user stats
	 * (251-5), or MOTD (375-6).
	 */
	protected static final ImmutableList<String> CONNECT_CODES = ImmutableList.of("001", "002", "003", "004", "005",
			"251", "252", "253", "254", "255", "375", "376");
	protected static final ImmutableList<ChannelModeHandler> DEFAULT_CHANNEL_MODE_HANDLERS;

	static {
		DEFAULT_CHANNEL_MODE_HANDLERS = ImmutableList.<ChannelModeHandler>builder()
				.add(new OpChannelModeHandler('o', UserLevel.OP) {
					@Override
					public void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding) {
						Utils.dispatchEvent(bot, new OpEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding));
					}
				})
				.add(new OpChannelModeHandler('v', UserLevel.VOICE) {
					@Override
					public void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding) {
						Utils.dispatchEvent(bot, new VoiceEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding));
					}
				})
				.add(new OpChannelModeHandler('h', UserLevel.HALFOP) {
					@Override
					public void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding) {
						Utils.dispatchEvent(bot, new HalfOpEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding));
					}
				})
				.add(new OpChannelModeHandler('a', UserLevel.SUPEROP) {
					@Override
					public void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding) {
						Utils.dispatchEvent(bot, new SuperOpEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding));
					}
				})
				.add(new OpChannelModeHandler('q', UserLevel.OWNER) {
					@Override
					public void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding) {
						Utils.dispatchEvent(bot, new OwnerEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding));
					}
				})
				.add(new ChannelModeHandler('k') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						if (adding) {
							String key = params.next();
							channel.setChannelKey(key);
							if (dispatchEvent)
								Utils.dispatchEvent(bot, new SetChannelKeyEvent(bot, channel, sourceHostmask, sourceUser, key));
						} else {
							String key = params.hasNext() ? params.next() : null;
							channel.setChannelKey(null);
							if (dispatchEvent)
								Utils.dispatchEvent(bot, new RemoveChannelKeyEvent(bot, channel, sourceHostmask, sourceUser, key));
						}
					}
				})
				.add(new ChannelModeHandler('l') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						if (adding) {
							int limit = Integer.parseInt(params.next());
							channel.setChannelLimit(limit);
							if (dispatchEvent)
								Utils.dispatchEvent(bot, new SetChannelLimitEvent(bot, channel, sourceHostmask, sourceUser, limit));
						} else {
							channel.setChannelLimit(-1);
							if (dispatchEvent)
								Utils.dispatchEvent(bot, new RemoveChannelLimitEvent(bot, channel, sourceHostmask, sourceUser));
						}
					}
				})
				.add(new ChannelModeHandler('b') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						if (dispatchEvent) {
							UserHostmask banHostmask = bot.getConfiguration().getBotFactory().createUserHostmask(bot, params.next());
							if (adding)
								Utils.dispatchEvent(bot, new SetChannelBanEvent(bot, channel, sourceHostmask, sourceUser, banHostmask));
							else
								Utils.dispatchEvent(bot, new RemoveChannelBanEvent(bot, channel, sourceHostmask, sourceUser, banHostmask));
						}
					}
				})
				.add(new ChannelModeHandler('t') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setTopicProtection(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetTopicProtectionEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemoveTopicProtectionEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.add(new ChannelModeHandler('n') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setNoExternalMessages(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetNoExternalMessagesEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemoveNoExternalMessagesEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.add(new ChannelModeHandler('i') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setInviteOnly(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetInviteOnlyEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemoveInviteOnlyEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.add(new ChannelModeHandler('m') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setModerated(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetModeratedEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemoveModeratedEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.add(new ChannelModeHandler('p') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setChannelPrivate(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetPrivateEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemovePrivateEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.add(new ChannelModeHandler('s') {
					@Override
					public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
						channel.setSecret(adding);
						if (dispatchEvent)
							if (adding)
								Utils.dispatchEvent(bot, new SetSecretEvent(bot, channel, sourceHostmask, sourceUser));
							else
								Utils.dispatchEvent(bot, new RemoveSecretEvent(bot, channel, sourceHostmask, sourceUser));
					}
				})
				.build();
	}
	protected final Configuration configuration;
	protected final PircBotX bot;
	protected final List<CapHandler> capHandlersFinished = Lists.newArrayList();
	protected boolean capEndSent = false;
	protected BufferedReader inputReader;
	//Builders
	/**
	 * Map to keep active WhoisEvents. Must be a treemap to be case insensitive
	 */
	protected final Map<String, WhoisEvent.Builder> whoisBuilder = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
	protected StringBuilder motdBuilder;
	@Getter
	protected boolean channelListRunning = false;
	protected ImmutableList.Builder<ChannelListEntry> channelListBuilder;
	protected int nickSuffix = 0;
	protected final Multimap<Channel, BanListEvent.Entry> banListBuilder = LinkedListMultimap.create();

	public InputParser(PircBotX bot) {
		this.bot = bot;
		this.configuration = bot.getConfiguration();
	}

	/**
	 * This method handles events when any line of text arrives from the server,
	 * then dispatching the appropriate event.
	 *
	 * @param rawLine The raw line of text from the server.
	 */
	public void handleLine(@NonNull String rawLine) throws IOException, IrcException {
		String line = CharMatcher.WHITESPACE.trimFrom(rawLine);
		log.info(INPUT_MARKER, line);

		// Parse out v3Tags before
		ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();
		if (line.startsWith("@")) {
			//This message has IRCv3 tags
			String v3Tags = line.substring(1, line.indexOf(" "));
			line = line.substring(line.indexOf(" ") + 1);

			StringTokenizer tokenizer = new StringTokenizer(v3Tags);

			while (tokenizer.hasMoreTokens()) {
				String tag = tokenizer.nextToken(";");
				if (tag.contains("=")) {
					String[] parts = tag.split("=");
					tags.put(parts[0], (parts.length == 2 ? parts[1] : ""));
				} else {
					tags.put(tag, "");
				}
			}
		}

		List<String> parsedLine = Utils.tokenizeLine(line);

		String sourceRaw = "";
		if (parsedLine.get(0).charAt(0) == ':')
			sourceRaw = parsedLine.remove(0);

		String command = parsedLine.remove(0).toUpperCase(configuration.getLocale());

		// Check for server pings.
		if (command.equals("PING")) {
			// Respond to the ping and return immediately.
			configuration.getListenerManager().onEvent(new ServerPingEvent(bot, parsedLine.get(0)));
			return;
		} else if (command.startsWith("ERROR")) {
			//Server is shutting us down
			bot.close();
			return;
		}

		String target = (parsedLine.isEmpty()) ? "" : parsedLine.get(0);
		if (target.startsWith(":"))
			target = target.substring(1);

		//Make sure this is a valid IRC line
		if (!sourceRaw.startsWith(":")) {
			// We don't know what this line means.
			configuration.getListenerManager().onEvent(new UnknownEvent(bot, line));
			if (!bot.loggedIn)
				//Pass to CapHandlers, could be important
				for (CapHandler curCapHandler : configuration.getCapHandlers())
					if (curCapHandler.handleUnknown(bot, line))
						addCapHandlerFinished(curCapHandler);
			//Do not continue
			return;
		}

		if (!bot.loggedIn)
			processConnect(line, command, target, parsedLine);

		//Might be a backend code 
		int code = Utils.tryParseInt(command, -1);
		if (code != -1) {
			processServerResponse(code, line, parsedLine);
			//Do not continue
			return;
		}

		//Must be from user
		UserHostmask source = bot.getConfiguration().getBotFactory().createUserHostmask(bot, sourceRaw.substring(1));
		processCommand(target, source, command, line, parsedLine, tags.build());
	}

	/**
	 * Process any lines relevant to connect. Only called before bot is logged
	 * into the server
	 *
	 * @param rawLine Raw, unprocessed line from the server
	 * @param code
	 * @param target
	 * @param parsedLine Processed line
	 * @throws IrcException If the server rejects the bot (nick already in use
	 * or a 4** or 5** code
	 * @throws IOException If an error occurs during upgrading to SSL
	 */
	public void processConnect(String rawLine, String code, String target, List<String> parsedLine) throws IrcException, IOException {
		if (CONNECT_CODES.contains(code)) {
			// We're connected to the server.
			bot.onLoggedIn(parsedLine.get(0));
			log.debug("Logged onto server.");

			configuration.getListenerManager().onEvent(new ConnectEvent(bot));

			//Handle automatic on connect stuff
			if (configuration.getNickservPassword() != null)
				bot.sendIRC().identify(configuration.getNickservPassword());
			ImmutableMap<String, String> autoConnectChannels = bot.reconnectChannels();
			if (autoConnectChannels == null)
				if (configuration.isNickservDelayJoin())
					autoConnectChannels = ImmutableMap.of();
				else
					autoConnectChannels = configuration.getAutoJoinChannels();
			for (Map.Entry<String, String> channelEntry : autoConnectChannels.entrySet())
				bot.sendIRC().joinChannel(channelEntry.getKey(), channelEntry.getValue());
		} else if (code.equals("439"))
			//EXAMPLE: PircBotX: Target change too fast. Please wait 104 seconds
			// No action required.
			//TODO: Should we delay joining channels here or something?
			log.warn("Ignoring too fast error");
		else if (configuration.isCapEnabled() && code.equals("421") && parsedLine.get(1).equals("CAP"))
			//EXAMPLE: 421 you CAP :Unknown command
			log.warn("Ignoring unknown command error, server does not support CAP negotiation");
		else if (configuration.isCapEnabled() && code.equals("451") && target.equals("CAP")) {
			//EXAMPLE: 451 CAP :You have not registered
			//Ignore, this is from servers that don't support CAP
			log.warn("Ignoring not registered error, server does not support CAP negotiation");
		} else if (configuration.isCapEnabled() && code.equals("410") && rawLine.contains("CAP")) {
			//EXAMPLE: 410 :Invalid CAP command
			//Ignore, Twitch.tv uses this code for some reason
			log.warn("Ignoring invalid command error, server does not support CAP negotiation");
		} else if ((code.startsWith("5") || code.startsWith("4")) && !code.equals("433"))
			//Ignore 433 NickAlreadyInUse, handled later
			throw new IrcException(IrcException.Reason.CannotLogin, "Received error: " + rawLine);
		else if (code.equals("670")) {
			//Server is saying that we can upgrade to TLS
			log.debug("Upgrading to TLS connection");
			SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) SSLSocketFactory.getDefault());
			for (CapHandler curCapHandler : configuration.getCapHandlers())
				if (curCapHandler instanceof TLSCapHandler)
					sslSocketFactory = ((TLSCapHandler) curCapHandler).getSslSocketFactory();
			SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(
					bot.getSocket(),
					bot.getLocalAddress().getHostAddress(),
					bot.getSocket().getPort(),
					true);
			sslSocket.startHandshake();
			bot.changeSocket(sslSocket);

			//Notify CAP Handlers
			for (CapHandler curCapHandler : configuration.getCapHandlers())
				if (curCapHandler.handleUnknown(bot, rawLine))
					addCapHandlerFinished(curCapHandler);
		} else if (code.equals("CAP") && configuration.isCapEnabled()) {
			//Handle CAP Code; remove extra from params
			String capCommand = parsedLine.get(1);
			ImmutableList<String> capParams = ImmutableList.copyOf(StringUtils.split(parsedLine.get(2)));
			if (capCommand.equals("LS")) {
				log.debug("Starting Cap Handlers {}", getCapHandlersRemaining());
				for (CapHandler curCapHandler : getCapHandlersRemaining()) {
					if (curCapHandler.handleLS(bot, capParams))
						addCapHandlerFinished(curCapHandler);
				}
			} else if (capCommand.equals("ACK")) {
				//Server is enabling a capability, store that
				bot.getEnabledCapabilities().addAll(capParams);

				for (CapHandler curCapHandler : getCapHandlersRemaining())
					if (curCapHandler.handleACK(bot, capParams))
						addCapHandlerFinished(curCapHandler);
			} else if (capCommand.equals("NAK")) {
				for (CapHandler curCapHandler : getCapHandlersRemaining())
					if (curCapHandler.handleNAK(bot, capParams))
						addCapHandlerFinished(curCapHandler);
			} else {
				//Maybe the CapHandlers know how to use it
				for (CapHandler curCapHandler : getCapHandlersRemaining())
					if (curCapHandler.handleUnknown(bot, rawLine))
						addCapHandlerFinished(curCapHandler);
			}
		} else
			//Pass to CapHandlers, could be important
			for (CapHandler curCapHandler : getCapHandlersRemaining())
				if (curCapHandler.handleUnknown(bot, rawLine))
					addCapHandlerFinished(curCapHandler);
	}

	protected List<CapHandler> getCapHandlersRemaining() {
		List<CapHandler> remaining = Lists.newArrayList(configuration.getCapHandlers());
		remaining.removeAll(capHandlersFinished);
		return remaining;
	}

	protected void addCapHandlerFinished(CapHandler capHandler) {
		log.debug("Cap Handler finished " + capHandler);
		capHandlersFinished.add(capHandler);
		if (!capEndSent && capHandlersFinished.size() == configuration.getCapHandlers().size()) {
			capEndSent = true;
			bot.sendCAP().end();
			bot.enabledCapabilities = Collections.unmodifiableList(bot.enabledCapabilities);
		}
	}

	public void processCommand(String target, UserHostmask source, String command, String line, List<String> parsedLine, ImmutableMap<String, String> tags) throws IOException {
		//If the channel matches a prefix, then its a channel
		Channel channel = (target.length() != 0 && bot.getUserChannelDao().containsChannel(target))
				? bot.getUserChannelDao().getChannel(target) : null;
		String message = parsedLine.size() >= 2 ? parsedLine.get(1) : "";
		//Try to load the source user if it exists
		User sourceUser = bot.getUserChannelDao().containsUser(source) ? bot.getUserChannelDao().getUser(source) : null;

		// Check for CTCP requests.
		if (command.equals("PRIVMSG") && message.startsWith("\u0001") && message.endsWith("\u0001")) {
			sourceUser = createUserIfNull(sourceUser, source);
			String request = message.substring(1, message.length() - 1);
			if (request.equals("VERSION"))
				// VERSION request
				configuration.getListenerManager().onEvent(new VersionEvent(bot, source, sourceUser, channel));
			else if (request.startsWith("ACTION "))
				// ACTION request
				configuration.getListenerManager().onEvent(new ActionEvent(bot, source, sourceUser, channel, target, request.substring(7)));
			else if (request.startsWith("PING "))
				// PING request
				configuration.getListenerManager().onEvent(new PingEvent(bot, source, sourceUser, channel, request.substring(5)));
			else if (request.equals("TIME"))
				// TIME request
				configuration.getListenerManager().onEvent(new TimeEvent(bot, channel, source, sourceUser));
			else if (request.equals("FINGER"))
				// FINGER request
				configuration.getListenerManager().onEvent(new FingerEvent(bot, source, sourceUser, channel));
			else if (request.startsWith("DCC ")) {
				// This is a DCC request.
				boolean success = bot.getDccHandler().processDcc(source, sourceUser, request);
				if (!success)
					// The DccManager didn't know what to do with the line.
					configuration.getListenerManager().onEvent(new UnknownEvent(bot, line));
			} else
				// An unknown CTCP message - ignore it.
				configuration.getListenerManager().onEvent(new UnknownEvent(bot, line));
		} else if (command.equals("PRIVMSG") && channel != null) {
			// This is a normal message to a channel.
			sourceUser = createUserIfNull(sourceUser, source);
			configuration.getListenerManager().onEvent(new MessageEvent(bot, channel, target, source, sourceUser, message, tags));
		} else if (command.equals("PRIVMSG")) {
			// This is a private message to us.
			//Add to private message
			sourceUser = createUserIfNull(sourceUser, source);
			bot.getUserChannelDao().addUserToPrivate(sourceUser);
			configuration.getListenerManager().onEvent(new PrivateMessageEvent(bot, source, sourceUser, message));
		} else if (command.equals("JOIN")) {
			// Someone is joining a channel.
			if (source.getNick().equalsIgnoreCase(bot.getNick())) {
				//Its us, get channel info
				channel = bot.getUserChannelDao().createChannel(target);
				if (configuration.isOnJoinWhoEnabled())
					bot.sendRaw().rawLine("WHO " + target);
				bot.sendRaw().rawLine("MODE " + target);
			}
			//Create user if it doesn't exist already
			sourceUser = createUserIfNull(sourceUser, source);

			bot.getUserChannelDao().addUserToChannel(sourceUser, channel);
			configuration.getListenerManager().onEvent(new JoinEvent(bot, channel, source, sourceUser));
		} else if (command.equals("PART")) {
			// Someone is parting from a channel.
			UserChannelDaoSnapshot daoSnapshot;
			ChannelSnapshot channelSnapshot;
			UserSnapshot sourceSnapshot;
			if (configuration.isSnapshotsEnabled()) {
				daoSnapshot = bot.getUserChannelDao().createSnapshot();
				channelSnapshot = daoSnapshot.getChannel(channel.getName());
				sourceSnapshot = daoSnapshot.getUser(source);
			} else {
				daoSnapshot = null;
				channelSnapshot = null;
				sourceSnapshot = null;
			}

			if (source.getNick().equalsIgnoreCase(bot.getNick()))
				//We parted the channel
				bot.getUserChannelDao().removeChannel(channel);
			else
				//Just remove the user from memory
				bot.getUserChannelDao().removeUserFromChannel(sourceUser, channel);
			configuration.getListenerManager().onEvent(new PartEvent(bot, daoSnapshot, channelSnapshot, source, sourceSnapshot, message));
		} else if (command.equals("NICK")) {
			// Somebody is changing their nick.
			sourceUser = createUserIfNull(sourceUser, source);
			String newNick = target;
			bot.getUserChannelDao().renameUser(sourceUser, newNick);
			if (source.getNick().equals(bot.getNick()))
				// Update our nick if it was us that changed nick.
				bot.setNick(newNick);
			configuration.getListenerManager().onEvent(new NickChangeEvent(bot, source.getNick(), newNick, source, sourceUser));
		} else if (command.equals("NOTICE")) {
			// Someone is sending a notice.
			configuration.getListenerManager().onEvent(new NoticeEvent(bot, source, sourceUser, channel, target, message));
		} else if (command.equals("QUIT")) {
			UserChannelDaoSnapshot daoSnapshot;
			UserSnapshot sourceSnapshot;
			if (configuration.isSnapshotsEnabled()) {
				daoSnapshot = bot.getUserChannelDao().createSnapshot();
				sourceSnapshot = daoSnapshot.getUser(sourceUser.getNick());
			} else {
				daoSnapshot = null;
				sourceSnapshot = null;
			}
			//A real target is missing, so index is off
			String reason = target;
			// Someone has quit from the IRC server.
			if (!source.getNick().equals(bot.getNick()))
				//Someone else
				bot.getUserChannelDao().removeUser(sourceUser);
			configuration.getListenerManager().onEvent(new QuitEvent(bot, daoSnapshot, source, sourceSnapshot, reason));
		} else if (command.equals("KICK")) {
			// Somebody has been kicked from a channel.
			UserHostmask recipientHostmask = bot.getConfiguration().getBotFactory().createUserHostmask(bot, message);
			User recipient = bot.getUserChannelDao().getUser(message);

			if (recipient.getNick().equals(bot.getNick()))
				//We were just kicked
				bot.getUserChannelDao().removeChannel(channel);
			else
				//Someone else
				bot.getUserChannelDao().removeUserFromChannel(recipient, channel);
			configuration.getListenerManager().onEvent(new KickEvent(bot, channel, source, sourceUser, recipientHostmask, recipient, parsedLine.get(2)));
		} else if (command.equals("MODE")) {
			// Somebody is changing the mode on a channel or user (Use long form since mode isn't after a : )
			String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
			if (mode.startsWith(":"))
				mode = mode.substring(1);
			//TODO: ummm... what does this do?
			//Handle situations where source doesn't have a full username (IE server setting user mode on connect)
			//User sourceModeUser = sourceUser;
			//if (sourceModeUser == null)
			//	sourceModeUser = bot.getUserChannelDao().getUser(source);
			processMode(source, sourceUser, target, mode);
		} else if (command.equals("TOPIC")) {
			// Someone is changing the topic.
			long currentTime = System.currentTimeMillis();
			String oldTopic = channel.getTopic();
			channel.setTopic(message);
			channel.setTopicSetter(source);
			channel.setTopicTimestamp(currentTime);

			configuration.getListenerManager().onEvent(new TopicEvent(bot, channel, oldTopic, message, source, currentTime, true));
		} else if (command.equals("INVITE")) {
			// Somebody is inviting somebody else into a channel.
			configuration.getListenerManager().onEvent(new InviteEvent(bot, source, sourceUser, message));
		} else if (command.equals("AWAY"))
			//IRCv3 AWAY notify
			if (parsedLine.isEmpty())
				sourceUser.setAwayMessage("");
			else
				sourceUser.setAwayMessage(parsedLine.get(0));
		else
			// If we reach this point, then we've found something that the PircBotX
			// Doesn't currently deal with.
			configuration.getListenerManager().onEvent(new UnknownEvent(bot, line));
	}

	/**
	 * This method is called by the PircBotX when a numeric response is received
	 * from the IRC server. We use this method to allow PircBotX to process
	 * various responses from the server before then passing them on to the
	 * onServerResponse method.
	 * <p>
	 * Note that this method is private and should not appear in any of the
	 * javadoc generated documentation.
	 *
	 * @param code The three-digit numerical code for the response.
	 */
	public void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
		ImmutableList<String> parsedResponse = ImmutableList.copyOf(parsedResponseOrig);
		//Parsed response format: Everything after code
		if (code == 433) {
			//EXAMPLE: * AnAlreadyUsedName :Nickname already in use
			//EXAMPLE: AnAlreadyUsedName :Nickname already in use (spec)
			//TODO: When output parsing is implemented intercept outgoing NICK?
			//Nickname in use, rename
			boolean autoNickChange = configuration.isAutoNickChange();
			String autoNewNick = null;
			String usedNick = null;

			boolean doAutoNickChange = false;
			//Ignore cases where we already have a valid nick but changed to a used one
			if (parsedResponse.size() == 3) {
				usedNick = parsedResponse.get(1);
				if (parsedResponse.get(0).equals("*")) {
					doAutoNickChange = true;
				}
			} //For spec-compilant servers, if were not logged in its safe to assume we don't have a valid nick on connect
			else {
				usedNick = parsedResponse.get(0);
				if (!bot.loggedIn) {
					doAutoNickChange = true;
				}
			}

			if (autoNickChange && doAutoNickChange) {
				nickSuffix++;
				autoNewNick = configuration.getName() + nickSuffix;
				bot.sendIRC().changeNick(autoNewNick);
				bot.setNick(autoNewNick);
				bot.getUserChannelDao().renameUser(bot.getUserChannelDao().getUser(usedNick), autoNewNick);
			}

			configuration.getListenerManager().onEvent(new NickAlreadyInUseEvent(bot, usedNick, autoNewNick, autoNickChange));
		} else if (code == RPL_LISTSTART) {
			//EXAMPLE: 321 Channel :Users Name (actual text)
			//A channel list is about to be sent
			channelListBuilder = ImmutableList.builder();
			channelListRunning = true;
		} else if (code == RPL_LIST) {
			//This is part of a full channel listing as part of /LIST
			//EXAMPLE: 322 lordquackstar #xomb 12 :xomb exokernel project @ www.xomb.org
			String channel = parsedResponse.get(1);
			int userCount = Utils.tryParseInt(parsedResponse.get(2), -1);
			String topic = parsedResponse.get(3);
			channelListBuilder.add(new ChannelListEntry(channel, userCount, topic));
		} else if (code == RPL_LISTEND) {
			//EXAMPLE: 323 :End of /LIST
			//End of channel list, dispatch event
			configuration.getListenerManager().onEvent(new ChannelInfoEvent(bot, channelListBuilder.build()));
			channelListBuilder = null;
			channelListRunning = false;
		} else if (code == RPL_TOPIC) {
			//EXAMPLE: 332 PircBotX #aChannel :I'm some random topic
			//This is topic about a channel we've just joined. From /JOIN or /TOPIC
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			String topic = parsedResponse.get(2);

			channel.setTopic(topic);
		} else if (code == RPL_TOPICINFO) {
			//EXAMPLE: 333 PircBotX #aChannel ISetTopic 1564842512
			//This is information on the topic of the channel we've just joined. From /JOIN or /TOPIC
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			UserHostmask setBy = configuration.getBotFactory().createUserHostmask(bot, parsedResponse.get(2));
			long date = Utils.tryParseLong(parsedResponse.get(3), -1);

			channel.setTopicTimestamp(date * 1000);
			channel.setTopicSetter(setBy);

			configuration.getListenerManager().onEvent(new TopicEvent(bot, channel, null, channel.getTopic(), setBy, date, false));
		} else if (code == RPL_WHOREPLY) {
			//EXAMPLE: 352 PircBotX #aChannel ~someName 74.56.56.56.my.Hostmask wolfe.freenode.net someNick H :0 Full Name
			//Part of a WHO reply on information on individual users
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));

			//Setup user
			String nick = parsedResponse.get(5);
			User curUser = bot.getUserChannelDao().containsUser(nick) ? bot.getUserChannelDao().getUser(nick) : null;
			UserHostmask curUserHostmask = bot.getConfiguration().getBotFactory().createUserHostmask(bot, null,
					nick, parsedResponse.get(2), parsedResponse.get(3));
			curUser = createUserIfNull(curUser, curUserHostmask);

			curUser.setServer(parsedResponse.get(4));
			processUserStatus(channel, curUser, parsedResponse.get(6));
			//Extra parsing needed since tokenizer stopped at :
			String rawEnding = parsedResponse.get(7);
			int rawEndingSpaceIndex = rawEnding.indexOf(' ');
			if (rawEndingSpaceIndex == -1) {
				//parsedResponse data is trimmed, so if the index == -1, then there was no real name given and the space separating hops from real name was trimmed.
				curUser.setHops(Integer.parseInt(rawEnding));
				curUser.setRealName("");
			} else {
				//parsedResponse data contains a real name
				curUser.setHops(Integer.parseInt(rawEnding.substring(0, rawEndingSpaceIndex)));
				curUser.setRealName(rawEnding.substring(rawEndingSpaceIndex + 1));
			}

			//Associate with channel
			bot.getUserChannelDao().addUserToChannel(curUser, channel);
		} else if (code == RPL_ENDOFWHO) {
			//EXAMPLE: 315 PircBotX #aChannel :End of /WHO list
			//End of the WHO reply
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			configuration.getListenerManager().onEvent(new UserListEvent(bot, channel, bot.getUserChannelDao().getUsers(channel), true));
		} else if (code == RPL_CHANNELMODEIS) {
			//EXAMPLE: 324 PircBotX #aChannel +cnt
			//Full channel mode (In response to MODE <channel>)
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			ImmutableList<String> modeParsed = parsedResponse.subList(2, parsedResponse.size());
			String mode = StringUtils.join(modeParsed, ' ');

			channel.setMode(mode, modeParsed);
			configuration.getListenerManager().onEvent(new ModeEvent(bot, channel, null, null, mode, modeParsed));
		} else if (code == 329) {
			//EXAMPLE: 329 lordquackstar #botters 1199140245
			//Tells when channel was created. From /JOIN
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			int createDate = Utils.tryParseInt(parsedResponse.get(2), -1);

			//Set in channel
			channel.setCreateTimestamp(createDate);
		} else if (code == RPL_MOTDSTART)
			//Example: 375 PircBotX :- wolfe.freenode.net Message of the Day -
			//Motd is starting, reset the StringBuilder
			motdBuilder = new StringBuilder();
		else if (code == RPL_MOTD)
			//Example: 372 PircBotX :- Welcome to wolfe.freenode.net in Manchester, England, Uk!  Thanks to
			//This is part of the MOTD, add a new line
			motdBuilder.append(CharMatcher.WHITESPACE.trimFrom(parsedResponse.get(1).substring(1))).append("\n");
		else if (code == RPL_ENDOFMOTD) {
			//Example: PircBotX :End of /MOTD command.
			//End of MOTD, clean it and dispatch MotdEvent
			ServerInfo serverInfo = bot.getServerInfo();
			serverInfo.setMotd(motdBuilder.toString().trim());
			motdBuilder = null;
			configuration.getListenerManager().onEvent(new MotdEvent(bot, serverInfo.getMotd()));
		} else if (code == 4 || code == 5) {
			//Example: 004 PircBotX sendak.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
			//Server info line, remove ending comment and let ServerInfo class parse it
			int endCommentIndex = rawResponse.lastIndexOf(" :");
			if (endCommentIndex > 1) {
				String endComment = rawResponse.substring(endCommentIndex + 2);
				int lastIndex = parsedResponseOrig.size() - 1;
				if (endComment.equals(parsedResponseOrig.get(lastIndex)))
					parsedResponseOrig.remove(lastIndex);
			}
			bot.getServerInfo().parse(code, parsedResponseOrig);
		} else if (code == RPL_WHOISUSER) {
			//Example: 311 TheLQ Plazma ~Plazma freenode/staff/plazma * :Plazma Rooolz!
			//New whois is starting
			String whoisNick = parsedResponse.get(1);

			WhoisEvent.Builder builder = WhoisEvent.builder();
			builder.nick(whoisNick);
			builder.login(parsedResponse.get(2));
			builder.hostname(parsedResponse.get(3));
			builder.realname(parsedResponse.get(5));
			whoisBuilder.put(whoisNick, builder);
		} else if (code == RPL_AWAY) {
			//Example: 301 PircBotXUser TheLQ_ :I'm away, sorry
			//Can be sent during whois
			String nick = parsedResponse.get(1);
			String awayMessage = parsedResponse.get(2);
			if (bot.getUserChannelDao().containsUser(nick))
				bot.getUserChannelDao().getUser(nick).setAwayMessage(awayMessage);
			if (whoisBuilder.containsKey(nick))
				whoisBuilder.get(nick).awayMessage(awayMessage);
		} else if (code == RPL_WHOISCHANNELS) {
			//Example: 319 TheLQ Plazma :+#freenode
			//Channel list from whois. Re-tokenize since they're after the :
			String whoisNick = parsedResponse.get(1);
			ImmutableList<String> parsedChannels = ImmutableList.copyOf(Utils.tokenizeLine(parsedResponse.get(2)));

			whoisBuilder.get(whoisNick).channels(parsedChannels);
		} else if (code == RPL_WHOISSERVER) {
			//Server info from whois
			//312 TheLQ Plazma leguin.freenode.net :Ume?, SE, EU
			String whoisNick = parsedResponse.get(1);

			whoisBuilder.get(whoisNick).server(parsedResponse.get(2));
			whoisBuilder.get(whoisNick).serverInfo(parsedResponse.get(3));
		} else if (code == RPL_WHOISIDLE) {
			//Idle time from whois
			//317 TheLQ md_5 6077 1347373349 :seconds idle, signon time
			String whoisNick = parsedResponse.get(1);

			whoisBuilder.get(whoisNick).idleSeconds(Long.parseLong(parsedResponse.get(2)));
			whoisBuilder.get(whoisNick).signOnTime(Long.parseLong(parsedResponse.get(3)));
		} else if (code == 330) {
			//RPL_WHOISACCOUNT: Extra Whois info
			//330 TheLQ Utoxin Utoxin :is logged in as
			//Make sure we set registered as to the nick, not to the note after the colon
			String registeredNick = "";
			if (!rawResponse.endsWith(":" + parsedResponse.get(2)))
				registeredNick = parsedResponse.get(2);
			whoisBuilder.get(parsedResponse.get(1)).registeredAs(registeredNick);
		} else if (code == 307) {
			//If shown, tells us that the user is registered with nickserv
			//307 TheLQ TheLQ-PircBotX :has identified for this nick
			whoisBuilder.get(parsedResponse.get(1)).registeredAs("");
		} else if (code == ERR_NOSUCHSERVER) {
			//Whois failed when doing "WHOIS invaliduser invaliduser"
			//402 TheLQ asdfasdf :No such server
			String whoisNick = parsedResponse.get(1);
			WhoisEvent event = WhoisEvent.builder()
					.nick(whoisNick)
					.exists(false)
					.generateEvent(bot);
			configuration.getListenerManager().onEvent(event);
		} else if (code == RPL_ENDOFWHOIS) {
			//End of whois
			//318 TheLQ Plazma :End of /WHOIS list.
			String whoisNick = parsedResponse.get(1);
			WhoisEvent.Builder builder;
			if (whoisBuilder.containsKey(whoisNick)) {
				builder = whoisBuilder.get(whoisNick);
				builder.exists(true);
			} else {
				builder = WhoisEvent.builder();
				builder.nick(whoisNick);
				builder.exists(false);
			}
			configuration.getListenerManager().onEvent(builder.generateEvent(bot));
			whoisBuilder.remove(whoisNick);
		} else if (code == 367) {
			//Ban list entry
			//367 TheLQ #aChannel *!*@test1.host TheLQ!~quackstar@some.host 1415143822
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));

			UserHostmask recipient = bot.getConfiguration().getBotFactory().createUserHostmask(bot, parsedResponse.get(2));
			UserHostmask source = bot.getConfiguration().getBotFactory().createUserHostmask(bot, parsedResponse.get(3));
			long time = Long.parseLong(parsedResponse.get(4));
			banListBuilder.put(channel, new BanListEvent.Entry(recipient, source, time));
			log.debug("Adding entry");
		} else if (code == 368) {
			//Ban list is finished
			//368 TheLQ #aChannel :End of Channel Ban List
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			ImmutableList<BanListEvent.Entry> entries = ImmutableList.copyOf(banListBuilder.removeAll(channel));
			log.debug("Dispatching event");
			configuration.getListenerManager().onEvent(new BanListEvent(bot, channel, entries));
		} else if (code == 353) {
			//NAMES response
			//353 PircBotXUser = #aChannel :aUser1 aUser2
			for (String curUser : StringUtils.split(parsedResponse.get(3))) {
				//Siphon off any levels this user has
				String nick = curUser;
				List<UserLevel> levels = Lists.newArrayList();
				UserLevel parsedLevel;
				while ((parsedLevel = UserLevel.fromSymbol(nick.charAt(0))) != null) {
					nick = nick.substring(1);
					levels.add(parsedLevel);
				}

				User user;
				if (!bot.getUserChannelDao().containsUser(nick))
					//Create user with nick only
					user = bot.getUserChannelDao().createUser(new UserHostmask(bot, nick));
				else
					user = bot.getUserChannelDao().getUser(nick);
				Channel chan = bot.getUserChannelDao().getChannel(parsedResponse.get(2));
				bot.getUserChannelDao().addUserToChannel(user, chan);

				//Now that the user is created, add them to the appropiate levels
				for (UserLevel curLevel : levels) {
					bot.getUserChannelDao().addUserToLevel(curLevel, user, chan);
				}
			}
		} else if (code == 366) {
			//NAMES response finished
			//366 PircBotXUser #aChannel :End of /NAMES list.
			Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
			configuration.getListenerManager().onEvent(new UserListEvent(bot, channel, bot.getUserChannelDao().getUsers(channel), false));
		}
		configuration.getListenerManager().onEvent(new ServerResponseEvent(bot, code, rawResponse, parsedResponse));
	}

	/**
	 * Called when the mode of a channel is set. We process this in order to
	 * call the appropriate onOp, onDeop, etc method before finally calling the
	 * override-able onMode method.
	 * <p>
	 * Note that this method is private and is not intended to appear in the
	 * javadoc generated documentation.
	 *
	 * @param target The channel or nick that the mode operation applies to.
	 * @param mode The mode that has been set.
	 */
	public void processMode(UserHostmask userHostmask, User user, String target, String mode) {
		if (configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0) {
			// The mode of a channel is being changed.
			Channel channel = bot.getUserChannelDao().getChannel(target);
			channel.parseMode(mode);
			ImmutableList<String> modeParsed = ImmutableList.copyOf(StringUtils.split(mode, ' '));
			PeekingIterator<String> params = Iterators.peekingIterator(modeParsed.iterator());

			//Process modes letter by letter, grabbing paramaters as needed
			boolean adding = true;
			String modeLetters = params.next();
			for (int i = 0; i < modeLetters.length(); i++) {
				char curModeChar = modeLetters.charAt(i);
				if (curModeChar == '+')
					adding = true;
				else if (curModeChar == '-')
					adding = false;
				else {
					ChannelModeHandler modeHandler = configuration.getChannelModeHandlers().get(curModeChar);
					if (modeHandler != null)
						modeHandler.handleMode(bot, channel, userHostmask, user, params, adding, true);
				}
			}
			configuration.getListenerManager().onEvent(new ModeEvent(bot, channel, userHostmask, user, mode, modeParsed));
		} else {
			// The mode of a user is being changed.
			UserHostmask targetHostmask = bot.getConfiguration().getBotFactory().createUserHostmask(bot, target);
			User targetUser = bot.getUserChannelDao().getUser(target);
			configuration.getListenerManager().onEvent(new UserModeEvent(bot, userHostmask, user, targetHostmask, targetUser, mode));
		}
	}

	public void processUserStatus(Channel chan, User user, String prefix) {
		for (char prefixChar : prefix.toCharArray()) {
			UserLevel level = UserLevel.fromSymbol(prefixChar);
			if (level != null)
				bot.getUserChannelDao().addUserToLevel(level, user, chan);
		}
		//Assume here (H) if there is no G
		user.setAwayMessage(prefix.contains("G") ? "" : null);
		user.setIrcop(prefix.contains("*"));
	}

	public User createUserIfNull(User otherUser, @NonNull UserHostmask hostmask) {
		if (otherUser != null) {
			//We could have fresh user data
			otherUser.updateHostmask(hostmask);
			return otherUser;
		} else if (bot.getUserChannelDao().containsUser(hostmask))
			throw new RuntimeException("User wasn't fetched but user exists in DAO. Please report this bug");
		return bot.getUserChannelDao().createUser(hostmask);
	}

	/**
	 * Clear out builders.
	 */
	public void close() {
		capEndSent = false;
		capHandlersFinished.clear();
		whoisBuilder.clear();
		motdBuilder = null;
		channelListRunning = false;
		channelListBuilder = null;
	}

	protected static abstract class OpChannelModeHandler extends ChannelModeHandler {
		protected final UserLevel level;

		public OpChannelModeHandler(char mode, UserLevel level) {
			super(mode);
			this.level = level;
		}

		@Override
		public void handleMode(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
			String recipient = params.next();
			UserHostmask recipientHostmask = bot.getConfiguration().getBotFactory().createUserHostmask(bot, recipient);
			User recipientUser = null;
			if (bot.getUserChannelDao().containsUser(recipient)) {
				recipientUser = bot.getUserChannelDao().getUser(recipient);
				if (adding)
					bot.getUserChannelDao().addUserToLevel(level, recipientUser, channel);
				else
					bot.getUserChannelDao().removeUserFromLevel(level, recipientUser, channel);
			}

			if (dispatchEvent)
				dispatchEvent(bot, channel, sourceHostmask, sourceUser, recipientHostmask, recipientUser, adding);
		}

		public abstract void dispatchEvent(PircBotX bot, Channel channel, UserHostmask sourceHostmask, User sourceUser, UserHostmask recipientHostmask, User recipientUser, boolean adding);
	}
}
