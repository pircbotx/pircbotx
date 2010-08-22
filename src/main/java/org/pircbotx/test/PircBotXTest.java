/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx.test;

import java.util.Set;
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
	protected void onUserList(String channel, Set<User> users) {
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
