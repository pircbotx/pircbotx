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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * This is a giant info bean of various things about the server. This is separate
 * from the {@link PircBotX} class due to its length
 * 
 * Most info thanks to <a href="www.irc.org/tech_docs/005.html">this great website
 * </a> on what each one does
 *
 * @author  Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@Setter(AccessLevel.PACKAGE)
public class ServerInfo {
	protected final PircBotX bot;

	public void parse(int code, String input) {
		//Strip off name, irrelevant
		String[] parts = input.split(" ", 2);
		bot.log("---RECIEVED: " + input);
		
		//Pass off to speicific methods
		if(parts[0].equals("004"))
			parse004(parts[1]);
		else if(parts[0].equals("005"))
			parse005(parts[1]);
	}
	protected void parse004(String input) {
		//004 PircBotX pratchett.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
		String[] inputParts = input.split(" ");
		serverName = inputParts[0];
		serverVersion = inputParts[1];
		userModes = inputParts[2];
		channelModes = inputParts[3];
	}
	
	protected void parse005(String input) {
		//Freenode
		//005 PircBotX CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
		//005 PircBotX CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=16 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
		//005 PircBotX EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
		//Rizon
		//005 PircBotX CALLERID CASEMAPPING=rfc1459 DEAF=D KICKLEN=160 MODES=4 NICKLEN=30 TOPICLEN=390 PREFIX=(qaohv)~&@%+ STATUSMSG=~&@%+ NETWORK=Rizon MAXLIST=beI:100 TARGMAX=ACCEPT:,KICK:1,LIST:1,NAMES:1,NOTICE:4,PRIVMSG:4,WHOIS:1 CHANTYPES=# :are supported by this server
		//005 PircBotX CHANLIMIT=#:75 CHANNELLEN=50 CHANMODES=beI,k,l,BCMNORScimnpstz AWAYLEN=160 ELIST=CMNTU SAFELIST KNOCK NAMESX UHNAMES FNC EXCEPTS=e INVEX=I :are supported by this server
	}
	
	//004 information
	protected String serverName;
	protected String serverVersion;
	protected String userModes;
	
	//005 information
	protected String prefixes = "";
	protected String channelTypes = "";
	protected String channelModes = "";
	protected int modes;
	protected int maxChannels;
	protected int chanlimit;
	protected int maxNickLength;
	protected int maxBans;
	protected String maxList = "";
	protected String network = "";
	protected String exceptions = "";
	protected String invites = "";
	protected boolean wallChOps = false;
	protected boolean wallVoices = false;
	protected String statusMessage = "";
	protected String caseMapping = "";
	protected String EList = "";
	protected int topicLength;
	protected int kickLength;
	protected int channelLength;
	protected int channelIDLength;
	protected String standard = "";
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
	protected String language = "";
	//Other information
	protected String motd = "";
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
}