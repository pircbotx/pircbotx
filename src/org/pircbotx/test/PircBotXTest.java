/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx.test;

import java.util.Collection;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 *
 * @author LordQuackstar
 */
public class PircBotXTest extends PircBotX {
	public PircBotXTest() {
		setName("TheLQ");
		setLogin("LQ");
		setAutoNickChange(true);
		setFinger("Quackbot IRC bot by Lord.Quackstar. Source: http://quackbot.googlecode.com/");
		setMessageDelay(0);
		setVersion("Quackbot 3.3");
		setVerbose(true);
	}

	@Override
	protected void onConnect() {
		sendRawLine("NICKSERV identify manganip");
		joinChannel("#quackbot");
		joinChannel("##c++");
		joinChannel("##linux");
		joinChannel("#archlinux");
		joinChannel("#debian");
		joinChannel("#gentoo");
		joinChannel("#git");
		joinChannel("#jquery");
		joinChannel("#python");
		joinChannel("#ubuntu");
		joinChannel("#maemo");
		joinChannel("#mysql");
		joinChannel("#vim");
		joinChannel("#perl");
	}

	@Override
	protected void onUserList(String channel, Collection<User> users) {
		log("INNEFFIENT CALLED");
	}



	public static void main(String[] args) {
		try {
			PircBotXTest test = new PircBotXTest();
			test.connect("irc.freenode.net");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
