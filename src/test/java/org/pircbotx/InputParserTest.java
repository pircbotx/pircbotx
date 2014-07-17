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
package org.pircbotx;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.exception.DaoException;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
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
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;
import org.pircbotx.output.OutputRaw;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Usability tests for PircBotX that test how PircBotX handles lines and events.
 * Any other tests not involving processing lines should be in PircBotXTest
 * <p/>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Slf4j
@Test(singleThreaded = true)
public class InputParserTest {
	final static String aString = "I'm some super long string that has multiple words";
	protected List<Event> events;
	protected UserChannelDao dao;
	protected InputParser inputParser;
	protected PircBotX bot;

	/**
	 * General bot setup: Use GenericListenerManager (no threading), add custom
	 * listener to add all called events to Event set, set nick, etc
	 */
	@BeforeMethod
	public void setUp() {
		events = new ArrayList<Event>();
		Configuration configuration = TestUtils.generateConfigurationBuilder()
				.addListener(new Listener() {
			public void onEvent(Event event) throws Exception {
				events.add(event);
			}
		})
				.buildConfiguration();
		bot = new PircBotX(configuration) {
			@Override
			public boolean isConnected() {
				return true;
			}

			@Override
			protected void sendRawLineToServer(String line) {
				//Do nothing
			}
		};
		bot.nick = "PircBotXBot";

		//Save objects into fields for easier access
		this.dao = bot.getUserChannelDao();
		this.inputParser = bot.getInputParser();
	}

	@Test(description = "Verifies UserModeEvent from user mode being changed")
	public void userModeTest() throws IOException, IrcException {
		//Use two users to differentiate between source and target
		User aUser = dao.getUser("PircBotXUser");
		User aUser2 = dao.getUser("PircBotXUser2");
		inputParser.handleLine(":PircBotXUser MODE PircBotXUser2 :+i");

		//Verify event contents
		UserModeEvent uevent = getEvent(UserModeEvent.class, "UserModeEvent not dispatched on change");
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
		ChannelInfoEvent cevent = getEvent(ChannelInfoEvent.class, "No ChannelInfoEvent dispatched");

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
		inputParser.handleLine(":AUser!~ALogin@some.host INVITE PircBotXUser :#aChannel");

		//Verify event values
		InviteEvent ievent = getEvent(InviteEvent.class, "No InviteEvent dispatched");
		assertEquals(ievent.getUser(), "AUser", "InviteEvent user is wrong");
		assertEquals(ievent.getChannel(), "#aChannel", "InviteEvent channel is wrong");

		//Make sure the event doesn't create a user or a channel
		assertFalse(dao.channelExists("#aChannel"), "InviteEvent created channel, shouldn't have");
		assertFalse(dao.userExists("AUser"), "InviteEvent created user, shouldn't have");
	}

	@Test(description = "Verifies JoinEvent from user joining our channel")
	public void joinTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host JOIN :#aChannel");

		//Make sure the event gives us the same channels
		JoinEvent jevent = getEvent(JoinEvent.class, "No aChannel dispatched");
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
		assertTrue(dao.userExists("AUser"));
	}
	
	@Test(description = "Verifies DAO allows case insensitive lookups")
	public void insensitiveLookupTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host JOIN :#aChannel");
		
		assertEquals(dao.getUser("AUSER"), aUser, "Cannot lookup AUSER ignoring case");
		assertEquals(dao.getUser("aUser"), aUser, "Cannot lookup aUser ignoring case");
		assertEquals(dao.getUser("auser"), aUser, "Cannot lookup auser ignoring case");
		
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
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host TOPIC #aChannel :" + aString);

		//Verify event contents
		TopicEvent tevent = getEvent(TopicEvent.class, "No topic event dispatched");
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
	}

	@Test(description = "Verify TopicEvent's extended information from line sent after TOPIC line")
	public void topicInfoTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 333 PircBotXUser #aChannel AUser 1268522937");
		assertEquals(aChannel.getTopicSetter(), "AUser");
		assertEquals(aChannel.getTopicTimestamp(), (long) 1268522937 * 1000);

		TopicEvent tevent = getEvent(TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getChannel(), aChannel, "Event channel and origional channel do not match");
	}

	@Test(description = "Verify MessageEvent from channel message")
	public void messageTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :" + aString);

		//Verify event contents
		MessageEvent mevent = getEvent(MessageEvent.class, "MessageEvent not dispatched");
		assertEquals(mevent.getChannel(), aChannel, "Event channel and origional channel do not match");
		assertEquals(mevent.getUser(), aUser, "Event user and origional user do not match");
		assertEquals(mevent.getMessage(), aString, "Message sent does not match");
	}

	@Test(description = "Verify PrivateMessage from random user")
	public void privateMessageTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG PircBotXUser :" + aString);

		//Verify event contents
		PrivateMessageEvent pevent = getEvent(PrivateMessageEvent.class, "MessageEvent not dispatched");
		assertEquals(pevent.getUser(), aUser, "Event user and origional user do not match");
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
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host NOTICE " + target + " :" + aString);

		//Verify event contents
		NoticeEvent nevent = getEvent(NoticeEvent.class, "NoticeEvent not dispatched for channel notice");
		assertEquals(nevent.getChannel(), aChannel, "NoticeEvent's channel does not match given");
		assertEquals(nevent.getUser(), aUser, "NoticeEvent's user does not match given");
		assertEquals(nevent.getNotice(), aString, "NoticeEvent's notice message does not match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify ActionEvent from /me from a user in a channel")
	public void actionTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG " + target + " :\u0001ACTION " + aString + "\u0001");

		//Verify event contents
		ActionEvent aevent = getEvent(ActionEvent.class, "ActionEvent not dispatched for channel action");
		assertEquals(aevent.getChannel(), aChannel, "ActionEvent's channel doesn't match given");
		assertEquals(aevent.getUser(), aUser, "ActionEvent's user doesn't match given");
		assertEquals(aevent.getMessage(), aString, "ActionEvent's message doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify VersionEvent from a user")
	public void versionTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG " + target + " :\u0001VERSION\u0001");

		//Verify event contents
		VersionEvent vevent = getEvent(VersionEvent.class, "VersionEvent not dispatched for version");
		assertEquals(vevent.getUser(), aUser, "VersionEvent's user doesn't match given");
		assertEquals(vevent.getChannel(), aChannel, "VersionEvent's channel doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify PingEvent from a user")
	public void pingTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = dao.getUser("AUser");
		String pingValue = "2435fdfd3f3d";
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG " + target + " :\u0001PING " + pingValue + "\u0001");

		//Verify event contents
		PingEvent pevent = getEvent(PingEvent.class, "PingEvent not dispatched for version");
		assertEquals(pevent.getUser(), aUser, "PingEvent's user doesn't match given");
		assertEquals(pevent.getChannel(), aChannel, "PingEvent's channel doesn't match given");
		assertEquals(pevent.getPingValue(), pingValue, "PingEvent's ping value doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify TimeEvent from a user")
	public void timeTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG " + target + " :\u0001TIME\u0001");

		//Verify event contents
		TimeEvent tevent = getEvent(TimeEvent.class, "TimeEvent not dispatched for version");
		assertEquals(tevent.getUser(), aUser, "TimeEvent's user doesn't match given");
		assertEquals(tevent.getChannel(), aChannel, "TimeEvent's channel doesn't match given");
	}

	@Test(dataProvider = "channelOrUserDataProvider", description = "Verify FingerEvent from a user")
	public void fingerTest(String target) throws IOException, IrcException {
		Channel aChannel = target.startsWith("#") ? dao.createChannel(target) : null;
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host PRIVMSG " + target + " :\u0001FINGER\u0001");

		//Verify event contents
		FingerEvent fevent = getEvent(FingerEvent.class, "FingerEvent not dispatched for version");
		assertEquals(fevent.getUser(), aUser, "FingerEvent's user doesn't match given");
		assertEquals(fevent.getChannel(), aChannel, "FingerEvent's channel doesn't match given");
	}
	
	@Test
	public void awayTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":irc.someserver.net 301 PircBotXUser AUser :" + aString);
		assertEquals(aUser.getAwayMessage(), aString, "Away message isn't expected");
	}
	
	@Test
	public void awayNotifyTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":AUser!~ALogin@some.host AWAY :" + aString);
		assertEquals(aUser.getAwayMessage(), aString, "Away message isn't expected");
	}
	
	@Test
	public void awayNotifyMessageEmptyTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":AUser!~ALogin@some.host AWAY :");
		assertEquals(aUser.getAwayMessage(), "", "Away message isn't empty");
	}
	
	@Test
	public void awayNotifyMessageMissingTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		
		assertEquals(aUser.getAwayMessage(), null, "Away default isn't null");
		inputParser.handleLine(":AUser!~ALogin@some.host AWAY");
		assertEquals(aUser.getAwayMessage(), "", "Away message isn't empty");
	}

	@Test
	public void modeResponseTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 324 PircBotXUser #aChannel +cnt");

		ModeEvent mevent = getEvent(ModeEvent.class, "ModeEvent not dispatched for mode response");
		assertEquals(mevent.getMode(), "+cnt", "ModeEvent's mode doesn't equal given");
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel doesn't match given");
		assertNull(mevent.getUser(), "ModeEvent's user not null for mode response");
	}
	
	@Test
	public void initModeTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		
		assertFalse(aChannel.isModerated());
		assertFalse(aChannel.isInviteOnly());
		assertFalse(aChannel.isChannelPrivate());
		
		inputParser.handleLine(":irc.someserver.net 324 PircBotX #aChannel +ipmd");
		
		assertTrue(aChannel.isModerated());
		assertTrue(aChannel.isInviteOnly());
		assertTrue(aChannel.isChannelPrivate());
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
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		User otherUser = dao.getUser("OtherUser");
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel " + mode + " OtherUser");

		//Verify generic ModeEvent contents
		ModeEvent mevent = getEvent(ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given with mode " + mode);
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given with mode " + mode);
		assertEquals(mevent.getMode(), mode + " OtherUser", "ModeEvent's mode does not match given mode");

		//Verify specific event contents
		GenericUserModeEvent event = (GenericUserModeEvent) getEvent(eventClass, "No " + eventClass.getSimpleName() + " dispatched with " + mode);
		assertEquals(event.getChannel(), aChannel, eventClass.getSimpleName() + "'s channel does not match given with mode " + mode);
		assertEquals(event.getUser(), aUser, eventClass.getSimpleName() + "'s source user does not match given with mode " + mode);
		assertEquals(event.getRecipient(), otherUser, eventClass.getSimpleName() + "'s recipient user does not match given with mode " + mode);

		//Make sure the event's is* method returns the correct value
		assertEquals(eventClass.getMethod(checkMethod).invoke(event), mode.startsWith("+"), "Event's " + checkMethod + " method doesn't return correct value");

		//Make sure the channels is* method returns the correct value
		assertEquals(aChannel.getClass().getMethod(checkMethod, User.class).invoke(aChannel, otherUser), mode.startsWith("+"), "Channels's " + checkMethod + " method doesn't return correct value");
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
		List<Object[]> testParams = new ArrayList();
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
	public void channelModeChangeTest(String mode, String parentMode, Class<?> modeClass) throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		if (mode.startsWith("-")) {
			//Set the mode first
			String channelMode = (parentMode != null) ? parentMode : mode.substring(1);
			ImmutableList<String> channelModeParsed = ImmutableList.copyOf(StringUtils.split(channelMode, ' '));
			aChannel.setMode(channelMode, channelModeParsed);
		}
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel " + mode);

		//Verify generic ModeEvent contents
		ModeEvent mevent = getEvent(ModeEvent.class, "No ModeEvent dispatched with " + mode);
		assertEquals(mevent.getChannel(), aChannel, "ModeEvent's channel does not match given");
		assertEquals(mevent.getUser(), aUser, "ModeEvent's user does not match given");
		assertEquals(mevent.getMode(), mode, "ModeEvent's mode does not match given mode");

		//Verify generic mode event contents
		String modeName = modeClass.getSimpleName();
		GenericChannelModeEvent subEvent = (GenericChannelModeEvent) getEvent(modeClass, "No " + modeName + " dispatched with " + mode);
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
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel +k testPassword");
		SetChannelKeyEvent event = getEvent(SetChannelKeyEvent.class, "No SetChannelKeyEvent dispatched + made it past genericChannelModeTest");
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
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel -k");
		RemoveChannelKeyEvent event = getEvent(RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertNull(event.getKey(), "RemoveChannelKeyEvent key doesn't match given");
		assertNull(aChannel.getChannelKey(), "Channel key doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelKeyEvent has the correct key")
	public void removeChannelKeyEventTest() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel"); 
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel -k testPassword");
		RemoveChannelKeyEvent event = getEvent(RemoveChannelKeyEvent.class, "No RemoveChannelKeyEvent dispatched + made it past genericChannelModeTest");
		assertEquals(event.getKey(), "testPassword", "RemoveChannelKeyEvent key doesn't match given");
		assertNull(aChannel.getChannelKey(), "Channel key doesn't match given");
	}

	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify SetChannelLimitEvent has the correct limit")
	public void setChannelLimitEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel"); 
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel +l 10");
		SetChannelLimitEvent event = getEvent(SetChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
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
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel -l 10");
		RemoveChannelLimitEvent event = getEvent(RemoveChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(aChannel.getChannelLimit(), -1, "Channel limit doesn't match given");
	}
	
	@Test(dependsOnMethods = "channelModeChangeTest", description = "Verify RemoveChannelLimitEvent has the correct limit")
	public void removeChannelLimitEmptyEvent() throws IOException, IrcException {
		//Since genericChannelModeTest does most of the verification, not much is needed here
		Channel aChannel = dao.createChannel("#aChannel"); 
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel -l");
		RemoveChannelLimitEvent event = getEvent(RemoveChannelLimitEvent.class, "No SetChannelLimitEvent dispatched + made it past genericChannelModeTest");
		assertEquals(aChannel.getChannelLimit(), -1, "Channel limit doesn't match given");
	}

	/**
	 * Simulate WHO response.
	 */
	@Test(description = "Verify WHO response handling + UserListEvent")
	public void whoTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~ALogin some.host irc.someserver.net AUser H@+ :2 " + aString);
		//Issue #151: Test without full name
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #aChannel ~OtherLogin some.host1 irc.otherserver.net OtherUser G :4");
		inputParser.handleLine(":irc.someserver.net 315 PircBotXUser #aChannel :End of /WHO list.");

		//Make sure all information was created correctly
		assertTrue(dao.channelExists("#aChannel"), "WHO response didn't create channel");
		assertTrue(dao.userExists("AUser"), "WHO response didn't create user AUser");
		assertTrue(dao.userExists("OtherUser"), "WHO response didn't create user OtherUser");
		Channel aChannel = dao.getChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		User otherUser = dao.getUser("OtherUser");
		assertNotNull(aChannel, "Channel from WHO response is null");
		assertNotNull(aUser, "AUser from WHO response is null");
		assertNotNull(otherUser, "OtherUser from WHO response is null");

		//Verify event
		UserListEvent uevent = getEvent(UserListEvent.class, "UserListEvent not dispatched");
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
		assertEquals(otherUser.getRealName(), "", "RealName doesn't match one given during WHO");
		assertEquals(otherUser.getServer(), "irc.otherserver.net", "Server doesn't match one given during WHO");
		assertTrue(otherUser.isAway(), "User is not away though specified as here in WHO");
		assertFalse(aChannel.isOp(otherUser), "User is labeled as an op even though specified as one in WHO");
		assertFalse(aChannel.hasVoice(otherUser), "User is labeled as voiced even though specified as one in WHO");
	}
	
	@Test(dependsOnMethods = "whoTest", description = "Veryfy that we don't falsely registers all WHO responses as valid channels", expectedExceptions = DaoException.class)
	public void whoTestFalseChannels() throws IOException, IrcException {
		assertFalse(dao.channelExists("#randomChannel"), "Intial test to ensure channel doesn't exist");
		
		//Sending out a "WHO AUser" command
		inputParser.handleLine(":irc.someserver.net 352 PircBotXUser #randomChannel ~ALogin 8dce28.83b021.3a4fde.2fed84 irc.someserver.net AUser H :0 " + aString);
		inputParser.handleLine(":irc.someserver.net 315 PircBotXUser AUser :End of /WHO list.");

		assertFalse(dao.channelExists("#randomChannel"), "WHO response for a user may not result in unrelated channel creation");
		assertFalse(dao.channelExists("AUser"), "WHO response for a user may not result in channel creation");
	}
	
	@Test(dependsOnMethods = "joinTest", description = "Verify KickEvent from some user kicking another user")
	public void kickTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User aUser = dao.getUser("AUser");
		User otherUser = dao.getUser("OtherUser");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":AUser!~ALogin@some.host KICK #aChannel OtherUser :" + aString);

		//Verify event contents
		KickEvent kevent = getEvent(KickEvent.class, "KickEvent not dispatched");
		assertEquals(kevent.getChannel(), aChannel, "KickEvent channel does not match");
		assertEquals(kevent.getUser(), aUser, "KickEvent's getSource doesn't match kicker user");
		assertEquals(kevent.getRecipient(), otherUser, "KickEvent's getRecipient doesn't match kickee user");
		assertEquals(kevent.getReason(), aString, "KickEvent's reason doesn't match given one");

		//Make sure we've sufficently forgotten about the user
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that was kicked an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that was kicked with voice");
		assertFalse(dao.userExists("OtherUser"), "Bot still considers user to exist after kick");
		assertNotSame(dao.getUser("OtherUser"), otherUser, "User fetched with getUser and previous user object are exactly the same");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify QuitEvent from user that just joined quitting")
	public void quitWithMessageTest() throws IOException, IrcException {
		Channel aChannel = dao.createChannel("#aChannel");
		User otherUser = dao.getUser("OtherUser");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel +o OtherUser");
		inputParser.handleLine(":AUser!~ALogin@some.host MODE #aChannel +v OtherUser");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 QUIT :" + aString);

		//Check event contents
		QuitEvent qevent = getEvent(QuitEvent.class, "QuitEvent not dispatched");
		//Since QuitEvent gives us a snapshot, compare contents
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), aString, "QuitEvent's reason does not match given");

		//Make sure user is gone
		assertFalse(aChannel.isOp(otherUser), "Channel still considers user that quit an op");
		assertFalse(aChannel.hasVoice(otherUser), "Channel still considers user that quit with voice");
		assertFalse(dao.userExists("OtherUser"), "Bot still considers user to exist after quit");
		assertTrue(otherUser.getChannels().isEmpty(), "User still connected to other channels after quit");
		assertFalse(aChannel.getUsers().contains(otherUser), "Channel still associated with user that quit");
		assertNotSame(dao.getUser("OtherUser"), otherUser, "User fetched with getUser and previous user object are exactly the same");
	}

	@Test(dependsOnMethods = "quitWithMessageTest", description = "Verify QuitEvent with no message")
	public void quitWithoutMessageTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		User otherUser = dao.getUser("OtherUser");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 QUIT :");

		//Check event contents
		QuitEvent qevent = getEvent(QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), "", "QuitEvent's reason does not match given");
	}

	@Test(dependsOnMethods = "quitWithMessageTest", description = "Verify QuitEvent with no message")
	public void quitWithoutMessageAndColonTest() throws IOException, IrcException {
		dao.createChannel("#aChannel");
		User otherUser = dao.getUser("OtherUser");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 QUIT");

		//Check event contents
		QuitEvent qevent = getEvent(QuitEvent.class, "QuitEvent not dispatched");
		assertEquals(qevent.getUser().getGeneratedFrom(), otherUser, "QuitEvent's user does not match given");
		assertEquals(qevent.getReason(), "", "QuitEvent's reason does not match given");
	}

	@Test(dependsOnMethods = "joinTest", description = "Verify part with message")
	public void partWithMessageTest() throws IOException, IrcException {
		User otherUser = dao.getUser("OtherUser");
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 PART #aChannel :" + aString);

		//Check event contents
		PartEvent event = getEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), aString, "PartEvent's reason doesn't match given");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part without message")
	public void partWithoutMessageTest() throws IOException, IrcException {
		User otherUser = dao.getUser("OtherUser");
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 PART #aChannel :");

		//Check event contents
		PartEvent event = getEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), "", "PartEvent's reason doesn't match given");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part without message")
	public void partWithoutMessageAndColonTest() throws IOException, IrcException {
		User otherUser = dao.getUser("OtherUser");
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":OtherUser!~OtherLogin@some.host1 PART #aChannel");

		//Check event contents
		PartEvent event = getEvent(PartEvent.class, "PartEvent not dispatched");
		assertEquals(event.getChannel(), aChannel, "PartEvent's channel doesn't match given");
		assertEquals(event.getUser().getGeneratedFrom(), otherUser, "PartEvent's user doesn't match given");
		assertEquals(event.getReason(), "", "PartEvent's reason doesn't match given");
	}

	@Test(dependsOnMethods = "partWithMessageTest", description = "Verify part with us")
	public void partUs() throws IOException, IrcException {
		User otherUser = dao.getUser("PircBotXBot");
		Channel aChannel = dao.createChannel("#aChannel");
		inputParser.handleLine(":PircBotXBot!PircBotX@some.host1 JOIN :#aChannel");
		inputParser.handleLine(":PircBotXBot!PircBotX@some.host1 PART #aChannel");

		//Check event contents
		PartEvent event = getEvent(PartEvent.class, "PartEvent not dispatched for this bot");
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
		MotdEvent event = getEvent(MotdEvent.class, "MotdEvent not dispatched");
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
		WhoisEvent event = getEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getNick(), "OtherUser", "Nick does not match given");
		assertEquals(event.getLogin(), "~OtherLogin", "Login does not match given");
		assertEquals(event.getHostname(), "some.host1", "Hostname does not match given");
		assertEquals(event.getRealname(), aString, "Real name does not match given");
		assertEquals(event.getServer(), "irc2.someserver.net", "Server address wrong");
		assertEquals(event.getServerInfo(), aString + aString, "Server info wrong");
		assertEquals(event.getIdleSeconds(), 6077, "Idle time doesn't match given");
		assertEquals(event.getSignOnTime(), 1347373349, "Sign on time doesn't match given");
		assertNull(event.getRegisteredAs(), "User isn't registered");

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
		WhoisEvent event = getEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "", "Nickserv account does not match given");
	}
	
	@Test
	public void whoisRegistered330NoNameTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 330 PircBotXUser OtherUser :is logged in as");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");
		
		//Check event contents
		WhoisEvent event = getEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "", "Nickserv account does not match given");
	}
	
	@Test
	public void whoisRegistered330NameTest() throws IOException, IrcException {
		inputParser.handleLine(":irc.someserver.net 311 PircBotXUser OtherUser ~OtherLogin some.host1 * :" + aString);
		inputParser.handleLine(":irc.someserver.net 330 PircBotXUser OtherUser nickservAccount :is logged in as");
		inputParser.handleLine(":irc.someserver.net 318 PircBotXUser OtherUser :End of /WHOIS list.");
		
		//Check event contents
		WhoisEvent event = getEvent(WhoisEvent.class, "WhoisEvent not dispatched");
		assertEquals(event.getRegisteredAs(), "nickservAccount", "Nickserv account does not match given");
	}

	@Test
	public void serverPingTest() throws IOException, IrcException {
		String pingString = "FDS9AG65FH32";
		inputParser.handleLine("PING " + pingString);

		//Check event contents
		ServerPingEvent event = getEvent(ServerPingEvent.class, "ServerPingEvent not dispatched");
		assertEquals(event.getResponse(), pingString, "Ping string doesn't match given");
	}

	@Test
	public void nickChangeOtherTest() throws IOException, IrcException {
		User aUser = dao.getUser("AUser");
		inputParser.handleLine(":AUser!~ALogin@some.host NICK :AnotherUser");

		NickChangeEvent event = getEvent(NickChangeEvent.class, "NickChangeEvent not dispatched for NICK");
		assertEquals(event.getOldNick(), "AUser");
		assertEquals(event.getNewNick(), "AnotherUser");
		assertEquals(event.getUser(), aUser);
	}

	@Test
	public void nickChangeBotTest() throws IOException, IrcException {
		User botUser = bot.getUserBot();
		inputParser.handleLine(":PircBotXBot!~PircBotXBot@bot.host NICK :PircBotXBetter");

		NickChangeEvent event = getEvent(NickChangeEvent.class, "NickChangeEvent not dispatched for NICK");
		assertEquals(event.getOldNick(), "PircBotXBot");
		assertEquals(event.getNewNick(), "PircBotXBetter");
		assertEquals(bot.getNick(), "PircBotXBetter");
		assertEquals(event.getUser(), botUser);
		assertEquals(event.getUser(), bot.getUserBot());
	}

	/**
	 * After simulating a server response, call this to get a specific Event from
	 * the Event set. Note that if the event does not exist an Assertion error will
	 * be thrown. Also note that only the last occurrence of the event will be fetched
	 * @param <B> The event type to be fetched
	 * @param clazz The class of the event type
	 * @param errorMessage An error message if the event type does not exist
	 * @return A single requested event
	 */
	protected <B> B getEvent(Class<B> clazz, String errorMessage) {
		B cevent = null;
		for (Event curEvent : events)
			if (curEvent.getClass().isAssignableFrom(clazz))
				cevent = (B) curEvent;
		//Failed, does not exist
		assertNotNull(cevent, errorMessage);
		return cevent;
	}
}