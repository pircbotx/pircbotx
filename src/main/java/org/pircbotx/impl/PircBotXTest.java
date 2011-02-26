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
package org.pircbotx.impl;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.UserListEvent;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class PircBotXTest implements Listener {
	public void onEvent(Event rawevent) throws Exception {
		if (rawevent instanceof ConnectEvent) {
			ConnectEvent event = (ConnectEvent) rawevent;
			event.getBot().joinChannel("#quackbot");
		} else if (rawevent instanceof UserListEvent)
			rawevent.getBot().log("INNEFFIENT CALLED");
	}

	public static void main(String[] args) {
		PircBotX bot = new PircBotX();
		bot.setName("TheLQ");
		bot.setLogin("LQ");
		bot.setAutoNickChange(true);
		bot.setFinger("Quackbot IRC bot by Lord.Quackstar. Source: http://quackbot.googlecode.com/");
		bot.setMessageDelay(0);
		bot.setVersion("Quackbot 3.3");
		bot.setVerbose(true);
		try {
			bot.connect("irc.freenode.org");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
