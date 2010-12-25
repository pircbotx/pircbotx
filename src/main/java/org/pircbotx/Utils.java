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

package org.pircbotx;

import org.pircbotx.events.ActionEvent;
import org.pircbotx.events.DeVoiceEvent;
import org.pircbotx.events.DeopEvent;
import org.pircbotx.events.FileTransferFinishedEvent;
import org.pircbotx.events.FingerEvent;
import org.pircbotx.events.IncomingChatRequestEvent;
import org.pircbotx.events.IncomingFileTransferEvent;
import org.pircbotx.events.InviteEvent;
import org.pircbotx.events.JoinEvent;
import org.pircbotx.events.KickEvent;
import org.pircbotx.events.MessageEvent;
import org.pircbotx.events.ModeEvent;
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
import org.pircbotx.events.UserModeEvent;
import org.pircbotx.events.VersionEvent;
import org.pircbotx.events.VoiceEvent;
import org.pircbotx.hooks.Event;


/**
 *
 * @author  Forked by Leon Blakey as part of the PircBotX project
 *          <a href="http://pircbotx.googlecode.com">http://pircbotx.googlecode.com/</a>
 * @version    2.0 Alpha
 */
public class Utils {
	public static boolean isBlank(String str) {
		return (str != null) && (str.trim().equals(""));
	}



	/**
	 * Extract the source user from any Event
	 * @param event An event to get information from
	 * @return The object of the user or null if the event doesn't have a source
	 */
	public static User getSource(Event event) {
		if (event == null)
			return null;
		else if (event instanceof ActionEvent)
			return ((ActionEvent) event).getSource();
		else if (event instanceof DeopEvent)
			return ((DeopEvent) event).getSource();
		else if (event instanceof DeVoiceEvent)
			return ((DeVoiceEvent) event).getSource();
		else if (event instanceof FileTransferFinishedEvent)
			return ((FileTransferFinishedEvent) event).getTransfer().getSource();
		else if (event instanceof FingerEvent)
			return ((FingerEvent) event).getSource();
		else if (event instanceof IncomingChatRequestEvent)
			return ((IncomingChatRequestEvent) event).getChat().getSource();
		else if (event instanceof IncomingFileTransferEvent)
			return ((IncomingFileTransferEvent) event).getTransfer().getSource();
		else if (event instanceof InviteEvent)
			return ((InviteEvent) event).getSource();
		else if (event instanceof JoinEvent)
			return ((JoinEvent) event).getSource();
		else if (event instanceof KickEvent)
			return ((KickEvent) event).getSource();
		else if (event instanceof MessageEvent)
			return ((MessageEvent) event).getSource();
		else if (event instanceof ModeEvent)
			return ((ModeEvent) event).getSource();
		else if (event instanceof NickChangeEvent)
			return ((NickChangeEvent) event).getSource();
		else if (event instanceof NoticeEvent)
			return ((NoticeEvent) event).getSource();
		else if (event instanceof OpEvent)
			return ((OpEvent) event).getSource();
		else if (event instanceof PartEvent)
			return ((PartEvent) event).getSource();
		else if (event instanceof PingEvent)
			return ((PingEvent) event).getSource();
		else if (event instanceof PrivateMessageEvent)
			return ((PrivateMessageEvent) event).getSource();
		else if (event instanceof QuitEvent)
			return ((QuitEvent) event).getSource();
		else if (event instanceof RemoveChannelBanEvent)
			return ((RemoveChannelBanEvent) event).getSource();
		else if (event instanceof RemoveChannelKeyEvent)
			return ((RemoveChannelKeyEvent) event).getSource();
		else if (event instanceof RemoveChannelLimitEvent)
			return ((RemoveChannelLimitEvent) event).getSource();
		else if (event instanceof RemoveInviteOnlyEvent)
			return ((RemoveInviteOnlyEvent) event).getSource();
		else if (event instanceof RemoveModeratedEvent)
			return ((RemoveModeratedEvent) event).getSource();
		else if (event instanceof RemoveNoExternalMessagesEvent)
			return ((RemoveNoExternalMessagesEvent) event).getSource();
		else if (event instanceof RemovePrivateEvent)
			return ((RemovePrivateEvent) event).getSource();
		else if (event instanceof RemoveSecretEvent)
			return ((RemoveSecretEvent) event).getSource();
		else if (event instanceof RemoveTopicProtectionEvent)
			return ((RemoveTopicProtectionEvent) event).getSource();
		else if (event instanceof SetChannelBanEvent)
			return ((SetChannelBanEvent) event).getSource();
		else if (event instanceof SetChannelKeyEvent)
			return ((SetChannelKeyEvent) event).getSource();
		else if (event instanceof SetChannelLimitEvent)
			return ((SetChannelLimitEvent) event).getSource();
		else if (event instanceof SetInviteOnlyEvent)
			return ((SetInviteOnlyEvent) event).getSource();
		else if (event instanceof SetModeratedEvent)
			return ((SetModeratedEvent) event).getSource();
		else if (event instanceof SetNoExternalMessagesEvent)
			return ((SetNoExternalMessagesEvent) event).getSource();
		else if (event instanceof SetPrivateEvent)
			return ((SetPrivateEvent) event).getSource();
		else if (event instanceof SetSecretEvent)
			return ((SetSecretEvent) event).getSource();
		else if (event instanceof SetTopicProtectionEvent)
			return ((SetTopicProtectionEvent) event).getSource();
		else if (event instanceof TimeEvent)
			return ((TimeEvent) event).getSource();
		else if (event instanceof TopicEvent)
			return ((TopicEvent) event).getSource();
		else if (event instanceof UserModeEvent)
			return ((UserModeEvent) event).getSource();
		else if (event instanceof VersionEvent)
			return ((VersionEvent) event).getSource();
		else if (event instanceof VoiceEvent)
			return ((VoiceEvent) event).getSource();
		return null;
	}
}
