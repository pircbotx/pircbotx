/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
public class ServerInfo {
	private PircBotX _bot;

	public ServerInfo(PircBotX bot) {
		_bot = bot;
	}

	public void parse(String input) {
	}
	//005 information
	@Setter(AccessLevel.PACKAGE)
	private String prefixes = "";
	@Setter(AccessLevel.PACKAGE)
	private String channelTypes = "";
	@Setter(AccessLevel.PACKAGE)
	private String channelModes = "";
	@Setter(AccessLevel.PACKAGE)
	private int modes;
	@Setter(AccessLevel.PACKAGE)
	private int maxChannels;
	@Setter(AccessLevel.PACKAGE)
	private int chanlimit;
	@Setter(AccessLevel.PACKAGE)
	private int maxNickLength;
	@Setter(AccessLevel.PACKAGE)
	private int maxBans;
	@Setter(AccessLevel.PACKAGE)
	private String maxList = "";
	@Setter(AccessLevel.PACKAGE)
	private String network = "";
	@Setter(AccessLevel.PACKAGE)
	private String exceptions = "";
	@Setter(AccessLevel.PACKAGE)
	private String invites = "";
	@Setter(AccessLevel.PACKAGE)
	private boolean wallChOps = false;
	@Setter(AccessLevel.PACKAGE)
	private boolean wallVoices = false;
	@Setter(AccessLevel.PACKAGE)
	private String statusMessage = "";
	@Setter(AccessLevel.PACKAGE)
	private String caseMapping = "";
	@Setter(AccessLevel.PACKAGE)
	private String EList = "";
	@Setter(AccessLevel.PACKAGE)
	private int topicLength;
	@Setter(AccessLevel.PACKAGE)
	private int kickLength;
	@Setter(AccessLevel.PACKAGE)
	private int channelLength;
	@Setter(AccessLevel.PACKAGE)
	private int channelIDLength;
	@Setter(AccessLevel.PACKAGE)
	private String standard = "";
	@Setter(AccessLevel.PACKAGE)
	private int silence;
	@Setter(AccessLevel.PACKAGE)
	private boolean RFC2812;
	@Setter(AccessLevel.PACKAGE)
	private boolean penalty;
	@Setter(AccessLevel.PACKAGE)
	private boolean forcedNickChanges;
	@Setter(AccessLevel.PACKAGE)
	private boolean safeList;
	@Setter(AccessLevel.PACKAGE)
	private int awayLength;
	@Setter(AccessLevel.PACKAGE)
	private boolean noQuit;
	@Setter(AccessLevel.PACKAGE)
	private boolean userIPExists;
	@Setter(AccessLevel.PACKAGE)
	private boolean cPrivMsgExists;
	@Setter(AccessLevel.PACKAGE)
	private boolean cNoticeExists;
	@Setter(AccessLevel.PACKAGE)
	private int maxTargets;
	@Setter(AccessLevel.PACKAGE)
	private boolean knockExists;
	@Setter(AccessLevel.PACKAGE)
	private boolean vChannels;
	@Setter(AccessLevel.PACKAGE)
	private int watchMax;
	@Setter(AccessLevel.PACKAGE)
	private boolean whoX;
	@Setter(AccessLevel.PACKAGE)
	private boolean callerID;
	@Setter(AccessLevel.PACKAGE)
	private boolean accept;
	@Setter(AccessLevel.PACKAGE)
	private String language = "";
	
	//Other information
	@Setter(AccessLevel.PACKAGE)
	private String motd = "";
	@Setter(AccessLevel.PACKAGE)
	private int highestConnections;
	@Setter(AccessLevel.PACKAGE)
	private int highestClients;
	@Setter(AccessLevel.PACKAGE)
	private int totalUsers;
	@Setter(AccessLevel.PACKAGE)
	private int totalInvisibleUsers;
	@Setter(AccessLevel.PACKAGE)
	private int totalServers;
	@Setter(AccessLevel.PACKAGE)
	private int totalOperatorsOnline;
	@Setter(AccessLevel.PACKAGE)
	private int totalUnknownConnections;
	@Setter(AccessLevel.PACKAGE)
	private int totalChannelsFormed;
	@Setter(AccessLevel.PACKAGE)
	private int serverUsers;
	@Setter(AccessLevel.PACKAGE)
	private int connectedServers;
}