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

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * This is a giant info bean of various things about the server. This is separate
 * from the {@link PircBotX} class due to its length
 * <p/>
 * Most info thanks to <a href="www.irc.org/tech_docs/005.html">this great website
 * </a> on what each one does
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@Setter(AccessLevel.NONE)
public class ServerInfo {
	protected final PircBotX bot;
	//004 information
	protected String serverName;
	protected String serverVersion;
	protected String userModes;
	//005 information
	protected LinkedHashMap<String, String> isupportRaw = new LinkedHashMap<String, String>();
	protected String prefixes;
	protected String channelTypes;
	protected String channelModes;
	protected int maxModes;
	protected int maxChannels;
	protected String chanlimit;
	protected int maxNickLength;
	protected int maxBans;
	protected ImmutableMap<String, Integer> maxList;
	protected String network;
	protected String exceptBans;
	protected String exceptInvites;
	protected String invites;
	protected boolean wallOps;
	protected boolean wallVoices;
	protected String statusMessage;
	protected String caseMapping;
	protected String eList;
	protected int topicLength;
	protected int kickLength;
	protected int channelLength;
	protected String channelIDLength;
	protected String standard;
	protected int silence;
	protected boolean RFC2812;
	protected boolean penalty;
	protected boolean forcedNickChanges;
	protected boolean safeList;
	protected int awayLength;
	protected boolean noQuit;
	protected boolean userIPExists;
	protected boolean cPrivMsgExists;
	protected boolean cNoticeExists;
	protected int maxTargets;
	protected boolean knockExists;
	protected boolean vChannels;
	protected int watchMax;
	protected boolean whoX;
	protected boolean callerID;
	protected boolean accept;
	protected String language;
	//Other information
	@Setter(AccessLevel.PROTECTED)
	protected String motd;
	protected int highestConnections;
	protected int highestClients;
	protected int totalUsers;
	protected int totalInvisibleUsers;
	protected int totalServers;
	protected int totalOperatorsOnline;
	protected int totalUnknownConnections;
	protected int totalChannelsFormed;
	protected int serverUsers;
	protected int connectedServers;

	public void parse(int code, List<String> parsedLine) {
		//Pass off to speicific methods
		if (code == 004)
			parse004(parsedLine);
		else if (code == 005)
			parse005(parsedLine);
	}

	protected void parse004(List<String> parsedLine) {
		//004 PircBotX pratchett.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
		serverName = parsedLine.get(1);
		serverVersion = parsedLine.get(2);
		userModes = parsedLine.get(3);
		channelModes = parsedLine.get(4);
	}

	protected void parse005(List<String> parsedLine) {
		//REFERENCE: http://www.irc.org/tech_docs/005.html
		for (String curItem : parsedLine) {
			String[] itemParts = curItem.split("=", 2);
			String key = itemParts[0];
			String value = (itemParts.length == 2) ? itemParts[1] : "";
			isupportRaw.put(key, value);
			if (key.equalsIgnoreCase("PREFIX"))
				prefixes = value;
			else if (key.equalsIgnoreCase("CHANTYPES"))
				channelTypes = value;
			else if (key.equalsIgnoreCase("CHANMODES"))
				channelModes = value;
			else if (key.equalsIgnoreCase("MODES"))
				maxModes = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("MAXCHANNELS"))
				maxChannels = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("CHANLIMIT"))
				chanlimit = value;
			else if (key.equalsIgnoreCase("NICKLEN"))
				maxNickLength = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("MAXBANS"))
				maxBans = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("MAXLIST")) {
				StringTokenizer maxListTokens = new StringTokenizer(value, ":,");
				ImmutableMap.Builder<String, Integer> maxListBuilder = ImmutableMap.builder();
				while (maxListTokens.hasMoreTokens())
					maxListBuilder.put(maxListTokens.nextToken(), Integer.parseInt(maxListTokens.nextToken()));
				maxList = maxListBuilder.build();
			} else if (key.equalsIgnoreCase("NETWORK"))
				network = value;
			else if (key.equalsIgnoreCase("EXCEPTS"))
				exceptBans = value;
			else if (key.equalsIgnoreCase("INVEX"))
				exceptInvites = value;
			else if (key.equalsIgnoreCase("WALLCHOPS"))
				wallOps = true;
			else if (key.equalsIgnoreCase("WALLVOICES"))
				wallVoices = true;
			else if (key.equalsIgnoreCase("STATUSMSG"))
				statusMessage = value;
			else if (key.equalsIgnoreCase("CASEMAPPING"))
				caseMapping = value;
			else if (key.equalsIgnoreCase("ELIST"))
				eList = value;
			else if (key.equalsIgnoreCase("TOPICLEN"))
				topicLength = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("KICKLEN"))
				kickLength = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("CHANNELLEN"))
				channelLength = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("CHIDLEN"))
				channelIDLength = "!:" + Integer.parseInt(value);
			else if (key.equalsIgnoreCase("IDCHAN"))
				channelIDLength = value;
			else if (key.equalsIgnoreCase("STD"))
				standard = value;
			else if (key.equalsIgnoreCase("SILENCE"))
				silence = Integer.parseInt(value);
			else if (key.equalsIgnoreCase("RFC2812"))
				RFC2812 = true;
			else if (key.equalsIgnoreCase("PENALTY"))
				penalty = true;
			else if (key.equalsIgnoreCase("CPRIVMSG"))
				cPrivMsgExists = true;
			else if (key.equalsIgnoreCase("CNOTICE"))
				cNoticeExists = true;
			else if (key.equalsIgnoreCase("SAFELIST"))
				safeList = true;
			else if (key.equalsIgnoreCase("KNOCK"))
				knockExists = true;
			else if (key.equalsIgnoreCase("WHOX"))
				whoX = true;
			else if (key.equalsIgnoreCase("CALLERID") || key.equalsIgnoreCase("ACCEPT"))
				callerID = true;
			else if (key.equalsIgnoreCase("USERIP"))
				userIPExists = true;
			else if (key.equalsIgnoreCase("CNOTICE"))
				cNoticeExists = true;
		}
		//Freenode
		//005 PircBotX CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
		//005 PircBotX CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=16 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
		//005 PircBotX EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
		//Rizon
		//005 PircBotX CALLERID CASEMAPPING=rfc1459 DEAF=D KICKLEN=160 MODES=4 NICKLEN=30 TOPICLEN=390 PREFIX=(qaohv)~&@%+ STATUSMSG=~&@%+ NETWORK=Rizon MAXLIST=beI:100 TARGMAX=ACCEPT:,KICK:1,LIST:1,NAMES:1,NOTICE:4,PRIVMSG:4,WHOIS:1 CHANTYPES=# :are supported by this server
		//005 PircBotX CHANLIMIT=#:75 CHANNELLEN=50 CHANMODES=beI,k,l,BCMNORScimnpstz AWAYLEN=160 ELIST=CMNTU SAFELIST KNOCK NAMESX UHNAMES FNC EXCEPTS=e INVEX=I :are supported by this server
	}
	
	/**
	 * Get all supported server options as a map. Be careful about calling this
	 * very early in the connection phase as we might not of received all the 005
	 * lines yet
	 * @return An <i>immutable copy</i> of the current supported options
	 */
	public ImmutableMap<String, String> getIsupportRaw() {
		return ImmutableMap.copyOf(isupportRaw);
	}
}
