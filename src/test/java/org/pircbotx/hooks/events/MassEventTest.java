/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.hooks.events;

import java.lang.reflect.Constructor;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MassEventTest {
	@Test(description = "Verify event has a single constructor", dataProvider = "getEvents", dataProviderClass = TestUtils.class)
	public void singleConstructorTest(Class<?> event) {
		assertEquals(event.getDeclaredConstructors().length, 1, TestUtils.wrapClass(event, "No constructor or extra constructor found"));
	}

	@Test(description = "Verify event has a single constructor", dependsOnMethods = "singleConstructorTest", dataProvider = "getEvents", dataProviderClass = TestUtils.class)
	public void firstConstructorArgBotTest(Class<?> event) {
		Constructor constructor = event.getDeclaredConstructors()[0];
		assertEquals(constructor.getParameterTypes()[0], PircBotX.class, TestUtils.wrapClass(event, "First parameter of constructor isn't of PircBotX type"));
	}

	@Test(description = "Verify number of class fields and number of constructor params match", dependsOnMethods = "singleConstructorTest", dataProvider = "getEvents", dataProviderClass = TestUtils.class)
	public void constructorParamToFieldTest(Class<?> event) {
		Constructor constructor = event.getDeclaredConstructors()[0];
		//(subtract one parameter to account for bot)
		assertEquals(constructor.getParameterTypes().length - 1, event.getDeclaredFields().length, TestUtils.wrapClass(event, "Number of constructor parameters don't match number of fields"));
	}
}
