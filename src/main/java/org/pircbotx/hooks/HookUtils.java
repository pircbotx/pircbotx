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

import java.util.ArrayList;
import java.util.List;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
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
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class HookUtils {
	private static final List<Class<? extends Event>> allEvents = new ArrayList();

	static {
		//Add all Event classes
		allEvents.add(ActionEvent.class);
		allEvents.add(ChannelInfoEvent.class);
		allEvents.add(ConnectEvent.class);
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
	
	/**
	 * @return the allEvents
	 */
	public static List<Class<? extends Event>> getAllEvents() {
		return allEvents;
	}
}
