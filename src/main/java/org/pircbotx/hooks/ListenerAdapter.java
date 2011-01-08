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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DeVoiceEvent;
import org.pircbotx.hooks.events.DeopEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.FileTransferFinishedEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveModeratedEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelBanEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetModeratedEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ListenerAdapter implements Listener {
	protected static final Map<Class<? extends Event>, Method> eventToMethod = new HashMap();
	
	static {
		for(Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			if(curMethod.getName().equals("onEvent"))
				continue;
			eventToMethod.put((Class<? extends Event>)curMethod.getParameterTypes()[0], curMethod);
		}
	}
	
	public void onEvent(Event event) throws Exception {
		try {
			eventToMethod.get(event.getClass()).invoke(this, event);
		} catch (InvocationTargetException ex) {
			throw (Exception)ex.getCause();
		}
	}
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
