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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.hooks.helpers.BaseSimpleListener;
import org.pircbotx.hooks.helpers.ListenerManager;
import org.pircbotx.hooks.helpers.MetaSimpleListenerInterface;
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
public class SimplePircBotX extends PircBotX implements MetaSimpleListenerInterface {
	public void onAction(User source, Channel chanTarget, String action) {
	}

	public void onChannelInfo(Channel channel, int userCount, String topic) {
	}

	public void onConnect() {
	}

	public void onDeop(Channel channel, User source, User recipient) {
	}

	public void onDeVoice(Channel channel, User source, User recipient) {
	}

	public void onDisconnect() {
	}

	public void onFileTransferFinished(DccFileTransfer transfer, Exception e) {
	}

	public void onFinger(User source, Channel channel) {
	}

	public void onIncomingChatRequest(DccChat chat) {
	}

	public void onIncomingFileTransfer(DccFileTransfer transfer) {
	}

	public void onInvite(User source, Channel channel) {
	}

	public void onJoin(Channel channel, User user) {
	}

	public void onKick(Channel channel, User kicker, User recipient, String reason) {
	}

	public void onMessage(Channel channel, User user, String message) {
	}

	public void onMode(Channel channel, User source, String mode) {
	}

	public void onNickChange(String oldNick, String newNick, User user) {
	}

	public void onNotice(User source, Channel target, String notice) {
	}

	public void onOp(Channel channel, User source, User recipient) {
	}

	public void onPart(Channel channel, User sender) {
	}

	public void onPing(User source, Channel target, String pingValue) {
	}

	public void onPrivateMessage(User sender, String message) {
	}

	public void onQuit(User source, String reason) {
	}

	public void onRemoveChannelBan(Channel channel, User source, String hostmask) {
	}

	public void onRemoveChannelKey(Channel channel, User source, String key) {
	}

	public void onRemoveChannelLimit(Channel channel, User user) {
	}

	public void onRemoveInviteOnly(Channel channel, User user) {
	}

	public void onRemoveModerated(Channel channel, User user) {
	}

	public void onRemoveNoExternalMessages(Channel channel, User user) {
	}

	public void onRemovePrivate(Channel channel, User user) {
	}

	public void onRemoveSecret(Channel channel, User user) {
	}

	public void onRemoveTopicProtection(Channel channel, User user) {
	}

	public void onServerPing(String response) {
	}

	public void onServerResponse(int code, String response) {
	}

	public void onSetChannelBan(Channel channel, User source, String hostmask) {
	}

	public void onSetChannelKey(Channel channel, User source, String key) {
	}

	public void onSetChannelLimit(Channel channel, User source, int limit) {
	}

	public void onSetInviteOnly(Channel channel, User user) {
	}

	public void onSetModerated(Channel channel, User user) {
	}

	public void onSetNoExternalMessages(Channel channel, User user) {
	}

	public void onSetPrivate(Channel channel, User user) {
	}

	public void onSetSecret(Channel channel, User source) {
	}

	public void onSetTopicProtection(Channel channel, User source) {
	}

	public void onTime(User source, Channel target) {
	}

	public void onTopic(Channel channel, String topic, User setBy, boolean changed) {
	}

	public void onUnknown(String line) {
	}

	public void onUserList(Channel channel, Set<User> users) {
	}

	public void onUserMode(User target, User source, String mode) {
	}

	public void onVersion(User source, Channel target) {
	}

	public void onVoice(Channel channel, User source, User recipient) {
	}

	public void onNickChange(String motd) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void onMotd(String motd) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public class SimpleListenerManager implements ListenerManager<BaseSimpleListener> {
		protected Set<BaseSimpleListener> listeners = new HashSet<BaseSimpleListener>();

		public void addListener(BaseSimpleListener listener) {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public void removeListener(BaseSimpleListener listener) {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public Set<BaseSimpleListener> getListeners() {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public void dispatchEvent(BaseEvent baseEvent) {
			//Yes, this is once again ugly. However it gets the job done
			//Call appropiate method based on incomming Event
			/**
			if (baseEvent instanceof Action.Event) {
			Action.Event e = (Action.Event) baseEvent;
			onAction(e.getSender(), e.getLogin(), e.getHostname(), e.getTarget(), e.getAction());
			} else if (baseEvent instanceof ChannelInfo.Event) {
			ChannelInfo.Event e = (ChannelInfo.Event) baseEvent;
			onChannelInfo(e.getChannel(), e.getUserCount(), e.getTopic());
			} else if (baseEvent instanceof Connect.Event)
			onConnect();
			else if (baseEvent instanceof Deop.Event) {
			Deop.Event e = (Deop.Event) baseEvent;
			onDeop(e.getChannel(), e.getSourceNick(), e.getSourceLogin(), e.getSourceHostname(), e.getRecipient());
			} else if (baseEvent instanceof DeVoice.Event) {
			DeVoice.Event e = (DeVoice.Event) baseEvent;
			onDeVoice(e.getChannel(), e.getSourceNick(), e.getSourceLogin(), e.getSourceHostname(), e.getRecipient());
			} else if (baseEvent instanceof Disconnect.Event)
			onDisconnect();
			else if (baseEvent instanceof FileTransferFinished.Event) {
			FileTransferFinished.Event e = (FileTransferFinished.Event) baseEvent;
			onFileTransferFinished(e.getTransfer(), e.getException());
			} else if (baseEvent instanceof Finger.Event) {
			Finger.Event e = (Finger.Event) baseEvent;
			onFinger(e.getSourceNick(), e.getSourceLogin(), e.getSourceHostname(), e.getTarget());
			} else if (baseEvent instanceof IncomingChatRequest.Event) {
			IncomingChatRequest.Event e = (IncomingChatRequest.Event) baseEvent;
			onIncomingChatRequest(e.getChat());
			} else if (baseEvent instanceof IncomingFileTransfer.Event) {
			IncomingFileTransfer.Event e = (IncomingFileTransfer.Event) baseEvent;
			onIncomingFileTransfer(e.getTransfer());
			} else if (baseEvent instanceof Invite.Event) {
			Invite.Event e = (Invite.Event) baseEvent;
			onInvite(e.getTargetNick(), e.getSourceNick(), e.getSourceLogin(), e.getSourceHostname(), e.getChannel());
			} else if (baseEvent instanceof Join.Event) {
			Join.Event e = (Join.Event) baseEvent;
			onJoin(e.getChannel(), e.getSender(), e.getLogin(), e.getHostname());
			} else if (baseEvent instanceof Kick.Event) {
			Kick.Event e = (Kick.Event) baseEvent;
			onKick(e.getChannel(), e.getKickerNick(), e.getKickerLogin(), e.getKickerHostname(), e.getRecipientNick(), e.getReason());
			} else if (baseEvent instanceof Message.Event) {
			Message.Event e = (Message.Event) baseEvent;
			onMessage(e.getChannel(), e.getSender(), e.getLogin(), e.getHostname(), e.getMessage());
			} else if (baseEvent instanceof Mode.Event) {
			Mode.Event e = (Mode.Event) baseEvent;
			onMode(e.getChannel(), e.getSourceNick(), e.getSourceHostname(), e.getSourceHostname(), e.getMode());
			} else if (baseEvent instanceof NickChange.Event) {
			NickChange.Event e = (NickChange.Event) baseEvent;
			onNickChange(e.getOldNick(), e.getLogin(), e.getHostname(), e.getNewNick());
			} else if (baseEvent instanceof Notice.Event) {
			Notice.Event e = (Notice.Event) baseEvent;
			onNotice;
			} else if (baseEvent instanceof Op.Event) {
			Op.Event e = (Op.Event) baseEvent;
			onOp;
			} else if (baseEvent instanceof Part.Event) {
			Part.Event e = (Part.Event) baseEvent;
			onPart;
			} else if (baseEvent instanceof Ping.Event) {
			Ping.Event e = (Ping.Event) baseEvent;
			onPing;
			} else if (baseEvent instanceof PrivateMessage.Event) {
			PrivateMessage.Event e = (PrivateMessage.Event) baseEvent;
			onPrivateMessage;
			} else if (baseEvent instanceof Quit.Event) {
			Quit.Event e = (Quit.Event) baseEvent;
			onQuit;
			} else if (baseEvent instanceof RemoveChannelBan.Event) {
			RemoveChannelBan.Event e = (RemoveChannelBan.Event) baseEvent;
			onRemoveChannelBan;
			} else if (baseEvent instanceof RemoveChannelKey.Event) {
			RemoveChannelKey.Event e = (RemoveChannelKey.Event) baseEvent;
			onRemoveChannelKey;
			} else if (baseEvent instanceof RemoveChannelLimit.Event) {
			RemoveChannelLimit.Event e = (RemoveChannelLimit.Event) baseEvent;
			onRemoveChannelLimit;
			} else if (baseEvent instanceof RemoveInviteOnly.Event) {
			RemoveInviteOnly.Event e = (RemoveInviteOnly.Event) baseEvent;
			onRemoveInviteOnly;
			} else if (baseEvent instanceof RemoveModerated.Event) {
			RemoveModerated.Event e = (RemoveModerated.Event) baseEvent;
			onRemoveModerated;
			} else if (baseEvent instanceof RemoveNoExternalMessages.Event) {
			RemoveNoExternalMessages.Event e = (RemoveNoExternalMessages.Event) baseEvent;
			onRemoveNoExternalMessages;
			} else if (baseEvent instanceof RemovePrivate.Event) {
			RemovePrivate.Event e = (RemovePrivate.Event) baseEvent;
			onRemovePrivate;
			} else if (baseEvent instanceof RemoveSecret.Event) {
			RemoveSecret.Event e = (RemoveSecret.Event) baseEvent;
			onRemoveSecret;
			} else if (baseEvent instanceof RemoveTopicProtection.Event) {
			RemoveTopicProtection.Event e = (RemoveTopicProtection.Event) baseEvent;
			onRemoveTopicProtection;
			} else if (baseEvent instanceof ServerPing.Event) {
			ServerPing.Event e = (ServerPing.Event) baseEvent;
			onServerPing;
			} else if (baseEvent instanceof ServerResponse.Event) {
			ServerResponse.Event e = (ServerResponse.Event) baseEvent;
			onServerResponse;
			} else if (baseEvent instanceof SetChannelBan.Event) {
			SetChannelBan.Event e = (SetChannelBan.Event) baseEvent;
			onSetChannelBan;
			} else if (baseEvent instanceof SetChannelKey.Event) {
			SetChannelKey.Event e = (SetChannelKey.Event) baseEvent;
			onSetChannelKey;
			} else if (baseEvent instanceof SetChannelLimit.Event) {
			SetChannelLimit.Event e = (SetChannelLimit.Event) baseEvent;
			onSetChannelLimit;
			} else if (baseEvent instanceof SetInviteOnly.Event) {
			SetInviteOnly.Event e = (SetInviteOnly.Event) baseEvent;
			onSetInviteOnly;
			} else if (baseEvent instanceof SetModerated.Event) {
			SetModerated.Event e = (SetModerated.Event) baseEvent;
			onSetModerated;
			} else if (baseEvent instanceof SetNoExternalMessages.Event) {
			SetNoExternalMessages.Event e = (SetNoExternalMessages.Event) baseEvent;
			onSetNoExternalMessages;
			} else if (baseEvent instanceof SetPrivate.Event) {
			SetPrivate.Event e = (SetPrivate.Event) baseEvent;
			onSetPrivate;
			} else if (baseEvent instanceof SetSecret.Event) {
			SetSecret.Event e = (SetSecret.Event) baseEvent;
			onSetSecret;
			} else if (baseEvent instanceof SetTopicProtection.Event) {
			SetTopicProtection.Event e = (SetTopicProtection.Event) baseEvent;
			onSetTopicProtection;
			} else if (baseEvent instanceof Time.Event) {
			Time.Event e = (Time.Event) baseEvent;
			onTime;
			} else if (baseEvent instanceof Topic.Event) {
			Topic.Event e = (Topic.Event) baseEvent;
			onTopic;
			} else if (baseEvent instanceof Unknown.Event) {
			Unknown.Event e = (Unknown.Event) baseEvent;
			onUnknown;
			} else if (baseEvent instanceof UserList.Event) {
			UserList.Event e = (UserList.Event) baseEvent;
			onUserList;
			} else if (baseEvent instanceof UserMode.Event) {
			UserMode.Event e = (UserMode.Event) baseEvent;
			onUserMode;
			} else if (baseEvent instanceof Version.Event) {
			Version.Event e = (Version.Event) baseEvent;
			onVersion;
			} else if (baseEvent instanceof Voice.Event) {
			Voice.Event e = (Voice.Event) baseEvent;
			onVoice;
			}*/
		}
	}
}
