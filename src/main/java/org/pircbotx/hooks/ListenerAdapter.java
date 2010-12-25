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

package org.pircbotx.hooks;

import org.pircbotx.events.ActionEvent;
import org.pircbotx.events.ChannelInfoEvent;
import org.pircbotx.events.ConnectEvent;
import org.pircbotx.events.DeVoiceEvent;
import org.pircbotx.events.DeopEvent;
import org.pircbotx.events.DisconnectEvent;
import org.pircbotx.events.FileTransferFinishedEvent;
import org.pircbotx.events.FingerEvent;
import org.pircbotx.events.IncomingChatRequestEvent;
import org.pircbotx.events.IncomingFileTransferEvent;
import org.pircbotx.events.InviteEvent;
import org.pircbotx.events.JoinEvent;
import org.pircbotx.events.KickEvent;
import org.pircbotx.events.MessageEvent;
import org.pircbotx.events.ModeEvent;
import org.pircbotx.events.MotdEvent;
import org.pircbotx.events.NickChangeEvent;
import org.pircbotx.events.NoticeEvent;
import org.pircbotx.events.OpEvent;
import org.pircbotx.events.PartEvent;
import org.pircbotx.events.PingEvent;
import org.pircbotx.events.PrivateMessageEvent;
import org.pircbotx.events.QuitEvent;
import org.pircbotx.events.RemoveChannelBanEvent;
import org.pircbotx.events.RemoveChannelKeyEvent;
import org.pircbotx.events.RemoveChannelLimitEvent;
import org.pircbotx.events.RemoveInviteOnlyEvent;
import org.pircbotx.events.RemoveModeratedEvent;
import org.pircbotx.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.events.RemovePrivateEvent;
import org.pircbotx.events.RemoveSecretEvent;
import org.pircbotx.events.RemoveTopicProtectionEvent;
import org.pircbotx.events.ServerPingEvent;
import org.pircbotx.events.ServerResponseEvent;
import org.pircbotx.events.SetChannelBanEvent;
import org.pircbotx.events.SetChannelKeyEvent;
import org.pircbotx.events.SetChannelLimitEvent;
import org.pircbotx.events.SetInviteOnlyEvent;
import org.pircbotx.events.SetModeratedEvent;
import org.pircbotx.events.SetNoExternalMessagesEvent;
import org.pircbotx.events.SetPrivateEvent;
import org.pircbotx.events.SetSecretEvent;
import org.pircbotx.events.SetTopicProtectionEvent;
import org.pircbotx.events.TimeEvent;
import org.pircbotx.events.TopicEvent;
import org.pircbotx.events.UnknownEvent;
import org.pircbotx.events.UserListEvent;
import org.pircbotx.events.UserModeEvent;
import org.pircbotx.events.VersionEvent;
import org.pircbotx.events.VoiceEvent;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ListenerAdapter implements ListenerAdapterInterface {
	public void onAction(ActionEvent event) {
	}

	public void onChannelInfo(ChannelInfoEvent event) {
	}

	public void onConnect(ConnectEvent event) {
	}

	public void onDeop(DeopEvent event) {
	}

	public void onDeVoice(DeVoiceEvent event) {
	}

	public void onDisconnect(DisconnectEvent event) {
	}

	public void onFileTransferFinished(FileTransferFinishedEvent event) {
	}

	public void onFinger(FingerEvent event) {
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent event) {
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent event) {
	}

	public void onInvite(InviteEvent event) {
	}

	public void onJoin(JoinEvent event) {
	}

	public void onKick(KickEvent event) {
	}

	public void onMessage(MessageEvent event) {
	}

	public void onMode(ModeEvent event) {
	}

	public void onMotd(MotdEvent event) {
	}

	public void onNickChange(NickChangeEvent event) {
	}

	public void onNotice(NoticeEvent event) {
	}

	public void onOp(OpEvent event) {
	}

	public void onPart(PartEvent event) {
	}

	public void onPing(PingEvent event) {
	}

	public void onPrivateMessage(PrivateMessageEvent event) {
	}

	public void onQuit(QuitEvent event) {
	}

	public void onRemoveChannelBan(RemoveChannelBanEvent event) {
	}

	public void onRemoveChannelKey(RemoveChannelKeyEvent event) {
	}

	public void onRemoveChannelLimit(RemoveChannelLimitEvent event) {
	}

	public void onRemoveInviteOnly(RemoveInviteOnlyEvent event) {
	}

	public void onRemoveModerated(RemoveModeratedEvent event) {
	}

	public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent event) {
	}

	public void onRemovePrivate(RemovePrivateEvent event) {
	}

	public void onRemoveSecret(RemoveSecretEvent event) {
	}

	public void onRemoveTopicProtection(RemoveTopicProtectionEvent event) {
	}

	public void onServerPing(ServerPingEvent event) {
	}

	public void onServerResponse(ServerResponseEvent event) {
	}

	public void onSetChannelBan(SetChannelBanEvent event) {
	}

	public void onSetChannelKey(SetChannelKeyEvent event) {
	}

	public void onSetChannelLimit(SetChannelLimitEvent event) {
	}

	public void onSetInviteOnly(SetInviteOnlyEvent event) {
	}

	public void onSetModerated(SetModeratedEvent event) {
	}

	public void onSetNoExternalMessages(SetNoExternalMessagesEvent event) {
	}

	public void onSetPrivate(SetPrivateEvent event) {
	}

	public void onSetSecret(SetSecretEvent event) {
	}

	public void onSetTopicProtection(SetTopicProtectionEvent event) {
	}

	public void onTime(TimeEvent event) {
	}

	public void onTopic(TopicEvent event) {
	}

	public void onUnknown(UnknownEvent event) {
	}

	public void onUserList(UserListEvent event) {
	}

	public void onUserMode(UserModeEvent event) {
	}

	public void onVersion(VersionEvent event) {
	}

	public void onVoice(VoiceEvent event) {
	}
}
