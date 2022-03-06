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

import java.util.List;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;

/**
 *
 */
public class UtilsTest {
	protected static final String testString = "Hi there: how are you?";

	@DataProvider
	public Object[][] suffixDataProvider() {
		return new Object[][]{{""}, {"    "}};
	}

	@Test(dataProvider = "suffixDataProvider")
	public void tokenizeChannelMessageTest(String suffix) {
		List<String> tokens = Utils.tokenizeLine(":AUser!~ALogin@some.host PRIVMSG #aChannel :" + testString + suffix);

		assertEquals(tokens.size(), 4, "Unexpected length: " + tokens);
		assertEquals(tokens.get(0), ":AUser!~ALogin@some.host");
		assertEquals(tokens.get(1), "PRIVMSG");
		assertEquals(tokens.get(2), "#aChannel");
		assertEquals(tokens.get(3), testString);
	}

	@Test(dataProvider = "suffixDataProvider")
	public void tokenizePing(String suffix) {
		List<String> tokens = Utils.tokenizeLine("PING sa3214323" + suffix);

		assertEquals(tokens.size(), 2, "Unexpected length: " + tokens);
		assertEquals(tokens.get(0), "PING");
		assertEquals(tokens.get(1), "sa3214323");
	}

	@Test
	public void parseCommandTest() {
		assertEquals(Utils.parseCommand("?say ", "?say hi everybody"), "hi everybody");
		assertEquals(Utils.parseCommand("?say ", "?other stuff"), null);
		assertEquals(Utils.parseCommand("?start", "?start"), "");
		assertEquals(Utils.parseCommand("?start", "?stop"), null);
		assertEquals(Utils.parseCommand("?ping", "?ping"), "");
	}
}
