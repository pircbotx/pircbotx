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
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.FileTransferFinishedEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.FounderEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
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
import org.pircbotx.hooks.events.SuperOpEvent;
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
 * <p>
 * <b>WARNING:</b> If you are going to be implementing {@link Listener}'s 
 * {@link Listener#onEvent(org.pircbotx.hooks.Event) } method, you must call
 * <code>super.onEvent(event)</code>, otherwise none of the Adapter hook methods
 * will work!
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public abstract class ListenerAdapter<T extends PircBotX> implements Listener<T> {
	protected static final Map<Class<? extends Event>, Method> eventToMethod = new HashMap();

	static {
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			if (curMethod.getName().equals("onEvent"))
				continue;
			eventToMethod.put((Class<? extends Event>) curMethod.getParameterTypes()[0], curMethod);
		}
	}

	public void onEvent(Event<T> event) throws Exception {
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

	public void onAction(ActionEvent<T> event) throws Exception {
	}

	public void onChannelInfo(ChannelInfoEvent<T> event) throws Exception {
	}

	public void onConnect(ConnectEvent<T> event) throws Exception {
	}

	public void onDisconnect(DisconnectEvent<T> event) throws Exception {
	}

	public void onFileTransferFinished(FileTransferFinishedEvent<T> event) throws Exception {
	}

	public void onFinger(FingerEvent<T> event) throws Exception {
	}
	
	public void onFounder(FounderEvent<T> event) throws Exception {
	}
	
	public void onHalfOp(HalfOpEvent<T> event) throws Exception {
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent<T> event) throws Exception {
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent<T> event) throws Exception {
	}

	public void onInvite(InviteEvent<T> event) throws Exception {
	}

	public void onJoin(JoinEvent<T> event) throws Exception {
	}

	public void onKick(KickEvent<T> event) throws Exception {
	}

	public void onMessage(MessageEvent<T> event) throws Exception {
	}

	public void onMode(ModeEvent<T> event) throws Exception {
	}

	public void onMotd(MotdEvent<T> event) throws Exception {
	}

	public void onNickChange(NickChangeEvent<T> event) throws Exception {
	}

	public void onNotice(NoticeEvent<T> event) throws Exception {
	}

	public void onOp(OpEvent<T> event) throws Exception {
	}

	public void onPart(PartEvent<T> event) throws Exception {
	}

	public void onPing(PingEvent<T> event) throws Exception {
	}

	public void onPrivateMessage(PrivateMessageEvent<T> event) throws Exception {
	}

	public void onQuit(QuitEvent<T> event) throws Exception {
	}

	public void onRemoveChannelBan(RemoveChannelBanEvent<T> event) throws Exception {
	}

	public void onRemoveChannelKey(RemoveChannelKeyEvent<T> event) throws Exception {
	}

	public void onRemoveChannelLimit(RemoveChannelLimitEvent<T> event) throws Exception {
	}

	public void onRemoveInviteOnly(RemoveInviteOnlyEvent<T> event) throws Exception {
	}

	public void onRemoveModerated(RemoveModeratedEvent<T> event) throws Exception {
	}

	public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<T> event) throws Exception {
	}

	public void onRemovePrivate(RemovePrivateEvent<T> event) throws Exception {
	}

	public void onRemoveSecret(RemoveSecretEvent<T> event) throws Exception {
	}

	public void onRemoveTopicProtection(RemoveTopicProtectionEvent<T> event) throws Exception {
	}

	public void onServerPing(ServerPingEvent<T> event) throws Exception {
	}

	public void onServerResponse(ServerResponseEvent<T> event) throws Exception {
	}

	public void onSetChannelBan(SetChannelBanEvent<T> event) throws Exception {
	}

	public void onSetChannelKey(SetChannelKeyEvent<T> event) throws Exception {
	}

	public void onSetChannelLimit(SetChannelLimitEvent<T> event) throws Exception {
	}

	public void onSetInviteOnly(SetInviteOnlyEvent<T> event) throws Exception {
	}

	public void onSetModerated(SetModeratedEvent<T> event) throws Exception {
	}

	public void onSetNoExternalMessages(SetNoExternalMessagesEvent<T> event) throws Exception {
	}

	public void onSetPrivate(SetPrivateEvent<T> event) throws Exception {
	}

	public void onSetSecret(SetSecretEvent<T> event) throws Exception {
	}

	public void onSetTopicProtection(SetTopicProtectionEvent<T> event) throws Exception {
	}
	
	public void onSuperOp(SuperOpEvent<T> event) throws Exception {
	}

	public void onTime(TimeEvent<T> event) throws Exception {
	}

	public void onTopic(TopicEvent<T> event) throws Exception {
	}

	public void onUnknown(UnknownEvent<T> event) throws Exception {
	}

	public void onUserList(UserListEvent<T> event) throws Exception {
	}

	public void onUserMode(UserModeEvent<T> event) throws Exception {
	}

	public void onVersion(VersionEvent<T> event) throws Exception {
	}

	public void onVoice(VoiceEvent<T> event) throws Exception {
	}
}
