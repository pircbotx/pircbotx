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

import java.io.IOException;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.testng.annotations.Test;

/**
 *
 */
public class ServerInfoTest {
	@Test
	@SuppressWarnings("resource")
	public void rawParseTest() throws IOException, IrcException {
		//Just make sure it doesn't throw an exception
		new PircTestRunner(TestUtils.generateConfigurationBuilder())
				.assertBotHello()
				//Freenode
				.botIn(":irc.freenode.net 005 PircBotX CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server")
				.assertEventClass(ConnectEvent.class)
				.assertEventClass(ServerResponseEvent.class)
				.botIn(":irc.freenode.net 005 PircBotX EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				//Rizon
				.botIn(":irc.rizon.net 005 PircBotX CALLERID CASEMAPPING=rfc1459 DEAF=D KICKLEN=160 MODES=4 NICKLEN=30 TOPICLEN=390 PREFIX=(qaohv)~&@%+ STATUSMSG=~&@%+ NETWORK=Rizon MAXLIST=beI:100 TARGMAX=ACCEPT:,KICK:1,LIST:1,NAMES:1,NOTICE:4,PRIVMSG:4,WHOIS:1 CHANTYPES=# :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				.botIn(":irc.rizon.net 005 PircBotX CHANLIMIT=#:75 CHANNELLEN=50 CHANMODES=beI,k,l,BCMNORScimnpstz AWAYLEN=160 ELIST=CMNTU SAFELIST KNOCK NAMESX UHNAMES FNC EXCEPTS=e INVEX=I :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				//Mozilla
				.botIn(":irc.mozilla.org 005 QTest AWAYLEN=200 CASEMAPPING=rfc1459 CHANMODES=Zbeg,k,FLfjl,ABCDKMNOQRSTcimnprstuz CHANNELLEN=64 CHANTYPES=# CHARSET=ascii ELIST=MU ESILENCE EXCEPTS=e EXTBAN=,ABCNOQRSTUcmprz FNC KICKLEN=255 MAP :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				.botIn(":irc.mozilla.org 005 QTest MAXBANS=60 MAXCHANNELS=100 MAXPARA=32 MAXTARGETS=20 MODES=20 NAMESX NETWORK=Mozilla NICKLEN=31 OPERLOG OVERRIDE PREFIX=(Yqaohv)!~&@%+ SECURELIST SILENCE=32 :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				.botIn(":irc.mozilla.org 005 QTest SSL=[::]:6697 STARTTLS STATUSMSG=!~&@%+ TOPICLEN=307 UHNAMES USERIP VBANLIST WALLCHOPS WALLVOICES WATCH=32 :are supported by this server")
				.assertEventClass(ServerResponseEvent.class)
				//Issue #257
				.botIn(":irc.esylum.net 005 NICKNAME SAFELIST SILENCE KNOCK FNC WATCH=128 CHANLIMIT=#&:30 MAXLIST=be:60 NICKLEN=30 TOPICLEN=307 KICKLEN=307 CHANNELLEN=32")
				.assertEventClass(ServerResponseEvent.class)
				.close();
	}
}
