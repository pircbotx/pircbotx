/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import static org.pircbotx.ReplyConstants.*;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.CoreHooks;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

/**
 * PircBotX is a Java framework for writing IRC bots quickly and easily.
 * <p>
 * It provides an event-driven architecture to handle common IRC
 * events, flood protection, DCC support, ident support, and more.
 * The comprehensive logfile format is suitable for use with pisg to generate
 * channel statistics.
 * <p>
 * Methods of the PircBotX class can be called to send events to the IRC server
 * that it connects to. For example, calling the sendMessage method will
 * send a message to a channel or user on the IRC server. Multiple servers
 * can be supported using multiple instances of PircBotX.
 * <p>
 * To perform an action when the PircBotX receives a normal message from the IRC
 * server, you would listen for the MessageEvent in your listener (see {@link ListenerAdapter}).
 * Many other events are dispatched as well for other incoming lines
 *
 * @author Origionally by:
 * <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 * <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 * Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotX {
	/**
	 * The definitive version number of this release of PircBotX.
	 * (Note: Change this before automatically building releases)
	 */
	public static final String VERSION = "1.7";
	protected static AtomicInteger botCount = new AtomicInteger();
	protected Socket socket;
	// Connection stuff.
	protected InputThread inputThread = null;
	protected OutputThread outputThread = null;
	protected Charset charset = Charset.defaultCharset();
	@Setter
	protected InetAddress inetAddress = null;
	// Details about the last server that we connected to.
	protected String server = null;
	protected int port = -1;
	protected String password = null;
	protected long messageDelay = 1000;
	/*
	 * A Many to Many map that links Users to Channels and channels to users. Modified
	 * to remove each channel's and user's internal refrences to each other.
	 */
	protected ManyToManyMap<Channel, User> userChanInfo = new UserChannelMap();
	/**
	 * Map to provide extremely fast lookup of user object by nick
	 */
	protected final Map<String, User> userNickMap = Collections.synchronizedMap(new HashMap());
	// DccManager to process and handle all DCC events.
	@Getter
	protected DccManager dccManager = new DccManager(this);
	@Setter(AccessLevel.PROTECTED)
	protected List<Integer> dccPorts = new ArrayList();
	protected InetAddress dccInetAddress = null;
	// Default settings for the PircBotX.
	protected boolean autoNickChange = false;
	protected boolean verbose = false;
	protected String name = "PircBotX";
	protected String nick = name;
	protected String login = "PircBotX";
	protected String version = "PircBotX " + VERSION + ", a fork of PircBot, the Java IRC bot - pircbotx.googlecode.com";
	protected String finger = "You ought to be arrested for fingering a bot!";
	protected String channelPrefixes = "#&+!";
	/**
	 * The logging lock object preventing lines from being printed as other
	 * lines are being printed
	 */
	protected final Object logLock = new Object();
	protected ListenerManager<? extends PircBotX> listenerManager = new ThreadedListenerManager();
	/**
	 * The number of milliseconds to wait before the socket times out on read
	 * operations. This does not mean the socket is invalid. By default its 5
	 * minutes
	 */
	protected int socketTimeout = 1000 * 60 * 5;
	protected final ServerInfo serverInfo = new ServerInfo(this);
	protected final ListBuilder<ChannelListEntry> channelListBuilder = new ListBuilder();
	protected SocketFactory socketFactory = null;
	protected boolean loggedIn = false;
	@Setter
	@Getter
	protected String webIrcHostname = null;
	@Setter
	@Getter
	protected InetAddress webIrcAddress = null;
	@Setter
	@Getter
	protected String webIrcPassword = null;
	@Setter
	@Getter
	protected boolean autoSplitMessage = true;
	protected Thread shutdownHook;
	@Getter
	@Setter
	protected boolean autoReconnect;
	@Getter
	@Setter
	protected boolean autoReconnectChannels;
	protected Map<String, WhoisEvent.WhoisEventBuilder> whoisBuilder = new HashMap();

	/**
	 * Constructs a PircBotX with the default settings and adding {@link CoreHooks}
	 * to the default ListenerManager, {@link ThreadedListenerManager}. This also
	 * adds a shutdown hook to the current runtime while will properly shutdown
	 * the bot by calling {@link #disconnect() } 
	 */
	public PircBotX() {
		botCount.getAndIncrement();
		listenerManager.addListener(new CoreHooks());
		useShutdownHook(true);
	}

	/**
	 * Attempt to connect to the specified IRC server.
	 * The onConnect method is called upon success.
	 *
	 * @param hostname The hostname of the server to connect to.
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void connect(String hostname) throws IOException, IrcException, NickAlreadyInUseException {
		this.connect(hostname, 6667, null, null);
	}

	/**
	 * Attempt to connect to the specified IRC server and port number.
	 * The onConnect method is called upon success.
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void connect(String hostname, int port) throws IOException, IrcException, NickAlreadyInUseException {
		this.connect(hostname, port, null, null);
	}

	/**
	 * Attempt to connect to the specified IRC server using the given port
	 * and password.
	 * The onConnect method is called upon success.
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param password The password to use to join the server.
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void connect(String hostname, int port, String password) throws IOException, IrcException, NickAlreadyInUseException {
		this.connect(hostname, port, password, null);
	}

	/**
	 * Attempt to connect to the specified IRC server using the given port number
	 * and socketFactory.
	 * The onConnect method is called upon success.
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param socketFactory The factory to use for creating sockets, including secure sockets
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void connect(String hostname, int port, SocketFactory socketFactory) throws IOException, IrcException, NickAlreadyInUseException {
		this.connect(hostname, port, null, socketFactory);
	}

	/**
	 * Attempt to connect to the specified IRC server using the supplied
	 * port number, password, and socketFactory. On success a {@link ConnectEvent}
	 * will be dispatched
	 * <p>
	 * Note that a WebIrc line will be sent ONLY if the {@link #setWebIrcPassword(java.lang.String)
	 * WebIrc password is set}
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port The port number to connect to on the server.
	 * @param password The password to use to join the server.
	 * @param socketFactory The factory to use for creating sockets, including secure sockets
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void connect(String hostname, int port, String password, SocketFactory socketFactory) throws IOException, IrcException, NickAlreadyInUseException {
		server = hostname;
		this.port = port;
		this.password = password;
		this.socketFactory = socketFactory;

		if (isConnected())
			throw new IrcException("The PircBotX is already connected to an IRC server.  Disconnect first.");

		// Clear everything we may have know about channels.
		userChanInfo.clear();

		// Connect to the server by DNS server
		Throwable lastException = null;
		for (InetAddress curAddress : InetAddress.getAllByName(hostname)) {
			System.out.println("Trying address " + curAddress);
			try {
				//Create socket from appropiate place
				if (socketFactory == null)
					socket = new Socket(hostname, port, inetAddress, 0);
				else
					socket = socketFactory.createSocket(hostname, port, inetAddress, 0);
				
				//No exception, assume successful
				break;
			} catch (Throwable t) {
				this.log("*** Unable to connect to " + hostname + " using the IP address " + curAddress.getHostAddress() + ", trying to check another address.");
				lastException = t;
			}
		}
		
		//Make sure were connected
		if (socket == null || (socket != null && !socket.isConnected())) {
			this.log("*** Unable to connect to any working IP address for hostname " + hostname);
			throw new IOException("Unable to connect to the IRC network " + hostname, lastException);
		}
		this.log("*** Connected to server.");

		inetAddress = socket.getLocalAddress();

		InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), getEncoding());
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), getEncoding());

		BufferedReader breader = new BufferedReader(inputStreamReader);
		BufferedWriter bwriter = new BufferedWriter(outputStreamWriter);

		//Construct the output and input threads
		inputThread = createInputThread(socket, breader);
		outputThread = createOutputThread(bwriter);
		outputThread.start();

		// Attempt to join the server.
		if (webIrcPassword != null)
			outputThread.sendRawLineNow("WEBIRC " + webIrcPassword + " cgiirc " + webIrcHostname + " " + webIrcAddress.getHostAddress());
		if (password != null && !password.trim().equals(""))
			outputThread.sendRawLineNow("PASS " + password);
		String tempNick = getName();
		outputThread.sendRawLineNow("NICK " + tempNick);
		outputThread.sendRawLineNow("USER " + getLogin() + " 8 * :" + getVersion());

		// Read stuff back from the server to see if we connected.
		String line;
		int tries = 1;
		while ((line = breader.readLine()) != null) {
			handleLine(line);

			int firstSpace = line.indexOf(" ");
			int secondSpace = line.indexOf(" ", firstSpace + 1);
			if (secondSpace >= 0) {
				String code = line.substring(firstSpace + 1, secondSpace);

				//Check for both a successful connection. Inital connection (001-4), user stats (251-5), or MOTD (375-6)
				String[] codes = {"001", "002", "003", "004", "005", "251", "252", "253", "254", "255", "375", "376"};
				if (Arrays.asList(codes).contains(code))
					// We're connected to the server.
					break;
				else if (code.equals("433"))
					//EXAMPLE: AnAlreadyUsedName :Nickname already in use
					//Nickname in use, rename
					if (autoNickChange) {
						tries++;
						tempNick = getName() + tries;
						outputThread.sendRawLineNow("NICK " + tempNick);
					} else {
						socket.close();
						inputThread = null;
						throw new NickAlreadyInUseException(line);
					}
				else if (code.equals("439")) {
					//EXAMPLE: PircBotX: Target change too fast. Please wait 104 seconds
					// No action required.
				} else if (code.startsWith("5") || code.startsWith("4")) {
					socket.close();
					inputThread = null;
					throw new IrcException("Could not log into the IRC server: " + line);
				}
			}
			this.nick = tempNick;
		}

		loggedIn = true;
		log("*** Logged onto server.");

		// This makes the socket timeout on read operations after 5 minutes.
		socket.setSoTimeout(getSocketTimeout());

		//Start input to start accepting lines
		inputThread.start();

		getListenerManager().dispatchEvent(new ConnectEvent(this));
	}

	protected InputThread createInputThread(Socket socket, BufferedReader breader) {
		InputThread input = new InputThread(this, socket, breader);
		input.setName("bot" + botCount + "-input");
		return input;
	}

	protected OutputThread createOutputThread(BufferedWriter bwriter) {
		OutputThread output = new OutputThread(this, bwriter);
		output.setName("bot" + botCount + "-output");
		return output;
	}

	/**
	 * Reconnects to the IRC server that we were previously connected to.
	 * If necessary, the appropriate port number and password will be used.
	 * This method will throw an IrcException if we have never connected
	 * to an IRC server previously.
	 *
	 * @since PircBot 0.9.9
	 *
	 * @throws IOException if it was not possible to connect to the server.
	 * @throws IrcException if the server would not let us join it.
	 * @throws NickAlreadyInUseException if our nick is already in use on the server.
	 */
	public synchronized void reconnect() throws IOException, IrcException, NickAlreadyInUseException {
		if (getServer() == null)
			throw new IrcException("Cannot reconnect to an IRC server because we were never connected to one previously!");
		try {
			connect(getServer(), getPort(), getPassword(), getSocketFactory());
		} catch (IOException e) {
			getListenerManager().dispatchEvent(new ReconnectEvent(this, false, e));
			throw e;
		} catch (IrcException e) {
			getListenerManager().dispatchEvent(new ReconnectEvent(this, false, e));
			throw e;
		}
		//Should be good
		getListenerManager().dispatchEvent(new ReconnectEvent(this, true, null));
	}

	/**
	 * This method disconnects from the server cleanly by calling the
	 * quitServer() method. Providing the PircBotX was connected to an
	 * IRC server, DisconnectEvent will be dispatched as soon as the
	 * disconnection is made by the server.
	 *
	 * @see #quitServer() quitServer
	 * @see #quitServer(String) quitServer
	 */
	public synchronized void disconnect() {
		quitServer();
	}

	/**
	 * When you connect to a server and your nick is already in use and
	 * this is set to true, a new nick will be automatically chosen.
	 * This is done by adding numbers to the end of the nick until an
	 * available nick is found.
	 *
	 * @param autoNickChange Set to true if you want automatic nick changes
	 * during connection.
	 */
	public void setAutoNickChange(boolean autoNickChange) {
		this.autoNickChange = autoNickChange;
	}

	/**
	 * Starts an ident server (Identification Protocol Server, RFC 1413).
	 * <p>
	 * Most IRC servers attempt to contact the ident server on connecting
	 * hosts in order to determine the user's identity. A few IRC servers
	 * will not allow you to connect unless this information is provided.
	 * <p>
	 * So when a PircBotX is run on a machine that does not run an ident server,
	 * it may be necessary to call this method to start one up.
	 * <p>
	 * Calling this method starts up an ident server which will respond with
	 * the login provided by calling getLogin() and then shut down immediately.
	 * It will also be shut down if it has not been contacted within 60 seconds
	 * of creation.
	 * <p>
	 * If you require an ident response, then the correct procedure is to start
	 * the ident server and then connect to the IRC server. The IRC server may
	 * then contact the ident server to get the information it needs.
	 * <p>
	 * The ident server will fail to start if there is already an ident server
	 * running on port 113, or if you are running as an unprivileged user who
	 * is unable to create a server socket on that port number.
	 * <p>
	 * If it is essential for you to use an ident server when connecting to an
	 * IRC server, then make sure that port 113 on your machine is visible to
	 * the IRC server so that it may contact the ident server.
	 *
	 * @since PircBot 0.9c
	 */
	public IdentServer startIdentServer() {
		return new IdentServer(this, getLogin());
	}

	/**
	 * Joins a channel.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 */
	public void joinChannel(String channel) {
		if (channel == null)
			throw new IllegalArgumentException("Can't join a null channel");
		sendRawLine("JOIN " + channel);
	}

	/**
	 * Joins a channel with a key.
	 *
	 * @param channel The name of the channel to join (eg "#cs").
	 * @param key The key that will be used to join the channel.
	 */
	public void joinChannel(String channel, String key) {
		if (channel == null)
			throw new IllegalArgumentException("Can't join a null channel");
		if (key == null)
			throw new IllegalArgumentException("Can't channel " + channel + " with null key");
		joinChannel(channel + " " + key);
	}

	/**
	 * Parts a channel.
	 *
	 * @param channel The name of the channel to leave.
	 */
	public void partChannel(Channel channel) {
		if (channel == null)
			throw new IllegalArgumentException("Can't part a null channel");
		sendRawLine("PART " + channel.getName());
	}

	/**
	 * Parts a channel, giving a reason.
	 *
	 * @param channel The name of the channel to leave.
	 * @param reason The reason for parting the channel.
	 */
	public void partChannel(Channel channel, String reason) {
		if (channel == null)
			throw new IllegalArgumentException("Can't part a null channel");
		sendRawLine("PART " + channel.getName() + " :" + reason);
	}

	/**
	 * Part and rejoin specified channel. Useful for obtaining auto privileges
	 * after identifying
	 * @param chan The channel to part and join from. Note that the object will
	 * be invalid after this method executes and a new one will be created
	 */
	public void cycle(final Channel chan) {
		partChannel(chan);
		//As we might not immediatly part and you can't join a channel that your
		//already joined to, wait for the PART event before rejoining
		listenerManager.addListener(new ListenerAdapter() {
			@Override
			public void onPart(PartEvent event) throws Exception {
				//Make sure this bot is us to prevent nasty errors in multi bot sitations
				if (event.getBot() == PircBotX.this)
					event.getBot().joinChannel(chan.getName());
				//Self destrust, this listener has no more porpose
				event.getBot().getListenerManager().removeListener(this);
			}
		});
	}

	/**
	 * Part and rejoin specified channel using channel key. Useful for obtaining
	 * auto privileges after identifying
	 * @param chan The channel to part and join from. Note that the object will
	 * be invalid after this method executes and a new one will be created
	 * @param key The key to use when rejoining the channel
	 */
	public void cycle(final Channel chan, final String key) {
		partChannel(chan);
		//As we might not immediatly part and you can't join a channel that your
		//already joined to, wait for the PART event before rejoining
		listenerManager.addListener(new ListenerAdapter() {
			@Override
			public void onPart(PartEvent event) throws Exception {
				//Make sure this bot is us to prevent nasty errors in multi bot sitations
				if (event.getBot() == PircBotX.this) {
					event.getBot().joinChannel(chan.getName(), key);
					//Self destrust, this listener has no more porpose
					event.getBot().getListenerManager().removeListener(this);
				}
			}
		});
	}

	/**
	 * Quits from the IRC server.
	 * Providing we are actually connected to an IRC server, a {@link DisconnectEvent}
	 * will be dispatched as soon as the IRC server disconnects us.
	 */
	public void quitServer() {
		if (!isConnected())
			throw new RuntimeException("Can't quit from server because we are already disconnected!");
		quitServer("");
	}

	/**
	 * Quits from the IRC server with a reason.
	 * Providing we are actually connected to an IRC server, a {@link DisconnectEvent}
	 * will be dispatched as soon as the IRC server disconnects us.
	 *
	 * @param reason The reason for quitting the server.
	 */
	public void quitServer(String reason) {
		sendRawLine("QUIT :" + reason);
	}

	/**
	 * Sends a raw line through the outgoing message queue.
	 *
	 * @param line The raw line to send to the IRC server.
	 */
	public void sendRawLine(String line) {
		if (line == null)
			throw new NullPointerException("Cannot send null messages to server");
		if (isConnected())
			outputThread.send(line);
	}

	/**
	 * Sends a raw line to the IRC server as soon as possible, bypassing the
	 * outgoing message queue.
	 *
	 * @param line The raw line to send to the IRC server.
	 */
	public void sendRawLineNow(String line) {
		if (line == null)
			throw new NullPointerException("Cannot send null messages to server");
		if (isConnected())
			outputThread.sendRawLineNow(line);
	}

	protected void sendRawLineSplit(String prefix, String message) {
		sendRawLineSplit(prefix, message, "");
	}

	protected void sendRawLineSplit(String prefix, String message, String suffix) {
		//Make sure suffix is valid
		if (suffix == null)
			suffix = "";

		//Find if final line is going to be shorter than the max line length
		String finalMessage = prefix + message + suffix;
		int realMaxLineLength = getMaxLineLength() - 2;
		if (!autoSplitMessage || finalMessage.length() < realMaxLineLength) {
			//Length is good (or auto split message is set), just go ahead and send it
			sendRawLine(finalMessage);
			return;
		}

		//Too long, split it up
		int maxMessageLength = realMaxLineLength - (prefix + suffix).length();
		//Oh look, no function to split every nth char. Since regex is expensive, use this nonsense
		int iterations = (int) Math.ceil(message.length() / (double) maxMessageLength);
		for (int i = 0; i < iterations; i++) {
			int endPoint = (i != iterations - 1) ? ((i + 1) * maxMessageLength) : message.length();
			String curMessagePart = prefix + message.substring(i * maxMessageLength, endPoint) + suffix;
			sendRawLine(curMessagePart);
		}
	}

	/**
	 * Sends a message to a channel or a private message to a user. These
	 * messages are added to the outgoing message queue and sent at the
	 * earliest possible opportunity.
	 * <p>
	 * Some examples: -
	 * <pre>    // Send the message "Hello!" to the channel #cs.
	 *    sendMessage("#cs", "Hello!");
	 *
	 *    // Send a private message to Paul that says "Hi".
	 *    sendMessage("Paul", "Hi");</pre>
	 *
	 * You may optionally apply colours, boldness, underlining, etc to
	 * the message by using the
	 * <code>Colors</code> class.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param message The message to send.
	 *
	 * @see Colors
	 */
	public void sendMessage(String target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null target");
		sendRawLineSplit("PRIVMSG " + target + " :", message);
	}

	/**
	 * Send a private message to a user. See {@link #sendMessage(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the message to
	 * @param message The message to send
	 */
	public void sendMessage(User target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null user");
		sendMessage(target.getNick(), message);
	}

	/**
	 * Send a message to the channel. See {@link #sendMessage(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the message to
	 * @param message The message to send
	 */
	public void sendMessage(Channel target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		sendMessage(target.getName(), message);
	}

	/**
	 * Send a message to the given user in the given channel in this format:
	 * <code>user: message</code>. Very useful for responding directly to a command
	 * @param chan The channel to send the message to
	 * @param user The user to recieve the message in the channel
	 * @param message The message to send
	 */
	public void sendMessage(Channel chan, User user, String message) {
		if (chan == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		if (user == null)
			throw new IllegalArgumentException("Can't send message to null user");
		sendMessage(chan.getName(), user.getNick() + ": " + message);
	}

	/**
	 * Sends an action to the channel or to a user.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param action The action to send.
	 *
	 * @see Colors
	 */
	public void sendAction(String target, String action) {
		if (target == null)
			throw new IllegalArgumentException("Can't send action to null target");
		sendCTCPCommand(target, "ACTION " + action);
	}

	/**
	 * Send an action to the user. See {@link #sendAction(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the action to
	 * @param action The action message to send
	 */
	public void sendAction(User target, String action) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null user");
		sendAction(target.getNick(), action);
	}

	/**
	 * Send an action to the channel. See {@link #sendAction(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the action to
	 * @param action The action message to send
	 */
	public void sendAction(Channel target, String action) {
		if (target == null)
			throw new IllegalArgumentException("Can't send message to null channel");
		sendAction(target.getName(), action);
	}

	/**
	 * Sends a notice to the channel or to a user.
	 *
	 * @param target The name of the channel or user nick to send to.
	 * @param notice The notice to send.
	 */
	public void sendNotice(String target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null target");
		sendRawLineSplit("NOTICE " + target + " :", notice);
	}

	/**
	 * Send a notice to the user. See {@link #sendNotice(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the notice to
	 * @param notice The notice to send
	 */
	public void sendNotice(User target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null user");
		sendNotice(target.getNick(), notice);
	}

	/**
	 * Send a notice to the channel. See {@link #sendNotice(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the notice to
	 * @param notice The notice to send
	 */
	public void sendNotice(Channel target, String notice) {
		if (target == null)
			throw new IllegalArgumentException("Can't send notice to null channel");
		sendNotice(target.getName(), notice);
	}

	/**
	 * Sends a CTCP command to a channel or user. (Client to client protocol).
	 * Examples of such commands are "PING <number>", "FINGER", "VERSION", etc.
	 * For example, if you wish to request the version of a user called "Dave",
	 * then you would call
	 * <code>sendCTCPCommand("Dave", "VERSION");</code>.
	 * The type of response to such commands is largely dependant on the target
	 * client software.
	 *
	 * @since PircBot 0.9.5
	 *
	 * @param target The name of the channel or user to send the CTCP message to.
	 * @param command The CTCP command to send.
	 */
	public void sendCTCPCommand(String target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null target");
		sendRawLineSplit("PRIVMSG " + target + " :\u0001", command, "\u0001");
	}

	/**
	 * Send a CTCP command to the user. See {@link #sendCTCPCommand(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param command The CTCP command to send
	 */
	public void sendCTCPCommand(User target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null user");
		sendCTCPCommand(target.getNick(), command);
	}

	/**
	 * Send a CTCP command to the channel. See {@link #sendCTCPCommand(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the CTCP command to
	 * @param command The CTCP command to send
	 */
	public void sendCTCPCommand(Channel target, String command) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP command to null channel");
		sendCTCPCommand(target.getName(), command);
	}

	/**
	 * Send a CTCP response to the target channel or user. Note that the
	 * {@link CoreHooks} class already handles responding to the most common CTCP
	 * commands. Only respond to other commands that aren't implemented
	 * @param target The target of the response
	 * @param message The message to send
	 */
	public void sendCTCPResponse(String target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP response to null target");
		sendRawLine("NOTICE " + target + " :\u0001" + message + "\u0001");
	}

	/**
	 * Send a CTCP Response to the user. See {@link #sendCTCPResponse(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP Response to
	 * @param message The response to send
	 */
	public void sendCTCPResponse(User target, String message) {
		if (target == null)
			throw new IllegalArgumentException("Can't send CTCP response to null user");
		sendCTCPResponse(target.getNick(), message);
	}

	/**
	 * Attempt to change the current nick (nickname) of the bot when it
	 * is connected to an IRC server.
	 * After confirmation of a successful nick change, the getNick method
	 * will return the new nick.
	 *
	 * @param newNick The new nick to use.
	 */
	public void changeNick(String newNick) {
		if (newNick == null)
			throw new IllegalArgumentException("Can't change to null nick");
		sendRawLine("NICK " + newNick);
	}

	/**
	 * Identify the bot with NickServ, supplying the appropriate password.
	 * Some IRC Networks (such as freenode) require users to <i>register</i> and
	 * <i>identify</i> with NickServ before they are able to send private messages
	 * to other users, thus reducing the amount of spam. If you are using
	 * an IRC network where this kind of policy is enforced, you will need
	 * to make your bot <i>identify</i> itself to NickServ before you can send
	 * private messages. Assuming you have already registered your bot's
	 * nick with NickServ, this method can be used to <i>identify</i> with
	 * the supplied password. It usually makes sense to identify with NickServ
	 * immediately after connecting to a server.
	 * <p>
	 * This method issues a raw NICKSERV command to the server, and is therefore
	 * safer than the alternative approach of sending a private message to
	 * NickServ. The latter approach is considered dangerous, as it may cause
	 * you to inadvertently transmit your password to an untrusted party if you
	 * connect to a network which does not run a NickServ service and where the
	 * untrusted party has assumed the nick "NickServ". However, if your IRC
	 * network is only compatible with the private message approach, you may
	 * typically identify like so:
	 * <pre>sendMessage("NickServ", "identify PASSWORD");</pre>
	 * <p>
	 * Note that this method will add a temporary listener for ConnectEvent if
	 * the bot is not logged in yet. If the bot is logged in the command is sent
	 * immediately to the server
	 *
	 * @param password The password which will be used to identify with NickServ.
	 */
	public void identify(final String password) {
		if (password == null)
			throw new IllegalArgumentException("Can't identify with null password");
		if (loggedIn)
			sendRawLine("NICKSERV IDENTIFY " + password);
		else
			listenerManager.addListener(new ListenerAdapter() {
				@Override
				public void onConnect(ConnectEvent event) throws Exception {
					//Make sure this bot is us to prevent nasty errors in multi bot sitations
					if (event.getBot() == PircBotX.this)
						sendRawLine("NICKSERV IDENTIFY " + password);
					//Self destrust, this listener has no more porpose
					event.getBot().getListenerManager().removeListener(this);
				}
			});
	}

	/**
	 * Set the mode of a channel.
	 * This method attempts to set the mode of a channel. This
	 * may require the bot to have operator status on the channel.
	 * For example, if the bot has operator status, we can grant
	 * operator status to "Dave" on the #cs channel
	 * by calling setMode("#cs", "+o Dave");
	 * An alternative way of doing this would be to use the op method.
	 *
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel. This may include
	 * zero or more arguments if necessary.
	 *
	 * @see #op(org.pircbotx.Channel, org.pircbotx.User)
	 */
	public void setMode(Channel chan, String mode) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set mode on null channel");
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		sendRawLine("MODE " + chan.getName() + " " + mode);
	}

	/**
	 * Set a mode for the channel with arguments. Nicer way to pass arguments than
	 * with string concatenation. See {@link #setMode(org.pircbotx.Channel, java.lang.String) }
	 * for more information
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel. This may include
	 * zero or more arguments if necessary.
	 * @param args Arguments to be passed to the mode. All will be converted to
	 * a string using {@link Object#toString() } and added together
	 * with a single space separating them
	 */
	public void setMode(Channel chan, String mode, Object... args) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set mode on null channel");
		if (mode == null)
			throw new IllegalArgumentException("Can't set mode on channel to null");
		if (args == null)
			throw new IllegalArgumentException("Can't set mode arguments to null");
		//Build arg string
		StringBuilder argBuilder = new StringBuilder(" ");
		for (Object curArg : args)
			argBuilder.append(curArg.toString()).append(" ");
		setMode(chan, mode + argBuilder.toString());
	}

	/**
	 * Set a mode for a user. See {@link #setMode(org.pircbotx.Channel, java.lang.String) }
	 * @param chan The channel on which to perform the mode change.
	 * @param mode The new mode to apply to the channel.
	 * @param user The user to perform the mode change on
	 * @see #setMode(org.pircbotx.Channel, java.lang.String)
	 */
	public void setMode(Channel chan, String mode, User user) {
		if (mode == null)
			throw new IllegalArgumentException("Can't set user mode on channel to null");
		if (user == null)
			throw new IllegalArgumentException("Can't set user mode on null user");
		setMode(chan, mode + user.getNick());
	}

	/**
	 * Attempt to set the channel limit (+l) to specified value. May require operator
	 * privileges in the channel
	 * @param chan The channel to set the limit on
	 * @param limit The maximum amount of people that can be in the channel
	 */
	public void setChannelLimit(Channel chan, int limit) {
		setMode(chan, "+l", limit);
	}

	/**
	 * Attempt to remove the channel limit (-l) on the specified channel. May require
	 * operator privileges in the channel
	 * @param chan
	 */
	public void removeChannelLimit(Channel chan) {
		setMode(chan, "-l");
	}

	/**
	 * Sets the channel key (+k) or password to get into the channel. May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 * @param key The secret key to use
	 */
	public void setChannelKey(Channel chan, String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't set channel key to null");
		setMode(chan, "+k", key);
	}

	/**
	 * Removes the channel key (-k) or password to get into the channel. May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 * @param key The secret key to remove. If this is not known a blank key or
	 * asterisk might work
	 */
	public void removeChannelKey(Channel chan, String key) {
		if (key == null)
			throw new IllegalArgumentException("Can't remove channel key with null key");
		setMode(chan, "-k", key);
	}

	/**
	 * Set the channel as invite only (+i). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setInviteOnly(Channel chan) {
		setMode(chan, "+i");
	}

	/**
	 * Removes invite only (-i) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeInviteOnly(Channel chan) {
		setMode(chan, "-i");
	}

	/**
	 * Set the channel as moderated (+m). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setModerated(Channel chan) {
		setMode(chan, "+m");
	}

	/**
	 * Removes moderated (-m) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeModerated(Channel chan) {
		setMode(chan, "-m");
	}

	/**
	 * Prevent external messages from appearing in the channel (+n). May require
	 * operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setNoExternalMessages(Channel chan) {
		setMode(chan, "+n");
	}

	/**
	 * Allow external messages to appear in the channel (+n). May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeNoExternalMessages(Channel chan) {
		setMode(chan, "-n");
	}

	/**
	 * Set the channel as secret (+s). May require operator privileges in
	 * the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setSecret(Channel chan) {
		setMode(chan, "+s");
	}

	/**
	 * Removes secret (-s) status from the channel. May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeSecret(Channel chan) {
		setMode(chan, "-s");
	}

	/**
	 * Prevent non-operator users from changing the channel topic (+t). May
	 * require operator privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void setTopicProtection(Channel chan) {
		setMode(chan, "+t");
	}

	/**
	 * Allow non-operator users to change the channel topic (-t). May require operator
	 * privileges in the channel
	 * @param chan The channel to preform the mode change on
	 */
	public void removeTopicProtection(Channel chan) {
		setMode(chan, "-t");
	}

	/**
	 * Sends an invitation to join a channel. Some channels can be marked
	 * as "invite-only", so it may be useful to allow a bot to invite people
	 * into it.
	 *
	 * @param nick The nick of the user to invite
	 * @param channel The channel you are inviting the user to join.
	 *
	 */
	public void sendInvite(String nick, String channel) {
		if (nick == null)
			throw new IllegalArgumentException("Can't send invite to null nick");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null channel");
		sendRawLine("INVITE " + nick + " :" + channel);
	}

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the CTCP command to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void sendInvite(User target, String channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null user");
		sendInvite(target.getNick(), channel);
	}

	/**
	 * Send an invite to the user. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The user to send the invite to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void sendInvite(User target, Channel channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null user");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null channel");
		sendInvite(target.getNick(), channel.getName());
	}

	/**
	 * Send an invite to the channel. See {@link #sendInvite(java.lang.String, java.lang.String) }
	 * for more information
	 * @param target The channel to send the invite to
	 * @param channel The channel you are inviting the user to join.
	 */
	public void sendInvite(Channel target, Channel channel) {
		if (target == null)
			throw new IllegalArgumentException("Can't send invite to null target channel");
		if (channel == null)
			throw new IllegalArgumentException("Can't send invite to null invite channel");
		sendInvite(target.getName(), channel.getName());
	}

	/**
	 * Bans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net". This may be used in conjunction with the
	 * kick method to permanently remove a user from a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param channel The channel to ban the user from.
	 * @param hostmask A hostmask representing the user we're banning.
	 */
	public void ban(Channel channel, String hostmask) {
		if (channel == null)
			throw new IllegalArgumentException("Can't set ban on null channel");
		if (hostmask == null)
			throw new IllegalArgumentException("Can't set ban on null hostmask");
		sendRawLine("MODE " + channel.getName() + " +b " + hostmask);
	}

	/**
	 * Unbans a user from a channel. An example of a valid hostmask is
	 * "*!*compu@*.18hp.net".
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param channel The channel to unban the user from.
	 * @param hostmask A hostmask representing the user we're unbanning.
	 */
	public void unBan(Channel channel, String hostmask) {
		if (channel == null)
			throw new IllegalArgumentException("Can't remove ban on null channel");
		if (hostmask == null)
			throw new IllegalArgumentException("Can't remove ban on null hostmask");
		sendRawLine("MODE " + channel.getName() + " -b " + hostmask);
	}

	/**
	 * Grants operator privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're opping the user on.
	 * @param user The user we are opping.
	 */
	public void op(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set op on null user");
		setMode(chan, "+o " + user.getNick());
	}

	/**
	 * Removes operator privileges from a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're deopping the user on.
	 * @param user The user we are deopping.
	 */
	public void deOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove op on null user");
		setMode(chan, "-o " + user.getNick());
	}

	/**
	 * Grants voice privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're voicing the user on.
	 * @param user The user we are voicing.
	 */
	public void voice(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set voice on null user");
		setMode(chan, "+v " + user.getNick());
	}

	/**
	 * Removes voice privileges from a user on a channel.
	 * Successful use of this method may require the bot to have operator
	 * status itself.
	 *
	 * @param chan The channel we're devoicing the user on.
	 * @param user The user we are devoicing.
	 */
	public void deVoice(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove voice on null user");
		setMode(chan, "-v " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator or
	 * halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void halfOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set halfop on null user");
		setMode(chan, "+h " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have operator or
	 * halfOp status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deHalfOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove halfop on null user");
		setMode(chan, "-h " + user.getNick());
	}

	/**
	 * Grants owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void owner(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set owner on null user");
		setMode(chan, "+q " + user.getNick());
	}

	/**
	 * Removes owner privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deOwner(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove owner on null user");
		setMode(chan, "-q " + user.getNick());
	}

	/**
	 * Grants superOp privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner or superOp
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void superOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't set super op on null user");
		setMode(chan, "+a " + user.getNick());
	}

	/**
	 * Removes superOp privileges to a user on a channel.
	 * Successful use of this method may require the bot to have owner or superOp
	 * status itself.
	 * <p>
	 * <b>Warning:</b> Not all IRC servers support this. Some servers may even use
	 * it to mean something else!
	 * @param chan
	 * @param user
	 */
	public void deSuperOp(Channel chan, User user) {
		if (user == null)
			throw new IllegalArgumentException("Can't remove super op on null user");
		setMode(chan, "-a " + user.getNick());
	}

	/**
	 * Set the topic for a channel.
	 * This method attempts to set the topic of a channel. This
	 * may require the bot to have operator status if the topic
	 * is protected.
	 *
	 * @param chan The channel on which to perform the mode change.
	 * @param topic The new topic for the channel.
	 *
	 */
	public void setTopic(Channel chan, String topic) {
		if (chan == null)
			throw new IllegalArgumentException("Can't set topic on null channel");
		if (topic == null)
			throw new IllegalArgumentException("Can't set topic to null");
		sendRawLine("TOPIC " + chan.getName() + " :" + topic);
	}

	/**
	 * Kicks a user from a channel.
	 * This method attempts to kick a user from a channel and
	 * may require the bot to have operator status in the channel.
	 *
	 * @param chan The channel to kick the user from.
	 * @param user The user to kick.
	 */
	public void kick(Channel chan, User user) {
		kick(chan, user, "");
	}

	/**
	 * Kicks a user from a channel, giving a reason.
	 * This method attempts to kick a user from a channel and
	 * may require the bot to have operator status in the channel.
	 *
	 * @param chan The channel to kick the user from.
	 * @param user The user to kick.
	 * @param reason A description of the reason for kicking a user.
	 */
	public void kick(Channel chan, User user, String reason) {
		if (chan == null)
			throw new IllegalArgumentException("Can't kick on null channel");
		if (user == null)
			throw new IllegalArgumentException("Can't kick null user");
		sendRawLine("KICK " + chan.getName() + " " + user.getNick() + " :" + reason);
	}

	/**
	 * Issues a request for a list of all channels on the IRC server.
	 * When the PircBotX receives information for each channel, a {@link ChannelInfoEvent}
	 * will be dispatched which you will need to listen for if you want to do
	 * anything useful.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels() {
		listChannels(null);
	}

	/**
	 * Issues a request for a list of all channels on the IRC server.
	 * When the PircBotX receives information for each channel, a {@link ChannelInfoEvent}
	 * will be dispatched which you will need to listen for if you want to do
	 * anything useful
	 * <p>
	 * Some IRC servers support certain parameters for LIST requests.
	 * One example is a parameter of ">10" to list only those channels
	 * that have more than 10 users in them. Whether these parameters
	 * are supported or not will depend on the IRC server software.
	 * <p>
	 * <b>NOTE:</b> This will do nothing if a channel list is already in effect
	 * @param parameters The parameters to supply when requesting the
	 * list.
	 *
	 * @see ChannelInfoEvent
	 */
	public void listChannels(String parameters) {
		if (!channelListBuilder.isRunning())
			if (parameters == null)
				sendRawLine("LIST");
			else
				sendRawLine("LIST " + parameters);
	}

	/**
	 * Sends a file to another user. Resuming is supported.
	 * The other user must be able to connect directly to your bot to be
	 * able to receive the file.
	 * <p>
	 * You may throttle the speed of this file transfer by calling the
	 * setPacketDelay method on the DccFileTransfer that is returned.
	 *
	 * @since PircBot 0.9c
	 *
	 * @param file The file to send.
	 * @param reciever The user to whom the file is to be sent.
	 * @param timeout The number of milliseconds to wait for the recipient to
	 * accept the file (we recommend about 120000).
	 *
	 * @return The DccFileTransfer that can be used to monitor this transfer.
	 *
	 * @see DccFileTransfer
	 *
	 */
	public DccFileTransfer dccSendFile(File file, User reciever, int timeout) {
		if (file == null)
			throw new IllegalArgumentException("Can't send a null file");
		if (reciever == null)
			throw new IllegalArgumentException("Can't send file to null user");
		DccFileTransfer transfer = new DccFileTransfer(this, file, reciever, timeout);
		transfer.doSend(true);
		return transfer;
	}

	/**
	 * Attempts to establish a DCC CHAT session with a client. This method
	 * issues the connection request to the client and then waits for the
	 * client to respond. If the connection is successfully made, then a
	 * DccChat object is returned by this method. If the connection is not
	 * made within the time limit specified by the timeout value, then null
	 * is returned.
	 * <p>
	 * Note that this method blocks until the user accepts the DCC chat request
	 *
	 * @throws IOException If any issue occurs with creating the connection
	 * @throws SocketTimeoutException If the user does not connect within the
	 * specified timeout period
	 * @param sender The user object representing the user we are trying to
	 * establish a chat with.
	 * @param timeout The number of milliseconds to wait for the recipient to
	 * accept the chat connection (we recommend about 120000).
	 *
	 * @return a DccChat object that can be used to send and recieve lines of
	 * text. Returns <b>null</b> if the connection could not be made.
	 *
	 * @see DccChat
	 * @since PircBot 0.9.8
	 */
	public DccChat dccSendChatRequest(User sender, int timeout) throws IOException, SocketTimeoutException {
		if (sender == null)
			throw new IllegalArgumentException("Can't send chat request to null user");
		ServerSocket ss = dccManager.createServerSocket();
		ss.setSoTimeout(timeout);
		int serverPort = ss.getLocalPort();

		InetAddress ourAddress = getDccInetAddress();
		if (ourAddress == null)
			ourAddress = getInetAddress();
		String ipNum = DccManager.addressToInteger(ourAddress);

		sendCTCPCommand(sender, "DCC CHAT chat " + ipNum + " " + serverPort);

		// The client may now connect to us to chat.
		Socket userSocket = ss.accept();

		// Close the server socket now that we've finished with it.
		ss.close();

		return new DccChat(this, sender, userSocket);
	}

	/**
	 * Adds a line to the log. This log is currently output to the standard
	 * output and is in the correct format for use by tools such as pisg, the
	 * Perl IRC Statistics Generator. You may override this method if you wish
	 * to do something else with log entries.
	 * Each line in the log begins with a number which
	 * represents the logging time (as the number of milliseconds since the
	 * epoch). This timestamp and the following log entry are separated by
	 * a single space character, " ". Outgoing messages are distinguishable
	 * by a log entry that has ">>>" immediately following the space character
	 * after the timestamp. DCC events use "+++" and warnings about unhandled
	 * Exceptions and Errors use "###".
	 * <p>
	 * This implementation of the method will only cause log entries to be
	 * output if the PircBotX has had its verbose mode turned on by calling
	 * setVerbose(true);
	 *
	 * @param line The line to add to the log.
	 */
	@Synchronized(value = "logLock")
	public void log(String line) {
		if (verbose)
			System.out.println(System.currentTimeMillis() + " " + line);
	}

	@Synchronized(value = "logLock")
	public void logException(Throwable t) {
		if (!verbose)
			return;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		StringTokenizer tokenizer = new StringTokenizer(sw.toString(), "\r\n");
		log("### Your implementation of PircBotX is faulty and you have");
		log("### allowed an uncaught Exception or Error to propagate in your");
		log("### code. It may be possible for PircBotX to continue operating");
		log("### normally. Here is the stack trace that was produced: -");
		log("### ");
		while (tokenizer.hasMoreTokens())
			log("### " + tokenizer.nextToken());
	}

	/**
	 * This method handles events when any line of text arrives from the server,
	 * then calling the appropriate method in the PircBotX. This method is
	 * protected and only called by the InputThread for this instance.
	 *
	 * @param line The raw line of text from the server.
	 */
	protected void handleLine(String line) {
		if (line == null)
			throw new IllegalArgumentException("Can't process null line");
		log("<<<" + line);

		// Check for server pings.
		if (line.startsWith("PING ")) {
			// Respond to the ping and return immediately.
			getListenerManager().dispatchEvent(new ServerPingEvent(this, line.substring(5)));
			return;
		}

		String sourceNick = "";
		String sourceLogin = "";
		String sourceHostname = "";

		StringTokenizer tokenizer = new StringTokenizer(line);
		String senderInfo = tokenizer.nextToken();
		String command = tokenizer.nextToken();
		String target = null;

		int exclamation = senderInfo.indexOf("!");
		int at = senderInfo.indexOf("@");
		if (senderInfo.startsWith(":"))
			if (exclamation > 0 && at > 0 && exclamation < at) {
				sourceNick = senderInfo.substring(1, exclamation);
				sourceLogin = senderInfo.substring(exclamation + 1, at);
				sourceHostname = senderInfo.substring(at + 1);
			} else if (tokenizer.hasMoreTokens()) {
				String token = command;

				int code = -1;
				try {
					code = Integer.parseInt(token);
				} catch (NumberFormatException e) {
					// Keep the existing value.
				}

				if (code != -1) {
					String errorStr = token;
					String response = line.substring(line.indexOf(errorStr, senderInfo.length()) + 4, line.length());
					processServerResponse(code, response);
					// Return from the method.
					return;
				} else
					// This is not a server response.
					// It must be a nick without login and hostname.
					// (or maybe a NOTICE or suchlike from the server)
					sourceNick = senderInfo; //WARNING: Changed from origional PircBot. Instead of command as target, use channel/user (setup later)
			} else {
				// We don't know what this line means.
				getListenerManager().dispatchEvent(new UnknownEvent(this, line));
				// Return from the method;
				return;
			}

		command = command.toUpperCase();
		if (sourceNick.startsWith(":"))
			sourceNick = sourceNick.substring(1);
		if (target == null)
			target = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
		if (target.startsWith(":"))
			target = target.substring(1);

		User source = getUser(sourceNick);
		//If the channel matches a prefix, then its a channel
		Channel channel = (target.length() != 0 && channelPrefixes.indexOf(target.charAt(0)) >= 0) ? getChannel(target) : null;
		String message = (line.contains(" :")) ? line.substring(line.indexOf(" :") + 2) : "";
		// Check for CTCP requests.
		if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
			String request = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
			if (request.equals("VERSION"))
				// VERSION request
				getListenerManager().dispatchEvent(new VersionEvent(this, source, channel));
			else if (request.startsWith("ACTION "))
				// ACTION request
				getListenerManager().dispatchEvent(new ActionEvent(this, source, channel, request.substring(7)));
			else if (request.startsWith("PING "))
				// PING request
				getListenerManager().dispatchEvent(new PingEvent(this, source, channel, request.substring(5)));
			else if (request.equals("TIME"))
				// TIME request
				getListenerManager().dispatchEvent(new TimeEvent(this, source, channel));
			else if (request.equals("FINGER"))
				// FINGER request
				getListenerManager().dispatchEvent(new FingerEvent(this, source, channel));
			else if ((tokenizer = new StringTokenizer(request)).countTokens() >= 5 && tokenizer.nextToken().equals("DCC")) {
				// This is a DCC request.
				boolean success = dccManager.processRequest(source, request);
				if (!success)
					// The DccManager didn't know what to do with the line.
					getListenerManager().dispatchEvent(new UnknownEvent(this, line));
			} else
				// An unknown CTCP message - ignore it.
				getListenerManager().dispatchEvent(new UnknownEvent(this, line));
		} else if (command.equals("PRIVMSG") && channelPrefixes.indexOf(target.charAt(0)) >= 0)
			// This is a normal message to a channel.
			getListenerManager().dispatchEvent(new MessageEvent(this, channel, source, message));
		else if (command.equals("PRIVMSG"))
			// This is a private message to us.
			getListenerManager().dispatchEvent(new PrivateMessageEvent(this, source, message));
		else if (command.equals("JOIN")) {
			// Someone is joining a channel.
			if (sourceNick.equalsIgnoreCase(nick)) {
				//Its us, do some setup (don't use channel var since channel doesn't exist yet)
				sendRawLine("WHO " + target);
				sendRawLine("MODE " + target);
			}
			source.setLogin(sourceLogin);
			source.setHostmask(sourceHostname);
			userChanInfo.put(channel, source);
			getListenerManager().dispatchEvent(new JoinEvent(this, channel, source));
		} else if (command.equals("PART"))
			// Someone is parting from a channel.
			if (sourceNick.equals(getNick()))
				//We parted the channel
				userChanInfo.deleteA(channel);
			else {
				//Just remove the user from memory
				userChanInfo.dissociate(channel, getUser(sourceNick));
				getListenerManager().dispatchEvent(new PartEvent(this, channel, source, message));
			}
		else if (command.equals("NICK")) {
			// Somebody is changing their nick.
			String newNick = target;
			getUser(sourceNick).setNick(newNick);
			if (sourceNick.equals(getNick()))
				// Update our nick if it was us that changed nick.
				setNick(newNick);
			getListenerManager().dispatchEvent(new NickChangeEvent(this, sourceNick, newNick, source));
		} else if (command.equals("NOTICE"))
			// Someone is sending a notice.
			getListenerManager().dispatchEvent(new NoticeEvent(this, source, channel, message));
		else if (command.equals("QUIT")) {
			UserSnapshot snapshot = source.generateSnapshot();
			// Someone has quit from the IRC server.
			if (!sourceNick.equals(getNick()))
				//Someone else
				userChanInfo.deleteB(source);
			getListenerManager().dispatchEvent(new QuitEvent(this, snapshot, message));
		} else if (command.equals("KICK")) {
			// Somebody has been kicked from a channel.
			User recipient = getUser(tokenizer.nextToken());
			if (recipient.getNick().equals(getNick()))
				//We were just kicked
				userChanInfo.deleteA(channel);
			else
				//Someone else
				userChanInfo.dissociate(channel, recipient, true);
			getListenerManager().dispatchEvent(new KickEvent(this, channel, source, recipient, message));
		} else if (command.equals("MODE")) {
			// Somebody is changing the mode on a channel or user.
			String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
			if (mode.startsWith(":"))
				mode = mode.substring(1);
			processMode(target, sourceNick, sourceLogin, sourceHostname, mode);
		} else if (command.equals("TOPIC")) {
			// Someone is changing the topic.
			String topic = line.substring(line.indexOf(" :") + 2);
			long currentTime = System.currentTimeMillis();
			channel.setTopic(topic);
			channel.setTopicSetter(sourceNick);
			channel.setTopicTimestamp(currentTime);

			getListenerManager().dispatchEvent(new TopicEvent(this, channel, topic, source, currentTime, true));
		} else if (command.equals("INVITE")) {
			// Somebody is inviting somebody else into a channel.
			//Use line method instead of channel since channel is wrong
			getListenerManager().dispatchEvent(new InviteEvent(this, sourceNick, message));
			//Delete user if not part of any of our channels
			if (source.getChannels().isEmpty())
				userChanInfo.deleteB(source);
		} else
			// If we reach this point, then we've found something that the PircBotX
			// Doesn't currently deal with.
			getListenerManager().dispatchEvent(new UnknownEvent(this, line));
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
	protected void processServerResponse(int code, String response) {
		if (response == null)
			throw new IllegalArgumentException("Can't process null response");
		//NOTE: Update tests if adding support for a new code
		String[] parsed = response.split(" ");
		if (code == RPL_LISTSTART)
			//EXAMPLE: 321 Channel :Users Name (actual text)
			//A channel list is about to be sent
			channelListBuilder.setRunning(true);
		else if (code == RPL_LIST) {
			//This is part of a full channel listing as part of /LIST
			//EXAMPLE: 322 lordquackstar #xomb 12 :xomb exokernel project @ www.xomb.org
			int firstSpace = response.indexOf(' ');
			int secondSpace = response.indexOf(' ', firstSpace + 1);
			int thirdSpace = response.indexOf(' ', secondSpace + 1);
			int colon = response.indexOf(':');
			String channel = response.substring(firstSpace + 1, secondSpace);
			int userCount = 0;
			try {
				userCount = Integer.parseInt(response.substring(secondSpace + 1, thirdSpace));
			} catch (NumberFormatException e) {
				// Stick with the value of zero.
			}
			String topic = response.substring(colon + 1);
			channelListBuilder.add(new ChannelListEntry(channel, userCount, topic));
		} else if (code == RPL_LISTEND) {
			//EXAMPLE: 323 :End of /LIST
			//End of channel list, dispatch event
			getListenerManager().dispatchEvent(new ChannelInfoEvent(this, channelListBuilder.finish()));
			channelListBuilder.setRunning(false);
		} else if (code == RPL_TOPIC) {
			//EXAMPLE: 332 PircBotX #aChannel :I'm some random topic
			//This is topic about a channel we've just joined. From /JOIN or /TOPIC
			parsed = response.split(" ", 3);
			String channel = parsed[1];
			String topic = parsed[2].substring(1);

			getChannel(channel).setTopic(topic);
		} else if (code == RPL_TOPICINFO) {
			//EXAMPLE: 333 PircBotX #aChannel ISetTopic 1564842512
			//This is information on the topic of the channel we've just joined. From /JOIN or /TOPIC
			String channel = parsed[1];
			User setBy = getUser(parsed[2]);
			long date = 0;
			try {
				date = Long.parseLong(parsed[3]) * 1000;
			} catch (NumberFormatException e) {
				// Stick with the default value of zero.
			}

			Channel chan = getChannel(channel);
			chan.setTopicTimestamp(date);
			chan.setTopicSetter(setBy.getNick());

			getListenerManager().dispatchEvent(new TopicEvent(this, chan, chan.getTopic(), setBy, date, false));
		} else if (code == RPL_WHOREPLY) {
			//EXAMPLE: PircBotX #aChannel ~someName 74.56.56.56.my.Hostmask wolfe.freenode.net someNick H :0 Full Name
			//Part of a WHO reply on information on individual users
			parsed = response.split(" ", 9);
			Channel chan = getChannel(parsed[1]);

			//Setup user
			User curUser = getUser(parsed[5]);
			curUser.setLogin(parsed[2]);
			curUser.setIdentified(!parsed[2].startsWith("~"));
			curUser.setHostmask(parsed[3]);
			curUser.setServer(parsed[4]);
			curUser.setNick(parsed[5]);
			curUser.parseStatus(chan, parsed[6]);
			curUser.setHops(Integer.parseInt(parsed[7].substring(1)));
			curUser.setRealName(parsed[8]);

			//Associate with channel
			userChanInfo.put(chan, curUser);
		} else if (code == RPL_ENDOFWHO) {
			//EXAMPLE: PircBotX #aChannel :End of /WHO list
			//End of the WHO reply
			Channel channel = getChannel(response.split(" ")[1]);
			getListenerManager().dispatchEvent(new UserListEvent(this, channel, getUsers(channel)));
		} else if (code == RPL_CHANNELMODEIS)
			//EXAMPLE: PircBotX #aChannel +cnt
			//Full channel mode (In response to MODE <channel>)
			getChannel(parsed[1]).setMode(parsed[2]);
		else if (code == 329) {
			//EXAMPLE: 329 lordquackstar #botters 1199140245
			//Tells when channel was created. Note mIRC says lordquackstar shouldn't be there while Freenode
			//displays it. From /JOIN(?)
			int createDate;
			String channel;

			//Freenode version
			try {
				createDate = Integer.parseInt(parsed[2]);
				channel = parsed[1];
			} catch (NumberFormatException e) {
				//mIRC version
				createDate = Integer.parseInt(parsed[1]);
				channel = parsed[0];
			}

			//Set in channel
			getChannel(channel).setCreateTimestamp(createDate);
		} else if (code == RPL_MOTDSTART)
			//Example: 375 PircBotX :- wolfe.freenode.net Message of the Day -
			//Motd is starting, reset the StringBuilder
			getServerInfo().setMotd("");
		else if (code == RPL_MOTD)
			//Example: PircBotX :- Welcome to wolfe.freenode.net in Manchester, England, Uk!  Thanks to
			//This is part of the MOTD, add a new line
			getServerInfo().setMotd(getServerInfo().getMotd() + response.split(" ", 3)[2].trim() + "\n");
		else if (code == RPL_ENDOFMOTD) {
			//Example: PircBotX :End of /MOTD command.
			//End of MOTD, clean it and dispatch MotdEvent
			getServerInfo().setMotd(getServerInfo().getMotd().trim());
			getListenerManager().dispatchEvent(new MotdEvent(this, (getServerInfo().getMotd())));
		} else if (code == 004 || code == 005)
			//Example: 004 PircBotX sendak.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
			//Server info line, let ServerInfo class parse it
			getServerInfo().parse(code, response);
		else if (code == RPL_WHOISUSER) {
			//New whois is starting
			//311 TheLQ Plazma ~Plazma freenode/staff/plazma * :Plazma Rooolz!
			String[] parts = response.split(" ", 6);
			WhoisEvent.WhoisEventBuilder builder = new WhoisEvent.WhoisEventBuilder();
			builder.setNick(parts[1]);
			builder.setLogin(parts[2]);
			builder.setHostname(parts[3]);
			builder.setRealname(parts[5].substring(1));
			whoisBuilder.put(parsed[1], builder);
		} else if (code == RPL_WHOISCHANNELS) {
			//Channel list from whois
			//319 TheLQ Plazma :+#freenode 
			String chans = response.split(" ", 3)[2].substring(1);
			whoisBuilder.get(parsed[1]).setChannels(Arrays.asList(chans.split(" ")));
		} else if (code == RPL_WHOISSERVER) {
			//Server info from whois
			//312 TheLQ Plazma leguin.freenode.net :Ume?, SE, EU
			String[] info = response.split(" ", 4);
			whoisBuilder.get(parsed[1]).setServer(info[2]);
			whoisBuilder.get(parsed[1]).setServerInfo(info[3].substring(1));
		} else if (code == RPL_ENDOFWHOIS) {
			//End of whois
			//318 TheLQ Plazma :End of /WHOIS list.
			getListenerManager().dispatchEvent(whoisBuilder.get(parsed[1]).generateEvent(this));
			whoisBuilder.remove(parsed[1]);
		}
		//WARNING: Parsed array might be modified, recreate if you're going to use down here
		getListenerManager().dispatchEvent(new ServerResponseEvent(this, code, response));
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
	protected void processMode(String target, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		User source = getUser(sourceNick);
		if (channelPrefixes.indexOf(target.charAt(0)) >= 0) {
			// The mode of a channel is being changed.
			Channel channel = getChannel(target);
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
					User reciepeint = getUser(params[p]);
					if (pn == '+') {
						channel.ops.add(reciepeint);
						getListenerManager().dispatchEvent(new OpEvent(this, channel, source, reciepeint, true));
					} else {
						channel.ops.remove(reciepeint);
						getListenerManager().dispatchEvent(new OpEvent(this, channel, source, reciepeint, false));
					}
					p++;
				} else if (atPos == 'v') {
					User reciepeint = getUser(params[p]);
					if (pn == '+') {
						channel.voices.add(reciepeint);
						getListenerManager().dispatchEvent(new VoiceEvent(this, channel, source, reciepeint, true));
					} else {
						channel.voices.remove(reciepeint);
						getListenerManager().dispatchEvent(new VoiceEvent(this, channel, source, reciepeint, false));
					}
					p++;
				} else if (atPos == 'h') {
					//Half-op change
					User reciepeint = getUser(params[p]);
					if (pn == '+') {
						channel.halfOps.add(reciepeint);
						getListenerManager().dispatchEvent(new HalfOpEvent(this, channel, source, reciepeint, true));
					} else {
						channel.halfOps.remove(reciepeint);
						getListenerManager().dispatchEvent(new HalfOpEvent(this, channel, source, reciepeint, false));
					}
					p++;
				} else if (atPos == 'a') {
					//SuperOp change
					User reciepeint = getUser(params[p]);
					if (pn == '+') {
						channel.superOps.add(reciepeint);
						getListenerManager().dispatchEvent(new SuperOpEvent(this, channel, source, reciepeint, true));
					} else {
						channel.superOps.remove(reciepeint);
						getListenerManager().dispatchEvent(new SuperOpEvent(this, channel, source, reciepeint, false));
					}
					p++;
				} else if (atPos == 'q') {
					//Owner change
					User reciepeint = getUser(params[p]);
					if (pn == '+') {
						channel.owners.add(reciepeint);
						getListenerManager().dispatchEvent(new OwnerEvent(this, channel, source, reciepeint, true));
					} else {
						channel.owners.remove(reciepeint);
						getListenerManager().dispatchEvent(new OwnerEvent(this, channel, source, reciepeint, false));
					}
					p++;
				} else if (atPos == 'k') {
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetChannelKeyEvent(this, channel, source, params[p]));
					else
						getListenerManager().dispatchEvent(new RemoveChannelKeyEvent(this, channel, source, (p < params.length) ? params[p] : null));
					p++;
				} else if (atPos == 'l')
					if (pn == '+') {
						getListenerManager().dispatchEvent(new SetChannelLimitEvent(this, channel, source, Integer.parseInt(params[p])));
						p++;
					} else
						getListenerManager().dispatchEvent(new RemoveChannelLimitEvent(this, channel, source));
				else if (atPos == 'b') {
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetChannelBanEvent(this, channel, source, params[p]));
					else
						getListenerManager().dispatchEvent(new RemoveChannelBanEvent(this, channel, source, params[p]));
					p++;
				} else if (atPos == 't')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetTopicProtectionEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemoveTopicProtectionEvent(this, channel, source));
				else if (atPos == 'n')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetNoExternalMessagesEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemoveNoExternalMessagesEvent(this, channel, source));
				else if (atPos == 'i')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetInviteOnlyEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemoveInviteOnlyEvent(this, channel, source));
				else if (atPos == 'm')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetModeratedEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemoveModeratedEvent(this, channel, source));
				else if (atPos == 'p')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetPrivateEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemovePrivateEvent(this, channel, source));
				else if (atPos == 's')
					if (pn == '+')
						getListenerManager().dispatchEvent(new SetSecretEvent(this, channel, source));
					else
						getListenerManager().dispatchEvent(new RemoveSecretEvent(this, channel, source));
			}
			getListenerManager().dispatchEvent(new ModeEvent(this, channel, source, mode));
		} else {
			// The mode of a user is being changed.
			String userNick = target;
			getListenerManager().dispatchEvent(new UserModeEvent(this, getUser(userNick), source, mode));
		}
	}

	/**
	 * Sets the verbose mode. If verbose mode is set to true, then log entries
	 * will be printed to the standard output. The default value is false and
	 * will result in no output. For general development, we strongly recommend
	 * setting the verbose mode to true.
	 *
	 * @param verbose true if verbose mode is to be used. Default is false.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the name of the bot, which will be used as its nick when it
	 * tries to join an IRC server. This should be set before joining
	 * any servers, otherwise the default nick will be used. You would
	 * typically call this method from the constructor of the class that
	 * extends PircBotX.
	 * <p>
	 * The changeNick method should be used if you wish to change your nick
	 * when you are connected to a server.
	 *
	 * @param name The new name of the Bot.
	 */
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Can't set name to null");
		this.name = name;
	}

	/**
	 * Sets the internal nick of the bot. This is only to be called by the
	 * PircBotX class in response to notification of nick changes that apply
	 * to us.
	 *
	 * @param nick The new nick.
	 */
	protected void setNick(String nick) {
		synchronized (userNickMap) {
			User user = getUser(this.nick);
			userNickMap.remove(this.nick);
			userNickMap.put(nick, user);
			this.nick = nick;
		}
	}

	/**
	 * Sets the internal login of the Bot. This should be set before joining
	 * any servers.
	 *
	 * @param login The new login of the Bot.
	 */
	public void setLogin(String login) {
		if (login == null)
			throw new IllegalArgumentException("Can't login to null");
		this.login = login;
	}

	/**
	 * Sets the internal version of the Bot. This should be set before joining
	 * any servers.
	 *
	 * @param version The new version of the Bot.
	 */
	public void setVersion(String version) {
		if (version == null)
			throw new IllegalArgumentException("Can't version to null");
		this.version = version;
	}

	/**
	 * Sets the interal finger message. This should be set before joining
	 * any servers.
	 *
	 * @param finger The new finger message for the Bot.
	 */
	public void setFinger(String finger) {
		if (finger == null)
			throw new IllegalArgumentException("Can't finger to null");
		this.finger = finger;
	}

	/**
	 * Gets the name of the PircBotX. This is the name that will be used as
	 * as a nick when we try to join servers.
	 *
	 * @return The name of the PircBotX.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the current nick of the bot. Note that if you have just changed
	 * your nick, this method will still return the old nick until confirmation
	 * of the nick change is received from the server.
	 * <p>
	 * The nick returned by this method is maintained only by the PircBotX
	 * class and is guaranteed to be correct in the context of the IRC server.
	 *
	 * @since PircBot 1.0.0
	 *
	 * @return The current nick of the bot.
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Gets the internal login of the PircBotX.
	 *
	 * @return The login of the PircBotX.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Gets the internal version of the PircBotX.
	 *
	 * @return The version of the PircBotX.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the internal finger message of the PircBotX.
	 *
	 * @return The finger message of the PircBotX.
	 */
	public String getFinger() {
		return finger;
	}

	/**
	 * Returns whether or not the PircBotX is currently connected to a server.
	 * The result of this method should only act as a rough guide,
	 * as the result may not be valid by the time you act upon it.
	 *
	 * @return True if and only if the PircBotX is currently connected to a server.
	 */
	public boolean isConnected() {
		return inputThread != null && inputThread.isConnected();
	}

	/**
	 * Sets the number of milliseconds to delay between consecutive
	 * messages when there are multiple messages waiting in the
	 * outgoing message queue. This has a default value of 1000ms.
	 * It is a good idea to stick to this default value, as it will
	 * prevent your bot from spamming servers and facing the subsequent
	 * wrath! However, if you do need to change this delay value (<b>not
	 * recommended</b>), then this is the method to use.
	 *
	 * @param delay The number of milliseconds between each outgoing message.
	 *
	 */
	public void setMessageDelay(long delay) {
		if (delay < 0)
			throw new IllegalArgumentException("Cannot have a negative time.");
		messageDelay = delay;
	}

	/**
	 * Returns the number of milliseconds that will be used to separate
	 * consecutive messages to the server from the outgoing message queue.
	 *
	 * @return Number of milliseconds.
	 */
	public long getMessageDelay() {
		return messageDelay;
	}

	/**
	 * Gets the maximum length of any line that is sent via the IRC protocol.
	 * The IRC RFC specifies that line lengths, including the trailing \r\n
	 * must not exceed 512 bytes. Hence, there is currently no option to
	 * change this value in PircBotX. All lines greater than this length
	 * will be truncated before being sent to the IRC server.
	 *
	 * @return The maximum line length (currently fixed at 512)
	 */
	public int getMaxLineLength() {
		return InputThread.MAX_LINE_LENGTH;
	}

	/**
	 * Gets the number of lines currently waiting in the outgoing message Queue.
	 * If this returns 0, then the Queue is empty and any new message is likely
	 * to be sent to the IRC server immediately.
	 *
	 * @since PircBot 0.9.9
	 *
	 * @return The number of lines in the outgoing message Queue.
	 */
	public int getOutgoingQueueSize() {
		return outputThread.getQueueSize();
	}

	/**
	 * Returns the name of the last IRC server the PircBotX tried to connect to.
	 * This does not imply that the connection attempt to the server was
	 * successful (we suggest you look at the dispatched {@link ConnectEvent}).
	 * A value of null is returned if the PircBotX has never tried to connect
	 * to a server.
	 *
	 * @return The name of the last machine we tried to connect to. Returns
	 * null if no connection attempts have ever been made.
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Returns the port number of the last IRC server that the PircBotX tried
	 * to connect to.
	 * This does not imply that the connection attempt to the server was
	 * successful (we suggest you look at the dispatched {@link ConnectEvent}).
	 * A value of -1 is returned if the PircBotX has never tried to connect
	 * to a server.
	 *
	 * @since PircBot 0.9.9
	 *
	 * @return The port number of the last IRC server we connected to.
	 * Returns -1 if no connection attempts have ever been made.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the last password that we used when connecting to an IRC server.
	 * This does not imply that the connection attempt to the server was
	 * successful (we suggest you look at the dispatched {@link ConnectEvent}).
	 * A value of null is returned if the PircBotX has never tried to connect
	 * to a server using a password.
	 *
	 * @since PircBot 0.9.9
	 *
	 * @return The last password that we used when connecting to an IRC server.
	 * Returns null if we have not previously connected using a password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the encoding charset to be used when sending or receiving lines
	 * from the IRC server. Simply a convenience method for {@link #setEncoding(java.nio.charset.Charset) }
	 *
	 * @since PircBot 1.0.4
	 * @see #setEncoding(java.nio.charset.Charset)
	 * @param charset The charset as a string to use
	 * @throws NullPointerException If the charset is null
	 * @throws UnsupportedEncodingException If the passed encoding isn't supported
	 * by the JMV
	 */
	public void setEncoding(String charset) throws UnsupportedEncodingException {
		if (charset == null)
			throw new NullPointerException("Can't set charset to null");
		setEncoding(Charset.forName(charset));
	}

	/**
	 * Sets the encoding charset to be used when sending or receiving lines
	 * from the IRC server. If set to null, then the platform's default
	 * charset is used. You should only use this method if you are
	 * trying to send text to an IRC server in a different charset, e.g.
	 * "GB2312" for Chinese encoding. If a PircBotX is currently connected
	 * to a server, then it must reconnect before this change takes effect.
	 *
	 * @param charset The new encoding charset to be used by PircBotX.
	 * @throws NullPointerException If the charset is null
	 * @throws UnsupportedEncodingException If the named charset is not
	 * supported.
	 */
	public void setEncoding(Charset charset) {
		if (charset == null)
			throw new NullPointerException("Can't set charset to null");
		this.charset = charset;
	}

	/**
	 * Returns the encoding used to send and receive lines from
	 * the IRC server. Never will return null Use the {@link #setEncoding(java.nio.charset.Charset) }
	 * method to change the encoding charset.
	 *
	 * @since PircBot 1.0.4
	 *
	 * @return The encoding used to send outgoing messages. Never null
	 */
	public Charset getEncoding() {
		return charset;
	}

	/**
	 * Returns the InetAddress used by the PircBotX.
	 * This can be used to find the I.P. address from which the PircBotX is
	 * connected to a server.
	 *
	 * @since PircBot 1.4.4
	 *
	 * @return The current local InetAddress, or null if never connected.
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	/**
	 * Sets the InetAddress to be used when sending DCC chat or file transfers.
	 * This can be very useful when you are running a bot on a machine which
	 * is behind a firewall and you need to tell receiving clients to connect
	 * to a NAT/router, which then forwards the connection.
	 *
	 * @since PircBot 1.4.4
	 *
	 * @param dccInetAddress The new InetAddress, or null to use the default.
	 */
	public void setDccInetAddress(InetAddress dccInetAddress) {
		this.dccInetAddress = dccInetAddress;
	}

	/**
	 * Returns the InetAddress used when sending DCC chat or file transfers.
	 * If this is null, the default InetAddress will be used.
	 *
	 * @since PircBot 1.4.4
	 *
	 * @return The current DCC InetAddress, or null if left as default.
	 */
	public InetAddress getDccInetAddress() {
		return dccInetAddress;
	}

	/**
	 * Returns the list of port numbers to be used when sending a DCC chat
	 * or file transfer. This is useful when you are behind a firewall and
	 * need to set up port forwarding. The array of port numbers is traversed
	 * in sequence until a free port is found to listen on. A DCC tranfer will
	 * fail if all ports are already in use.
	 * If empty, <i>any</i> free port number will be used.
	 *
	 * @since PircBot 1.4.4
	 *
	 * @return An array of port numbers that PircBotX can use to send DCC
	 * transfers, or null if any port is allowed.
	 */
	public List<Integer> getDccPorts() {
		return dccPorts;
	}

	/**
	 * Returns a String representation of this object.
	 * You may find this useful for debugging purposes, particularly
	 * if you are using more than one PircBotX instance to achieve
	 * multiple server connectivity. The format of
	 * this String may change between different versions of PircBotX
	 * but is currently something of the form
	 * <code>
	 *   Version{PircBotX x.y.z Java IRC Bot - www.jibble.org}
	 *   Connected{true}
	 *   Server{irc.dal.net}
	 *   Port{6667}
	 *   Password{}
	 * </code>
	 *
	 * @since PircBot 0.9.10
	 *
	 * @return a String representation of this object.
	 */
	@Override
	public String toString() {
		return "Version{" + version + "}"
				+ " Connected{" + isConnected() + "}"
				+ " Server{" + server + "}"
				+ " Port{" + port + "}"
				+ " Password{" + password + "}";
	}

	/**
	 * The number of milliseconds to wait before the socket times out on read
	 * operations. This does not mean the socket is invalid. By default its 5
	 * minutes
	 * @return the socketTimeout
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}

	/**
	 * The number of milliseconds to wait before the socket times out on read
	 * operations. This does not mean the socket is invalid. By default its 5
	 * minutes
	 * @param socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/**
	 * Returns an array of all channels that we are in. Note that if you
	 * call this method immediately after joining a new channel, the new
	 * channel may not appear in this array as it is not possible to tell
	 * if the join was successful until a response is received from the
	 * IRC server.
	 *
	 * @since PircBot 1.0.0
	 *
	 * @return An <i>unmodifiable</i> Set of all channels were connected to
	 */
	public Set<Channel> getChannels() {
		return userChanInfo.getAValues();
	}

	/**
	 * Get all channels that the given user is connected to
	 * @param user The user to lookup
	 * @return An <i>unmodifiable</i> Set of user's in the channel
	 */
	public Set<Channel> getChannels(User user) {
		if (user == null)
			throw new NullPointerException("Can't get a null user");
		return userChanInfo.getAValues(user);
	}

	/**
	 * Gets a channel or creates a <u>new</u> one. Never returns null
	 * @param name The name of the channel
	 * @return The channel object requested, never null
	 */
	public Channel getChannel(String name) {
		if (name == null)
			throw new NullPointerException("Can't get a null channel");
		for (Channel curChan : userChanInfo.getAValues())
			if (curChan.getName().equals(name))
				return curChan;

		//Channel does not exist, create one
		Channel chan = new Channel(this, name);
		userChanInfo.putB(chan);
		return chan;
	}

	/**
	 * Gets all the name's of all the channels that we are connected to
	 * @return An <i>Unmodifiable</i> set of Channel names
	 */
	public Set<String> getChannelsNames() {
		return Collections.unmodifiableSet(new HashSet<String>() {
			{
				for (Channel curChan : userChanInfo.getAValues())
					add(curChan.getName());
			}
		});
	}

	/**
	 * Check if we are still connected to given channel by string.
	 * @param channel A channel name as a string
	 * @return True if we are still connected to the channel, false if not
	 */
	public boolean channelExists(String channel) {
		if (channel == null)
			throw new IllegalArgumentException("Can't check for null channel existing");
		for (Channel curChan : userChanInfo.getAValues())
			if (curChan.getName().equals(channel))
				return true;
		return false;
	}

	/**
	 * Get all user's in the channel.
	 *
	 * There are some important things to note about this method:-
	 * <ul>
	 * <li>This method may not return a full list of users if you call it
	 * before the complete nick list has arrived from the IRC server.
	 * </li>
	 * <li>If you wish to find out which users are in a channel as soon
	 * as you join it, then you should listen for a {@link UserListEvent}
	 * instead of calling this method, as the {@link UserListEvent} is only
	 * dispatched as soon as the full user list has been received.
	 * </li>
	 * <li>This method will return immediately, as it does not require any
	 * interaction with the IRC server.
	 * </li>
	 * <li>The bot must be in a channel to be able to know which users are
	 * in it.
	 * </li>
	 * </ul>
	 *
	 * @since PircBot 1.0.0
	 *
	 * @param chan The channel object to search in
	 * @return A Set of all user's in the channel
	 *
	 * @see UserListEvent
	 */
	public Set<User> getUsers(Channel chan) {
		if (chan == null)
			throw new NullPointerException("Can't get a null channel");
		return userChanInfo.getBValues(chan);
	}

	/**
	 * Gets an existing user or creates a new one.
	 * @param nick
	 * @return The requested User. Never is null
	 */
	public User getUser(String nick) {
		if (nick == null)
			throw new NullPointerException("Can't get a null user");
		if (userNickMap.containsKey(nick))
			return userNickMap.get(nick);

		//User does not exist, create one
		User user = new User(this, nick);
		userChanInfo.putA(user);
		return user;
	}

	/**
	 * Gets the bots own user object
	 * @return The user object representing this bot
	 */
	public User getUserBot() {
		return getUser(getNick());
	}

	/**
	 * Check if a user exists
	 * @param nick The nick of the user to lookup
	 * @return True if they exist, false if not
	 */
	public boolean userExists(String nick) {
		return userNickMap.containsKey(nick);
	}

	/**
	 * @return the serverInfo
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * Returns the current ListenerManager in use by this bot.
	 * @return Current ListenerManager
	 */
	public ListenerManager<? extends PircBotX> getListenerManager() {
		return listenerManager;
	}

	/**
	 * Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are added
	 * when this method is called. If you do not want this, remove CoreHooks with
	 * {@link ListenerManager#removeListener(org.pircbotx.hooks.Listener) }
	 * @param listenerManager The listener manager
	 */
	public void setListenerManager(ListenerManager<? extends PircBotX> listenerManager) {
		if (listenerManager == null)
			throw new IllegalArgumentException("Can't set listener manager to null");
		this.listenerManager = listenerManager;
		//Check if corehooks already exist
		for (Listener curListener : listenerManager.getListeners())
			if (curListener instanceof CoreHooks)
				return;
		listenerManager.addListener(new CoreHooks());
	}

	/**
	 * Returns the last SocketFactory that we used to connect to an IRC server.
	 * This does not imply that the connection attempt to the server was
	 * successful (we suggest you look at the dispatched {@link ConnectEvent}).
	 * A value of null is returned if the PircBot has never tried to connect
	 * to a server using a SocketFactory.
	 *
	 * @return The last SocketFactory that we used when connecting to an IRC server.
	 * Returns null if we have not previously connected using a SocketFactory.
	 */
	public SocketFactory getSocketFactory() {
		return socketFactory;
	}

	/**
	 * Get current verbose mode
	 * @return True if verbose is turned on, false if not
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * Get current auto nick change mode
	 * @return True if auto nick change is turned on, false otherwise
	 */
	public boolean isAutoNickChange() {
		return autoNickChange;
	}

	/**
	 * Checks if there is an active shutdown hook for this bot
	 * @return True if there is, false if not
	 * @see #useShutdownHook(boolean) 
	 */
	public boolean hasShutdownHook() {
		return shutdownHook != null;
	}

	/**
	 * Configure if a shutdown hook is used or not.
	 * <p>
	 * For reference, a shutdown hook will properly disconnect a bot from the
	 * server. This is useful in cases where the JVM doesn't properly close the
	 * socket meaning the bot still appears online to the IRC server.
	 * @param use True to create a shutdown hook if one doesn't exist, or false
	 * to remove the shutdown hook if it exists
	 */
	public void useShutdownHook(boolean use) {
		if (use && shutdownHook == null) {
			//Define as a weakreference so PircBotX can be GC'd
			final WeakReference<PircBotX> thisBotRef = new WeakReference(this);
			Runtime.getRuntime().addShutdownHook(shutdownHook = new Thread() {
				@Override
				public void run() {
					PircBotX thisBot = thisBotRef.get();
					if (thisBot != null && thisBot.isConnected()) {
						thisBot.disconnect();
						thisBot.shutdown(true);
					}
				}
			});
			shutdownHook.setName("bot" + botCount + "-shutdownhook");
		} else if (!use && shutdownHook != null) {
			//Remove the shutdownHook, if it exists
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
			shutdownHook = null;
		}
	}

	/**
	 * Calls shutdown allowing reconnect
	 */
	public void shutdown() {
		shutdown(false);
	}

	/**
	 * Fully shutdown the bot and all internal resources. This will close the
	 * connections to the server, kill background threads, clear server specific
	 * state, and dispatch a DisconnectedEvent
	 * 
	 * @param noReconnect Toggle whether to reconnect if enabled. Set to true to
	 * 100% shutdown the bot
	 */
	public void shutdown(boolean noReconnect) {
		//Close the socket from here and let the threads die
		try {
			socket.close();
		} catch (Exception e) {
			//Something went wrong, interrupt to make sure they are closed
			outputThread.interrupt();
			inputThread.interrupt();
		}

		//Close the DCC Manager
		try {
			dccManager.close();
		} catch (Exception ex) {
			//Not much we can do with it here. And throwing it would not let other things shutdown
			logException(ex);
		}

		//Cache channels for possible next reconnect
		Map<String, String> previousChannels = new HashMap();
		for (Channel curChannel : getChannels()) {
			String key = (curChannel.getChannelKey() == null) ? "" : curChannel.getChannelKey();
			previousChannels.put(curChannel.getName(), key);
		}

		//Clear relevant variables of information
		userChanInfo.clear();
		userNickMap.clear();
		channelListBuilder.finish();

		//Dispatch event
		getListenerManager().dispatchEvent(new DisconnectEvent(this));
		log("*** Disconnected.");

		if (!autoReconnect || noReconnect)
			return;
		try {
			reconnect();
			if (autoReconnectChannels)
				for (Map.Entry<String, String> curEntry : previousChannels.entrySet())
					joinChannel(curEntry.getKey(), curEntry.getValue());
		} catch (Exception e) {
			//Not much we can do with it
			logException(e);
		}
	}

	/**
	 * Using the specified eventClass, block until the Event occurs. Eg wait for
	 * a response from a user, capturing the MessageEvent or PrivateMessageEvent.
	 * <p>
	 * <b>Warning:</b> The listener manager in use <i>must</i> support multithreading.
	 * If not, the entire bot will freeze since its waiting in the same thread
	 * that's reading input from the server. This means you <i>can't</i> use
	 * {@link GenericListenerManager}.
	 * @param eventClass The class representing the Event to capture
	 * @return The requested event
	 * @throws InterruptedException If the thread is interrupted, this exception
	 * is thrown
	 */
	public <E extends Event> E waitFor(Class<? extends E> eventClass) throws InterruptedException {
		if (eventClass == null)
			throw new IllegalArgumentException("Can't wait for null event");
		//Create a WaitListener for getting the event
		WaitListener waitListener = new WaitListener();
		listenerManager.addListener(waitListener);

		//Call waitFor which blocks until the desired event is captured
		Event finalEvent = waitListener.waitFor(eventClass);

		//Remove listener since its no longer needed
		listenerManager.removeListener(waitListener);

		//Return requested listener
		return (E) finalEvent;
	}

	/**
	 * A listener that waits for the specified event before returning. Used in
	 * {@link PircBotX#waitFor(java.lang.Class) }
	 */
	protected class WaitListener implements Listener {
		protected CountDownLatch signal = new CountDownLatch(1);
		protected Class<? extends Event> eventClass;
		protected Event endEvent;

		public void onEvent(Event event) throws Exception {
			if (eventClass.isInstance(event)) {
				endEvent = event;

				//Unblock waitFor now that we have an event
				signal.countDown();
			}
		}

		/**
		 * Block until the Event represented by the given class is passed
		 * @param event A class representing the event to wait for
		 * @return The specified event
		 * @throws InterruptedException If the thread is interrupted, this exception
		 * is thrown
		 */
		public Event waitFor(Class<? extends Event> event) throws InterruptedException {
			eventClass = event;
			signal.await();
			return endEvent;
		}
	}

	protected class ListBuilder<A> {
		@Getter
		@Setter
		private boolean running = false;
		private Set<A> channels = new HashSet();

		public Set<A> finish() {
			running = false;
			Set<A> copy = new HashSet(channels);
			channels.clear();
			return copy;
		}

		public void add(A entry) {
			running = true;
			channels.add(entry);
		}
	}

	protected class UserChannelMap<C extends Channel, U extends User> extends ManyToManyMap<C, U> {
		public UserChannelMap() {
		}

		@Override
		public boolean put(C a, U b) {
			//Add to nick map
			if (b != null)
				userNickMap.put(b.getNick(), b);
			return super.put(a, b);
		}

		@Override
		public Set<C> deleteB(U b) {
			//Remove the Channels internal reference to the User
			synchronized (lockObject) {
				for (Channel curChan : BMap.get(b))
					curChan.removeUser(b);
			}
			//Remove from nick map
			userNickMap.remove(b.getNick());
			return super.deleteB(b);
		}

		@Override
		public boolean dissociate(C a, U b, boolean remove) {
			//Remove the Channels internal reference to the User
			a.removeUser(b);
			return super.dissociate(a, b, remove);
		}
	}
}
