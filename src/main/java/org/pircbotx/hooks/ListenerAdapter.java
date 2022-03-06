/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.*;

/**
 * Adapter that provides methods to capture each event separately, removing the
 * need to check, cast, and call your custom method for each event you want to
 * capture.
 * <p>
 * To use, simply override the method that has the event you want to capture.
 * <p>
 * <b>WARNING:</b> If you are going to be implementing {@link Listener}'s
 * {@link Listener#onEvent(org.pircbotx.hooks.Event) } method, you must call
 * <code>super.onEvent(event)</code>, otherwise none of the Adapter hook methods
 * will be called!
 */
public abstract class ListenerAdapter implements Listener {
	public void onEvent(Event event) throws Exception {
		//While reflection would make this significantly shorter, 
		//nothing beats if statements in performance.
		//Also polymorphism, while theoretically correct, just means that this code
		//would be in every single Event and make an explicit dependency on this.
		//This is just simple and fast
		if (event instanceof ActionEvent)
			onAction((ActionEvent) event);
		else if (event instanceof BanListEvent)
			onBanList((BanListEvent) event);
		else if (event instanceof QuietListEvent)
			onQuietList((QuietListEvent) event);
		else if (event instanceof ChannelInfoEvent)
			onChannelInfo((ChannelInfoEvent) event);
		else if (event instanceof ConnectEvent)
			onConnect((ConnectEvent) event);
		else if (event instanceof ConnectAttemptFailedEvent)
			onConnectAttemptFailed((ConnectAttemptFailedEvent) event);
		else if (event instanceof DisconnectEvent)
			onDisconnect((DisconnectEvent) event);
		else if (event instanceof FingerEvent)
			onFinger((FingerEvent) event);
		else if (event instanceof HalfOpEvent)
			onHalfOp((HalfOpEvent) event);
		else if (event instanceof IncomingChatRequestEvent)
			onIncomingChatRequest((IncomingChatRequestEvent) event);
		else if (event instanceof IncomingFileTransferEvent)
			onIncomingFileTransfer((IncomingFileTransferEvent) event);
		else if (event instanceof FileTransferCompleteEvent)
			onFileTransferComplete((FileTransferCompleteEvent) event);
		else if (event instanceof InviteEvent)
			onInvite((InviteEvent) event);
		else if (event instanceof JoinEvent)
			onJoin((JoinEvent) event);
		else if (event instanceof KickEvent)
			onKick((KickEvent) event);
		else if (event instanceof MessageEvent)
			onMessage((MessageEvent) event);
		else if (event instanceof ModeEvent)
			onMode((ModeEvent) event);
		else if (event instanceof MotdEvent)
			onMotd((MotdEvent) event);
		else if (event instanceof NickAlreadyInUseEvent)
			onNickAlreadyInUse((NickAlreadyInUseEvent) event);
		else if (event instanceof NickChangeEvent)
			onNickChange((NickChangeEvent) event);
		else if (event instanceof NoticeEvent)
			onNotice((NoticeEvent) event);
		else if (event instanceof OpEvent)
			onOp((OpEvent) event);		
		else if (event instanceof OperFailedEvent)
			onOperFailed((OperFailedEvent) event);
		else if (event instanceof OperSuccessEvent)
			onOperSuccess((OperSuccessEvent) event);		
		else if (event instanceof OutputEvent)
			onOutput((OutputEvent) event);
		else if (event instanceof OwnerEvent)
			onOwner((OwnerEvent) event);
		else if (event instanceof PartEvent)
			onPart((PartEvent) event);
		else if (event instanceof PingEvent)
			onPing((PingEvent) event);
		else if (event instanceof PrivateMessageEvent)
			onPrivateMessage((PrivateMessageEvent) event);
		else if (event instanceof QuitEvent)
			onQuit((QuitEvent) event);
		else if (event instanceof RemoveChannelBanEvent)
			onRemoveChannelBan((RemoveChannelBanEvent) event);
		else if (event instanceof RemoveChannelKeyEvent)
			onRemoveChannelKey((RemoveChannelKeyEvent) event);
		else if (event instanceof RemoveChannelLimitEvent)
			onRemoveChannelLimit((RemoveChannelLimitEvent) event);
		else if (event instanceof RemoveInviteOnlyEvent)
			onRemoveInviteOnly((RemoveInviteOnlyEvent) event);
		else if (event instanceof RemoveModeratedEvent)
			onRemoveModerated((RemoveModeratedEvent) event);
		else if (event instanceof RemoveNoExternalMessagesEvent)
			onRemoveNoExternalMessages((RemoveNoExternalMessagesEvent) event);
		else if (event instanceof RemovePrivateEvent)
			onRemovePrivate((RemovePrivateEvent) event);
		else if (event instanceof RemoveSecretEvent)
			onRemoveSecret((RemoveSecretEvent) event);
		else if (event instanceof RemoveTopicProtectionEvent)
			onRemoveTopicProtection((RemoveTopicProtectionEvent) event);
		else if (event instanceof ServerPingEvent)
			onServerPing((ServerPingEvent) event);
		else if (event instanceof ServerResponseEvent)
			onServerResponse((ServerResponseEvent) event);
		else if (event instanceof SetChannelBanEvent)
			onSetChannelBan((SetChannelBanEvent) event);
		else if (event instanceof SetChannelKeyEvent)
			onSetChannelKey((SetChannelKeyEvent) event);
		else if (event instanceof SetChannelLimitEvent)
			onSetChannelLimit((SetChannelLimitEvent) event);
		else if (event instanceof SetInviteOnlyEvent)
			onSetInviteOnly((SetInviteOnlyEvent) event);
		else if (event instanceof SetModeratedEvent)
			onSetModerated((SetModeratedEvent) event);
		else if (event instanceof SetNoExternalMessagesEvent)
			onSetNoExternalMessages((SetNoExternalMessagesEvent) event);
		else if (event instanceof SetPrivateEvent)
			onSetPrivate((SetPrivateEvent) event);
		else if (event instanceof SetSecretEvent)
			onSetSecret((SetSecretEvent) event);
		else if (event instanceof SetTopicProtectionEvent)
			onSetTopicProtection((SetTopicProtectionEvent) event);
		else if (event instanceof SocketConnectEvent)
			onSocketConnect((SocketConnectEvent) event);
		else if (event instanceof SuperOpEvent)
			onSuperOp((SuperOpEvent) event);
		else if (event instanceof TimeEvent)
			onTime((TimeEvent) event);
		else if (event instanceof TopicEvent)
			onTopic((TopicEvent) event);
		else if (event instanceof UnknownEvent)
			onUnknown((UnknownEvent) event);
		else if (event instanceof UserListEvent)
			onUserList((UserListEvent) event);
		else if (event instanceof UserModeEvent)
			onUserMode((UserModeEvent) event);
		else if (event instanceof VersionEvent)
			onVersion((VersionEvent) event);
		else if (event instanceof VoiceEvent)
			onVoice((VoiceEvent) event);		
		else if (event instanceof WhoisEvent)
			onWhois((WhoisEvent) event);
		else if (event instanceof WhoEvent)
			onWho((WhoEvent) event);	
		
		//Exception methods
		if (event instanceof ExceptionEvent)
			onException((ExceptionEvent) event);
		if (event instanceof ListenerExceptionEvent)
			onListenerException((ListenerExceptionEvent) event);

		//Generic methods
		if (event instanceof GenericCTCPEvent)
			onGenericCTCP((GenericCTCPEvent) event);
		if (event instanceof GenericUserModeEvent)
			onGenericUserMode((GenericUserModeEvent) event);
		if (event instanceof GenericChannelModeEvent)
			onGenericChannelMode((GenericChannelModeEvent) event);
		if (event instanceof GenericChannelModeRecipientEvent)
			onGenericChannelModeRecipient((GenericChannelModeRecipientEvent) event);
		if (event instanceof GenericDCCEvent)
			onGenericDCC((GenericDCCEvent) event);
		if (event instanceof GenericMessageEvent)
			onGenericMessage((GenericMessageEvent) event);
		if (event instanceof GenericUserEvent)
			onGenericUser((GenericUserEvent) event);
		if (event instanceof GenericChannelEvent)
			onGenericChannel((GenericChannelEvent) event);
		if (event instanceof GenericChannelUserEvent)
			onGenericChannelUser((GenericChannelUserEvent) event);
	}

	public void onAction(ActionEvent event) throws Exception {
	}

	public void onBanList(BanListEvent event) throws Exception {
	}

	public void onQuietList(QuietListEvent event) throws Exception {
	}

	public void onChannelInfo(ChannelInfoEvent event) throws Exception {
	}

	public void onConnect(ConnectEvent event) throws Exception {
	}

	public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
	}

	public void onDisconnect(DisconnectEvent event) throws Exception {
	}

	public void onException(ExceptionEvent event) throws Exception {
	}

	public void onFinger(FingerEvent event) throws Exception {
	}

	public void onHalfOp(HalfOpEvent event) throws Exception {
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent event) throws Exception {
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent event) throws Exception {
	}

	public void onFileTransferComplete(FileTransferCompleteEvent event) throws Exception {
	}

	public void onInvite(InviteEvent event) throws Exception {
	}

	public void onJoin(JoinEvent event) throws Exception {
	}

	public void onKick(KickEvent event) throws Exception {
	}

	public void onListenerException(ListenerExceptionEvent event) throws Exception {
	}

	public void onMessage(MessageEvent event) throws Exception {
	}

	public void onMode(ModeEvent event) throws Exception {
	}

	public void onMotd(MotdEvent event) throws Exception {
	}

	public void onNickAlreadyInUse(NickAlreadyInUseEvent event) throws Exception {
	}

	public void onNickChange(NickChangeEvent event) throws Exception {
	}

	public void onNotice(NoticeEvent event) throws Exception {
	}

	public void onOp(OpEvent event) throws Exception {
	}

	public void onOutput(OutputEvent event) throws Exception {
	}

	public void onOwner(OwnerEvent event) throws Exception {
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

	public void onSocketConnect(SocketConnectEvent event) throws Exception {
	}

	public void onSuperOp(SuperOpEvent event) throws Exception {
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

	public void onWhois(WhoisEvent event) throws Exception {
	}
	
	public void onWho(WhoEvent event) throws Exception {
	}	

	public void onGenericCTCP(GenericCTCPEvent event) throws Exception {
	}

	public void onGenericUserMode(GenericUserModeEvent event) throws Exception {
	}

	public void onGenericChannelMode(GenericChannelModeEvent event) throws Exception {
	}

	public void onGenericChannelModeRecipient(GenericChannelModeRecipientEvent event) throws Exception {
	}

	public void onGenericDCC(GenericDCCEvent event) throws Exception {
	}

	public void onGenericMessage(GenericMessageEvent event) throws Exception {
	}

	public void onGenericChannel(GenericChannelEvent event) throws Exception {
	}

	public void onGenericUser(GenericUserEvent event) throws Exception {
	}

	public void onGenericChannelUser(GenericChannelUserEvent event) throws Exception {
	}
	
	public void onOperSuccess(OperSuccessEvent event) throws Exception {
	}
	
	public void onOperFailed(OperFailedEvent event) throws Exception {
	}
}
