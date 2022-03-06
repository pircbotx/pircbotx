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
package org.pircbotx;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NotReadyException;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.BanListEvent;
import org.pircbotx.hooks.events.QuietListEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.events.WhoEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;
import org.pircbotx.snapshot.ChannelSnapshot;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import lombok.extern.slf4j.Slf4j;

/**
 * Usability tests for PircBotX that test how PircBotX handles lines and events.
 * Any other tests not involving processing lines should be in PircBotXTest
 * <p/>
 */
@Slf4j
@Test(singleThreaded = true)
public class InputParserTest {
	final static String aString = "I'm some super long string that has multiple words";
	protected UserChannelDao<User,Channel> dao;
	protected InputParser inputParser;
	protected TestPircBotX bot;

	/**
	 * General bot setup: Use GenericListenerManager (no threading), add custom
	 * listener to add all called events to Event set, set nick, etc
	 */
	@BeforeMethod
	public void setUp() {
		bot = new TestPircBotX(TestUtils.generateConfigurationBuilder());
		bot.nick = "TestBot";

		//Save objects into fields for easier access
		this.dao = bot.getUserChannelDao();
		this.inputParser = bot.getInputParser();
	}

	@Test(description = "Verifies UserModeEvent from user mode being changed")
	public void userModeTest() throws IOException, IrcException {
		//Use two users to differentiate between source and target
		User aUser = TestUtils.generateTestUserSource(bot);
		User aUser2 = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + aUser.getNick() + " MODE " + aUser2.getNick() + " :+i");

		//Verify event contents
		UserModeEvent uevent = bot.getTestEvent(UserModeEvent.class, "UserModeEvent not dispatched on change");
		assertEquals(uevent.getUser(), aUser, "UserModeEvent's source does not match given");
		assertEquals(uevent.getRecipient(), aUser2, "UserModeEvent's target does not match given");
		assertEquals(uevent.getMode(), "+i", "UserModeEvent's mode does not match given");
	}

	@Test(description = "Verifies ChannelInfoEvent from /LIST command with 3 channels")
	public void listTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 321 Channel :Users Name");
		inputParser.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel 99 :" + aString);
		inputParser.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel1 100 :" + aString + aString);
		inputParser.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel2 101 :" + aString + aString + aString);
		inputParser.handleLine(":irc.someserver.net 323 :End of /LIST");
		ChannelInfoEvent cevent = bot.getTestEvent(ChannelInfoEvent.class, "No ChannelInfoEvent dispatched");

		//Verify event contents
		ImmutableList<ChannelListEntry> channels = cevent.getList();
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
	public void inviteTest() throws IOException, IrcException {
		UserHostmask sourceUser = TestUtils.generateTestUserSourceHostmask(bot);
		inputParser.handleLine(":" + sourceUser.getHostmask() + " INVITE PircBotXUser :#aChannel");

		//Verify event values
		InviteEvent ievent = bot.getTestEvent(InviteEvent.class, "No InviteEvent dispatched");
		assertEquals(ievent.getUserHostmask(), sourceUser, "InviteEvent user is wrong");
		assertNull(ievent.getUser(), "Invite from unknown user should be null");
		assertEquals(ievent.getChannel(), "#aChannel", "InviteEvent channel is wrong");

		//Make sure the event doesn't create a user or a channel
		assertFalse(dao.containsChannel("#aChannel"), "InviteEvent created channel, shouldn't have");
		assertFalse(dao.containsUser(sourceUser), "InviteEvent created user, shouldn't have");
	}

	@Test(description = "Verifies JoinEvent from user joining our channel")
	@SuppressWarnings("resource")
	public void joinTest() throws IOException, IrcException {
		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder())
				.assertBotHello()
				.joinChannel()
				.botIn(":%usersource JOIN #aChannel");
		JoinEvent jevent = test.getNextEvent(JoinEvent.class);

		UserHostmask aUserHostmask = TestUtils.generateTestUserSourceHostmask(test.bot);
		UserChannelDao dao = test.bot.getUserChannelDao();

		//Make sure the event gives us the same channels
		assertEquals(jevent.getChannel().getName(), "#aChannel", "Event's channel does not match origional channel");
		assertEquals(jevent.getUserHostmask(), aUserHostmask, "Event's user does not match origional user");

		//Make sure user info was updated
		User aUser = dao.getUser(aUserHostmask);
		assertEquals(jevent.getUser(), aUser, "User does not match");
		assertEquals(aUser.getNick(), aUserHostmask.getNick(), "Nick is wrong");
		assertEquals(aUser.getLogin(), "~SomeTest", "User login wrong on JoinEvent");
		assertEquals(aUser.getHostname(), "host.test", "User hostmask wrong on JoinEvent");

		Channel aChannel = dao.getChannel("#aChannel");
		
		assertTrue(dao.containsUser(aUserHostmask.getNick()));
		assertEquals(aUser.getChannels(), ImmutableSortedSet.of(aChannel), "User is not joined to channel after JoinEvent. Channels: " + aUser.getChannels());
		assertEquals(aChannel.getUsers(), ImmutableSortedSet.of(aUser, test.bot.getUserBot()), "Channel is not joined to user after JoinEvent. Users: " + aChannel.getUsers());
		assertEquals(aChannel.getUsersNicks(), ImmutableSortedSet.of(aUserHostmask.getNick(), test.bot.getUserBot().getNick()), "Channel nicks doesn't contain user");
		
		test.close();
	}
	
	@SuppressWarnings("resource")
	public void joinWhoDisabledTest() throws IOException, IrcException {
		new PircTestRunner(TestUtils.generateConfigurationBuilder()
				.setOnJoinWhoEnabled(false)
		)
				.assertBotHello()
				.botIn(":%userbot JOIN #aChannel")
				.assertEventClass(JoinEvent.class)
				.assertBotOut("MODE #aChannel")
				.close();
	}

	@Test(description = "Verifies DAO allows case insensitive lookups")
	public void insensitiveLookupTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		UserHostmask aUserHostmask = TestUtils.generateTestUserSourceHostmask(bot);
		inputParser.handleLine(":" + aUserHostmask.getHostmask() + " JOIN :#aChannel");

		User aUser = dao.getUser(aUserHostmask);
		assertNotNull(aUser, "DAO User should not be null");
		assertEquals(dao.getUser("SOURCEUSER"), aUser, "Cannot lookup AUSER ignoring case");
		assertEquals(dao.getUser("SourceUser"), aUser, "Cannot lookup aUser ignoring case");
		assertEquals(dao.getUser("sourceuser"), aUser, "Cannot lookup auser ignoring case");

		assertEquals(dao.getChannel("#ACHANNEL"), aChannel, "Cannot lookup #ACHANNEL ignoring case");
		assertEquals(dao.getChannel("#aChannel"), aChannel, "Cannot lookup #aChannel ignoring case");
		assertEquals(dao.getChannel("#achannel"), aChannel, "Cannot lookup #achannel ignoring case");
	}

	@Test(description = "Verify Channel creation date is set - Freenode")
	public void channelCreationDateFreenode() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		long creationTime = 1328490732;
		inputParser.handleLine(":irc.someserver.net 329 PircBotXUser #aChannel " + creationTime);

		//Verify channel creation date was set
		assertEquals(aChannel.getCreateTimestamp(), creationTime, "Channel creation time doesn't match given");
	}

	@Test(description = "Verify TopicEvent by user changing topic")
	public void topicChangeTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " TOPIC #aChannel :" + aString);

		//Verify event contents
		TopicEvent tevent = bot.getTestEvent(TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getUser(), aUser, "TopicEvent's user doesn't match given");
		assertEquals(tevent.getChannel(), aChannel, "TopicEvent's channel doesn't match given");
		assertEquals(tevent.getTopic(), aString, "TopicEvent's topic doesn't match given");
		//Just make sure the time is reasonable since its based off of System.currentTimeMillis
		if (tevent.getDate() < 100 || tevent.getDate() > System.currentTimeMillis())
			throw new AssertionError("Expected TopicEvent date to be greater than 100 and less than current time, got " + tevent.getDate());
	}

	@Test(description = "Verify TopicEvent from /JOIN or /TOPIC #channel commands")
	public void topicResponseTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 332 PircBotXUser #aChannel :" + aString + aString);
		assertEquals(aChannel.getTopic(), aString + aString);
		assertNull(aChannel.getTopicSetter(), "why is the topic setter set?");
	}

	@Test(description = "Verify TopicEvent's extended information from line sent after TOPIC line")
	public void topicInfoTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 333 PircBotXUser #aChannel AUser 1268522937");
		assertEquals(aChannel.getTopicSetter().getNick(), "AUser");
		assertEquals(aChannel.getTopicTimestamp(), (long) 1268522937 * 1000);

		TopicEvent tevent = bot.getTestEvent(TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getChannel(), aChannel, "Event channel and origional channel do not match");
	}

	@Test(description = "Verify TopicEvent's extended information from line sent after TOPIC line")
	public void topicInfoFullHostmaskTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		UserHostmask aUser = TestUtils.generateTestUserSourceHostmask(bot);
		inputParser.handleLine(":irc.someserver.net 333 PircBotXUser #aChannel " + aUser.getHostmask() + " 1268522937");
		assertEquals(aChannel.getTopicSetter(), aUser);
		assertEquals(aChannel.getTopicTimestamp(), (long) 1268522937 * 1000);

		TopicEvent tevent = bot.getTestEvent(TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getChannel(), aChannel, "Event channel and origional channel do not match");
	}

	@Test(description = "Verify MessageEvent from channel message")
	public void messageTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG #aChannel :" + aString);

		//Verify event contents
		MessageEvent mevent = bot.getTestEvent(MessageEvent.class, "MessageEvent not dispatched");
		assertEquals(mevent.getChannel(), aChannel, "Event channel and original channel do not match");
		assertEquals(mevent.getUser(), aUser, "Event user and original user do not match");
		assertEquals(mevent.getMessage(), aString, "Message sent does not match");
	}

	@Test(description = "Verify PrivateMessage from random user")
	public void privateMessageTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG PircBotXBot :" + aString);

		//Verify event contents
		PrivateMessageEvent pevent = bot.getTestEvent(PrivateMessageEvent.class, "MessageEvent not dispatched");
		assertEquals(pevent.getUser(), aUser, "Event user and original user do not match");
		assertEquals(pevent.getMessage(), aString, "Message sent does not match");
	}

	@DataProvider
	public Object[][] channelOrUserDataProvider() {
		System.out.println("Generating data");
		return new Object[][]{{"#aChannel"}, {"PircBotXUser"}};
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify NoticeEvent from NOTICE")
	public void noticeTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " NOTICE " + target + " :" + aString);

		//Verify event contents
		NoticeEvent nevent = bot.getTestEvent(NoticeEvent.class, "NoticeEvent not dispatched for channel notice");
		assertEquals(nevent.getChannel(), aChannel, "NoticeEvent's channel does not match given");
		assertEquals(nevent.getUser(), aUser, "NoticeEvent's user does not match given");
		assertEquals(nevent.getNotice(), aString, "NoticeEvent's notice message does not match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify ActionEvent from /me from a user in a channel")
	public void actionTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG " + target + " :\u0001ACTION " + aString + "\u0001");

		//Verify event contents
		ActionEvent aevent = bot.getTestEvent(ActionEvent.class, "ActionEvent not dispatched for channel action");
		assertEquals(aevent.getChannel(), aChannel, "ActionEvent's channel doesn't match given");
		assertEquals(aevent.getUser(), aUser, "ActionEvent's user doesn't match given");
		assertEquals(aevent.getMessage(), aString, "ActionEvent's message doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify VersionEvent from a user")
	public void versionTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG " + target + " :\u0001VERSION\u0001");

		//Verify event contents
		VersionEvent vevent = bot.getTestEvent(VersionEvent.class, "VersionEvent not dispatched for version");
		assertEquals(vevent.getUser(), aUser, "VersionEvent's user doesn't match given");
		assertEquals(vevent.getChannel(), aChannel, "VersionEvent's channel doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify PingEvent from a user")
	public void pingTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		String pingValue = "2435fdfd3f3d";
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG " + target + " :\u0001PING " + pingValue + "\u0001");

		//Verify event contents
		PingEvent pevent = bot.getTestEvent(PingEvent.class, "PingEvent not dispatched for version");
		assertEquals(pevent.getUser(), aUser, "PingEvent's user doesn't match given");
		assertEquals(pevent.getChannel(), aChannel, "PingEvent's channel doesn't match given");
		assertEquals(pevent.getPingValue(), pingValue, "PingEvent's ping value doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify TimeEvent from a user")
	public void timeTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG " + target + " :\u0001TIME\u0001");

		//Verify event contents
		TimeEvent tevent = bot.getTestEvent(TimeEvent.class, "TimeEvent not dispatched for version");
		assertEquals(tevent.getUser(), aUser, "TimeEvent's user doesn't match given");
		assertEquals(tevent.getChannel(), aChannel, "TimeEvent's channel doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify FingerEvent from a user")
	public void fingerTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG " + target + " :\u0001FINGER\u0001");

		//Verify event contents
		FingerEvent fevent = bot.getTestEvent(FingerEvent.class, "FingerEvent not dispatched for version");
		assertEquals(fevent.getUser(), aUser, "FingerEvent's user doesn't match given");
		assertEquals(fevent.getChannel(), aChannel, "FingerEvent's channel doesn't match given");
	}

	@Test
	public void awayTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":irc.someserver.net 301 PircBotXUser " + aUser.getNick() + " :" + aString);
		assertEquals(aUser.getAwayMessage(), aString, "Away message isn't expected");
	}

	@Test
	public void awayNotifyTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":" + aUser.getHostmask() + " AWAY :" + aString);
		assertEquals(aUser.getAwayMessage(), aString, "Away message isn't expected");
	}

	@Test
	public void awayNotifyMessageEmptyTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":" + aUser.getHostmask() + " AWAY :");
		assertEquals(aUser.getAwayMessage(), "", "Away message isn't empty");
	}

	@Test
	public void awayNotifyMessageMissingTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":" + aUser.getHostmask() + " AWAY");
		assertEquals(aUser.getAwayMessage(), "", "Away message isn't empty");
	}

	@Test
	public void modeResponseTest() throws IOException, IrcException, NotReadyException {
		Channel aChannel = dao.createChannel("#aChannel");

		assertFalse(aChannel.isModerated());
		assertFalse(aChannel.isInviteOnly());
		assertFalse(aChannel.isChannelPrivate());

		inputParser.handleLine(":irc.someserver.net 324 PircBotXUser #aChannel +ipmd");

		ModeEvent mevent = bot.getTestEvent(ModeEvent.class, "ModeEvent not dispatched for mode response");
		assertEquals(mevent.getMode(), "+ipmd", "ModeEvent's mode doesn't equal given");
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel doesn't match given");
		assertNull(mevent.getUser(), "ModeEvent's user not null for mode response");

		assertTrue(aChannel.isModerated());
		assertTrue(aChannel.isInviteOnly());
		assertTrue(aChannel.isChannelPrivate());
		assertEquals(aChannel.getMode(), "+ipmd");
	}

	@Test
	public void containsModeTest() throws IOException, IrcException, NotReadyException {
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 324 PircBotXUser #aChannel +ipmd");

		assertTrue(aChannel.containsMode('i'));
		assertTrue(aChannel.containsMode('p'));
		assertTrue(aChannel.containsMode('m'));
		assertTrue(aChannel.containsMode('d'));
		assertFalse(aChannel.containsMode('q'));
		assertFalse(aChannel.containsMode('r'));
		assertFalse(aChannel.containsMode('s'));
	}

	@Test
	public void userModeInitTest() throws Exception {
		inputParser.handleLine(":" + bot.getNick() + " MODE " + bot.getNick() + " :+i");
		UserHostmask botHostmask = new UserHostmask(bot, bot.getNick());

		UserModeEvent event = bot.getTestEvent(UserModeEvent.class);
		assertEquals(event.getMode(), "+i", "Mode is wrong");
		assertEquals(event.getRecipient(), bot.getUserBot(), "Recipient is wrong");
		assertEquals(event.getRecipientHostmask(), botHostmask, "Recipient hostmask is wrong");
		assertEquals(event.getUser(), bot.getUserBot(), "User is wrong");
		assertEquals(event.getUserHostmask(), bot.getUserBot(), "User hostmask is wrong");
	}

	@DataProvider
	public Object[][] channelUserModeProvider() {
		return new Object[][]{
			{"+o", OpEvent.class, "isOp"},
			{"-o", OpEvent.class, "isOp"},
			{"+v", VoiceEvent.class, "hasVoice"},
			{"-v", VoiceEvent.class, "hasVoice"},
			{"+q", OwnerEvent.class, "isOwner"},
			{"-q", OwnerEvent.class, "isOwner"},
			{"+h", HalfOpEvent.class, "isHalfOp"},
			{"-h", HalfOpEvent.class, "isHalfOp"},
			{"+a", SuperOpEvent.class, "isSuperOp"},
			{"-a", SuperOpEvent.class, "isSuperOp"}
		};
	}

	@Test(dataProvider = "channelUserModeProvider", description = "Test setting various user modes and verifying events")
	public void channelUserModeTest(String mode, Class<?> eventClass, String checkMethod) throws Exception {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		User aUser2 = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel " + mode + " " + aUser2.getNick());

		//Verify generic ModeEvent contents
		ModeEvent mevent = bot.getTestEvent(ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given with mode " + mode);
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given with mode " + mode);
		assertEquals(mevent.getMode(), mode + " OtherUser", "ModeEvent's mode does not match given mode");

		//Verify specific event contents
		GenericUserModeEvent event = (GenericUserModeEvent) bot.getTestEvent(eventClass, "No " + eventClass.getSimpleName() + " dispatched with " + mode);
//		assertEquals(event.getChannel(), aChannel, eventClass.getSimpleName() + "'s channel does not match given with mode " + mode);
		assertEquals(event.getUser(), aUser, eventClass.getSimpleName() + "'s source user does not match given with mode " + mode);
		assertEquals(event.getRecipient(), aUser2, eventClass.getSimpleName() + "'s recipient user does not match given with mode " + mode);

		//Make sure the event's is* method returns the correct value
		assertEquals(eventClass.getMethod(checkMethod).invoke(event), mode.startsWith("+"), "Event's " + checkMethod + " method doesn't return correct value");

		//Make sure the channels is* method returns the correct value
		assertEquals(aChannel.getClass().getMethod(checkMethod, User.class).invoke(aChannel, aUser2), mode.startsWith("+"), "Channels's " + checkMethod + " method doesn't return correct value");
	}
	
	
	//Tests the same as previous but with a : befor the target name
	//Inspircd 3 uses this variant
	@Test(dataProvider = "channelUserModeProvider", description = "Test setting various user modes and verifying events")
	public void channelUserModeTest2(String mode, Class<?> eventClass, String checkMethod) throws Exception {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		User aUser2 = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel " + mode + " :" + aUser2.getNick()); 

		//Verify generic ModeEvent contents
		ModeEvent mevent = bot.getTestEvent(ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given with mode " + mode);
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given with mode " + mode);
		assertEquals(mevent.getMode(), mode + " :OtherUser", "ModeEvent's mode does not match given mode");

		//Verify specific event contents
		GenericUserModeEvent event = (GenericUserModeEvent) bot.getTestEvent(eventClass, "No " + eventClass.getSimpleName() + " dispatched with " + mode);
//		assertEquals(event.getChannel(), aChannel, eventClass.getSimpleName() + "'s channel does not match given with mode " + mode);
		assertEquals(event.getUser(), aUser, eventClass.getSimpleName() + "'s source user does not match given with mode " + mode);
		assertEquals(event.getRecipient(), aUser2, eventClass.getSimpleName() + "'s recipient user does not match given with mode " + mode);

		//Make sure the event's is* method returns the correct value
		assertEquals(eventClass.getMethod(checkMethod).invoke(event), mode.startsWith("+"), "Event's " + checkMethod + " method doesn't return correct value");

		//Make sure the channels is* method returns the correct value
		assertEquals(aChannel.getClass().getMethod(checkMethod, User.class).invoke(aChannel, aUser2), mode.startsWith("+"), "Channels's " + checkMethod + " method doesn't return correct value");
	}	

	@DataProvider
	protected Object[][] channelModeProvider() {
		ImmutableList<Object[]> testTemplates = ImmutableList.of(
				new Object[]{"+l 10", null, SetChannelLimitEvent.class},
				new Object[]{"-l", "l 10", RemoveChannelLimitEvent.class},
				new Object[]{"+k testPassword", null, SetChannelKeyEvent.class},
				new Object[]{"-k", "k testPassword", RemoveChannelKeyEvent.class},
				new Object[]{"-k testPassword", "k testPassword", RemoveChannelKeyEvent.class},
				new Object[]{"+i", null, SetInviteOnlyEvent.class},
				new Object[]{"-i", null, RemoveInviteOnlyEvent.class},
				new Object[]{"+n", null, SetNoExternalMessagesEvent.class},
				new Object[]{"-n", null, RemoveNoExternalMessagesEvent.class},
				new Object[]{"+s", null, SetSecretEvent.class},
				new Object[]{"-s", null, RemoveSecretEvent.class},
				new Object[]{"+t", null, SetTopicProtectionEvent.class},
				new Object[]{"-t", null, RemoveTopicProtectionEvent.class},
				new Object[]{"+p", null, SetPrivateEvent.class},
				new Object[]{"-p", null, RemovePrivateEvent.class});
		List<Object[]> testParams = Lists.newArrayList();
		for (Object[] curTemplate : testTemplates) {
			//Normal version
			testParams.add(curTemplate);

			//Create other versions
			String modeRaw = (String) curTemplate[0];
			String modePrefix = modeRaw.substring(0, 1);
			log.trace("Mode prefix: " + modePrefix);
			String mode = (modeRaw.contains(" ")) ? StringUtils.split(modeRaw)[0] : modeRaw;
			mode = mode.substring(1);
			log.trace("Mode: " + mode);

			//Version with random modes at end
			Object[] newTemplate = ArrayUtils.clone(curTemplate);
			newTemplate[0] = StringUtils.replace(modeRaw, modePrefix + mode, modePrefix + mode + "xyz");
			log.trace("Current: " + newTemplate[0]);
			testParams.add(newTemplate);

			//Version with random modes at the beggining
			newTemplate = ArrayUtils.clone(curTemplate);
			newTemplate[0] = StringUtils.replace(modeRaw, modePrefix + mode, modePrefix + "cde" + mode);
			testParams.add(newTemplate);

			//Version with random surrounding nodes
			newTemplate = ArrayUtils.clone(curTemplate);
			newTemplate[0] = StringUtils.replace(modeRaw, modePrefix + mode, modePrefix + "cde" + mode + "xyz");
			testParams.add(newTemplate);
		}
		return testParams.toArray(new Object[testParams.size()][]);
	}

	@Test(dataProvider = "channelModeProvider")
	public void channelModeChangeTest(String mode, String parentMode, Class<?> modeClass) throws IOException, IrcException, NotReadyException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		if (mode.startsWith("-")) {
			//Set the mode first
			String channelMode = (parentMode != null) ? parentMode : mode.substring(1);
			ImmutableList<String> channelModeParsed = ImmutableList.copyOf(StringUtils.split(channelMode, ' '));
			aChannel.setMode(channelMode, channelModeParsed);
		}
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel " + mode);

		//Verify generic ModeEvent contents
		ModeEvent mevent = bot.getTestEvent(ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given");
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given");
		assertEquals(mevent.getMode(), mode, "ModeEvent's mode does not match given mode");

		//Verify generic mode event contents
		String modeName = modeClass.getSimpleName();
		GenericChannelModeEvent subEvent = (GenericChannelModeEvent) bot.getTestEvent(modeClass, "No " + modeName + " dispatched with " + mode);
		assertEquals(subEvent.getChannel(), aChannel, modeName + "'s channel does not match given");
		assertEquals(subEvent.getUser(), aUser, modeName + "'s user does not match given");

		//Check channel mode
		if (parentMode == null && !mode.contains(" "))
			if (mode.startsWith("-"))
				assertEquals(aChannel.getMode(), "", "Channel mode not empty after removing only mode");
			else
				assertEquals(aChannel.getMode(), mode.substring(1), "Channels mode not updated");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify SetChannelKeyEvent has the correct key")
	public void setChannelKeyEventTest() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel +k testPassword");
		SetChannelKeyEvent event = bot.getTestEvent(SetChannelKeyEvent.class, "No SetChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getKey(), "testPassword", "SetChannelKeyEvent key doesn't match given");
		assertEquals(aChannel.getChannelKey(), "testPassword", "Key from channel doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify Channel has the correct key")
	public void setChannelKeyModeTest() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 324 PircBotX #aChannel +k testPassword");
		assertEquals(aChannel.getChannelKey(), "testPassword", "Key from channel doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelKeyEvent has a null key when not specified")
	public void removeChannelKeyEventEmptyTest() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel -k");
		RemoveChannelKeyEvent event = bot.getTestEvent(RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertNull(event.getKey(), "RemoveChannelKeyEvent key doesn't match given");
		assertNull(aChannel.getChannelKey(), "Channel key doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelKeyEvent has the correct key")
	public void removeChannelKeyEventTest() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel -k testPassword");
		RemoveChannelKeyEvent event = bot.getTestEvent(RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getKey(), "testPassword", "RemoveChannelKeyEvent key doesn't match given");
		assertNull(aChannel.getChannelKey(), "Channel key doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify SetChannelLimitEvent has the correct limit")
	public void setChannelLimitEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel +l 10");
		SetChannelLimitEvent event = bot.getTestEvent(SetChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getLimit(), 10, "SetChannelLimitEvent key doesn't match given");
		assertEquals(aChannel.getChannelLimit(), 10, "Channel limit doesn't match given");
	}

	@Test(/*dependsOnMethods = "channelModeChangeTest", */description = "Verify SetChannelLimitEvent has the correct limit")
	public void setChannelLimitModeEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 324 PircBotX #aChannel +l 10");
		assertEquals(aChannel.getChannelLimit(), 10, "Channel limit doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelLimitEvent has the correct limit")
	public void removeChannelLimitEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel -l 10");
		RemoveChannelLimitEvent event = bot.getTestEvent(RemoveChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(aChannel.getChannelLimit(), -1, "Channel limit doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelLimitEvent has the correct limit")
	public void removeChannelLimitEmptyEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel -l");
		RemoveChannelLimitEvent event = bot.getTestEvent(RemoveChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(aChannel.getChannelLimit(), -1, "Channel limit doesn't match given");
	}

	@Test
	public void modeRecipientWithGlobHostmask() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		User otherUser = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel -q *!*@" + otherUser.getHostname());
		OwnerEvent event = bot.getTestEvent(OwnerEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertFalse(event.isOwner(), "Channel limit doesn't match given");
	}

	/**
	 * Simulate WHO response.
	 */
	@Test(description = "Verify WHO response handling + UserListEvent")
	public void userlistTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~ALogin some.host irc.someserver.net AUser H@+ :2 " + aString);
		//Issue #151: Test without full name
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~OtherLogin some.host1 irc.otherserver.net OtherUser G :4");
		inputParser.handleLine(":irc.someserver.net 315 PircBotXUser #aChannel :End of /WHO list.");

		//Make sure all information was created correctly
		assertTrue(dao.containsChannel("#aChannel"), "WHO response didn't create channel");
		assertTrue(dao.containsUser("AUser"), "WHO response didn't create user AUser");
		assertTrue(dao.containsUser("OtherUser"), "WHO response didn't create user OtherUser");
		Channel aChannel = dao.getChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		User otherUser = dao.getUser("OtherUser");
		assertNotNull(aChannel, "Channel from WHO response is null");
		assertNotNull(aUser, "AUser from WHO response is null");
		assertNotNull(otherUser, "OtherUser from WHO response is null");

		//Verify event
		UserListEvent uevent = bot.getTestEvent(UserListEvent.class, "UserListEvent not dispatched");
		assertTrue(uevent.isComplete());
		assertEquals(uevent.getChannel(), aChannel, "UserListEvent's channel does not match given");
		assertEquals(uevent.getUsers().size(), 2, "UserListEvent's users is larger than it should be");
		assertTrue(uevent.getUsers().contains(aUser), "UserListEvent doesn't contain aUser");
		assertTrue(uevent.getUsers().contains(otherUser), "UserListEvent doesn't contain OtherUser");
		assertTrue(aChannel.getUsers().contains(aUser), "aChannel doens't contain aUser");
		assertTrue(aChannel.getUsers().contains(otherUser), "aChannel doens't contain OtherUser");

		//Verify AUser
		assertEquals(aUser.getNick(), "AUser", "Login doesn't match one given during WHO");
		assertEquals(aUser.getLogin(), "~ALogin", "Login doesn't match one given during WHO");
		assertEquals(aUser.getHostname(), "some.host", "Host doesn't match one given during WHO");
		assertEquals(aUser.getHops(), 2, "Hops doesn't match one given during WHO");
		assertEquals(aUser.getRealName(), aString, "RealName doesn't match one given during WHO");
		assertEquals(aUser.getServer(), "irc.someserver.net", "Server doesn't match one given during WHO");
		assertFalse(aUser.isAway(), "User is away even though specified as here in WHO");
		assertTrue(aUser.getChannelsOpIn().contains(aChannel), "aUser not an op in aChannel: " + aUser.getChannelsOpIn().size());
		assertTrue(aChannel.isOp(aUser), "User isn't labeled as an op even though specified as one in WHO");
		assertTrue(aChannel.hasVoice(aUser), "User isn't voiced even though specified as one in WHO");

		//Verify otherUser
		assertEquals(otherUser.getNick(), "OtherUser", "Login doesn't match one given during WHO");
		assertEquals(otherUser.getLogin(), "~OtherLogin", "Login doesn't match one given during WHO");
		assertEquals(otherUser.getHostname(), "some.host1", "Host doesn't match one given during WHO");
		assertEquals(otherUser.getHops(), 4, "Hops doesn't match one given during WHO");
		assertEquals(otherUser.getRealName(), "", "RealName doesn't match one given during WHO");
		assertEquals(otherUser.getServer(), "irc.otherserver.net", "Server doesn't match one given during WHO");
		assertTrue(otherUser.isAway(), "User is not away though specified as here in WHO");
		assertFalse(aChannel.isOp(otherUser), "User is labeled as an op even though specified as one in WHO");
		assertFalse(aChannel.hasVoice(otherUser), "User is labeled as voiced even though specified as one in WHO");
	}
	
	/**
	 * Simulate WHO response eg a /WHO 1.2.3.4 i on inspircd for finding users origination on said IP
	 */
	@Test(description = "Verify WHO response handling + UserListEvent")
	public void whoTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~ALogin some.host irc.someserver.net AUser H@+ :2 " + aString);
		//Issue #151: Test without full name
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aAnotherchannel ~OtherLogin some.host1 irc.otherserver.net OtherUser G :4");
		inputParser.handleLine(":irc.someserver.net 315 PircBotXUser 1.2.3.4 :End of /WHO list.");

		//Make sure all information was created correctly
		assertFalse(dao.containsChannel("#aChannel"), "WHO response should not create channel");
		assertFalse(dao.containsChannel("#aAnotherchannel"), "WHO response should not create channel"); 
		assertFalse(dao.containsUser("AUser"), "WHO response should not create user AUser"); // TODO are we sure we want this to happen ??
		assertFalse(dao.containsUser("OtherUser"), "WHO response should not create user OtherUser");
		

		//Verify event
		WhoEvent wevent = bot.getTestEvent(WhoEvent.class, "WhoEvent not dispatched");
		
		assertEquals(wevent.getQuery(), "1.2.3.4", "WhoEvent's query does not match given");
		assertEquals(wevent.getUsers().size(), 2, "WhoEvent's users is different than it should be");

		User entryOne = wevent.getUsers().get(0);
		User entryTwo = wevent.getUsers().get(1);

		//Verify AUser
		assertEquals(entryOne.getNick(), "AUser", "Login doesn't match one given during WHO");
		assertEquals(entryOne.getLogin(), "~ALogin", "Login doesn't match one given during WHO");
		assertEquals(entryOne.getHostname(), "some.host", "Host doesn't match one given during WHO");
		assertEquals(entryOne.getHops(), 2, "Hops doesn't match one given during WHO");
		assertEquals(entryOne.getRealName(), aString, "RealName doesn't match one given during WHO");
		assertEquals(entryOne.getServer(), "irc.someserver.net", "Server doesn't match one given during WHO");



		//Verify otherUser
		assertEquals(entryTwo.getNick(), "OtherUser", "Login doesn't match one given during WHO");
		assertEquals(entryTwo.getLogin(), "~OtherLogin", "Login doesn't match one given during WHO");
		assertEquals(entryTwo.getHostname(), "some.host1", "Host doesn't match one given during WHO");
		assertEquals(entryTwo.getHops(), 4, "Hops doesn't match one given during WHO");
		assertEquals(entryTwo.getRealName(), "", "RealName doesn't match one given during WHO");
		assertEquals(entryTwo.getServer(), "irc.otherserver.net", "Server doesn't match one given during WHO");

	}	

	@Test(description = "Veryfy that we don't falsely registers all WHO responses as valid channels")
	public void whoTestFalseChannels() throws IOException, IrcException {
		assertFalse(dao.containsChannel("#randomChannel"), "Intial test to ensure channel doesn't exist");

		//Sending out a "WHO AUser" command
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #randomChannel ~ALogin 8dce28.83b021.3a4fde.2fed84 irc.someserver.net AUser H :0 " + aString);
		inputParser.handleLine(":irc.someserver.net 315 PircBotXUser AUser :End of /WHO list.");

		assertFalse(dao.containsChannel("#randomChannel"), "WHO response for a user may not result in unrelated channel creation");
		assertFalse(dao.containsChannel("AUser"), "WHO response for a user may not result in channel creation");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify KickEvent from some user kicking another user")
	public void kickTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		User otherUser = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + aUser.getHostmask() + " KICK #aChannel " + otherUser.getNick() + " :" + aString);

		//Verify event contents
		KickEvent kevent = bot.getTestEvent(KickEvent.class, "KickEvent not dispatched");
		assertEquals(kevent.getChannel(), aChannel, "KickEvent channel does not match");
		assertEquals(kevent.getUser(), aUser, "KickEvent's getSource doesn't match kicker user");
		assertEquals(kevent.getRecipient(), otherUser, "KickEvent's getRecipient doesn't match kickee user");
		assertEquals(kevent.getReason(), aString, "KickEvent's reason doesn't match given one");

		//Make sure we've sufficently forgotten about the user
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that was kicked an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that was kicked with voice");
		assertFalse(dao.containsUser("OtherUser"), "Bot still considers user to exist after kick");
	}

	@Test(description = "Verify QuitEvent from user that just joined quitting")
	public void quitWithMessageTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		User otherUser = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel +o " + otherUser.getNick());
		inputParser.handleLine(":" + aUser.getHostmask() + " MODE #aChannel +v " + otherUser.getNick());
		inputParser.handleLine(":" + otherUser.getHostmask() + " QUIT :" + aString);

		//Check event contents
		QuitEvent qevent = bot.getTestEvent(QuitEvent.class, "QuitEvent not dispatched");
		//Since QuitEvent gives us a snapshot, compare contents
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertTrue(qevent.getUserChannelDaoSnapshot().containsChannel("#aChannel"), "QuitEvent doesn't contain channel");
		assertTrue(qevent.getUserChannelDaoSnapshot().containsUser(aUser), "QuitEvent doesn't contain channel");
		assertEquals(qevent.getUser().getChannels().size(), 1, "QuitEvent user contains unexpected channels");
		assertEquals(qevent.getUser().getChannels().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
		assertEquals(qevent.getUser().getChannelsOpIn().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
		assertEquals(qevent.getUser().getChannelsVoiceIn().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
		assertTrue(qevent.getUser().getChannelsOwnerIn().isEmpty(), "QuitEvent user contains unexpected channels");
		assertTrue(qevent.getUser().getChannelsHalfOpIn().isEmpty(), "QuitEvent user contains unexpected channels");
		assertTrue(qevent.getUser().getChannelsSuperOpIn().isEmpty(), "QuitEvent user contains unexpected channels");
		ChannelSnapshot aChannelSnap = qevent.getUserChannelDaoSnapshot().getChannel("#aChannel");
		assertEquals(aChannelSnap.getUsers().size(), 1, "QuitEvent channel contains unexpected users");
		assertEquals(aChannelSnap.getUsers().first(), qevent.getUser(), "Channel user doesn't match");
		assertEquals(qevent.getReason(), aString, "QuitEvent's reason does not match given");

		//Make sure user is gone
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that quit an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that quit with voice");
		assertFalse(dao.containsUser("OtherUser"), "Bot still considers user to exist after quit");
		assertTrue(otherUser.getChannels().isEmpty(), "User still connected to other channels after quit");
		assertFalse(aChannel.getUsers().contains(otherUser), "Channel still associated with user that quit");
	}

	@Test(dependsOnMethods = "quitWithMessageTest", description = "Verify QuitEvent with no message")
	public void quitWithoutMessageTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		User otherUser = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " QUIT :");

		//Check event contents
		QuitEvent qevent = bot.getTestEvent(QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), "", "QuitEvent's reason does not match given");
		assertEquals(qevent.getUser().getChannels().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
	}

	@Test(dependsOnMethods = "quitWithMessageTest", description = "Verify QuitEvent with no message")
	public void quitWithoutMessageAndColonTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		User otherUser = TestUtils.generateTestUserOther(bot);
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " QUIT");

		//Check event contents
		QuitEvent qevent = bot.getTestEvent(QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), "", "QuitEvent's reason does not match given");
		assertEquals(qevent.getUser().getChannels().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
	}

	/**
	 * https://github.com/pircbotx/pircbotx/issues/256#issuecomment-124180823
	 */
	@Test
	public void quitTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		String quitMessage = "QUIT :this!looks@like a hostmask";

		//Also test prefixes in names
		inputParser.handleLine(":" + aUser.getHostmask() + " JOIN #aChannel");
		inputParser.handleLine(":" + aUser.getHostmask() + " QUIT :" + quitMessage);

		QuitEvent qevent = bot.getTestEvent(QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), aUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), quitMessage, "QuitEvent's reason does not match given");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify part with message")
	public void partWithMessageTest() throws IOException, IrcException {
		User otherUser = TestUtils.generateTestUserOther(bot);
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " PART #aChannel :" + aString);

		//Check event contents
		PartEvent event = bot.getTestEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), aString, "PartEvent's reason doesn't match given");
		assertEquals(event.getUser().getChannels().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part without message")
	public void partWithoutMessageTest() throws IOException, IrcException {
		User otherUser = TestUtils.generateTestUserOther(bot);
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " PART #aChannel :");

		//Check event contents
		PartEvent event = bot.getTestEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(aChannel.getName(), "#aChannel", "Channel name doesn't match");
		assertEquals(event.getUser().getChannels().first().getName(), "#aChannel", "QuitEvent user contains unexpected channels");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), "", "PartEvent's reason doesn't match given");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part without message")
	public void partWithoutMessageAndColonTest() throws IOException, IrcException {
		User otherUser = TestUtils.generateTestUserOther(bot);
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " PART #aChannel");

		//Check event contents
		PartEvent event = bot.getTestEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), "", "PartEvent's reason doesn't match given");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part with us")
	public void partUs() throws IOException, IrcException {
		User otherUser = TestUtils.generateTestUserOther(bot);
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " JOIN :#aChannel");
		inputParser.handleLine(":" + otherUser.getHostmask() + " PART #aChannel");

		//Check event contents
		PartEvent event = bot.getTestEvent(PartEvent.class, "PartEvent not dispatched for this bot");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given for this bot");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given for this bot");
		assertEquals(event.getReason(), "", "PartEvent's reason doesn't match given for this bot");
	}

	@Test()
	public void motdTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 375 PircBotXUser :- pratchett.freenode.net Message of the Day - ");
		inputParser.handleLine(":irc.someserver.net 372 PircBotXUser :- " + aString);
		inputParser.handleLine(":irc.someserver.net 372 PircBotXUser :- ");
		inputParser.handleLine(":irc.someserver.net 372 PircBotXUser :- " + aString);
		inputParser.handleLine(":irc.someserver.net 376 PircBotXUser :End of /MOTD command.");

		//Check event contents
		MotdEvent event = bot.getTestEvent(MotdEvent.class, "MotdEvent not dispatched");
		String realMotd = aString + "\n\n" + aString;
		assertEquals(event.getMotd(), realMotd, "Motd does not match given");
	}

	@Test
	public void whoisTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 319 PircBotXUser OtherUser :+#aChannel ##anotherChannel");
		inputParser.handleLine(":irc.someserver.net 317 PircBotXUser OtherUser 6077 1347373349 :seconds idle, signon time");
		inputParser.handleLine(":irc.someserver.net 312 PircBotXUser OtherUser irc2.someserver.net :" + aString + aString);
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");

		//Check event contents
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getNick(), "OtherUser", "Nick does not match given");
		assertEquals(event.getLogin(), "~OtherLogin", "Login does not match given");
		assertEquals(event.getHostname(), "some.host1", "Hostname does not match given");
		assertEquals(event.getRealname(), aString, "Real name does not match given");
		assertEquals(event.getServer(), "irc2.someserver.net", "Server address wrong");
		assertEquals(event.getServerInfo(), aString + aString, "Server info wrong");
		assertEquals(event.getIdleSeconds(), 6077, "Idle time doesn't match given");
		assertEquals(event.getSignOnTime(), 1347373349, "Sign on time doesn't match given");
		assertNull(event.getRegisteredAs(), "User isn't registered");
		assertTrue(event.isExists(), "User should exist");
		assertFalse(event.isSecureConnection(), "User should have insecure connection");
		assertFalse(event.isIrcOp(), "User should not be ircop");

		//Verify channels
		assertTrue(event.getChannels().contains("+#aChannel"), "Doesn't contain first given voice channel");
		assertTrue(event.getChannels().contains("##anotherChannel"), "Doesn't contain second given channel");
		assertEquals(event.getChannels().size(), 2, "Channels list size wrong");
	}

	@Test
	public void whoisRegistered307Test() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 307 PircBotXUser OtherUser :has identified for this nick");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");

		//Check event contents
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "", "Nickserv account does not match given");
	}

	@Test
	public void whoisRegistered330NoNameTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 330 PircBotXUser OtherUser :is logged in as");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");

		//Check event contents
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "", "Nickserv account does not match given");
	}

	@Test
	public void whoisRegistered330NameTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 330 PircBotXUser OtherUser nickservAccount :is logged in as");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");

		//Check event contents
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "nickservAccount", "Nickserv account does not match given");
	}

	@Test
	public void whoisCaseInsensitiveTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser otheruser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getNick(), "OtherUser", "Nickserv account does not match given");
	}

	@Test
	public void whoisInvalidUser401Test() throws IOException, IrcException {
		//Need 001 since processConnect fails on 4XX errors
		inputParser.handleLine(":irc.someserver.net 001 PircBotXUser :Test server");
		inputParser.handleLine(":irc.someserver.net 401 PircBotXUser randomuser :No such nick/channel");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser randomuser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getNick(), "randomuser", "Nick does not match given");
		assertFalse(event.isExists(), "User should not exist");
	}

	@Test
	public void whoisInvalidUser402Test() throws IOException, IrcException {
		//Need 001 since processConnect fails on 4XX errors
		inputParser.handleLine(":irc.someserver.net 001 PircBotXUser :Test server");
		inputParser.handleLine(":irc.someserver.net 402 PircBotXUser randomuser :No such server");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getNick(), "randomuser", "Nick does not match given");
		assertFalse(event.isExists(), "User should not exist");
	}

	@Test
	public void whoisNoChannelsTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser otheruser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertNotNull(event.getChannels(), "WhoisEvent.channels is null");
		assertTrue(event.getChannels().isEmpty(), "Unknown channels " + event.getChannels());
	}

	@Test
	public void whoisSecureConnectionTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 671 PircBotXUser OtherUser :is using a secure connection");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser otheruser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertTrue(event.isSecureConnection(), "Event used insecure connection");
	}
	
	
	
	@Test
	public void whoisIrcOpTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 313 PircBotXUser OtherUser :is a IRC operator");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser otheruser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertTrue(event.isIrcOp(), "User is not IRC operator");
	}

	@Test
	public void whoisIrcOpRegisteredTest() throws IOException, IrcException {
		User otherUser = TestUtils.generateTestUserOther(bot);
		assertFalse(otherUser.isIrcop(), "User is IRCop at start");
		
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 313 PircBotXUser OtherUser :is a IRC operator");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser otheruser :End of /WHOIS list.");

		//Make sure we get the correct event
		WhoisEvent event = bot.getTestEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertTrue(event.isIrcOp(), "User is not IRC operator");
		
		//make sure the user object is registered as ircop
		assertTrue(otherUser.isIrcop(), "User is not IRCop after WHOIS response");
	}


	@Test
	public void serverPingTest() throws IOException, IrcException {
		String pingString = "FDS9AG65FH32";
		inputParser.handleLine("PING " + pingString);

		//Check event contents
		ServerPingEvent event = bot.getTestEvent(ServerPingEvent.class, "ServerPingEvent not dispatched");
		assertEquals(event.getResponse(), pingString, "Ping string doesn't match given");
	}

	@Test
	public void nickChangeOtherTest() throws IOException, IrcException {
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " NICK :AnotherUser");

		NickChangeEvent event = bot.getTestEvent(NickChangeEvent.class, "NickChangeEvent not dispatched for NICK");
		assertEquals(event.getOldNick(), "SourceUser");
		assertEquals(event.getNewNick(), "AnotherUser");
		assertEquals(event.getUser(), aUser);
	}

	@Test
	public void nickChangeBotTest() throws IOException, IrcException {
		User botUser = TestUtils.generateTestUserSource(bot);
		bot.setNick(botUser.getNick());
		String oldNick = botUser.getNick();
		log.debug("Old nick" + oldNick);
		inputParser.handleLine(":" + botUser.getHostmask() + " NICK :PircBotXBetter");

		NickChangeEvent event = bot.getTestEvent(NickChangeEvent.class, "NickChangeEvent not dispatched for NICK");
		assertEquals(event.getOldNick(), oldNick);
		assertEquals(event.getNewNick(), "PircBotXBetter");
		assertEquals(bot.getNick(), "PircBotXBetter");
		assertEquals(event.getUser(), botUser);
		assertEquals(event.getUser(), bot.getUserBot());
	}

	@Test
	@SuppressWarnings("resource")
	public void nickAlreadyInUse2ParamBeforeConnectTest() throws IOException, IrcException {
		assertEquals(bot.getUserBot().getNick(), bot.getConfiguration().getName(), "bots user name doesn't match config username");
		assertEquals(bot.getUserBot().getNick(), bot.getNick(), "bots user name doesn't match nick");

		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				.setAutoNickChange(true)
		)
				.assertBotHelloAndConnect()
				.botIn(":%server 433 * %nickbot :Nickname is already in use")
				.assertBotOut("NICK TestBot1");

		NickAlreadyInUseEvent event = test.getNextEvent(NickAlreadyInUseEvent.class);
		assertEquals(event.getUsedNick(), bot.getConfiguration().getName(), "event used nick doesn't match old one in config");

		String newNick = bot.getConfiguration().getName() + "1";
		assertEquals(event.getAutoNewNick(), newNick, "event auto new nick doesn't match 'nick1'");
		assertEquals(event.getBot().getNick(), newNick, "bots nick doesn't match events nick");
		assertEquals(event.getBot().getUserBot().getNick(), newNick, "bots user nick doesn't match events nick");

		test.assertEventClass(ServerResponseEvent.class);
		test.close();
	}
	
	@Test
	@SuppressWarnings("resource")
	public void nickAlreadyInUse2ParamAfterConnectTest() throws IOException, IrcException {
		assertEquals(bot.getUserBot().getNick(), bot.getConfiguration().getName(), "bots user name doesn't match config username");
		assertEquals(bot.getUserBot().getNick(), bot.getNick(), "bots user name doesn't match nick");

		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				.setAutoNickChange(true)
		)
				.assertBotHelloAndConnect()
				.botIn(":%server 433 %nickbot AnotherNick :Nickname is already in use");

		NickAlreadyInUseEvent event = test.getNextEvent(NickAlreadyInUseEvent.class);
		assertNull(event.getAutoNewNick(), "Nick shouldn't of changed");
		assertEquals(event.getUsedNick(), "AnotherNick", "event used nick doesn't match old one in config");

		String oldNick = bot.getConfiguration().getName();
		assertEquals(event.getBot().getNick(), oldNick, "bots nick doesn't match events nick");
		assertEquals(event.getBot().getUserBot().getNick(), oldNick, "bots user nick doesn't match events nick");

		test.assertEventClass(ServerResponseEvent.class);
		test.close();
	}
	
	@Test
	@SuppressWarnings("resource")
	public void nickAlreadyInUse1ParamBeforeConnectTest() throws IOException, IrcException {
		assertEquals(bot.getUserBot().getNick(), bot.getConfiguration().getName(), "bots user name doesn't match config username");
		assertEquals(bot.getUserBot().getNick(), bot.getNick(), "bots user name doesn't match nick");

		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				.setAutoNickChange(true)
		)
				.assertBotHello()
				.botIn(":%server 433 %nickbot :Nickname is already in use")
				.assertBotOut("NICK TestBot1");

		NickAlreadyInUseEvent event = test.getNextEvent(NickAlreadyInUseEvent.class);
		assertEquals(event.getUsedNick(), bot.getConfiguration().getName(), "event used nick doesn't match old one in config");

		String newNick = bot.getConfiguration().getName() + "1";
		assertEquals(event.getAutoNewNick(), newNick, "event auto new nick doesn't match 'nick1'");
		assertEquals(event.getBot().getNick(), newNick, "bots nick doesn't match events nick");
		assertEquals(event.getBot().getUserBot().getNick(), newNick, "bots user nick doesn't match events nick");

		test.assertEventClass(ServerResponseEvent.class);
		test.close();
	}
	
	@Test
	@SuppressWarnings("resource")
	public void nickAlreadyInUse1ParamAfterConnectTest() throws IOException, IrcException {
		assertEquals(bot.getUserBot().getNick(), bot.getConfiguration().getName(), "bots user name doesn't match config username");
		assertEquals(bot.getUserBot().getNick(), bot.getNick(), "bots user name doesn't match nick");

		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				.setAutoNickChange(true)
		)
				.assertBotHelloAndConnect()
				.botIn(":%server 433 %nickbot :Nickname is already in use");

		NickAlreadyInUseEvent event = test.getNextEvent(NickAlreadyInUseEvent.class);
		assertNull(event.getAutoNewNick(), "Nick shouldn't of changed");
		assertEquals(event.getUsedNick(), bot.getConfiguration().getName(), "event used nick doesn't match old one in config");

		assertEquals(event.getBot().getNick(), bot.getConfiguration().getName(), "bots nick doesn't match events nick");
		assertEquals(event.getBot().getUserBot().getNick(), bot.getConfiguration().getName(), "bots user nick doesn't match events nick");

		test.assertEventClass(ServerResponseEvent.class);
		test.close();
	}
	
	

	@Test
	public void nickRenameQuitTest() throws IOException, IrcException {
		UserHostmask testUser1 = TestUtils.generateTestUserSourceHostmask(bot);
		UserHostmask testUser2 = TestUtils.generateTestUserOtherHostmask(bot);
		dao.createChannel("#testchannel");

		//Join both users, have 1 quit, the remaining user takes its nick, then quits
		inputParser.handleLine(":" + testUser1.getHostmask() + " JOIN :#testChannel");
		inputParser.handleLine(":" + testUser2.getHostmask() + " JOIN :#testChannel");

		inputParser.handleLine(":" + testUser1.getHostmask() + " QUIT :");
		inputParser.handleLine(":" + testUser2.getHostmask() + " NICK :" + testUser1.getNick());
		assertTrue(dao.containsUser(testUser1), "Renamed failed, user 2 didn't get renamed to 1");
		assertFalse(dao.containsUser(testUser2), "Renamed failed, user 2 still exists");

		inputParser.handleLine(":" + testUser1.getHostmask() + " QUIT :");
		assertFalse(dao.containsUser(testUser2), "quit failed, user 2 still exists");
	}

	@Test
	public void nickRenamePartTest() throws IOException, IrcException {
		UserHostmask testUser1 = TestUtils.generateTestUserSourceHostmask(bot);
		UserHostmask testUser2 = TestUtils.generateTestUserOtherHostmask(bot);
		dao.createChannel("#testchannel");

		//Join both users, have 1 quit, the remaining user takes its nick, then quits
		inputParser.handleLine(":" + testUser1.getHostmask() + " JOIN :#testChannel");
		inputParser.handleLine(":" + testUser2.getHostmask() + " JOIN :#testChannel");

		inputParser.handleLine(":" + testUser1.getHostmask() + " PART #testChannel :");
		inputParser.handleLine(":" + testUser2.getHostmask() + " NICK :" + testUser1.getNick());
		assertTrue(dao.containsUser(testUser1), "Renamed failed, user 2 didn't get renamed to 1");
		assertFalse(dao.containsUser(testUser2), "Renamed failed, user 2 still exists");

		inputParser.handleLine(":" + testUser1.getHostmask() + " PART #testChannel :");
		assertFalse(dao.containsUser(testUser2), "quit failed, user 2 still exists");
	}

	@Test
	public void nickRenameWithQuitTest() throws IOException, IrcException {
		UserHostmask testUser1 = TestUtils.generateTestUserSourceHostmask(bot);
		UserHostmask testUser2 = TestUtils.generateTestUserOtherHostmask(bot);
		dao.createChannel("#testchannel");

		inputParser.handleLine(":" + testUser1.getHostmask() + " JOIN :#testchannel");
		inputParser.handleLine(":" + testUser1.getHostmask() + " NICK :" + testUser2.getNick());
		inputParser.handleLine(":" + testUser2.getHostmask() + " QUIT :");

		assertFalse(dao.containsUser(testUser1), "Renamed failed, user 1 still exists");
		assertFalse(dao.containsUser(testUser2), "quit failed, user 2 still exists");

		inputParser.handleLine(":" + testUser2.getHostmask() + " JOIN :#testchannel");
		inputParser.handleLine(":" + testUser2.getHostmask() + " NICK :" + testUser1.getNick());
		inputParser.handleLine(":" + testUser1.getHostmask() + " QUIT :");

		assertFalse(dao.containsUser(testUser1), "quit failed, user 1 still exists");
		assertFalse(dao.containsUser(testUser2), "Renamed failed, user 2 still exists");
	}

	@Test
	public void banListTest() throws IOException, IrcException {
		Channel channel = dao.createChannel("#aChannel");
		User source = TestUtils.generateTestUserSource(bot);
		long time = 1415143822;

		inputParser.handleLine(":irc.someserver.net 367 PircBotXUser #aChannel *!test1@host.test " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 367 PircBotXUser #aChannel test2!*@host.test " + source.getHostmask() + " " + (time + 1));
		inputParser.handleLine(":irc.someserver.net 368 PircBotXUser #aChannel :End of Channel Ban List");

		BanListEvent event = bot.getTestEvent(BanListEvent.class, "BanListEvent not dispatched");
		assertEquals(event.getChannel(), channel, "Channel is wrong");

		//Verify all the sources and times
		int timeCounter = 0;
		for (BanListEvent.Entry curEntry : event.getEntries()) {
			assertEquals(curEntry.getSource(), source, "Source is wrong in entry " + curEntry);
			assertEquals(curEntry.getTime(), time + (timeCounter++), "Time is wrong in entry " + curEntry);
		}

		//Verify recipient hostmasks
		assertEquals(event.getEntries().get(0).getRecipient(), new UserHostmask(bot, null, "*", "test1", "host.test"), "Hostname in 0 is wrong");
		assertEquals(event.getEntries().get(1).getRecipient(), new UserHostmask(bot, null, "test2", "*", "host.test"), "Hostname in 0 is wrong");
	}

	@Test
	public void banExtbanListTest() throws IOException, IrcException {
		Channel channel = dao.createChannel("#aChannel");
		User source = TestUtils.generateTestUserSource(bot);
		long time = 1415143822;

		inputParser.handleLine(":irc.someserver.net 367 PircBotXUser #aChannel $a:sutekh " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 367 PircBotXUser #aChannel ~b:sutekh!alogin@ahostmask " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 368 PircBotXUser #aChannel :End of Channel Ban List");

		BanListEvent event = bot.getTestEvent(BanListEvent.class, "BanListEvent not dispatched");
		assertEquals(event.getEntries().get(0).getRecipient().getExtbanPrefix(), "$a", "No extban prefix on prefix:nick");
		assertEquals(event.getEntries().get(0).getRecipient(), new UserHostmask(bot, null, "sutekh", null, null));
		assertEquals(event.getEntries().get(1).getRecipient().getExtbanPrefix(), "~b", "No extban prefix on prefix:nick!login@hostmask");
		assertEquals(event.getEntries().get(1).getRecipient(), new UserHostmask(bot, null, "sutekh", "alogin", "ahostmask"));
	}

	@Test
	public void quietListTest() throws IOException, IrcException {
		Channel channel = dao.createChannel("#aChannel");
		User source = TestUtils.generateTestUserSource(bot);
		long time = 1415143822;

		inputParser.handleLine(":irc.someserver.net 728 PircBotXUser #aChannel q *!test1@host.test " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 728 PircBotXUser #aChannel q test2!*@host.test " + source.getHostmask() + " " + (time + 1));
		inputParser.handleLine(":irc.someserver.net 729 PircBotXUser #aChannel q :End of Channel Quiet List");

		QuietListEvent event = bot.getTestEvent(QuietListEvent.class, "QuietListEvent not dispatched");
		assertEquals(event.getChannel(), channel, "Channel is wrong");

		//Verify all the sources and times
		int timeCounter = 0;
		for (QuietListEvent.Entry curEntry : event.getEntries()) {
			assertEquals(curEntry.getSource(), source, "Source is wrong in entry " + curEntry);
			assertEquals(curEntry.getTime(), time + (timeCounter++), "Time is wrong in entry " + curEntry);
		}

		//Verify recipient hostmasks
		assertEquals(event.getEntries().get(0).getRecipient(), new UserHostmask(bot, null, "*", "test1", "host.test"), "Hostname in 0 is wrong");
		assertEquals(event.getEntries().get(1).getRecipient(), new UserHostmask(bot, null, "test2", "*", "host.test"), "Hostname in 0 is wrong");
	}

	@Test
	public void quietExtquietListTest() throws IOException, IrcException {
		Channel channel = dao.createChannel("#aChannel");
		User source = TestUtils.generateTestUserSource(bot);
		long time = 1415143822;

		inputParser.handleLine(":irc.someserver.net 728 PircBotXUser #aChannel q $a:sutekh " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 728 PircBotXUser #aChannel q ~b:sutekh!alogin@ahostmask " + source.getHostmask() + " " + time);
		inputParser.handleLine(":irc.someserver.net 729 PircBotXUser #aChannel q :End of Channel Quiet List");

		QuietListEvent event = bot.getTestEvent(QuietListEvent.class, "QuietListEvent not dispatched");
		assertEquals(event.getEntries().get(0).getRecipient().getExtbanPrefix(), "$a", "No extban prefix on prefix:nick");
		assertEquals(event.getEntries().get(0).getRecipient(), new UserHostmask(bot, null, "sutekh", null, null));
		assertEquals(event.getEntries().get(1).getRecipient().getExtbanPrefix(), "~b", "No extban prefix on prefix:nick!login@hostmask");
		assertEquals(event.getEntries().get(1).getRecipient(), new UserHostmask(bot, null, "sutekh", "alogin", "ahostmask"));
	}

	@Test
	public void channelModeMessageTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = TestUtils.generateTestUserSource(bot);
		inputParser.handleLine(":" + aUser.getHostmask() + " PRIVMSG +#aChannel :" + aString);

		//Verify event contents
		MessageEvent mevent = bot.getTestEvent(MessageEvent.class, "MessageEvent not dispatched");
		assertEquals(mevent.getChannel(), aChannel, "Event channel and original channel do not match");
		assertEquals(mevent.getUser(), aUser, "Event user and original user do not match");
		assertEquals(mevent.getMessage(), aString, "Message sent does not match");
	}

	@Test
	public void userDataBotTest() throws IOException, IrcException {
		assertEquals(bot.getUserBot().getLogin(), "PircBotX", "User bots login doesn't match");

		String otherUser = TestUtils.generateTestUserOtherHostmask(bot).getHostmask();
		inputParser.handleLine(":" + otherUser + " PRIVMSG #aChannel :test");
		assertEquals(bot.getUserBot().getLogin(), "PircBotX", "User bots login got changed");

		inputParser.handleLine(":TestBot!~PircBotX@some.hostmask JOIN #aChannel");
		assertEquals(bot.getUserBot().getLogin(), "~PircBotX", "User bots new login doesn't match");
		assertEquals(bot.getUserBot().getHostname(), "some.hostmask", "User bots new hostmask doesn't match");
	}

	@Test
	public void namesTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		assertFalse(dao.containsUser("aUser1"));
		assertFalse(dao.containsUser("aUser2"));

		//Also test prefixes in names
		inputParser.handleLine(":irc.someserver.net 353 PircBotXUser = #aChannel :+aUser1 aUser2");
		inputParser.handleLine(":irc.someserver.net 366 PircBotXUser #aChannel :End of /NAMES list.");

		UserListEvent event = bot.getTestEvent(UserListEvent.class);
		assertFalse(event.isComplete());
		assertEquals(event.getChannel(), aChannel, "Channel does not match");
		assertEquals(event.getUsers(), event.getChannel().getUsers(), "Event has users not in channel");
		List<User> allUsersExpected = Lists.newArrayList(event.getBot().getUserChannelDao().getAllUsers());
		allUsersExpected.remove(bot.getUserBot());
		assertEquals(allUsersExpected, event.getChannel().getUsers(), "Extra users in DAO that don't exist in channel");

		assertTrue(dao.containsUser("aUser1"), "Users: " + dao.getAllUsers());
		User user = dao.getUser("aUser1");
		assertNull(user.getLogin(), "Unexpected login for aUser1");
		assertNull(user.getHostname(), "Unexpected hostmask for aUser1");

		assertTrue(dao.containsUser("aUser2"));
		user = dao.getUser("aUser2");
		assertNull(user.getLogin(), "Unexpected login for aUser2");
		assertNull(user.getHostname(), "Unexpected hostmask for aUser2");
	}

	@DataProvider
	protected static Object[][] nickDifferentTestProvider() {
		return new Object[][]{
			new Object[]{"001"},
			new Object[]{"002"},
			new Object[]{"003"},
			new Object[]{"004"},};
	}

	@Test(dataProvider = "nickDifferentTestProvider")
	public void nickDifferentTest(String code) throws IOException, IrcException {
		assertEquals(bot.getNick(), PircTestRunner.BOT_NICK, "Starting nick changed");

		inputParser.handleLine(":irc.someserver.net " + code + " PBot :Welcome to the server");

		assertEquals(bot.getNick(), "PBot", "Nick not changed");
	}
}
