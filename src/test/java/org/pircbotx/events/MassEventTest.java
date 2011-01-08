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
package org.pircbotx.events;

import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Test()
public class MassEventTest {
	@Test(dataProvider = "getEvents", dataProviderClass = TestUtils.class)
	public void constructorNumTest(Class<?> event) {
		//Is there only one constructor?
		assertEquals(event.getDeclaredConstructors().length, 1, TestUtils.wrapClass(event, "No constructor or extra constructor found"));
		System.out.println("Success: Event constructor is good in class "+event);
	}

	@Test(dataProvider = "getEvents", dataProviderClass = TestUtils.class, dependsOnMethods = {"constructorNumTest"})
	public void constructorFirstParamTest(Class<?> event) {
		//Is the first parameter a bot refrence?
		assertEquals(event.getDeclaredConstructors()[0].getParameterTypes()[0], PircBotX.class, TestUtils.wrapClass(event, "First parameter of constructor isn't of PircBotX type"));
		System.out.println("Success: First constructor parameter is good in class "+event);
	}

	@Test(dataProvider = "getEvents", dataProviderClass = TestUtils.class, dependsOnMethods = {"constructorNumTest", "constructorFirstParamTest"})
	public void constructorToFieldNumTest(Class<?> event) {
		//Are the number of fields and constructor parameters equal?
		//(subtract one parameter to account for bot)
		assertEquals(event.getDeclaredConstructors()[0].getParameterTypes().length - 1, event.getDeclaredFields().length, TestUtils.wrapClass(event, "Number of constructor parameters don't match number of fields"));
		System.out.println("Success: Number of parameters in constructors matches number of fields in class "+event);
	}
}
