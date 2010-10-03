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
 * but WITHOUT ANY WARRANTY.getSource(); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

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
import org.pircbotx.hooks.helpers.BaseEvent;

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

	public static User getUser(BaseEvent event) {
		if (event == null)
			return null;
		else if (event instanceof Action.Event)
			return ((Action.Event) event).getSource();
		else if (event instanceof Deop.Event)
			return ((Deop.Event) event).getSource();
		else if (event instanceof DeVoice.Event)
			return ((DeVoice.Event) event).getSource();
		else if (event instanceof FileTransferFinished.Event)
			return ((FileTransferFinished.Event) event).getTransfer().getSource();
		else if (event instanceof Finger.Event)
			return ((Finger.Event) event).getSource();
		else if (event instanceof IncomingChatRequest.Event)
			return ((IncomingChatRequest.Event) event).getChat().getSource();
		else if (event instanceof IncomingFileTransfer.Event)
			return ((IncomingFileTransfer.Event) event).getTransfer().getSource();
		else if (event instanceof Invite.Event)
			return ((Invite.Event) event).getSource();
		else if (event instanceof Join.Event)
			return ((Join.Event) event).getSource();
		else if (event instanceof Kick.Event)
			return ((Kick.Event) event).getKicker();
		else if (event instanceof Message.Event)
			return ((Message.Event) event).getUser();
		else if (event instanceof Mode.Event)
			return ((Mode.Event) event).getSource();
		else if (event instanceof NickChange.Event)
			return ((NickChange.Event) event).getUser();
		else if (event instanceof Notice.Event)
			return ((Notice.Event) event).getSource();
		else if (event instanceof Op.Event)
			return ((Op.Event) event).getSource();
		else if (event instanceof Part.Event)
			return ((Part.Event) event).getSender();
		else if (event instanceof Ping.Event)
			return ((Ping.Event) event).getSource();
		else if (event instanceof PrivateMessage.Event)
			return ((PrivateMessage.Event) event).getSender();
		else if (event instanceof Quit.Event)
			return ((Quit.Event) event).getSource();
		else if (event instanceof RemoveChannelBan.Event)
			return ((RemoveChannelBan.Event) event).getSource();
		else if (event instanceof RemoveChannelKey.Event)
			return ((RemoveChannelKey.Event) event).getSource();
		else if (event instanceof RemoveChannelLimit.Event)
			return ((RemoveChannelLimit.Event) event).getSource();
		else if (event instanceof RemoveInviteOnly.Event)
			return ((RemoveInviteOnly.Event) event).getSource();
		else if (event instanceof RemoveModerated.Event)
			return ((RemoveModerated.Event) event).getSource();
		else if (event instanceof RemoveNoExternalMessages.Event)
			return ((RemoveNoExternalMessages.Event) event).getSource();
		else if (event instanceof RemovePrivate.Event)
			return ((RemovePrivate.Event) event).getSource();
		else if (event instanceof RemoveSecret.Event)
			return ((RemoveSecret.Event) event).getSource();
		else if (event instanceof RemoveTopicProtection.Event)
			return ((RemoveTopicProtection.Event) event).getSource();
		else if (event instanceof SetChannelBan.Event)
			return ((SetChannelBan.Event) event).getSource();
		else if (event instanceof SetChannelKey.Event)
			return ((SetChannelKey.Event) event).getSource();
		else if (event instanceof SetChannelLimit.Event)
			return ((SetChannelLimit.Event) event).getSource();
		else if (event instanceof SetInviteOnly.Event)
			return ((SetInviteOnly.Event) event).getSource();
		else if (event instanceof SetModerated.Event)
			return ((SetModerated.Event) event).getSource();
		else if (event instanceof SetNoExternalMessages.Event)
			return ((SetNoExternalMessages.Event) event).getSource();
		else if (event instanceof SetPrivate.Event)
			return ((SetPrivate.Event) event).getSource();
		else if (event instanceof SetSecret.Event)
			return ((SetSecret.Event) event).getSource();
		else if (event instanceof SetTopicProtection.Event)
			return ((SetTopicProtection.Event) event).getSource();
		else if (event instanceof Time.Event)
			return ((Time.Event) event).getSource();
		else if (event instanceof Topic.Event)
			return ((Topic.Event) event).getSetBy();
		else if (event instanceof UserMode.Event)
			return ((UserMode.Event) event).getSource();
		else if (event instanceof Version.Event)
			return ((Version.Event) event).getSource();
		else if (event instanceof Voice.Event)
			return ((Voice.Event) event).getSource();
		return null;
	}
}
