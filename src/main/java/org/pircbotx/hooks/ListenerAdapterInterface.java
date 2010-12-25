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

import org.pircbotx.listeners.ActionListener;
import org.pircbotx.listeners.ChannelInfoListener;
import org.pircbotx.listeners.ConnectListener;
import org.pircbotx.listeners.DeVoiceListener;
import org.pircbotx.listeners.DeopListener;
import org.pircbotx.listeners.DisconnectListener;
import org.pircbotx.listeners.FileTransferFinishedListener;
import org.pircbotx.listeners.FingerListener;
import org.pircbotx.listeners.IncomingChatRequestListener;
import org.pircbotx.listeners.IncomingFileTransferListener;
import org.pircbotx.listeners.InviteListener;
import org.pircbotx.listeners.JoinListener;
import org.pircbotx.listeners.KickListener;
import org.pircbotx.listeners.MessageListener;
import org.pircbotx.listeners.ModeListener;
import org.pircbotx.listeners.MotdListener;
import org.pircbotx.listeners.NickChangeListener;
import org.pircbotx.listeners.NoticeListener;
import org.pircbotx.listeners.OpListener;
import org.pircbotx.listeners.PartListener;
import org.pircbotx.listeners.PingListener;
import org.pircbotx.listeners.PrivateMessageListener;
import org.pircbotx.listeners.QuitListener;
import org.pircbotx.listeners.RemoveChannelBanListener;
import org.pircbotx.listeners.RemoveChannelKeyListener;
import org.pircbotx.listeners.RemoveChannelLimitListener;
import org.pircbotx.listeners.RemoveInviteOnlyListener;
import org.pircbotx.listeners.RemoveModeratedListener;
import org.pircbotx.listeners.RemoveNoExternalMessagesListener;
import org.pircbotx.listeners.RemovePrivateListener;
import org.pircbotx.listeners.RemoveSecretListener;
import org.pircbotx.listeners.RemoveTopicProtectionListener;
import org.pircbotx.listeners.ServerPingListener;
import org.pircbotx.listeners.ServerResponseListener;
import org.pircbotx.listeners.SetChannelBanListener;
import org.pircbotx.listeners.SetChannelKeyListener;
import org.pircbotx.listeners.SetChannelLimitListener;
import org.pircbotx.listeners.SetInviteOnlyListener;
import org.pircbotx.listeners.SetModeratedListener;
import org.pircbotx.listeners.SetNoExternalMessagesListener;
import org.pircbotx.listeners.SetPrivateListener;
import org.pircbotx.listeners.SetSecretListener;
import org.pircbotx.listeners.SetTopicProtectionListener;
import org.pircbotx.listeners.TimeListener;
import org.pircbotx.listeners.TopicListener;
import org.pircbotx.listeners.UnknownListener;
import org.pircbotx.listeners.UserListListener;
import org.pircbotx.listeners.UserModeListener;
import org.pircbotx.listeners.VersionListener;
import org.pircbotx.listeners.VoiceListener;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface ListenerAdapterInterface extends ActionListener, ChannelInfoListener, ConnectListener, DeopListener, DeVoiceListener, DisconnectListener, FileTransferFinishedListener, FingerListener, IncomingChatRequestListener, IncomingFileTransferListener, InviteListener, JoinListener, KickListener, MessageListener, ModeListener, MotdListener, NickChangeListener, NoticeListener, OpListener, PartListener, PingListener, PrivateMessageListener, QuitListener, RemoveChannelBanListener, RemoveChannelKeyListener, RemoveChannelLimitListener, RemoveInviteOnlyListener, RemoveModeratedListener, RemoveNoExternalMessagesListener, RemovePrivateListener, RemoveSecretListener, RemoveTopicProtectionListener, ServerPingListener, ServerResponseListener, SetChannelBanListener, SetChannelKeyListener, SetChannelLimitListener, SetInviteOnlyListener, SetModeratedListener, SetNoExternalMessagesListener, SetPrivateListener, SetSecretListener, SetTopicProtectionListener, TimeListener, TopicListener, UnknownListener, UserListListener, UserModeListener, VersionListener, VoiceListener {
}
