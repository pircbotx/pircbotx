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

/**
 * This is a giant info bean of various things about the server. This is separate
 * from the {@link PircBotX} class due to its length
 * 
 * Most info thanks to <a href="www.irc.org/tech_docs/005.html">this great website
 * </a> on what each one does
 *
 * @author LordQuackstar
 */
public class ServerInfo {
	private PircBotX _bot;

	public ServerInfo(PircBotX bot) {
		_bot = bot;
	}

	public void parse(String input) {
	}
	//005 information
	private String _prefixes = "";
	private String _channelTypes = "";
	private String _channelModes = "";
	private int _modes;
	private int _maxChannels;
	private int _chanlimit;
	private int _maxNickLength;
	private int _maxBans;
	private String _maxList = "";
	private String _network = "";
	private String _exceptions = "";
	private String _invites = "";
	private boolean _wallChOps = false;
	private boolean _wallVoices = false;
	private String _statusMessage = "";
	private String _caseMapping = "";
	private String _EList = "";
	private int _topicLength;
	private int _kickLength;
	private int _channelLength;
	private int _channelIDLength;
	private String _standard = "";
	private int _silence;
	private boolean _RFC2812;
	private boolean _penalty;
	private boolean _forcedNickChanges;
	private boolean _safeList;
	private int _awayLength;
	private boolean _noQuit;
	private boolean _userIPExists;
	private boolean _cPrivMsgExists;
	private boolean _cNoticeExists;
	private int _maxTargets;
	private boolean _knockExists;
	private boolean _vChannels;
	private int _watchMax;
	private boolean _whoX;
	private boolean _callerID;
	private boolean _accept;
	private String _language = "";
	//Other information
	private String _motd = "";
	private int _highestConnections;
	private int _highestClients;
	private int _totalUsers;
	private int _totalInvisibleUsers;
	private int _totalServers;
	private int _totalOperatorsOnline;
	private int _totalUnknownConnections;
	private int _totalChannelsFormed;
	private int _serverUsers;
	private int _connectedServers;
}
