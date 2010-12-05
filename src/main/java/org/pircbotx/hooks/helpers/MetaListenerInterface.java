/*
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
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
import org.pircbotx.hooks.IncomingFileTransfer;
import org.pircbotx.hooks.Invite;
import org.pircbotx.hooks.Join;
import org.pircbotx.hooks.Kick;
import org.pircbotx.hooks.Message;
import org.pircbotx.hooks.Mode;
import org.pircbotx.hooks.Motd;
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
public interface MetaListenerInterface extends Action.Listener, ChannelInfo.Listener, Connect.Listener, Deop.Listener, DeVoice.Listener, Disconnect.Listener, FileTransferFinished.Listener, Finger.Listener, IncomingChatRequest.Listener, IncomingFileTransfer.Listener, Invite.Listener, Join.Listener, Kick.Listener, Message.Listener, Mode.Listener, Motd.Listener, NickChange.Listener, Notice.Listener, Op.Listener, Part.Listener, Ping.Listener, PrivateMessage.Listener, Quit.Listener, RemoveChannelBan.Listener, RemoveChannelKey.Listener, RemoveChannelLimit.Listener, RemoveInviteOnly.Listener, RemoveModerated.Listener, RemoveNoExternalMessages.Listener, RemovePrivate.Listener, RemoveSecret.Listener, RemoveTopicProtection.Listener, ServerPing.Listener, ServerResponse.Listener, SetChannelBan.Listener, SetChannelKey.Listener, SetChannelLimit.Listener, SetInviteOnly.Listener, SetModerated.Listener, SetNoExternalMessages.Listener, SetPrivate.Listener, SetSecret.Listener, SetTopicProtection.Listener, Time.Listener, Topic.Listener, Unknown.Listener, UserList.Listener, UserMode.Listener, Version.Listener, Voice.Listener {
}
