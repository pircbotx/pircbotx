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

import org.pircbotx.hooks.listeners.ActionListener;
import org.pircbotx.hooks.listeners.ChannelInfoListener;
import org.pircbotx.hooks.listeners.ConnectListener;
import org.pircbotx.hooks.listeners.DeVoiceListener;
import org.pircbotx.hooks.listeners.DeopListener;
import org.pircbotx.hooks.listeners.DisconnectListener;
import org.pircbotx.hooks.listeners.FileTransferFinishedListener;
import org.pircbotx.hooks.listeners.FingerListener;
import org.pircbotx.hooks.listeners.IncomingChatRequestListener;
import org.pircbotx.hooks.listeners.IncomingFileTransferListener;
import org.pircbotx.hooks.listeners.InviteListener;
import org.pircbotx.hooks.listeners.JoinListener;
import org.pircbotx.hooks.listeners.KickListener;
import org.pircbotx.hooks.listeners.MessageListener;
import org.pircbotx.hooks.listeners.ModeListener;
import org.pircbotx.hooks.listeners.MotdListener;
import org.pircbotx.hooks.listeners.NickChangeListener;
import org.pircbotx.hooks.listeners.NoticeListener;
import org.pircbotx.hooks.listeners.OpListener;
import org.pircbotx.hooks.listeners.PartListener;
import org.pircbotx.hooks.listeners.PingListener;
import org.pircbotx.hooks.listeners.PrivateMessageListener;
import org.pircbotx.hooks.listeners.QuitListener;
import org.pircbotx.hooks.listeners.RemoveChannelBanListener;
import org.pircbotx.hooks.listeners.RemoveChannelKeyListener;
import org.pircbotx.hooks.listeners.RemoveChannelLimitListener;
import org.pircbotx.hooks.listeners.RemoveInviteOnlyListener;
import org.pircbotx.hooks.listeners.RemoveModeratedListener;
import org.pircbotx.hooks.listeners.RemoveNoExternalMessagesListener;
import org.pircbotx.hooks.listeners.RemovePrivateListener;
import org.pircbotx.hooks.listeners.RemoveSecretListener;
import org.pircbotx.hooks.listeners.RemoveTopicProtectionListener;
import org.pircbotx.hooks.listeners.ServerPingListener;
import org.pircbotx.hooks.listeners.ServerResponseListener;
import org.pircbotx.hooks.listeners.SetChannelBanListener;
import org.pircbotx.hooks.listeners.SetChannelKeyListener;
import org.pircbotx.hooks.listeners.SetChannelLimitListener;
import org.pircbotx.hooks.listeners.SetInviteOnlyListener;
import org.pircbotx.hooks.listeners.SetModeratedListener;
import org.pircbotx.hooks.listeners.SetNoExternalMessagesListener;
import org.pircbotx.hooks.listeners.SetPrivateListener;
import org.pircbotx.hooks.listeners.SetSecretListener;
import org.pircbotx.hooks.listeners.SetTopicProtectionListener;
import org.pircbotx.hooks.listeners.TimeListener;
import org.pircbotx.hooks.listeners.TopicListener;
import org.pircbotx.hooks.listeners.UnknownListener;
import org.pircbotx.hooks.listeners.UserListListener;
import org.pircbotx.hooks.listeners.UserModeListener;
import org.pircbotx.hooks.listeners.VersionListener;
import org.pircbotx.hooks.listeners.VoiceListener;

/**
 * TODO
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface ListenerAdapterInterface extends ActionListener, ChannelInfoListener, ConnectListener, DeopListener, DeVoiceListener, DisconnectListener, FileTransferFinishedListener, FingerListener, IncomingChatRequestListener, IncomingFileTransferListener, InviteListener, JoinListener, KickListener, MessageListener, ModeListener, MotdListener, NickChangeListener, NoticeListener, OpListener, PartListener, PingListener, PrivateMessageListener, QuitListener, RemoveChannelBanListener, RemoveChannelKeyListener, RemoveChannelLimitListener, RemoveInviteOnlyListener, RemoveModeratedListener, RemoveNoExternalMessagesListener, RemovePrivateListener, RemoveSecretListener, RemoveTopicProtectionListener, ServerPingListener, ServerResponseListener, SetChannelBanListener, SetChannelKeyListener, SetChannelLimitListener, SetInviteOnlyListener, SetModeratedListener, SetNoExternalMessagesListener, SetPrivateListener, SetSecretListener, SetTopicProtectionListener, TimeListener, TopicListener, UnknownListener, UserListListener, UserModeListener, VersionListener, VoiceListener {
}
