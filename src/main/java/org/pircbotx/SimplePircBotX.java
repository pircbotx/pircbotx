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

import java.util.HashSet;
import java.util.Set;
import org.pircbotx.exception.UnknownEventException;
import org.pircbotx.hooks.helpers.Event;
import org.pircbotx.hooks.helpers.BaseSimpleListener;
import org.pircbotx.hooks.helpers.HookUtils;
import org.pircbotx.hooks.helpers.ListenerManager;
import org.pircbotx.hooks.helpers.MetaSimpleListenerInterface;

/**
 * This a simple extendable version of {@link PircBotX } that doesn't require
 * separate listeners. All methods are mapped to a respective listener
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class SimplePircBotX extends PircBotX implements MetaSimpleListenerInterface {
	public SimplePircBotX() {
		setListeners(new SimpleListenerManager());
	}

	public void onAction(User source, Channel chanTarget, String action) {
	}

	public void onChannelInfo(Set<ChannelListEntry> list) {
	}

	public void onConnect() {
	}

	public void onDeop(Channel channel, User source, User recipient) {
	}

	public void onDeVoice(Channel channel, User source, User recipient) {
	}

	public void onDisconnect() {
	}

	public void onFileTransferFinished(DccFileTransfer transfer, Exception e) {
	}

	public void onFinger(User source, Channel channel) {
	}

	public void onIncomingChatRequest(DccChat chat) {
	}

	public void onIncomingFileTransfer(DccFileTransfer transfer) {
	}

	public void onInvite(User source, Channel channel) {
	}

	public void onJoin(Channel channel, User user) {
	}

	public void onKick(Channel channel, User kicker, User recipient, String reason) {
	}

	public void onMessage(Channel channel, User user, String message) {
	}

	public void onMode(Channel channel, User source, String mode) {
	}

	public void onNickChange(String oldNick, String newNick, User user) {
	}

	public void onNotice(User source, Channel target, String notice) {
	}

	public void onOp(Channel channel, User source, User recipient) {
	}

	public void onPart(Channel channel, User sender) {
	}

	public void onPing(User source, Channel target, String pingValue) {
	}

	public void onPrivateMessage(User sender, String message) {
	}

	public void onQuit(User source, String reason) {
	}

	public void onRemoveChannelBan(Channel channel, User source, String hostmask) {
	}

	public void onRemoveChannelKey(Channel channel, User source, String key) {
	}

	public void onRemoveChannelLimit(Channel channel, User user) {
	}

	public void onRemoveInviteOnly(Channel channel, User user) {
	}

	public void onRemoveModerated(Channel channel, User user) {
	}

	public void onRemoveNoExternalMessages(Channel channel, User user) {
	}

	public void onRemovePrivate(Channel channel, User user) {
	}

	public void onRemoveSecret(Channel channel, User user) {
	}

	public void onRemoveTopicProtection(Channel channel, User user) {
	}

	public void onServerPing(String response) {
	}

	public void onServerResponse(int code, String response) {
	}

	public void onSetChannelBan(Channel channel, User source, String hostmask) {
	}

	public void onSetChannelKey(Channel channel, User source, String key) {
	}

	public void onSetChannelLimit(Channel channel, User source, int limit) {
	}

	public void onSetInviteOnly(Channel channel, User user) {
	}

	public void onSetModerated(Channel channel, User user) {
	}

	public void onSetNoExternalMessages(Channel channel, User user) {
	}

	public void onSetPrivate(Channel channel, User user) {
	}

	public void onSetSecret(Channel channel, User source) {
	}

	public void onSetTopicProtection(Channel channel, User source) {
	}

	public void onTime(User source, Channel target) {
	}

	public void onTopic(Channel channel, String topic, User setBy, boolean changed) {
	}

	public void onUnknown(String line) {
	}

	public void onUserList(Channel channel, Set<User> users) {
	}

	public void onUserMode(User target, User source, String mode) {
	}

	public void onVersion(User source, Channel target) {
	}

	public void onVoice(Channel channel, User source, User recipient) {
	}

	public void onMotd(String motd) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public class SimpleListenerManager implements ListenerManager<BaseSimpleListener> {
		protected Set<BaseSimpleListener> listeners = new HashSet<BaseSimpleListener>();

		public void addListener(BaseSimpleListener listener) {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public void removeListener(BaseSimpleListener listener) {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public Set<BaseSimpleListener> getListeners() {
			throw new UnsupportedOperationException("SimplePircBotX uses built in methods as listeners, not seperate ones");
		}

		public void dispatchEvent(Event baseEvent) throws UnknownEventException {
			HookUtils.callSimpleListener(baseEvent, this);
		}
	}
}
