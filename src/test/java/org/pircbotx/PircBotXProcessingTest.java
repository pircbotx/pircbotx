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

import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Usability tests for PircBotX that test how PircBotX handles lines and events.
 * Any other tests not involving processing lines should be in PircBotXTest
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXProcessingTest {
	final String aString = "I'm some super long string that has multiple words";
	protected List<Event> events;
	protected PircBotX bot;

	/**
	 * General bot setup: Use GenericListenerManager (no threading), add custom
	 * listener to add all called events to Event set, set nick, etc
	 */
	@BeforeMethod
	public void setUp() {
		bot = new PircBotX();
		events = new ArrayList<Event>();
		bot.setListenerManager(new GenericListenerManager());
		bot.getListenerManager().addListener(new Listener() {
			public void onEvent(Event event) throws Exception {
				events.add(event);
			}
		});
		bot.setNick("PircBotXBot");
		bot.setName("PircBotXBot");
	}

	@Test(description = "Verifies UserModeEvent from user mode being changed")
	public void userModeTest() {
		//Use two users to differentiate between source and target
		User aUser = bot.getUser("PircBotXUser");
		User aUser2 = bot.getUser("PircBotXUser2");
		bot.handleLine(":PircBotXUser MODE PircBotXUser2 :+i");

		//Verify event contents
		UserModeEvent uevent = getEvent(events, UserModeEvent.class, "UserModeEvent not dispatched on change");
		assertEquals(uevent.getSource(), aUser, "UserModeEvent's source does not match given");
		assertEquals(uevent.getTarget(), aUser2, "UserModeEvent's target does not match given");
		assertEquals(uevent.getMode(), "+i", "UserModeEvent's mode does not match given");
	}

	@Test(description = "Verifies ChannelInfoEvent from /LIST command with 3 channels")
	public void listTest() {
		bot.handleLine(":irc.someserver.net 321 Channel :Users Name");
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel 99 :" + aString);
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel1 100 :" + aString + aString);
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel2 101 :" + aString + aString + aString);
		bot.handleLine(":irc.someserver.net 323 :End of /LIST");
		ChannelInfoEvent cevent = getEvent(events, ChannelInfoEvent.class, "No ChannelInfoEvent dispatched");

		//Verify event contents
		Set<ChannelListEntry> channels = cevent.getList();
		assertEquals(channels.size(), 3);
		boolean channelParsed = false;
		for (ChannelListEntry entry : channels)
			if (entry.getName().equals("#PircBotXChannel1")) {
				assertEquals(entry.getName(), "#PircBotXChannel1");
				assertEquals(entry.getTopic(), aString + aString);
				assertEquals(entry.getUsers(), 100);
				channelParsed = true;
			}
		assertTrue(channelParsed, "Channel #PircBotXChannel1 not found in /LIST results!");
	}

	@Test(description = "Verifies InviteEvent from incomming invite")
	public void inviteTest() {
		bot.handleLine(":AUser!~ALogin@some.host INVITE PircBotXUser :#aChannel");

		//Verify event values
		InviteEvent ievent = getEvent(events, InviteEvent.class, "No InviteEvent dispatched");
		assertEquals(ievent.getUser(), "AUser", "InviteEvent user is wrong");
		assertEquals(ievent.getChannel(), "#aChannel", "InviteEvent channel is wrong");

		//Make sure the event doesn't create a user or a channel
		assertFalse(bot.channelExists("#aChannel"), "InviteEvent created channel, shouldn't have");
		assertFalse(bot.userExists("AUser"), "InviteEvent created user, shouldn't have");
	}

	@Test(description = "Verifies JoinEvent from user joining our channel")
	public void joinTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host JOIN :#aChannel");

		//Make sure the event gives us the same channels
		JoinEvent jevent = getEvent(events, JoinEvent.class, "No aChannel dispatched");
		assertEquals(jevent.getChannel(), aChannel, "Event's channel does not match origional channel");
		assertEquals(jevent.getUser(), aUser, "Event's user does not match origional user");

		//Make sure user info was updated
		assertEquals(aUser.getLogin(), "~ALogin", "User login wrong on JoinEvent");
		assertEquals(aUser.getHostmask(), "some.host", "User hostmask wrong on JoinEvent");
		Channel userChan = null;
		for (Channel curChan : aUser.getChannels())
			if (curChan.getName().equals("#aChannel"))
				userChan = curChan;
		assertNotNull(userChan, "User is not joined to channel after JoinEvent");
		User chanUser = null;
		for (User curUser : aChannel.getUsers())
			if (curUser.getNick().equals("AUser"))
				chanUser = curUser;
		assertNotNull(chanUser, "Channel is not joined to user after JoinEvent");
		assertTrue(bot.userExists("AUser"));
	}

	@Test(description = "Verify TopicEvent from /JOIN or /TOPIC #channel commands")
	public void topicTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		bot.handleLine(":irc.someserver.net 332 PircBotXUser #aChannel :" + aString + aString);
		assertEquals(aChannel.getTopic(), aString + aString);
	}

	@Test(description = "Verify TopicEvent's extended information from line sent after TOPIC line")
	public void topicInfoTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		bot.handleLine(":irc.someserver.net 333 PircBotXUser #aChannel AUser 1268522937");
		assertEquals(aChannel.getTopicSetter(), "AUser");
		assertEquals(aChannel.getTopicTimestamp(), (long) 1268522937 * 1000);

		TopicEvent tevent = getEvent(events, TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getChannel(), aChannel, "Event channel and origional channel do not match");
	}

	@Test(description = "Verify MessageEvent from channel message")
	public void messageTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :" + aString);

		//Verify event contents
		MessageEvent mevent = getEvent(events, MessageEvent.class, "MessageEvent not dispatched");
		assertEquals(mevent.getChannel(), aChannel, "Event channel and origional channel do not match");
		assertEquals(mevent.getUser(), aUser, "Event user and origional user do not match");
		assertEquals(mevent.getMessage(), aString, "Message sent does not match");
	}

	@Test(description = "Verify PrivateMessage from random user")
	public void privateMessageTest() {
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host PRIVMSG PircBotXUser :" + aString);

		//Verify event contents
		PrivateMessageEvent pevent = getEvent(events, PrivateMessageEvent.class, "MessageEvent not dispatched");
		assertEquals(pevent.getUser(), aUser, "Event user and origional user do not match");
		assertEquals(pevent.getMessage(), aString, "Message sent does not match");
	}

	@Test(description = "Verify NoticeEvent from NOTICE in channel")
	public void channelNoticeTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host NOTICE #aChannel :" + aString);

		//Verify event contents
		NoticeEvent nevent = getEvent(events, NoticeEvent.class, "NoticeEvent not dispatched for channel notice");
		assertEquals(nevent.getChannel(), aChannel, "NoticeEvent's channel does not match given");
		assertEquals(nevent.getUser(), aUser, "NoticeEvent's user does not match given");
		assertEquals(nevent.getNotice(), aString, "NoticeEvent's notice message does not match given");
	}

	/**
	 * Simulate a NOTICE sent directly to us
	 */
	@Test(description = "Verify NoticeEvent from NOTICE from a user in PM")
	public void userNoticeTest() {
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host NOTICE PircBotXUser :" + aString);

		//Verify event contents
		NoticeEvent nevent = getEvent(events, NoticeEvent.class, "NoticeEvent not dispatched for channel notice");
		assertNull(nevent.getChannel(), "NoticeEvent's channel isn't null during private notice");
		assertEquals(nevent.getUser(), aUser, "NoticeEvent's user does not match given");
		assertEquals(nevent.getNotice(), aString, "NoticeEvent's notice message does not match given");
	}

	/**
	 * Do setup and basic verification of channel modes
	 * @param bot
	 * @param events
	 * @param mode
	 */
	protected void initModeTest(String mode, boolean checkChannelMode) {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel " + mode);

		//Verify generic ModeEvent contents
		ModeEvent mevent = getEvent(events, ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given");
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given");
		assertEquals(mevent.getMode(), mode, "ModeEvent's mode does not match given mode");

		//Check channel mode if told to
		if (checkChannelMode)
			if (mode.substring(0, 1).equals("-"))
				assertEquals(aChannel.getMode(), "", "Channel mode not empty after removing only mode");
			else
				assertEquals(aChannel.getMode(), mode.substring(1), "Channels mode not updated");
	}

	@DataProvider
	public Object[][] channelUserModeProvider() {
		return new Object[][]{{"+o", OpEvent.class, "isOp"},
					{"-o", OpEvent.class, "isOp"},
					{"+v", VoiceEvent.class, "hasVoice"},
					{"-v", VoiceEvent.class, "hasVoice"},
					{"+q", OwnerEvent.class, "isOwner"},
					{"-q", OwnerEvent.class, "isOwner"},
					{"+h", HalfOpEvent.class, "isHalfOp"},
					{"-h", HalfOpEvent.class, "isHalfOp"},
					{"+a", SuperOpEvent.class, "isSuperOp"},
					{"-a", SuperOpEvent.class, "isSuperOp"}};
	}

	@Test(dataProvider = "channelUserModeProvider", description = "Test setting various user modes and verifying events")
	public void channelUserModeTest(String mode, Class<?> eventClass, String checkMethod) throws Exception {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		User otherUser = bot.getUser("OtherUser");
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel " + mode + " OtherUser");

		//Verify generic ModeEvent contents
		ModeEvent mevent = getEvent(events, ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given with mode " + mode);
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given with mode " + mode);
		assertEquals(mevent.getMode(), mode + " OtherUser", "ModeEvent's mode does not match given mode");
		
		//Verify specific event contents
		GenericUserModeEvent event = (GenericUserModeEvent)getEvent(events, eventClass, "No " + eventClass.getSimpleName() + " dispatched with " + mode);
		assertEquals(event.getChannel(), aChannel, eventClass.getSimpleName() + "'s channel does not match given with mode " + mode);
		assertEquals(event.getSource(), aUser, eventClass.getSimpleName() + "'s source user does not match given with mode " + mode);
		assertEquals(event.getRecipient(), otherUser, eventClass.getSimpleName() + "'s recipient user does not match given with mode " + mode);
		
		//Make sure the event's is* method returns the correct value
		assertEquals(eventClass.getMethod(checkMethod).invoke(event), mode.startsWith("+"), "Event's " + checkMethod + " method doesn't return correct value");
		
		//Make sure the channels is* method returns the correct value
		assertEquals(aChannel.getClass().getMethod(checkMethod, User.class).invoke(aChannel, otherUser), mode.startsWith("+"), "Channels's " + checkMethod + " method doesn't return correct value");
	}

	@DataProvider
	protected Object[][] channelModeProvider() {
		return new Object[][]{{"+l 10", null, SetChannelLimitEvent.class},
					{"-l", "l 10", RemoveChannelLimitEvent.class},
					{"+k testPassword", null, SetChannelKeyEvent.class},
					{"-k", "k testPassword", RemoveChannelKeyEvent.class},
					{"-k testPassword", "k testPassword", RemoveChannelKeyEvent.class},
					{"+i", null, SetInviteOnlyEvent.class},
					{"-i", null, RemoveInviteOnlyEvent.class},
					{"+n", null, SetNoExternalMessagesEvent.class},
					{"-n", null, RemoveNoExternalMessagesEvent.class},
					{"+s", null, SetSecretEvent.class},
					{"-s", null, RemoveSecretEvent.class},
					{"+t", null, SetTopicProtectionEvent.class},
					{"-t", null, RemoveTopicProtectionEvent.class},
					{"+p", null, SetPrivateEvent.class},
					{"-p", null, RemovePrivateEvent.class}};
	}

	@Test(dataProvider = "channelModeProvider", timeOut = 1000)
	public void genericChannelModeTest(String mode, String parentMode, Class<?> modeClass) {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		if (mode.startsWith("-"))
			//Set the mode first
			aChannel.setMode((parentMode != null) ? parentMode : mode.substring(1));
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel " + mode);

		//Verify generic ModeEvent contents
		ModeEvent mevent = getEvent(events, ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given");
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given");
		assertEquals(mevent.getMode(), mode, "ModeEvent's mode does not match given mode");

		//Verify generic mode event contents
		String modeName = modeClass.getSimpleName();
		GenericChannelModeEvent subEvent = (GenericChannelModeEvent) getEvent(events, modeClass, "No " + modeName + " dispatched with " + mode);
		assertEquals(subEvent.getChannel(), aChannel, modeName + "'s channel does not match given");
		assertEquals(subEvent.getUser(), aUser, modeName + "'s user does not match given");

		//Check channel mode
		if (parentMode == null && !mode.contains(" "))
			if (mode.startsWith("-"))
				assertEquals(aChannel.getMode(), "", "Channel mode not empty after removing only mode");
			else
				assertEquals(aChannel.getMode(), mode.substring(1), "Channels mode not updated");
	}

	@Test(dependsOnMethods = "genericChannelModeTest", description = "Verify SetChannelKeyEvent has the correct key")
	public void setChannelKeyEventTest() {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel +k testPassword");
		SetChannelKeyEvent event = getEvent(events, SetChannelKeyEvent.class, "No SetChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getKey(), "testPassword", "SetChannelKeyEvent key doesn't match given");
	}
	
	@Test(dependsOnMethods = "genericChannelModeTest", description = "Verify RemoveChannelKeyEvent has a null key when not specified")
	public void removeChannelKeyEventEmptyTest() {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel -k");
		RemoveChannelKeyEvent event = getEvent(events, RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertNull(event.getKey(), "RemoveChannelKeyEvent key doesn't match given");
	}
	
	@Test(dependsOnMethods = "genericChannelModeTest", description = "Verify RemoveChannelKeyEvent has the correct key")
	public void removeChannelKeyEventTest() {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel -k testPassword");
		RemoveChannelKeyEvent event = getEvent(events, RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getKey(), "testPassword", "RemoveChannelKeyEvent key doesn't match given");
	}

	@Test(dependsOnMethods = "genericChannelModeTest", description = "Verify SetChannelLimitEvent has the correct limit")
	public void setChannelLimitEvent() {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel +l 10");
		SetChannelLimitEvent event = getEvent(events, SetChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getLimit(), 10, "SetChannelLimitEvent key doesn't match given");
	}

	@Test(description = "Verify NAMES response handling")
	public void namesTest() {
		bot.handleLine(":irc.someserver.net 353 PircBotXUser = #aChannel :AUser @+OtherUser");

		//Make sure all information was created correctly
		assertTrue(bot.channelExists("#aChannel"), "NAMES response didn't create channel");
		assertTrue(bot.userExists("AUser"), "NAMES response didn't create user AUser");
		assertTrue(bot.userExists("OtherUser"), "NAMES response didn't create user OtherUser");
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		User otherUser = bot.getUser("OtherUser");

		//Make sure channel contains the users
		assertTrue(aChannel.getUsers().contains(aUser), "Channel doesn't contain AUser from NAMES response");
		assertTrue(aChannel.getUsers().contains(otherUser), "Channel doesn't contain OtherUser from NAMES response");

		//Make sure channel classifies the users correctly
		assertTrue(aChannel.isOp(otherUser), "NAMES response doesn't give user op status");
		assertTrue(aChannel.hasVoice(otherUser), "NAMES response doesn't give user voice status");
		assertTrue(aChannel.getNormalUsers().contains(aUser), "NAMES response doesn't give AUser normal status in channel");
	}

	/**
	 * Simulate WHO response. 
	 */
	@Test(description = "Verify WHO response handling + UserListEvent")
	public void whoTest() {
		bot.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~ALogin some.host irc.someserver.net AUser H@+ :2 " + aString);
		bot.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~OtherLogin some.host1 irc.otherserver.net OtherUser G :4 " + aString);
		bot.handleLine(":irc.someserver.net 315 PircBotXUser #aChannel :End of /WHO list.");

		//Make sure all information was created correctly
		assertTrue(bot.channelExists("#aChannel"), "WHO response didn't create channel");
		assertTrue(bot.userExists("AUser"), "WHO response didn't create user AUser");
		assertTrue(bot.userExists("OtherUser"), "WHO response didn't create user OtherUser");
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		User otherUser = bot.getUser("OtherUser");
		assertNotNull(aChannel, "Channel from WHO response is null");
		assertNotNull(aUser, "AUser from WHO response is null");
		assertNotNull(otherUser, "OtherUser from WHO response is null");

		//Verify event
		UserListEvent uevent = getEvent(events, UserListEvent.class, "UserListEvent not dispatched");
		assertEquals(uevent.getChannel(), aChannel, "UserListEvent's channel does not match given");
		assertEquals(uevent.getUsers().size(), 2, "UserListEvent's users is larger than it should be");
		assertTrue(uevent.getUsers().contains(aUser), "UserListEvent doesn't contain aUser");
		assertTrue(uevent.getUsers().contains(aUser), "UserListEvent doesn't contain OtherUser");

		//Verify AUser
		assertEquals(aUser.getNick(), "AUser", "Login doesn't match one given during WHO");
		assertEquals(aUser.getLogin(), "~ALogin", "Login doesn't match one given during WHO");
		assertEquals(aUser.getHostmask(), "some.host", "Host doesn't match one given during WHO");
		assertEquals(aUser.getHops(), 2, "Hops doesn't match one given during WHO");
		assertEquals(aUser.getRealName(), aString, "RealName doesn't match one given during WHO");
		assertEquals(aUser.getServer(), "irc.someserver.net", "Server doesn't match one given during WHO");
		assertFalse(aUser.isAway(), "User is away even though specified as here in WHO");
		assertTrue(aChannel.isOp(aUser), "User isn't labeled as an op even though specified as one in WHO");
		assertTrue(aChannel.hasVoice(aUser), "User isn't voiced even though specified as one in WHO");

		//Verify otherUser
		assertEquals(otherUser.getNick(), "OtherUser", "Login doesn't match one given during WHO");
		assertEquals(otherUser.getLogin(), "~OtherLogin", "Login doesn't match one given during WHO");
		assertEquals(otherUser.getHostmask(), "some.host1", "Host doesn't match one given during WHO");
		assertEquals(otherUser.getHops(), 4, "Hops doesn't match one given during WHO");
		assertEquals(otherUser.getRealName(), aString, "RealName doesn't match one given during WHO");
		assertEquals(otherUser.getServer(), "irc.otherserver.net", "Server doesn't match one given during WHO");
		assertTrue(otherUser.isAway(), "User is not away though specified as here in WHO");
		assertFalse(aChannel.isOp(otherUser), "User is labeled as an op even though specified as one in WHO");
		assertFalse(aChannel.hasVoice(otherUser), "User is labeled as voiced even though specified as one in WHO");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify KickEvent from some user kicking another user")
	public void kickTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		User otherUser = bot.getUser("OtherUser");
		bot.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		bot.handleLine(":AUser!~ALogin@some.host KICK #aChannel OtherUser :" + aString);

		//Verify event contents
		KickEvent kevent = getEvent(events, KickEvent.class, "KickEvent not dispatched");
		assertEquals(kevent.getChannel(), aChannel, "KickEvent channel does not match");
		assertEquals(kevent.getSource(), aUser, "KickEvent's getSource doesn't match kicker user");
		assertEquals(kevent.getRecipient(), otherUser, "KickEvent's getRecipient doesn't match kickee user");
		assertEquals(kevent.getReason(), aString, "KickEvent's reason doesn't match given one");

		//Make sure we've sufficently forgotten about the user
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that was kicked an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that was kicked with voice");
		assertFalse(bot.userExists("OtherUser"), "Bot still considers user to exist after kick");
		assertNotSame(bot.getUser("OtherUser"), otherUser, "User fetched with getUser and previous user object are exactly the same");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify QuitEvent from user that just joined quitting")
	public void quitTest() {
		Channel aChannel = bot.getChannel("#aChannel");
		User otherUser = bot.getUser("OtherUser");
		bot.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel +o OtherUser");
		bot.handleLine(":AUser!~ALogin@some.host MODE #aChannel +v OtherUser");
		bot.handleLine(":OtherUser!~OtherLogin@some.host1 QUIT :" + aString);

		//Check event contents
		QuitEvent qevent = getEvent(events, QuitEvent.class, "QuitEvent not dispatched");
		//Since QuitEvent gives us a snapshot, compare contents
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), aString, "QuitEvent's reason does not match given");

		//Make sure user is gone
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that quit an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that quit with voice");
		assertFalse(bot.userExists("OtherUser"), "Bot still considers user to exist after quit");
		assertTrue(otherUser.getChannels().isEmpty(), "User still connected to other channels after quit");
		assertFalse(aChannel.getUsers().contains(otherUser), "Channel still associated with user that quit");
		assertNotSame(bot.getUser("OtherUser"), otherUser, "User fetched with getUser and previous user object are exactly the same");
	}

	@Test(dependsOnMethods = "quitTest", description = "Verify QuitEvent with no message")
	public void quitTest2() {
		User otherUser = bot.getUser("OtherUser");
		bot.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		bot.handleLine(":OtherUser!~OtherLogin@some.host1 QUIT :");

		//Check event contents
		QuitEvent qevent = getEvent(events, QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), "", "QuitEvent's reason does not match given");
	}

	/**
	 * After simulating a server response, call this to get a specific Event from
	 * the Event set. Note that if the event does not exist an Assertion error will
	 * be thrown. Also note that only the last occurence of the event will be fetched
	 * @param <B> The event type to be fetched
	 * @param clazz The class of the event type
	 * @param errorMessage An error message if the event type does not exist
	 * @return A single requested event
	 */
	protected <B> B getEvent(List<Event> events, Class<B> clazz, String errorMessage) {
		B cevent = null;
		for (Event curEvent : events)
			if (curEvent.getClass().isAssignableFrom(clazz))
				cevent = (B) curEvent;
		//Failed, does not exist
		assertNotNull(cevent, errorMessage);
		return cevent;
	}
}