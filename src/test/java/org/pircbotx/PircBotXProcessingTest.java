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
import org.apache.commons.lang.StringUtils;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Usability tests for PircBotX that test how PircBotX handles lines and events.
 * Any other tests not involving processing lines should be in PircBotXTest
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXProcessingTest {
	final PircBotX bot = new PircBotX();
	final Set<Event> events = new HashSet<Event>();
	final String aString = "I'm some super long string that has multiple words";
	
	@BeforeClass
	public void setup() {
		bot.setListenerManager(new GenericListenerManager());
		bot.getListenerManager().addListener(new Listener() {
			public void onEvent(Event event) throws Exception {
				events.add(event);
			}
		});
	}
	
	@Test
	public void listTest() {
		//Simulate /LIST response, verify results
		events.clear();
		bot.handleLine(":irc.someserver.net 321 Channel :Users Name");
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel 99 :" + aString);
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel1 100 :" + aString + aString);
		bot.handleLine(":irc.someserver.net 322 PircBotXUser #PircBotXChannel2 101 :" + aString + aString + aString);
		bot.handleLine(":irc.someserver.net 323 :End of /LIST");
		ChannelInfoEvent cevent = getEvent(ChannelInfoEvent.class, "No ChannelInfoEvent dispatched");
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

		System.out.println("Success: Output from /LIST command gives expected results");
	}
	
	@Test(dependsOnMethods="listTest")
	public void inviteTest() {
		//Simulate getting invited to a channel
		events.clear();
		bot.handleLine(":AUser!~ALogin@some.host INVITE PircBotXUser :#aChannel");
		
		//Verify event values
		InviteEvent ievent = getEvent(InviteEvent.class, "No InviteEvent dispatched");
		assertEquals(ievent.getUser(), "AUser", "InviteEvent user is wrong");
		assertEquals(ievent.getChannel(), "#aChannel", "InviteEvent channel is wrong");
		
		//Make sure the event doesn't create a user or a channel
		assertFalse(bot.channelExists("#aChannel"), "InviteEvent created channel, shouldn't have");
		assertFalse(bot.userExists("AUser"), "InviteEvent created user, shouldn't have");
	}
	
	@Test(dependsOnMethods="inviteTest")
	public void joinTest() {
		//Simulate another user joining
		events.clear();
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		bot.handleLine(":AUser!~ALogin@some.host JOIN :#aChannel");
		
		//Make sure the event gives us the same channels
		JoinEvent jevent = getEvent(JoinEvent.class, "No aChannel dispatched");
		assertEquals(jevent.getChannel(), aChannel, "Event's channel does not match origional channel");
		assertEquals(jevent.getUser(), aUser, "Event's user does not match origional user");
		
		//Make sure user info was updated
		assertEquals(aUser.getLogin(), "~ALogin", "User login wrong on JoinEvent");
		assertEquals(aUser.getHostmask(), "some.host", "User hostmask wrong on JoinEvent");
		Channel userChan = null;
		for(Channel curChan : aUser.getChannels())
			if(curChan.getName().equals("#aChannel"))
				userChan = curChan;
		assertNotNull(userChan, "User is not joined to channel after JoinEvent");
		User chanUser = null;
		for(User curUser : aChannel.getUsers())
			if(curUser.getNick().equals("AUser"))
				chanUser = curUser;
		assertNotNull(chanUser, "Channel is not joined to user after JoinEvent");
		assertTrue(bot.userExists("AUser"))
		
		System.out.println("Success: Information up to date when user joins");
	}
	
	@Test(dependsOnMethods="joinTest")
	public void topicTest() {
		//Simulate a /TOPIC or /JOIN, verify results
		Channel aChannel = bot.getChannel("#aChannel");
		bot.handleLine(":irc.someserver.net 332 PircBotXUser #aChannel :" + aString + aString);
		assertEquals(aChannel.getTopic(), aString + aString);

		System.out.println("Success: Topic content output from /TOPIC or /JOIN gives expected results");
	}

	@Test(dependsOnMethods="topicTest")
	public void topicInfoTest() {
		//Simulate a /TOPIC info (sent after joining a channel and topic is sent), verify results
		Channel aChannel = bot.getChannel("#aChannel");
		events.clear();
		bot.handleLine(":irc.someserver.net 333 PircBotXUser #aChannel AUser 1268522937");
		assertEquals(aChannel.getTopicSetter(), "AUser");
		assertEquals(aChannel.getTopicTimestamp(), (long) 1268522937 * 1000);

		TopicEvent tevent = getEvent(TopicEvent.class, "No topic event dispatched");
		assertEquals(tevent.getChannel(), aChannel, "Event channel and origional channel do not match");
		
		System.out.println("Success: Topic info output from /TOPIC or /JOIN gives expected results");
	}
	
	@Test(dependsOnMethods="topicInfoTest")
	public void messageTest() {
		//Simulate a channel message
		Channel aChannel = bot.getChannel("#aChannel");
		User aUser = bot.getUser("AUser");
		events.clear();
		bot.handleLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :" + aString);
		
		//Verify event contents
		MessageEvent mevent = getEvent(MessageEvent.class, "MessageEvent not dispatched");
		assertEquals(mevent.getChannel(), aChannel, "Event channel and origional channel do not match");
		assertEquals(mevent.getUser(), aUser, "Event user and origional user do not match");
		assertEquals(mevent.getMessage(), aString, "Message sent does not match");
		
		System.out.println("Success: MessageEvent gives expected results");
	}
	
	@Test(dependsOnMethods="messageTest")
	public void mapCheck1Test() {
		//Test in this stage the status of the Many To Many map
		ManyToManyMap<Channel, User> map = bot._userChanInfo;
		
		assertEquals(map.getAValues().size(), 1, "Extra Channel values. Full printout \n " + StringUtils.join(map.getAValues().toArray(), "\n "));
		assertEquals(map.getBValues().size(), 1, "Extra User values. Full printout \n " + StringUtils.join(map.getBValues().toArray(), "\n "));
	}
	
	public <B> B getEvent(Class<B> clazz, String errorMessage) {
		B cevent = null;
		for(Event curEvent : events)
			if(curEvent.getClass().isAssignableFrom(clazz))
				return (B)curEvent;
		//Failed, does not exist
		assertNotNull(cevent, errorMessage);
		return null;
	}
}
