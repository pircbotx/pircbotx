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
 * @author  Leon Blakey <lord.quackstar at gmail.com>
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
	private String motd;

	/**
	 * @return the _bot
	 */
	public PircBotX getBot() {
		return _bot;
	}

	/**
	 * @param bot the _bot to set
	 */
	void setBot(PircBotX bot) {
		this._bot = bot;
	}

	/**
	 * @return the _prefixes
	 */
	public String getPrefixes() {
		return _prefixes;
	}

	/**
	 * @param prefixes the _prefixes to set
	 */
	void setPrefixes(String prefixes) {
		this._prefixes = prefixes;
	}

	/**
	 * @return the _channelTypes
	 */
	public String getChannelTypes() {
		return _channelTypes;
	}

	/**
	 * @param channelTypes the _channelTypes to set
	 */
	void setChannelTypes(String channelTypes) {
		this._channelTypes = channelTypes;
	}

	/**
	 * @return the _channelModes
	 */
	public String getChannelModes() {
		return _channelModes;
	}

	/**
	 * @param channelModes the _channelModes to set
	 */
	void setChannelModes(String channelModes) {
		this._channelModes = channelModes;
	}

	/**
	 * @return the _modes
	 */
	public int getModes() {
		return _modes;
	}

	/**
	 * @param modes the _modes to set
	 */
	void setModes(int modes) {
		this._modes = modes;
	}

	/**
	 * @return the _maxChannels
	 */
	public int getMaxChannels() {
		return _maxChannels;
	}

	/**
	 * @param maxChannels the _maxChannels to set
	 */
	void setMaxChannels(int maxChannels) {
		this._maxChannels = maxChannels;
	}

	/**
	 * @return the _chanlimit
	 */
	public int getChanlimit() {
		return _chanlimit;
	}

	/**
	 * @param chanlimit the _chanlimit to set
	 */
	void setChanlimit(int chanlimit) {
		this._chanlimit = chanlimit;
	}

	/**
	 * @return the _maxNickLength
	 */
	public int getMaxNickLength() {
		return _maxNickLength;
	}

	/**
	 * @param maxNickLength the _maxNickLength to set
	 */
	void setMaxNickLength(int maxNickLength) {
		this._maxNickLength = maxNickLength;
	}

	/**
	 * @return the _maxBans
	 */
	public int getMaxBans() {
		return _maxBans;
	}

	/**
	 * @param maxBans the _maxBans to set
	 */
	void setMaxBans(int maxBans) {
		this._maxBans = maxBans;
	}

	/**
	 * @return the _maxList
	 */
	public String getMaxList() {
		return _maxList;
	}

	/**
	 * @param maxList the _maxList to set
	 */
	void setMaxList(String maxList) {
		this._maxList = maxList;
	}

	/**
	 * @return the _network
	 */
	public String getNetwork() {
		return _network;
	}

	/**
	 * @param network the _network to set
	 */
	void setNetwork(String network) {
		this._network = network;
	}

	/**
	 * @return the _exceptions
	 */
	public String getExceptions() {
		return _exceptions;
	}

	/**
	 * @param exceptions the _exceptions to set
	 */
	void setExceptions(String exceptions) {
		this._exceptions = exceptions;
	}

	/**
	 * @return the _invites
	 */
	public String getInvites() {
		return _invites;
	}

	/**
	 * @param invites the _invites to set
	 */
	void setInvites(String invites) {
		this._invites = invites;
	}

	/**
	 * @return the _wallChOps
	 */
	public boolean isWallChOps() {
		return _wallChOps;
	}

	/**
	 * @param wallChOps the _wallChOps to set
	 */
	void setWallChOps(boolean wallChOps) {
		this._wallChOps = wallChOps;
	}

	/**
	 * @return the _wallVoices
	 */
	public boolean isWallVoices() {
		return _wallVoices;
	}

	/**
	 * @param wallVoices the _wallVoices to set
	 */
	void setWallVoices(boolean wallVoices) {
		this._wallVoices = wallVoices;
	}

	/**
	 * @return the _statusMessage
	 */
	public String getStatusMessage() {
		return _statusMessage;
	}

	/**
	 * @param statusMessage the _statusMessage to set
	 */
	void setStatusMessage(String statusMessage) {
		this._statusMessage = statusMessage;
	}

	/**
	 * @return the _caseMapping
	 */
	public String getCaseMapping() {
		return _caseMapping;
	}

	/**
	 * @param caseMapping the _caseMapping to set
	 */
	void setCaseMapping(String caseMapping) {
		this._caseMapping = caseMapping;
	}

	/**
	 * @return the _EList
	 */
	public String getEList() {
		return _EList;
	}

	/**
	 * @param EList the _EList to set
	 */
	void setEList(String EList) {
		this._EList = EList;
	}

	/**
	 * @return the _topicLength
	 */
	public int getTopicLength() {
		return _topicLength;
	}

	/**
	 * @param topicLength the _topicLength to set
	 */
	void setTopicLength(int topicLength) {
		this._topicLength = topicLength;
	}

	/**
	 * @return the _kickLength
	 */
	public int getKickLength() {
		return _kickLength;
	}

	/**
	 * @param kickLength the _kickLength to set
	 */
	void setKickLength(int kickLength) {
		this._kickLength = kickLength;
	}

	/**
	 * @return the _channelLength
	 */
	public int getChannelLength() {
		return _channelLength;
	}

	/**
	 * @param channelLength the _channelLength to set
	 */
	void setChannelLength(int channelLength) {
		this._channelLength = channelLength;
	}

	/**
	 * @return the _channelIDLength
	 */
	public int getChannelIDLength() {
		return _channelIDLength;
	}

	/**
	 * @param channelIDLength the _channelIDLength to set
	 */
	void setChannelIDLength(int channelIDLength) {
		this._channelIDLength = channelIDLength;
	}

	/**
	 * @return the _standard
	 */
	public String getStandard() {
		return _standard;
	}

	/**
	 * @param standard the _standard to set
	 */
	void setStandard(String standard) {
		this._standard = standard;
	}

	/**
	 * @return the _silence
	 */
	public int getSilence() {
		return _silence;
	}

	/**
	 * @param silence the _silence to set
	 */
	void setSilence(int silence) {
		this._silence = silence;
	}

	/**
	 * @return the _RFC2812
	 */
	public boolean isRFC2812() {
		return _RFC2812;
	}

	/**
	 * @param RFC2812 the _RFC2812 to set
	 */
	void setRFC2812(boolean RFC2812) {
		this._RFC2812 = RFC2812;
	}

	/**
	 * @return the _penalty
	 */
	public boolean isPenalty() {
		return _penalty;
	}

	/**
	 * @param penalty the _penalty to set
	 */
	void setPenalty(boolean penalty) {
		this._penalty = penalty;
	}

	/**
	 * @return the _forcedNickChanges
	 */
	public boolean isForcedNickChanges() {
		return _forcedNickChanges;
	}

	/**
	 * @param forcedNickChanges the _forcedNickChanges to set
	 */
	void setForcedNickChanges(boolean forcedNickChanges) {
		this._forcedNickChanges = forcedNickChanges;
	}

	/**
	 * @return the _safeList
	 */
	public boolean isSafeList() {
		return _safeList;
	}

	/**
	 * @param safeList the _safeList to set
	 */
	void setSafeList(boolean safeList) {
		this._safeList = safeList;
	}

	/**
	 * @return the _awayLength
	 */
	public int getAwayLength() {
		return _awayLength;
	}

	/**
	 * @param awayLength the _awayLength to set
	 */
	void setAwayLength(int awayLength) {
		this._awayLength = awayLength;
	}

	/**
	 * @return the _noQuit
	 */
	public boolean isNoQuit() {
		return _noQuit;
	}

	/**
	 * @param noQuit the _noQuit to set
	 */
	void setNoQuit(boolean noQuit) {
		this._noQuit = noQuit;
	}

	/**
	 * @return the _userIPExists
	 */
	public boolean isUserIPExists() {
		return _userIPExists;
	}

	/**
	 * @param userIPExists the _userIPExists to set
	 */
	void setUserIPExists(boolean userIPExists) {
		this._userIPExists = userIPExists;
	}

	/**
	 * @return the _cPrivMsgExists
	 */
	public boolean iscPrivMsgExists() {
		return _cPrivMsgExists;
	}

	/**
	 * @param cPrivMsgExists the _cPrivMsgExists to set
	 */
	void setcPrivMsgExists(boolean cPrivMsgExists) {
		this._cPrivMsgExists = cPrivMsgExists;
	}

	/**
	 * @return the _cNoticeExists
	 */
	public boolean iscNoticeExists() {
		return _cNoticeExists;
	}

	/**
	 * @param cNoticeExists the _cNoticeExists to set
	 */
	void setcNoticeExists(boolean cNoticeExists) {
		this._cNoticeExists = cNoticeExists;
	}

	/**
	 * @return the _maxTargets
	 */
	public int getMaxTargets() {
		return _maxTargets;
	}

	/**
	 * @param maxTargets the _maxTargets to set
	 */
	void setMaxTargets(int maxTargets) {
		this._maxTargets = maxTargets;
	}

	/**
	 * @return the _knockExists
	 */
	public boolean isKnockExists() {
		return _knockExists;
	}

	/**
	 * @param knockExists the _knockExists to set
	 */
	void setKnockExists(boolean knockExists) {
		this._knockExists = knockExists;
	}

	/**
	 * @return the _vChannels
	 */
	public boolean isvChannels() {
		return _vChannels;
	}

	/**
	 * @param vChannels the _vChannels to set
	 */
	void setvChannels(boolean vChannels) {
		this._vChannels = vChannels;
	}

	/**
	 * @return the _watchMax
	 */
	public int getWatchMax() {
		return _watchMax;
	}

	/**
	 * @param watchMax the _watchMax to set
	 */
	void setWatchMax(int watchMax) {
		this._watchMax = watchMax;
	}

	/**
	 * @return the _whoX
	 */
	public boolean isWhoX() {
		return _whoX;
	}

	/**
	 * @param whoX the _whoX to set
	 */
	void setWhoX(boolean whoX) {
		this._whoX = whoX;
	}

	/**
	 * @return the _callerID
	 */
	public boolean isCallerID() {
		return _callerID;
	}

	/**
	 * @param callerID the _callerID to set
	 */
	void setCallerID(boolean callerID) {
		this._callerID = callerID;
	}

	/**
	 * @return the _accept
	 */
	public boolean isAccept() {
		return _accept;
	}

	/**
	 * @param accept the _accept to set
	 */
	void setAccept(boolean accept) {
		this._accept = accept;
	}

	/**
	 * @return the _language
	 */
	public String getLanguage() {
		return _language;
	}

	/**
	 * @param language the _language to set
	 */
	void setLanguage(String language) {
		this._language = language;
	}

	/**
	 * @return the _motd
	 */
	public String getMotd() {
		return _motd;
	}

	/**
	 * @param motd the _motd to set
	 */
	void setMotd(String motd) {
		this._motd = motd;
	}

	/**
	 * @return the _highestConnections
	 */
	public int getHighestConnections() {
		return _highestConnections;
	}

	/**
	 * @param highestConnections the _highestConnections to set
	 */
	void setHighestConnections(int highestConnections) {
		this._highestConnections = highestConnections;
	}

	/**
	 * @return the _highestClients
	 */
	public int getHighestClients() {
		return _highestClients;
	}

	/**
	 * @param highestClients the _highestClients to set
	 */
	void setHighestClients(int highestClients) {
		this._highestClients = highestClients;
	}

	/**
	 * @return the _totalUsers
	 */
	public int getTotalUsers() {
		return _totalUsers;
	}

	/**
	 * @param totalUsers the _totalUsers to set
	 */
	void setTotalUsers(int totalUsers) {
		this._totalUsers = totalUsers;
	}

	/**
	 * @return the _totalInvisibleUsers
	 */
	public int getTotalInvisibleUsers() {
		return _totalInvisibleUsers;
	}

	/**
	 * @param totalInvisibleUsers the _totalInvisibleUsers to set
	 */
	void setTotalInvisibleUsers(int totalInvisibleUsers) {
		this._totalInvisibleUsers = totalInvisibleUsers;
	}

	/**
	 * @return the _totalServers
	 */
	public int getTotalServers() {
		return _totalServers;
	}

	/**
	 * @param totalServers the _totalServers to set
	 */
	void setTotalServers(int totalServers) {
		this._totalServers = totalServers;
	}

	/**
	 * @return the _totalOperatorsOnline
	 */
	public int getTotalOperatorsOnline() {
		return _totalOperatorsOnline;
	}

	/**
	 * @param totalOperatorsOnline the _totalOperatorsOnline to set
	 */
	void setTotalOperatorsOnline(int totalOperatorsOnline) {
		this._totalOperatorsOnline = totalOperatorsOnline;
	}

	/**
	 * @return the _totalUnknownConnections
	 */
	public int getTotalUnknownConnections() {
		return _totalUnknownConnections;
	}

	/**
	 * @param totalUnknownConnections the _totalUnknownConnections to set
	 */
	void setTotalUnknownConnections(int totalUnknownConnections) {
		this._totalUnknownConnections = totalUnknownConnections;
	}

	/**
	 * @return the _totalChannelsFormed
	 */
	public int getTotalChannelsFormed() {
		return _totalChannelsFormed;
	}

	/**
	 * @param totalChannelsFormed the _totalChannelsFormed to set
	 */
	void setTotalChannelsFormed(int totalChannelsFormed) {
		this._totalChannelsFormed = totalChannelsFormed;
	}

	/**
	 * @return the _serverUsers
	 */
	public int getServerUsers() {
		return _serverUsers;
	}

	/**
	 * @param serverUsers the _serverUsers to set
	 */
	void setServerUsers(int serverUsers) {
		this._serverUsers = serverUsers;
	}

	/**
	 * @return the _connectedServers
	 */
	public int getConnectedServers() {
		return _connectedServers;
	}

	/**
	 * @param connectedServers the _connectedServers to set
	 */
	void setConnectedServers(int connectedServers) {
		this._connectedServers = connectedServers;
	}
}
