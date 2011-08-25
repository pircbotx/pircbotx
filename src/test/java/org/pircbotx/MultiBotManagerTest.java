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
	
	@Test
	public void listenerManagerExists() {
		manager.createBot("some.server");
		
		//Make sure the listenermanager is in the bots
		assertEquals(manager.getBots().size(), 1, "More than 1 bot has been created");
		PircBotX bot = manager.getBots().iterator().next();
		assertNotNull(bot.getListenerManager());
	}
}
