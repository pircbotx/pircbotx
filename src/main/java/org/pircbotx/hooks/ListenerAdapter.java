/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.*;

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
 * will be called!
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public abstract class ListenerAdapter<T extends PircBotX> implements Listener<T> {
	public void onEvent(Event<T> event) throws Exception {
		if (event instanceof ActionEvent)
			onAction((ActionEvent<T>) event);
		else if (event instanceof ChannelInfoEvent)
			onChannelInfo((ChannelInfoEvent<T>) event);
		else if (event instanceof ConnectEvent)
			onConnect((ConnectEvent<T>) event);
		else if (event instanceof DisconnectEvent)
			onDisconnect((DisconnectEvent<T>) event);
		else if (event instanceof FingerEvent)
			onFinger((FingerEvent<T>) event);
		else if (event instanceof HalfOpEvent)
			onHalfOp((HalfOpEvent<T>) event);
		else if (event instanceof IncomingChatRequestEvent)
			onIncomingChatRequest((IncomingChatRequestEvent<T>) event);
		else if (event instanceof IncomingFileTransferEvent)
			onIncomingFileTransfer((IncomingFileTransferEvent<T>) event);
		else if (event instanceof InviteEvent)
			onInvite((InviteEvent<T>) event);
		else if (event instanceof JoinEvent)
			onJoin((JoinEvent<T>) event);
		else if (event instanceof KickEvent)
			onKick((KickEvent<T>) event);
		else if (event instanceof MessageEvent)
			onMessage((MessageEvent<T>) event);
		else if (event instanceof ModeEvent)
			onMode((ModeEvent<T>) event);
		else if (event instanceof MotdEvent)
			onMotd((MotdEvent<T>) event);
		else if (event instanceof NickAlreadyInUseEvent)
			onNickAlreadyInUse((NickAlreadyInUseEvent<T>) event);
		else if (event instanceof NickChangeEvent)
			onNickChange((NickChangeEvent<T>) event);
		else if (event instanceof NoticeEvent)
			onNotice((NoticeEvent<T>) event);
		else if (event instanceof OpEvent)
			onOp((OpEvent<T>) event);
		else if (event instanceof OwnerEvent)
			onOwner((OwnerEvent<T>) event);
		else if (event instanceof PartEvent)
			onPart((PartEvent<T>) event);
		else if (event instanceof PingEvent)
			onPing((PingEvent<T>) event);
		else if (event instanceof PrivateMessageEvent)
			onPrivateMessage((PrivateMessageEvent<T>) event);
		else if (event instanceof QuitEvent)
			onQuit((QuitEvent<T>) event);
		else if (event instanceof RemoveChannelBanEvent)
			onRemoveChannelBan((RemoveChannelBanEvent<T>) event);
		else if (event instanceof RemoveChannelKeyEvent)
			onRemoveChannelKey((RemoveChannelKeyEvent<T>) event);
		else if (event instanceof RemoveChannelLimitEvent)
			onRemoveChannelLimit((RemoveChannelLimitEvent<T>) event);
		else if (event instanceof RemoveInviteOnlyEvent)
			onRemoveInviteOnly((RemoveInviteOnlyEvent<T>) event);
		else if (event instanceof RemoveModeratedEvent)
			onRemoveModerated((RemoveModeratedEvent<T>) event);
		else if (event instanceof RemoveNoExternalMessagesEvent)
			onRemoveNoExternalMessages((RemoveNoExternalMessagesEvent<T>) event);
		else if (event instanceof RemovePrivateEvent)
			onRemovePrivate((RemovePrivateEvent<T>) event);
		else if (event instanceof RemoveSecretEvent)
			onRemoveSecret((RemoveSecretEvent<T>) event);
		else if (event instanceof RemoveTopicProtectionEvent)
			onRemoveTopicProtection((RemoveTopicProtectionEvent<T>) event);
		else if (event instanceof ServerPingEvent)
			onServerPing((ServerPingEvent<T>) event);
		else if (event instanceof ServerResponseEvent)
			onServerResponse((ServerResponseEvent<T>) event);
		else if (event instanceof SetChannelBanEvent)
			onSetChannelBan((SetChannelBanEvent<T>) event);
		else if (event instanceof SetChannelKeyEvent)
			onSetChannelKey((SetChannelKeyEvent<T>) event);
		else if (event instanceof SetChannelLimitEvent)
			onSetChannelLimit((SetChannelLimitEvent<T>) event);
		else if (event instanceof SetInviteOnlyEvent)
			onSetInviteOnly((SetInviteOnlyEvent<T>) event);
		else if (event instanceof SetModeratedEvent)
			onSetModerated((SetModeratedEvent<T>) event);
		else if (event instanceof SetNoExternalMessagesEvent)
			onSetNoExternalMessages((SetNoExternalMessagesEvent<T>) event);
		else if (event instanceof SetPrivateEvent)
			onSetPrivate((SetPrivateEvent<T>) event);
		else if (event instanceof SetSecretEvent)
			onSetSecret((SetSecretEvent<T>) event);
		else if (event instanceof SetTopicProtectionEvent)
			onSetTopicProtection((SetTopicProtectionEvent<T>) event);
		else if (event instanceof SocketConnectEvent)
			onSocketConnect((SocketConnectEvent<T>) event);
		else if (event instanceof SuperOpEvent)
			onSuperOp((SuperOpEvent<T>) event);
		else if (event instanceof TimeEvent)
			onTime((TimeEvent<T>) event);
		else if (event instanceof TopicEvent)
			onTopic((TopicEvent<T>) event);
		else if (event instanceof UnknownEvent)
			onUnknown((UnknownEvent<T>) event);
		else if (event instanceof UserListEvent)
			onUserList((UserListEvent<T>) event);
		else if (event instanceof UserModeEvent)
			onUserMode((UserModeEvent<T>) event);
		else if (event instanceof VersionEvent)
			onVersion((VersionEvent<T>) event);
		else if (event instanceof VoiceEvent)
			onVoice((VoiceEvent<T>) event);
		else if (event instanceof WhoisEvent)
			onWhois((WhoisEvent<T>) event);

		//Generic methods
		if (event instanceof GenericCTCPEvent)
			onGenericCTCP((GenericCTCPEvent<T>) event);
		if (event instanceof GenericUserModeEvent)
			onGenericUserMode((GenericUserModeEvent<T>) event);
		if (event instanceof GenericChannelModeEvent)
			onGenericChannelMode((GenericChannelModeEvent<T>) event);
		if (event instanceof GenericDCCEvent)
			onGenericDCC((GenericDCCEvent<T>) event);
		if (event instanceof GenericMessageEvent)
			onGenericMessage((GenericMessageEvent<T>) event);
		if (event instanceof GenericUserEvent)
			onGenericUser((GenericUserEvent<T>) event);
		if (event instanceof GenericChannelEvent)
			onGenericChannel((GenericChannelEvent<T>) event);
		if (event instanceof GenericChannelUserEvent)
			onGenericChannelUser((GenericChannelUserEvent<T>) event);
	}

	public void onAction(ActionEvent<T> event) throws Exception {
	}

	public void onChannelInfo(ChannelInfoEvent<T> event) throws Exception {
	}

	public void onConnect(ConnectEvent<T> event) throws Exception {
	}

	public void onDisconnect(DisconnectEvent<T> event) throws Exception {
	}

	public void onFinger(FingerEvent<T> event) throws Exception {
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

	public void onNickAlreadyInUse(NickAlreadyInUseEvent<T> event) throws Exception {
	}

	public void onNickChange(NickChangeEvent<T> event) throws Exception {
	}

	public void onNotice(NoticeEvent<T> event) throws Exception {
	}

	public void onOp(OpEvent<T> event) throws Exception {
	}

	public void onOwner(OwnerEvent<T> event) throws Exception {
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

	public void onSocketConnect(SocketConnectEvent<T> event) throws Exception {
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

	public void onWhois(WhoisEvent<T> event) throws Exception {
	}

	public void onGenericCTCP(GenericCTCPEvent<T> event) throws Exception {
	}

	public void onGenericUserMode(GenericUserModeEvent<T> event) throws Exception {
	}

	public void onGenericChannelMode(GenericChannelModeEvent<T> event) throws Exception {
	}

	public void onGenericDCC(GenericDCCEvent<T> event) throws Exception {
	}

	public void onGenericMessage(GenericMessageEvent<T> event) throws Exception {
	}

	public void onGenericChannel(GenericChannelEvent<T> event) throws Exception {
	}

	public void onGenericUser(GenericUserEvent<T> event) throws Exception {
	}

	public void onGenericChannelUser(GenericChannelUserEvent<T> event) throws Exception {
	}
}
