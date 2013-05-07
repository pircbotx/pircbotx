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

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import static org.pircbotx.ReplyConstants.*;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.dcc.DccHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.ActionEvent;
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
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.output.OutputCAP;
import org.pircbotx.output.OutputIRC;
import org.pircbotx.output.OutputRaw;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
@Slf4j
public class InputParser implements Closeable {
	protected static final Marker inputMarker = MarkerFactory.getMarker("pircbotx.input");
	/**
	 * Codes that say we are connected: Initial connection (001-4), user stats (251-5), or MOTD (375-6)
	 */
	protected static final ImmutableList<String> connectCodes = ImmutableList.of("001", "002", "003", "004", "005", 
			"251", "252", "253", "254", "255", "375", "376");
	protected final Configuration configuration;
	protected final PircBotX bot;
	protected final ListenerManager listenerManager;
	protected final UserChannelDao dao;
	protected final String channelPrefixes;
	protected final ServerInfo serverInfo;
	protected final DccHandler dccHandler;
	protected final OutputRaw sendRaw;
	protected final OutputIRC sendIRC;
	protected final OutputCAP sendCAP;
	protected BufferedReader inputReader;
	//Builders
	protected final Map<String, WhoisEvent.WhoisEventBuilder> whoisBuilder = new HashMap();
	protected StringBuilder motdBuilder;
	@Getter
	protected boolean channelListRunning = false;
	protected ImmutableSet.Builder<ChannelListEntry> channelListBuilder;
	protected int nickSuffix = 0;
	protected boolean capEndSent = false;

	protected void init(Socket socket) throws IOException {
		this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), configuration.getEncoding()));
	}

	public void startLineProcessing() {
		while (true) {
			//Get line from the server
			String line;
			try {
				line = inputReader.readLine();
			} catch (InterruptedIOException iioe) {
				// This will happen if we haven't received anything from the server for a while.
				// So we shall send it a ping to check that we are still connected.
				sendRaw.rawLine("PING " + (System.currentTimeMillis() / 1000));
				// Now we go back to listening for stuff from the server...
				continue;
			} catch (Exception e) {
				if (e instanceof SocketException && bot.isShutdownCalled()) {
					log.info("Shutdown has been called, closing InputParser");
					return;
				} else {
					//Something is wrong. Assume its bad and begin disconnect
					log.error("Exception encountered when reading next line from server", e);
					line = null;
				}
			}

			//End the loop if the line is null
			if (line == null)
				break;

			//Start acting the line
			try {
				handleLine(line);
			} catch (Exception e) {
				//Exception in client code. Just log and continue
				log.error("Exception encountered when parsing line", e);
			}

			//Do nothing if this thread is being interrupted (meaning shutdown() was run)
			if (Thread.interrupted())
				return;
		}

		//Now that the socket is definatly closed call event, log, and kill the OutputThread
		bot.shutdown();
	}

	/**
	 * This method handles events when any line of text arrives from the server,
	 * then calling the appropriate method in the PircBotX. This method is
	 * protected and only called by the InputThread for this instance.
	 *
	 * @param line The raw line of text from the server.
	 */
	public void handleLine(String line) throws IOException, IrcException {
		if (line == null)
			throw new IllegalArgumentException("Can't process null line");
		log.info(inputMarker, line);

		List<String> parsedLine = Utils.tokenizeLine(line);

		String senderInfo = "";
		if (parsedLine.get(0).charAt(0) == ':')
			senderInfo = parsedLine.remove(0);

		String command = parsedLine.remove(0).toUpperCase(configuration.getLocale());

		// Check for server pings.
		if (command.equals("PING")) {
			// Respond to the ping and return immediately.
			listenerManager.dispatchEvent(new ServerPingEvent(bot, line.substring(5)));
			return;
		} else if (command.startsWith("ERROR")) {
			//Server is shutting us down
			bot.shutdown(true);
			return;
		}

		String sourceNick;
		String sourceLogin = "";
		String sourceHostname = "";
		String target = !parsedLine.isEmpty() ? parsedLine.get(0) : "";

		if (target.startsWith(":"))
			target = target.substring(1);

		int exclamation = senderInfo.indexOf('!');
		int at = senderInfo.indexOf('@');
		if (senderInfo.startsWith(":"))
			if (exclamation > 0 && at > 0 && exclamation < at) {
				sourceNick = senderInfo.substring(1, exclamation);
				sourceLogin = senderInfo.substring(exclamation + 1, at);
				sourceHostname = senderInfo.substring(at + 1);
			} else {
				int code = Utils.tryParseInt(command, -1);
				if (code != -1) {
					if (!bot.loggedIn)
						processConnect(line, command, target, parsedLine);
					processServerResponse(code, line, parsedLine);
					// Return from the method.
					return;
				} else
					// This is not a server response.
					// It must be a nick without login and hostname.
					// (or maybe a NOTICE or suchlike from the server)
					//WARNING: Changed from origional PircBot. Instead of command as target, use channel/user (setup later)
					sourceNick = senderInfo;
			}
		else {
			// We don't know what this line means.
			listenerManager.dispatchEvent(new UnknownEvent(bot, line));
			// Return from the method;
			return;
		}

		if (sourceNick.startsWith(":"))
			sourceNick = sourceNick.substring(1);

		if (!bot.loggedIn)
			processConnect(line, command, target, parsedLine);
		processCommand(target, sourceNick, sourceLogin, sourceHostname, command, line, parsedLine);
	}

	public void processConnect(String rawLine, String code, String target, List<String> parsedLine) throws IrcException, IOException {
		if (connectCodes.contains(code)) {
			// We're connected to the server.
			bot.loggedIn(configuration.getName() + nickSuffix);
			log.debug("Logged onto server.");

			configuration.getListenerManager().dispatchEvent(new ConnectEvent(bot));

			//Handle automatic on connect stuff
			if (configuration.getNickservPassword() != null)
				sendIRC.identify(configuration.getNickservPassword());
			for (Map.Entry<String, String> channelEntry : configuration.getAutoJoinChannels().entrySet())
				sendIRC.joinChannel(channelEntry.getKey(), channelEntry.getValue());
		} else if (code.equals("433"))
			//EXAMPLE: AnAlreadyUsedName :Nickname already in use
			//Nickname in use, rename
			if (configuration.isAutoNickChange()) {
				nickSuffix++;
				sendIRC.changeNick(configuration.getName() + nickSuffix);
			} else
				throw new IrcException(IrcException.Reason.NickAlreadyInUse, "Line: " + rawLine);
		else if (code.equals("439")) {
			//EXAMPLE: PircBotX: Target change too fast. Please wait 104 seconds
			// No action required.
		} else if (configuration.isCapEnabled() && code.equals("451") && target.equals("CAP")) {
			//EXAMPLE: 451 CAP :You have not registered
			//Ignore, this is from servers that don't support CAP
		} else if (code.startsWith("5") || code.startsWith("4"))
			throw new IrcException(IrcException.Reason.CannotLogin, "Received error: " + rawLine);
		else if (code.equals("670")) {
			//Server is saying that we can upgrade to TLS
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
				curCapHandler.handleUnknown(bot, rawLine);
		} else if (code.equals("CAP")) {
			//Handle CAP Code; remove extra from params
			String capCommand = parsedLine.get(1);
			ImmutableList<String> capParams = ImmutableList.copyOf(StringUtils.split(parsedLine.get(2)));
			if (capCommand.equals("LS"))
				for (CapHandler curCapHandler : configuration.getCapHandlers())
					curCapHandler.handleLS(bot, capParams);
			else if (capCommand.equals("ACK")) {
				//Server is enabling a capability, store that
				bot.getEnabledCapabilities().addAll(capParams);

				for (CapHandler curCapHandler : configuration.getCapHandlers())
					curCapHandler.handleACK(bot, capParams);
			} else if (capCommand.equals("NAK"))
				for (CapHandler curCapHandler : configuration.getCapHandlers())
					curCapHandler.handleNAK(bot, capParams);
			else
				//Maybe the CapHandlers know how to use it
				for (CapHandler curCapHandler : configuration.getCapHandlers())
					curCapHandler.handleUnknown(bot, rawLine);
		} else
			//Pass to CapHandlers, could be important
			for (CapHandler curCapHandler : configuration.getCapHandlers())
				curCapHandler.handleUnknown(bot, rawLine);


		//Send CAP END if all CapHandlers are finished
		if (configuration.isCapEnabled() && !capEndSent) {
			boolean allDone = true;
			for (CapHandler curCapHandler : configuration.getCapHandlers())
				if (!curCapHandler.isDone()) {
					allDone = false;
					break;
				}
			if (allDone) {
				sendCAP.end();
				capEndSent = true;

				//Make capabilities unmodifiable for the future
				bot.enabledCapabilities = Collections.unmodifiableList(bot.enabledCapabilities);
			}
		}
	}

	public void processCommand(String target, String sourceNick, String sourceLogin, String sourceHostname, String command, String line, List<String> parsedLine) throws IOException {
		User source = dao.getUser(sourceNick);
		//If the channel matches a prefix, then its a channel
		Channel channel = (target.length() != 0 && channelPrefixes.indexOf(target.charAt(0)) >= 0) ? dao.getChannel(target) : null;
		String message = parsedLine.size() >= 2 ? parsedLine.get(1) : "";

		// Check for CTCP requests.
		if (command.equals("PRIVMSG") && message.startsWith("\u0001") && message.endsWith("\u0001")) {
			String request = message.substring(1, message.length() - 1);
			if (request.equals("VERSION"))
				// VERSION request
				listenerManager.dispatchEvent(new VersionEvent(bot, source, channel));
			else if (request.startsWith("ACTION "))
				// ACTION request
				listenerManager.dispatchEvent(new ActionEvent(bot, source, channel, request.substring(7)));
			else if (request.startsWith("PING "))
				// PING request
				listenerManager.dispatchEvent(new PingEvent(bot, source, channel, request.substring(5)));
			else if (request.equals("TIME"))
				// TIME request
				listenerManager.dispatchEvent(new TimeEvent(bot, source, channel));
			else if (request.equals("FINGER"))
				// FINGER request
				listenerManager.dispatchEvent(new FingerEvent(bot, source, channel));
			else if (request.startsWith("DCC ")) {
				// This is a DCC request.
				boolean success = dccHandler.processDcc(source, request);
				if (!success)
					// The DccManager didn't know what to do with the line.
					listenerManager.dispatchEvent(new UnknownEvent(bot, line));
			} else
				// An unknown CTCP message - ignore it.
				listenerManager.dispatchEvent(new UnknownEvent(bot, line));
		} else if (command.equals("PRIVMSG") && channel != null)
			// This is a normal message to a channel.
			listenerManager.dispatchEvent(new MessageEvent(bot, channel, source, message));
		else if (command.equals("PRIVMSG")) {
			// This is a private message to us.
			//Add to private message
			dao.addUserToPrivate(source);
			listenerManager.dispatchEvent(new PrivateMessageEvent(bot, source, message));
		} else if (command.equals("JOIN")) {
			// Someone is joining a channel.
			if (sourceNick.equalsIgnoreCase(bot.getNick())) {
				//Its us, get channel info
				sendRaw.rawLine("WHO " + target);
				sendRaw.rawLine("MODE " + target);
			}
			source.setLogin(sourceLogin);
			source.setHostmask(sourceHostname);
			dao.addUserToChannel(source, channel);
			listenerManager.dispatchEvent(new JoinEvent(bot, channel, source));
		} else if (command.equals("PART")) {
			// Someone is parting from a channel.
			if (sourceNick.equals(bot.getNick()))
				//We parted the channel
				dao.removeChannel(channel);
			else
				//Just remove the user from memory
				dao.removeUserFromChannel(source, channel);
			listenerManager.dispatchEvent(new PartEvent(bot, channel, source, message));
		} else if (command.equals("NICK")) {
			// Somebody is changing their nick.
			String newNick = target;
			source.setNick(newNick);
			if (sourceNick.equals(bot.getNick()))
				// Update our nick if it was us that changed nick.
				bot.setNick(newNick);
			listenerManager.dispatchEvent(new NickChangeEvent(bot, sourceNick, newNick, source));
		} else if (command.equals("NOTICE"))
			// Someone is sending a notice.
			listenerManager.dispatchEvent(new NoticeEvent(bot, source, channel, message));
		else if (command.equals("QUIT")) {
			UserSnapshot snapshot = source.generateSnapshot();
			//A real target is missing, so index is off
			String reason = target;
			// Someone has quit from the IRC server.
			if (!sourceNick.equals(bot.getNick()))
				//Someone else
				dao.removeUser(source);
			listenerManager.dispatchEvent(new QuitEvent(bot, snapshot, reason));
		} else if (command.equals("KICK")) {
			// Somebody has been kicked from a channel.
			User recipient = dao.getUser(message);

			if (recipient.getNick().equals(bot.getNick()))
				//We were just kicked
				dao.removeChannel(channel);
			else
				//Someone else
				dao.removeUserFromChannel(recipient, channel);
			listenerManager.dispatchEvent(new KickEvent(bot, channel, source, recipient, parsedLine.get(2)));
		} else if (command.equals("MODE")) {
			// Somebody is changing the mode on a channel or user (Use long form since mode isn't after a : )
			String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
			if (mode.startsWith(":"))
				mode = mode.substring(1);
			processMode(source, target, mode);
		} else if (command.equals("TOPIC")) {
			// Someone is changing the topic.
			long currentTime = System.currentTimeMillis();
			String oldTopic = channel.getTopic();
			channel.setTopic(message);
			channel.setTopicSetter(sourceNick);
			channel.setTopicTimestamp(currentTime);

			listenerManager.dispatchEvent(new TopicEvent(bot, channel, oldTopic, message, source, currentTime, true));
		} else if (command.equals("INVITE")) {
			// Somebody is inviting somebody else into a channel.
			//Use line method instead of channel since channel is wrong
			listenerManager.dispatchEvent(new InviteEvent(bot, sourceNick, message));
			if (dao.getChannels(source).isEmpty())
				dao.removeUser(source);
		} else
			// If we reach this point, then we've found something that the PircBotX
			// Doesn't currently deal with.
			listenerManager.dispatchEvent(new UnknownEvent(bot, line));
	}

	/**
	 * This method is called by the PircBotX when a numeric response
	 * is received from the IRC server. We use this method to
	 * allow PircBotX to process various responses from the server
	 * before then passing them on to the onServerResponse method.
	 * <p>
	 * Note that this method is private and should not appear in any
	 * of the javadoc generated documentation.
	 *
	 * @param code The three-digit numerical code for the response.
	 * @param response The full response from the IRC server.
	 */
	public void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
		ImmutableList<String> parsedResponse = ImmutableList.copyOf(parsedResponseOrig);
		if (parsedResponse == null)
			throw new IllegalArgumentException("Can't process null response");
		//Parsed response format: Everything after code
		//eg: Response 321 Channel :Users Name gives us [Channel, Users Name]
		if (code == RPL_LISTSTART) {
			//EXAMPLE: 321 Channel :Users Name (actual text)
			//A channel list is about to be sent
			channelListBuilder = ImmutableSet.builder();
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
			listenerManager.dispatchEvent(new ChannelInfoEvent(bot, channelListBuilder.build()));
			channelListBuilder = null;
			channelListRunning = false;
		} else if (code == RPL_TOPIC) {
			//EXAMPLE: 332 PircBotX #aChannel :I'm some random topic
			//This is topic about a channel we've just joined. From /JOIN or /TOPIC
			Channel channel = dao.getChannel(parsedResponse.get(1));
			String topic = parsedResponse.get(2);

			channel.setTopic(topic);
		} else if (code == RPL_TOPICINFO) {
			//EXAMPLE: 333 PircBotX #aChannel ISetTopic 1564842512
			//This is information on the topic of the channel we've just joined. From /JOIN or /TOPIC
			Channel channel = dao.getChannel(parsedResponse.get(1));
			User setBy = dao.getUser(parsedResponse.get(2));
			long date = Utils.tryParseLong(parsedResponse.get(3), -1);

			channel.setTopicTimestamp(date * 1000);
			channel.setTopicSetter(setBy.getNick());

			listenerManager.dispatchEvent(new TopicEvent(bot, channel, null, channel.getTopic(), setBy, date, false));
		} else if (code == RPL_WHOREPLY) {
			//EXAMPLE: 352 PircBotX #aChannel ~someName 74.56.56.56.my.Hostmask wolfe.freenode.net someNick H :0 Full Name
			//Part of a WHO reply on information on individual users
			Channel channel = dao.getChannel(parsedResponse.get(1));

			//Setup user
			User curUser = dao.getUser(parsedResponse.get(5));
			curUser.setLogin(parsedResponse.get(2));
			curUser.setHostmask(parsedResponse.get(3));
			curUser.setServer(parsedResponse.get(4));
			curUser.setNick(parsedResponse.get(5));
			processUserStatus(channel, curUser, parsedResponse.get(6));
			//Extra parsing needed since tokenizer stopped at :
			String rawEnding = parsedResponse.get(7);
			int rawEndingSpaceIndex = rawEnding.indexOf(' ');
			curUser.setHops(Integer.parseInt(rawEnding.substring(0, rawEndingSpaceIndex)));
			curUser.setRealName(rawEnding.substring(rawEndingSpaceIndex + 1));

			//Associate with channel
			dao.addUserToChannel(curUser, channel);
		} else if (code == RPL_ENDOFWHO) {
			//EXAMPLE: 315 PircBotX #aChannel :End of /WHO list
			//End of the WHO reply
			Channel channel = dao.getChannel(parsedResponse.get(1));
			listenerManager.dispatchEvent(new UserListEvent(bot, channel, dao.getUsers(channel)));
		} else if (code == RPL_CHANNELMODEIS) {
			//EXAMPLE: 324 PircBotX #aChannel +cnt
			//Full channel mode (In response to MODE <channel>)
			Channel channel = dao.getChannel(parsedResponse.get(1));
			String mode = parsedResponse.get(2);

			channel.setMode(mode);
			listenerManager.dispatchEvent(new ModeEvent(bot, channel, null, mode));
		} else if (code == 329) {
			//EXAMPLE: 329 lordquackstar #botters 1199140245
			//Tells when channel was created. From /JOIN
			Channel channel = dao.getChannel(parsedResponse.get(1));
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
			serverInfo.setMotd(motdBuilder.toString().trim());
			motdBuilder = null;
			listenerManager.dispatchEvent(new MotdEvent(bot, (serverInfo.getMotd())));
		} else if (code == 004 || code == 005) {
			//Example: 004 PircBotX sendak.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
			//Server info line, remove ending comment and let ServerInfo class parse it
			int endCommentIndex = rawResponse.lastIndexOf(" :");
			if (endCommentIndex > 1) {
				String endComment = rawResponse.substring(endCommentIndex + 2);
				int lastIndex = parsedResponseOrig.size() - 1;
				if (endComment.equals(parsedResponseOrig.get(lastIndex)))
					parsedResponseOrig.remove(lastIndex);
			}
			serverInfo.parse(code, parsedResponseOrig);
		} else if (code == RPL_WHOISUSER) {
			//Example: 311 TheLQ Plazma ~Plazma freenode/staff/plazma * :Plazma Rooolz!
			//New whois is starting
			String whoisNick = parsedResponse.get(1);

			WhoisEvent.WhoisEventBuilder builder = new WhoisEvent.WhoisEventBuilder();
			builder.setNick(whoisNick);
			builder.setLogin(parsedResponse.get(2));
			builder.setHostname(parsedResponse.get(3));
			builder.setRealname(parsedResponse.get(5));
			whoisBuilder.put(whoisNick, builder);
		} else if (code == RPL_WHOISCHANNELS) {
			//Example: 319 TheLQ Plazma :+#freenode
			//Channel list from whois. Re-tokenize since they're after the :
			String whoisNick = parsedResponse.get(1);
			ImmutableList<String> parsedChannels = ImmutableList.copyOf(Utils.tokenizeLine(parsedResponse.get(2)));

			whoisBuilder.get(whoisNick).setChannels(parsedChannels);
		} else if (code == RPL_WHOISSERVER) {
			//Server info from whois
			//312 TheLQ Plazma leguin.freenode.net :Ume?, SE, EU
			String whoisNick = parsedResponse.get(1);

			whoisBuilder.get(whoisNick).setServer(parsedResponse.get(2));
			whoisBuilder.get(whoisNick).setServerInfo(parsedResponse.get(3));
		} else if (code == RPL_WHOISIDLE) {
			//Idle time from whois
			//317 TheLQ md_5 6077 1347373349 :seconds idle, signon time
			String whoisNick = parsedResponse.get(1);

			whoisBuilder.get(whoisNick).setIdleSeconds(Long.parseLong(parsedResponse.get(2)));
			whoisBuilder.get(whoisNick).setSignOnTime(Long.parseLong(parsedResponse.get(3)));
		} else if (code == 330)
			//RPL_WHOISACCOUNT: Extra Whois info
			//330 TheLQ Utoxin Utoxin :is logged in as
			whoisBuilder.get(parsedResponse.get(1)).setRegisteredAs(parsedResponse.get(2));
		else if (code == RPL_ENDOFWHOIS) {
			//End of whois
			//318 TheLQ Plazma :End of /WHOIS list.
			String whoisNick = parsedResponse.get(1);

			listenerManager.dispatchEvent(whoisBuilder.get(whoisNick).generateEvent(bot));
			whoisBuilder.remove(whoisNick);
		}
		listenerManager.dispatchEvent(new ServerResponseEvent(bot, code, rawResponse, parsedResponse));
	}

	/**
	 * Called when the mode of a channel is set. We process this in
	 * order to call the appropriate onOp, onDeop, etc method before
	 * finally calling the override-able onMode method.
	 * <p>
	 * Note that this method is private and is not intended to appear
	 * in the javadoc generated documentation.
	 *
	 * @param target The channel or nick that the mode operation applies to.
	 * @param sourceNick The nick of the user that set the mode.
	 * @param sourceLogin The login of the user that set the mode.
	 * @param sourceHostname The hostname of the user that set the mode.
	 * @param mode The mode that has been set.
	 */
	public void processMode(User user, String target, String mode) {
		if (channelPrefixes.indexOf(target.charAt(0)) >= 0) {
			// The mode of a channel is being changed.
			Channel channel = dao.getChannel(target);
			channel.parseMode(mode);
			StringTokenizer tok = new StringTokenizer(mode);
			String[] params = new String[tok.countTokens()];

			int t = 0;
			while (tok.hasMoreTokens()) {
				params[t] = tok.nextToken();
				t++;
			}

			char pn = ' ';
			int p = 1;

			// All of this is very large and ugly, but it's the only way of providing
			// what the users want :-/
			for (int i = 0; i < params[0].length(); i++) {
				char atPos = params[0].charAt(i);

				if (atPos == '+' || atPos == '-')
					pn = atPos;
				else if (atPos == 'o') {
					User recipient = dao.getUser(params[p]);
					if (pn == '+') {
						dao.addUserToOps(recipient, channel);
						listenerManager.dispatchEvent(new OpEvent(bot, channel, user, recipient, true));
					} else {
						dao.removeUserFromOps(recipient, channel);
						listenerManager.dispatchEvent(new OpEvent(bot, channel, user, recipient, false));
					}
					p++;
				} else if (atPos == 'v') {
					User recipient = dao.getUser(params[p]);
					if (pn == '+') {
						dao.addUserToVoices(recipient, channel);
						listenerManager.dispatchEvent(new VoiceEvent(bot, channel, user, recipient, true));
					} else {
						dao.removeUserFromVoices(recipient, channel);
						listenerManager.dispatchEvent(new VoiceEvent(bot, channel, user, recipient, false));
					}
					p++;
				} else if (atPos == 'h') {
					//Half-op change
					User recipient = dao.getUser(params[p]);
					if (pn == '+') {
						dao.addUserToHalfOps(recipient, channel);
						listenerManager.dispatchEvent(new HalfOpEvent(bot, channel, user, recipient, true));
					} else {
						dao.removeUserFromHalfOps(recipient, channel);
						listenerManager.dispatchEvent(new HalfOpEvent(bot, channel, user, recipient, false));
					}
					p++;
				} else if (atPos == 'a') {
					//SuperOp change
					User recipient = dao.getUser(params[p]);
					if (pn == '+') {
						dao.addUserToSuperOps(recipient, channel);
						listenerManager.dispatchEvent(new SuperOpEvent(bot, channel, user, recipient, true));
					} else {
						dao.removeUserFromSuperOps(recipient, channel);
						listenerManager.dispatchEvent(new SuperOpEvent(bot, channel, user, recipient, false));
					}
					p++;
				} else if (atPos == 'q') {
					//Owner change
					User recipient = dao.getUser(params[p]);
					if (pn == '+') {
						dao.addUserToOwners(recipient, channel);
						listenerManager.dispatchEvent(new OwnerEvent(bot, channel, user, recipient, true));
					} else {
						dao.removeUserFromOwners(recipient, channel);
						listenerManager.dispatchEvent(new OwnerEvent(bot, channel, user, recipient, false));
					}
					p++;
				} else if (atPos == 'k') {
					if (pn == '+')
						listenerManager.dispatchEvent(new SetChannelKeyEvent(bot, channel, user, params[p]));
					else
						listenerManager.dispatchEvent(new RemoveChannelKeyEvent(bot, channel, user, (p < params.length) ? params[p] : null));
					p++;
				} else if (atPos == 'l')
					if (pn == '+') {
						listenerManager.dispatchEvent(new SetChannelLimitEvent(bot, channel, user, Integer.parseInt(params[p])));
						p++;
					} else
						listenerManager.dispatchEvent(new RemoveChannelLimitEvent(bot, channel, user));
				else if (atPos == 'b') {
					if (pn == '+')
						listenerManager.dispatchEvent(new SetChannelBanEvent(bot, channel, user, params[p]));
					else
						listenerManager.dispatchEvent(new RemoveChannelBanEvent(bot, channel, user, params[p]));
					p++;
				} else if (atPos == 't')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetTopicProtectionEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemoveTopicProtectionEvent(bot, channel, user));
				else if (atPos == 'n')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetNoExternalMessagesEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemoveNoExternalMessagesEvent(bot, channel, user));
				else if (atPos == 'i')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetInviteOnlyEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemoveInviteOnlyEvent(bot, channel, user));
				else if (atPos == 'm')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetModeratedEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemoveModeratedEvent(bot, channel, user));
				else if (atPos == 'p')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetPrivateEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemovePrivateEvent(bot, channel, user));
				else if (atPos == 's')
					if (pn == '+')
						listenerManager.dispatchEvent(new SetSecretEvent(bot, channel, user));
					else
						listenerManager.dispatchEvent(new RemoveSecretEvent(bot, channel, user));
			}
			listenerManager.dispatchEvent(new ModeEvent(bot, channel, user, mode));
		} else
			// The mode of a user is being changed.
			listenerManager.dispatchEvent(new UserModeEvent(bot, dao.getUser(target), user, mode));
	}

	public void processUserStatus(Channel chan, User user, String prefix) {
		//TODO: Move into InputThread
		if (prefix.contains("@"))
			dao.addUserToOps(user, chan);
		if (prefix.contains("+"))
			dao.addUserToVoices(user, chan);
		if (prefix.contains("%"))
			dao.addUserToHalfOps(user, chan);
		if (prefix.contains("~"))
			dao.addUserToOwners(user, chan);
		if (prefix.contains("&"))
			dao.addUserToSuperOps(user, chan);
		user.setAway(prefix.contains("G")); //Assume here (H) if there is no G
		user.setIrcop(prefix.contains("*"));
	}

	public void close() {
		whoisBuilder.clear();
		motdBuilder = null;
		channelListRunning = false;
		channelListBuilder = null;
	}
}
