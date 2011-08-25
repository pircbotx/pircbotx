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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for MultiBotManager
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MultiBotManagerTest {
	protected MultiBotManager manager;

	@BeforeMethod
	public void setup() {
		manager = new MultiBotManager("TestBot");
	}

	@Test(description = "Make sure the listener manager is shared")
	public void listenerManagerSameAndNotNullTest() {
		PircBotX bot1 = manager.createBot("some.server1");
		PircBotX bot2 = manager.createBot("some.server2");
		assertNotNull(bot1.getListenerManager(), "MultiBotManager's first bot doesn't have a listener manager");
		assertNotNull(bot2.getListenerManager(), "MultiBotManager's second bot doesn't have a listener manager");
		assertEquals(bot1.getListenerManager(), bot2.getListenerManager(), "MultiBotManager's bots don't have the same listener");
	}
}
