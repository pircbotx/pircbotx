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

package org.pircbotx.hooks.helpers;

import java.util.Set;
import org.pircbotx.DccChat;
import org.pircbotx.DccFileTransfer;
import org.pircbotx.User;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MetaSimpleListener implements MetaSimpleListenerInterface {
	public void onAction(String sender, String login, String hostname, String target, String action) {
	}

	public void onChannelInfo(String channel, int userCount, String topic) {
	}

	public void onConnect() {
	}

	public void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
	}

	public void onDeVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
	}

	public void onDisconnect() {
	}

	public void onFileTransferFinished(DccFileTransfer transfer, Exception e) {
	}

	public void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
	}

	public void onIncomingChatRequest(DccChat chat) {
	}

	public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
	}

	public void onJoin(String channel, String sender, String login, String hostname) {
	}

	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
	}

	public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
	}

	public void onNickChange(String oldNick, String login, String hostname, String newNick) {
	}

	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
	}

	public void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
	}

	public void onPart(String channel, String sender, String login, String hostname) {
	}

	public void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
	}

	public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
	}

	public void onRemoveChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask) {
	}

	public void onRemoveChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key) {
	}

	public void onRemoveChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemoveInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemoveModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemoveNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemovePrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemoveSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onRemoveTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onServerPing(String response) {
	}

	public void onServerResponse(int code, String response) {
	}

	public void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask) {
	}

	public void onSetChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String key) {
	}

	public void onSetChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname, int limit) {
	}

	public void onSetInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onSetModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onSetNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onSetPrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onSetSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onSetTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
	}

	public void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
	}

	public void onTopic(String channel, String topic, String setBy, boolean changed) {
	}

	public void onUnknown(String line) {
	}

	public void onUserList(String channel, Set<User> users) {
	}

	public void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
	}

	public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
	}

	public void onVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
	}
}
