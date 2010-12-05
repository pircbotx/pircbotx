/*
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx.impl;

import java.util.Set;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Connect;
import org.pircbotx.hooks.UserList;

/**
 *
 * @author LordQuackstar
 */
public class PircBotXTest extends PircBotX implements Connect.Listener, UserList.Listener {
	public PircBotXTest() {
		setName("TheLQ");
		setLogin("LQ");
		setAutoNickChange(true);
		setFinger("Quackbot IRC bot by Lord.Quackstar. Source: http://quackbot.googlecode.com/");
		setMessageDelay(0);
		setVersion("Quackbot 3.3");
		setVerbose(true);
		getListeners().addListener(this);
	}

	@Override
	public void onConnect(Connect.Event event) {
		sendRawLine("NICKSERV identify manganip");
		joinChannel("#honcast");
		joinChannel("#3on3.et");
		joinChannel("#lemondogs");
		joinChannel("#5on5");
		joinChannel("#5on5.css");
		joinChannel("#pracc");
		joinChannel("#matsi");
		joinChannel("#k1ck");
		joinChannel("#honcast");
		joinChannel("#cod4.wars");
		joinChannel("#teamliquid");
		joinChannel("#pcw");
	}

	@Override
	public void onUserList(UserList.Event event) {
		log("INNEFFIENT CALLED");
	}

	public static void main(String[] args) {
		try {
			PircBotXTest test = new PircBotXTest();
			test.connect("irc.quakenet.org");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
