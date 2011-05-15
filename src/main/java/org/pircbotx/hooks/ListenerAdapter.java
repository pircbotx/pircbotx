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
 * Adapter that provides methods to capture each event separately, removing
 * the need to check, cast, and call your custom method for each event you want
 * to capture. 
 * <p>
 * To use, simply override the method that has the event you want to capture. 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ListenerAdapter implements Listener {
	protected static final Map<Class<? extends Event>, Method> eventToMethod = new HashMap();

	static {
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			if (curMethod.getName().equals("onEvent"))
				continue;
			eventToMethod.put((Class<? extends Event>) curMethod.getParameterTypes()[0], curMethod);
		}
	}

	public void onEvent(Event event) throws Exception {
		try {
			eventToMethod.get(event.getClass()).invoke(this, event);
		} catch (InvocationTargetException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof Exception)
				throw (Exception) ex.getCause();
			else
				//Must be something severe
				if (event.getBot() != null)
					event.getBot().logException(cause);
				else
					//No bot to work with, just print an exception
					cause.printStackTrace();
		}
	}

	public void onAction(ActionEvent event) throws Exception {
	}

	public void onChannelInfo(ChannelInfoEvent event) throws Exception {
	}

	public void onConnect(ConnectEvent event) throws Exception {
	}

	public void onDeop(DeopEvent event) throws Exception {
	}

	public void onDeVoice(DeVoiceEvent event) throws Exception {
	}

	public void onDisconnect(DisconnectEvent event) throws Exception {
	}

	public void onFileTransferFinished(FileTransferFinishedEvent event) throws Exception {
	}

	public void onFinger(FingerEvent event) throws Exception {
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent event) throws Exception {
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent event) throws Exception {
	}

	public void onInvite(InviteEvent event) throws Exception {
	}

	public void onJoin(JoinEvent event) throws Exception {
	}

	public void onKick(KickEvent event) throws Exception {
	}

	public void onMessage(MessageEvent event) throws Exception {
	}

	public void onMode(ModeEvent event) throws Exception {
	}

	public void onMotd(MotdEvent event) throws Exception {
	}

	public void onNickChange(NickChangeEvent event) throws Exception {
	}

	public void onNotice(NoticeEvent event) throws Exception {
	}

	public void onOp(OpEvent event) throws Exception {
	}

	public void onPart(PartEvent event) throws Exception {
	}

	public void onPing(PingEvent event) throws Exception {
	}

	public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
	}

	public void onQuit(QuitEvent event) throws Exception {
	}

	public void onRemoveChannelBan(RemoveChannelBanEvent event) throws Exception {
	}

	public void onRemoveChannelKey(RemoveChannelKeyEvent event) throws Exception {
	}

	public void onRemoveChannelLimit(RemoveChannelLimitEvent event) throws Exception {
	}

	public void onRemoveInviteOnly(RemoveInviteOnlyEvent event) throws Exception {
	}

	public void onRemoveModerated(RemoveModeratedEvent event) throws Exception {
	}

	public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent event) throws Exception {
	}

	public void onRemovePrivate(RemovePrivateEvent event) throws Exception {
	}

	public void onRemoveSecret(RemoveSecretEvent event) throws Exception {
	}

	public void onRemoveTopicProtection(RemoveTopicProtectionEvent event) throws Exception {
	}

	public void onServerPing(ServerPingEvent event) throws Exception {
	}

	public void onServerResponse(ServerResponseEvent event) throws Exception {
	}

	public void onSetChannelBan(SetChannelBanEvent event) throws Exception {
	}

	public void onSetChannelKey(SetChannelKeyEvent event) throws Exception {
	}

	public void onSetChannelLimit(SetChannelLimitEvent event) throws Exception {
	}

	public void onSetInviteOnly(SetInviteOnlyEvent event) throws Exception {
	}

	public void onSetModerated(SetModeratedEvent event) throws Exception {
	}

	public void onSetNoExternalMessages(SetNoExternalMessagesEvent event) throws Exception {
	}

	public void onSetPrivate(SetPrivateEvent event) throws Exception {
	}

	public void onSetSecret(SetSecretEvent event) throws Exception {
	}

	public void onSetTopicProtection(SetTopicProtectionEvent event) throws Exception {
	}

	public void onTime(TimeEvent event) throws Exception {
	}

	public void onTopic(TopicEvent event) throws Exception {
	}

	public void onUnknown(UnknownEvent event) throws Exception {
	}

	public void onUserList(UserListEvent event) throws Exception {
	}

	public void onUserMode(UserModeEvent event) throws Exception {
	}

	public void onVersion(VersionEvent event) throws Exception {
	}

	public void onVoice(VoiceEvent event) throws Exception {
	}
}
