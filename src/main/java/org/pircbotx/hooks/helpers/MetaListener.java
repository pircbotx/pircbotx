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

import org.pircbotx.hooks.Action;
import org.pircbotx.hooks.ChannelInfo;
import org.pircbotx.hooks.Connect;
import org.pircbotx.hooks.DeVoice;
import org.pircbotx.hooks.Deop;
import org.pircbotx.hooks.Disconnect;
import org.pircbotx.hooks.FileTransferFinished;
import org.pircbotx.hooks.Finger;
import org.pircbotx.hooks.IncomingChatRequest;
import org.pircbotx.hooks.IncomingFileTransfer.Event;
import org.pircbotx.hooks.Invite;
import org.pircbotx.hooks.Join;
import org.pircbotx.hooks.Kick;
import org.pircbotx.hooks.Message;
import org.pircbotx.hooks.Mode;
import org.pircbotx.hooks.NickChange;
import org.pircbotx.hooks.Notice;
import org.pircbotx.hooks.Op;
import org.pircbotx.hooks.Part;
import org.pircbotx.hooks.Ping;
import org.pircbotx.hooks.PrivateMessage;
import org.pircbotx.hooks.Quit;
import org.pircbotx.hooks.RemoveChannelBan;
import org.pircbotx.hooks.RemoveChannelKey;
import org.pircbotx.hooks.RemoveChannelLimit;
import org.pircbotx.hooks.RemoveInviteOnly;
import org.pircbotx.hooks.RemoveModerated;
import org.pircbotx.hooks.RemoveNoExternalMessages;
import org.pircbotx.hooks.RemovePrivate;
import org.pircbotx.hooks.RemoveSecret;
import org.pircbotx.hooks.RemoveTopicProtection;
import org.pircbotx.hooks.ServerPing;
import org.pircbotx.hooks.ServerResponse;
import org.pircbotx.hooks.SetChannelBan;
import org.pircbotx.hooks.SetChannelKey;
import org.pircbotx.hooks.SetChannelLimit;
import org.pircbotx.hooks.SetInviteOnly;
import org.pircbotx.hooks.SetModerated;
import org.pircbotx.hooks.SetNoExternalMessages;
import org.pircbotx.hooks.SetPrivate;
import org.pircbotx.hooks.SetSecret;
import org.pircbotx.hooks.SetTopicProtection;
import org.pircbotx.hooks.Time;
import org.pircbotx.hooks.Topic;
import org.pircbotx.hooks.Unknown;
import org.pircbotx.hooks.UserList;
import org.pircbotx.hooks.UserMode;
import org.pircbotx.hooks.Version;
import org.pircbotx.hooks.Voice;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MetaListener implements MetaListenerInterface {
	public void onAction(Action.Event event) {
	}

	public void onChannelInfo(ChannelInfo.Event event) {
	}

	public void onConnect(Connect.Event event) {
	}

	public void onDeop(Deop.Event event) {
	}

	public void onDeVoice(DeVoice.Event event) {
	}

	public void onDisconnect(Disconnect.Event event) {
	}

	public void onFileTransferFinished(FileTransferFinished.Event event) {
	}

	public void onFinger(Finger.Event event) {
	}

	public void onIncomingChatRequest(IncomingChatRequest.Event event) {
	}

	public void onInvite(Invite.Event event) {
	}

	public void onJoin(Join.Event event) {
	}

	public void onKick(Kick.Event event) {
	}

	public void onMessage(Message.Event event) {
	}

	public void onMode(Mode.Event event) {
	}

	public void onNickChange(NickChange.Event event) {
	}

	public void onNotice(Notice.Event event) {
	}

	public void onOp(Op.Event event) {
	}

	public void onPart(Part.Event event) {
	}

	public void onPing(Ping.Event event) {
	}

	public void onPrivateMessage(PrivateMessage.Event event) {
	}

	public void onQuit(Quit.Event event) {
	}

	public void onRemoveChannelBan(RemoveChannelBan.Event event) {
	}

	public void onRemoveChannelKey(RemoveChannelKey.Event event) {
	}

	public void onRemoveChannelLimit(RemoveChannelLimit.Event event) {
	}

	public void onRemoveInviteOnly(RemoveInviteOnly.Event event) {
	}

	public void onRemoveModerated(RemoveModerated.Event event) {
	}

	public void onRemoveNoExternalMessages(RemoveNoExternalMessages.Event event) {
	}

	public void onRemovePrivate(RemovePrivate.Event event) {
	}

	public void onRemoveSecret(RemoveSecret.Event event) {
	}

	public void onRemoveTopicProtection(RemoveTopicProtection.Event event) {
	}

	public void onServerPing(ServerPing.Event event) {
	}

	public void onServerResponse(ServerResponse.Event event) {
	}

	public void onSetChannelBan(SetChannelBan.Event event) {
	}

	public void onSetChannelKey(SetChannelKey.Event event) {
	}

	public void onSetChannelLimit(SetChannelLimit.Event event) {
	}

	public void onSetInviteOnly(SetInviteOnly.Event event) {
	}

	public void onSetModerated(SetModerated.Event event) {
	}

	public void onSetNoExternalMessages(SetNoExternalMessages.Event event) {
	}

	public void onSetPrivate(SetPrivate.Event event) {
	}

	public void onSetSecret(SetSecret.Event event) {
	}

	public void onSetTopicProtection(SetTopicProtection.Event event) {
	}

	public void onTime(Time.Event event) {
	}

	public void onTopic(Topic.Event event) {
	}

	public void onUnknown(Unknown.Event event) {
	}

	public void onUserList(UserList.Event event) {
	}

	public void onUserMode(UserMode.Event event) {
	}

	public void onVersion(Version.Event event) {
	}

	public void onVoice(Voice.Event event) {
	}

	public void onIncomingFileTransfer(Event event) {
	}
}
