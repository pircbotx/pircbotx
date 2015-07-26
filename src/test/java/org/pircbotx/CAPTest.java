/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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

import lombok.extern.slf4j.Slf4j;
import static org.testng.Assert.*;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.exception.CAPException;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey
 */
@Slf4j
public class CAPTest {
	@Test
	public void SASLTest() throws Exception {
		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				//Also test multiple cap handlers that may or may not fail
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("unused-cap", true))
				.addCapHandler(new SASLCapHandler("jilles", "sesame"))
				.addCapHandler(new EnableCapHandler("unused-cap2", true))
		)
				.assertBotOut("CAP LS")
				.assertBotHello()
				.botIn(":%server CAP * LS :random-cap2 sasl random-cap")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("CAP REQ :sasl")
				.botIn(":%server CAP * ACK :sasl")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("AUTHENTICATE PLAIN")
				.botIn("AUTHENTICATE +")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("AUTHENTICATE amlsbGVzAGppbGxlcwBzZXNhbWU=")
				.botIn(":%server 903 TestBot :SASL auth success")
				.assertEventClass(ServerResponseEvent.class)
				.assertBotOut("CAP END");

		assertTrue(test.bot.getEnabledCapabilities().contains("sasl"), "SASL isn't on the enabled capabilities list");
		assertEquals(test.bot.getEnabledCapabilities().size(), 1, "SASL isn't on the enabled capabilities list");

		test.close();
	}

	@Test
	public void enableTest() throws Exception {
		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				//Also test multiple cap handlers that may or may not fail
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("test-cap"))
				.addCapHandler(new SASLCapHandler("jilles", "sesame", true))
				.addCapHandler(new EnableCapHandler("unused-cap2", true))
		)
				.assertBotOut("CAP LS")
				.assertBotHello()
				.botIn(":%server CAP * LS :random-cap2 test-cap random-cap")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("CAP REQ :test-cap")
				.botIn(":%server CAP * ACK :test-cap")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("CAP END");

		assertTrue(test.bot.getEnabledCapabilities().contains("test-cap"), "test-cap isn't on the enabled capabilities list");
		assertEquals(test.bot.getEnabledCapabilities().size(), 1, "SASL isn't on the enabled capabilities list");

		test.close();
	}

	@Test
	public void capNoneTest() throws Exception {
		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				//Also test multiple cap handlers that may or may not fail
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("test-cap", true))
				.addCapHandler(new SASLCapHandler("jilles", "sesame", true))
				.addCapHandler(new EnableCapHandler("unused-cap2", true))
		)
				.assertBotOut("CAP LS")
				.assertBotHello()
				.botIn(":%server CAP * LS :random-cap2 random-cap")
				.assertEventClass(UnknownEvent.class) //TODO: Make CAP Events
				.assertBotOut("CAP END");

		assertTrue(test.bot.getEnabledCapabilities().isEmpty(), "unknown capabilities");

		test.close();
	}

	@Test(expectedExceptions = CAPException.class)
	public void enableUnsupportedCapExceptionTest() throws Exception {
		PircTestRunner test = new PircTestRunner(TestUtils.generateConfigurationBuilder()
				//Also test multiple cap handlers that may or may not fail
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("test-cap"))
				.addCapHandler(new SASLCapHandler("jilles", "sesame", true))
				.addCapHandler(new EnableCapHandler("unused-cap2", true))
		)
				.assertBotOut("CAP LS")
				.assertBotHello();

		try {
			test
					.botIn(":%server CAP * LS :random-cap2 random-cap")
					.assertBotOut("CAP END");
		} finally {
			assertTrue(test.bot.getEnabledCapabilities().isEmpty(), "unknown capabilities");
			test.close();
		}
	}
}
