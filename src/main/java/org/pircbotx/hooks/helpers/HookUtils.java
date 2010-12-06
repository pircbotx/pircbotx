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

import org.pircbotx.exception.UnknownHookException;
import org.pircbotx.hooks.Action;
import org.pircbotx.hooks.ChannelInfo;
import org.pircbotx.hooks.Connect;
import org.pircbotx.hooks.DeVoice;
import org.pircbotx.hooks.Deop;
import org.pircbotx.hooks.Disconnect;
import org.pircbotx.hooks.FileTransferFinished;
import org.pircbotx.hooks.Finger;
import org.pircbotx.hooks.IncomingChatRequest;
import org.pircbotx.hooks.IncomingFileTransfer;
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
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class HookUtils {
	/**
	 * Using the provided event, call the appropriate method on the target object
	 * @param baseEvent
	 * @param targetObj 
	 */
	public static void callSimpleListener(BaseEvent baseEvent, Object targetObj) throws UnknownHookException {
		//Yes, this is ugly and makes me ashamed. But its whats nessesary to get the job done
		if (baseEvent instanceof Action.Event && targetObj instanceof Action.SimpleListener) {
			Action.Event e = (Action.Event) baseEvent;
			((Action.SimpleListener) targetObj).onAction(e.getSource(), e.getTarget(), e.getAction());
		} else if (baseEvent instanceof ChannelInfo.Event && targetObj instanceof ChannelInfo.SimpleListener) {
			ChannelInfo.Event e = (ChannelInfo.Event) baseEvent;
			((ChannelInfo.SimpleListener) targetObj).onChannelInfo(e.getList());
		} else if (baseEvent instanceof Connect.Event && targetObj instanceof Connect.SimpleListener)
			((Connect.SimpleListener) targetObj).onConnect();
		else if (baseEvent instanceof Deop.Event && targetObj instanceof Deop.SimpleListener) {
			Deop.Event e = (Deop.Event) baseEvent;
			((Deop.SimpleListener) targetObj).onDeop(e.getChannel(), e.getSource(), e.getRecipient());
		} else if (baseEvent instanceof DeVoice.Event && targetObj instanceof DeVoice.SimpleListener) {
			DeVoice.Event e = (DeVoice.Event) baseEvent;
			((DeVoice.SimpleListener) targetObj).onDeVoice(e.getChannel(), e.getSource(), e.getRecipient());
		} else if (baseEvent instanceof Disconnect.Event && targetObj instanceof Disconnect.SimpleListener)
			((Disconnect.SimpleListener) targetObj).onDisconnect();
		else if (baseEvent instanceof FileTransferFinished.Event && targetObj instanceof FileTransferFinished.SimpleListener) {
			FileTransferFinished.Event e = (FileTransferFinished.Event) baseEvent;
			((FileTransferFinished.SimpleListener) targetObj).onFileTransferFinished(e.getTransfer(), e.getException());
		} else if (baseEvent instanceof Finger.Event && targetObj instanceof Finger.SimpleListener) {
			Finger.Event e = (Finger.Event) baseEvent;
			((Finger.SimpleListener) targetObj).onFinger(e.getSource(), e.getChannel());
		} else if (baseEvent instanceof IncomingChatRequest.Event && targetObj instanceof IncomingChatRequest.SimpleListener) {
			IncomingChatRequest.Event e = (IncomingChatRequest.Event) baseEvent;
			((IncomingChatRequest.SimpleListener) targetObj).onIncomingChatRequest(e.getChat());
		} else if (baseEvent instanceof IncomingFileTransfer.Event && targetObj instanceof IncomingFileTransfer.SimpleListener) {
			IncomingFileTransfer.Event e = (IncomingFileTransfer.Event) baseEvent;
			((IncomingFileTransfer.SimpleListener) targetObj).onIncomingFileTransfer(e.getTransfer());
		} else if (baseEvent instanceof Invite.Event && targetObj instanceof Invite.SimpleListener) {
			Invite.Event e = (Invite.Event) baseEvent;
			((Invite.SimpleListener) targetObj).onInvite(e.getSource(), e.getChannel());
		} else if (baseEvent instanceof Join.Event && targetObj instanceof Join.SimpleListener) {
			Join.Event e = (Join.Event) baseEvent;
			((Join.SimpleListener) targetObj).onJoin(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof Kick.Event && targetObj instanceof Kick.SimpleListener) {
			Kick.Event e = (Kick.Event) baseEvent;
			((Kick.SimpleListener) targetObj).onKick(e.getChannel(), e.getSource(), e.getRecipient(), e.getReason());
		} else if (baseEvent instanceof Message.Event && targetObj instanceof Message.SimpleListener) {
			Message.Event e = (Message.Event) baseEvent;
			((Message.SimpleListener) targetObj).onMessage(e.getChannel(), e.getSource(), e.getMessage());
		} else if (baseEvent instanceof Mode.Event && targetObj instanceof Mode.SimpleListener) {
			Mode.Event e = (Mode.Event) baseEvent;
			((Mode.SimpleListener) targetObj).onMode(e.getChannel(), e.getSource(), e.getMode());
		} else if (baseEvent instanceof NickChange.Event && targetObj instanceof NickChange.SimpleListener) {
			NickChange.Event e = (NickChange.Event) baseEvent;
			((NickChange.SimpleListener) targetObj).onNickChange(e.getOldNick(), e.getNewNick(), e.getSource());
		} else if (baseEvent instanceof Notice.Event && targetObj instanceof Notice.SimpleListener) {
			Notice.Event e = (Notice.Event) baseEvent;
			((Notice.SimpleListener) targetObj).onNotice(e.getSource(), e.getTarget(), e.getNotice());
		} else if (baseEvent instanceof Op.Event && targetObj instanceof Op.SimpleListener) {
			Op.Event e = (Op.Event) baseEvent;
			((Op.SimpleListener) targetObj).onOp(e.getChannel(), e.getSource(), e.getRecipient());
		} else if (baseEvent instanceof Part.Event && targetObj instanceof Part.SimpleListener) {
			Part.Event e = (Part.Event) baseEvent;
			((Part.SimpleListener) targetObj).onPart(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof Ping.Event && targetObj instanceof Ping.SimpleListener) {
			Ping.Event e = (Ping.Event) baseEvent;
			((Ping.SimpleListener) targetObj).onPing(e.getSource(), e.getTarget(), e.getPingValue());
		} else if (baseEvent instanceof PrivateMessage.Event && targetObj instanceof PrivateMessage.SimpleListener) {
			PrivateMessage.Event e = (PrivateMessage.Event) baseEvent;
			((PrivateMessage.SimpleListener) targetObj).onPrivateMessage(e.getSource(), e.getMessage());
		} else if (baseEvent instanceof Quit.Event && targetObj instanceof Quit.SimpleListener) {
			Quit.Event e = (Quit.Event) baseEvent;
			((Quit.SimpleListener) targetObj).onQuit(e.getSource(), e.getReason());
		} else if (baseEvent instanceof RemoveChannelBan.Event && targetObj instanceof RemoveChannelBan.SimpleListener) {
			RemoveChannelBan.Event e = (RemoveChannelBan.Event) baseEvent;
			((RemoveChannelBan.SimpleListener) targetObj).onRemoveChannelBan(e.getChannel(), e.getSource(), e.getHostmask());
		} else if (baseEvent instanceof RemoveChannelKey.Event && targetObj instanceof RemoveChannelKey.SimpleListener) {
			RemoveChannelKey.Event e = (RemoveChannelKey.Event) baseEvent;
			((RemoveChannelKey.SimpleListener) targetObj).onRemoveChannelKey(e.getChannel(), e.getSource(), e.getKey());
		} else if (baseEvent instanceof RemoveChannelLimit.Event && targetObj instanceof RemoveChannelLimit.SimpleListener) {
			RemoveChannelLimit.Event e = (RemoveChannelLimit.Event) baseEvent;
			((RemoveChannelLimit.SimpleListener) targetObj).onRemoveChannelLimit(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemoveInviteOnly.Event && targetObj instanceof RemoveInviteOnly.SimpleListener) {
			RemoveInviteOnly.Event e = (RemoveInviteOnly.Event) baseEvent;
			((RemoveInviteOnly.SimpleListener) targetObj).onRemoveInviteOnly(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemoveModerated.Event && targetObj instanceof RemoveModerated.SimpleListener) {
			RemoveModerated.Event e = (RemoveModerated.Event) baseEvent;
			((RemoveModerated.SimpleListener) targetObj).onRemoveModerated(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemoveNoExternalMessages.Event && targetObj instanceof RemoveNoExternalMessages.SimpleListener) {
			RemoveNoExternalMessages.Event e = (RemoveNoExternalMessages.Event) baseEvent;
			((RemoveNoExternalMessages.SimpleListener) targetObj).onRemoveNoExternalMessages(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemovePrivate.Event && targetObj instanceof RemovePrivate.SimpleListener) {
			RemovePrivate.Event e = (RemovePrivate.Event) baseEvent;
			((RemovePrivate.SimpleListener) targetObj).onRemovePrivate(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemoveSecret.Event && targetObj instanceof RemoveSecret.SimpleListener) {
			RemoveSecret.Event e = (RemoveSecret.Event) baseEvent;
			((RemoveSecret.SimpleListener) targetObj).onRemoveSecret(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof RemoveTopicProtection.Event && targetObj instanceof RemoveTopicProtection.SimpleListener) {
			RemoveTopicProtection.Event e = (RemoveTopicProtection.Event) baseEvent;
			((RemoveTopicProtection.SimpleListener) targetObj).onRemoveTopicProtection(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof ServerPing.Event && targetObj instanceof ServerPing.SimpleListener) {
			ServerPing.Event e = (ServerPing.Event) baseEvent;
			((ServerPing.SimpleListener) targetObj).onServerPing(e.getResponse());
		} else if (baseEvent instanceof ServerResponse.Event && targetObj instanceof ServerResponse.SimpleListener) {
			ServerResponse.Event e = (ServerResponse.Event) baseEvent;
			((ServerResponse.SimpleListener) targetObj).onServerResponse(e.getCode(), e.getResponse());
		} else if (baseEvent instanceof SetChannelBan.Event && targetObj instanceof SetChannelBan.SimpleListener) {
			SetChannelBan.Event e = (SetChannelBan.Event) baseEvent;
			((SetChannelBan.SimpleListener) targetObj).onSetChannelBan(e.getChannel(), e.getSource(), e.getHostmask());
		} else if (baseEvent instanceof SetChannelKey.Event && targetObj instanceof SetChannelKey.SimpleListener) {
			SetChannelKey.Event e = (SetChannelKey.Event) baseEvent;
			((SetChannelKey.SimpleListener) targetObj).onSetChannelKey(e.getChannel(), e.getSource(), e.getKey());
		} else if (baseEvent instanceof SetChannelLimit.Event && targetObj instanceof SetChannelLimit.SimpleListener) {
			SetChannelLimit.Event e = (SetChannelLimit.Event) baseEvent;
			((SetChannelLimit.SimpleListener) targetObj).onSetChannelLimit(e.getChannel(), e.getSource(), e.getLimit());
		} else if (baseEvent instanceof SetInviteOnly.Event && targetObj instanceof SetInviteOnly.SimpleListener) {
			SetInviteOnly.Event e = (SetInviteOnly.Event) baseEvent;
			((SetInviteOnly.SimpleListener) targetObj).onSetInviteOnly(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof SetModerated.Event && targetObj instanceof SetModerated.SimpleListener) {
			SetModerated.Event e = (SetModerated.Event) baseEvent;
			((SetModerated.SimpleListener) targetObj).onSetModerated(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof SetNoExternalMessages.Event && targetObj instanceof SetNoExternalMessages.SimpleListener) {
			SetNoExternalMessages.Event e = (SetNoExternalMessages.Event) baseEvent;
			((SetNoExternalMessages.SimpleListener) targetObj).onSetNoExternalMessages(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof SetPrivate.Event && targetObj instanceof SetPrivate.SimpleListener) {
			SetPrivate.Event e = (SetPrivate.Event) baseEvent;
			((SetPrivate.SimpleListener) targetObj).onSetPrivate(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof SetSecret.Event && targetObj instanceof SetSecret.SimpleListener) {
			SetSecret.Event e = (SetSecret.Event) baseEvent;
			((SetSecret.SimpleListener) targetObj).onSetSecret(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof SetTopicProtection.Event && targetObj instanceof SetTopicProtection.SimpleListener) {
			SetTopicProtection.Event e = (SetTopicProtection.Event) baseEvent;
			((SetTopicProtection.SimpleListener) targetObj).onSetTopicProtection(e.getChannel(), e.getSource());
		} else if (baseEvent instanceof Time.Event && targetObj instanceof Time.SimpleListener) {
			Time.Event e = (Time.Event) baseEvent;
			((Time.SimpleListener) targetObj).onTime(e.getSource(), e.getTarget());
		} else if (baseEvent instanceof Topic.Event && targetObj instanceof Topic.SimpleListener) {
			Topic.Event e = (Topic.Event) baseEvent;
			((Topic.SimpleListener) targetObj).onTopic(e.getChannel(), e.getTopic(), e.getSource(), e.isChanged());
		} else if (baseEvent instanceof Unknown.Event && targetObj instanceof Unknown.SimpleListener) {
			Unknown.Event e = (Unknown.Event) baseEvent;
			((Unknown.SimpleListener) targetObj).onUnknown(e.getLine());
		} else if (baseEvent instanceof UserList.Event && targetObj instanceof UserList.SimpleListener) {
			UserList.Event e = (UserList.Event) baseEvent;
			((UserList.SimpleListener) targetObj).onUserList(e.getChannel(), e.getUsers());
		} else if (baseEvent instanceof UserMode.Event && targetObj instanceof UserMode.SimpleListener) {
			UserMode.Event e = (UserMode.Event) baseEvent;
			((UserMode.SimpleListener) targetObj).onUserMode(e.getTarget(), e.getSource(), e.getMode());
		} else if (baseEvent instanceof Version.Event && targetObj instanceof Version.SimpleListener) {
			Version.Event e = (Version.Event) baseEvent;
			((Version.SimpleListener) targetObj).onVersion(e.getSource(), e.getTarget());
		} else if (baseEvent instanceof Voice.Event && targetObj instanceof Voice.SimpleListener) {
			Voice.Event e = (Voice.Event) baseEvent;
			((Voice.SimpleListener) targetObj).onVoice(e.getChannel(), e.getSource(), e.getRecipient());
		} else
			throw new UnknownHookException("Unkown hook " + baseEvent.getClass().toString());
	}
}
