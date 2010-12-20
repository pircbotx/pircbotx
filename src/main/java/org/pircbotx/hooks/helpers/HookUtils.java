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

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
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
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class HookUtils {
	@Getter
	public static final List<Class<? extends Listener>> allListeners = new ArrayList();
	@Getter
	public static final List<Class<? extends Event>> allEvents = new ArrayList();

	static {
		//Add all Event classes
		allEvents.add(ActionEvent.class);
		allEvents.add(ChannelInfoEvent.class);
		allEvents.add(ConnectEvent.class);
		allEvents.add(DeopEvent.class);
		allEvents.add(DeVoiceEvent.class);
		allEvents.add(DisconnectEvent.class);
		allEvents.add(FileTransferFinishedEvent.class);
		allEvents.add(FingerEvent.class);
		allEvents.add(IncomingChatRequestEvent.class);
		allEvents.add(IncomingFileTransferEvent.class);
		allEvents.add(InviteEvent.class);
		allEvents.add(JoinEvent.class);
		allEvents.add(KickEvent.class);
		allEvents.add(MessageEvent.class);
		allEvents.add(ModeEvent.class);
		allEvents.add(MotdEvent.class);
		allEvents.add(NickChangeEvent.class);
		allEvents.add(NoticeEvent.class);
		allEvents.add(OpEvent.class);
		allEvents.add(PartEvent.class);
		allEvents.add(PingEvent.class);
		allEvents.add(PrivateMessageEvent.class);
		allEvents.add(QuitEvent.class);
		allEvents.add(RemoveChannelBanEvent.class);
		allEvents.add(RemoveChannelKeyEvent.class);
		allEvents.add(RemoveChannelLimitEvent.class);
		allEvents.add(RemoveInviteOnlyEvent.class);
		allEvents.add(RemoveModeratedEvent.class);
		allEvents.add(RemoveNoExternalMessagesEvent.class);
		allEvents.add(RemovePrivateEvent.class);
		allEvents.add(RemoveSecretEvent.class);
		allEvents.add(RemoveTopicProtectionEvent.class);
		allEvents.add(ServerPingEvent.class);
		allEvents.add(ServerResponseEvent.class);
		allEvents.add(SetChannelBanEvent.class);
		allEvents.add(SetChannelKeyEvent.class);
		allEvents.add(SetChannelLimitEvent.class);
		allEvents.add(SetInviteOnlyEvent.class);
		allEvents.add(SetModeratedEvent.class);
		allEvents.add(SetNoExternalMessagesEvent.class);
		allEvents.add(SetPrivateEvent.class);
		allEvents.add(SetSecretEvent.class);
		allEvents.add(SetTopicProtectionEvent.class);
		allEvents.add(TimeEvent.class);
		allEvents.add(TopicEvent.class);
		allEvents.add(UnknownEvent.class);
		allEvents.add(UserListEvent.class);
		allEvents.add(UserModeEvent.class);
		allEvents.add(VersionEvent.class);
		allEvents.add(VoiceEvent.class);
	}
}
