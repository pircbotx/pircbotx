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
package org.pircbotx.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.TestUtils;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapterInterface;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MassListenerTest {
	@Test(dataProvider = "getListeners", dataProviderClass = TestUtils.class)
	public void methodNumTest(Class<?> listenerClass) {
		//Should only have 1 method
		assertEquals(listenerClass.getDeclaredMethods().length, 1, TestUtils.wrapClass(listenerClass, "More than one method found"));
		System.out.println("Success: Method number is good (1) in class " + listenerClass);
	}

	@Test(dataProvider = "getListeners", dataProviderClass = TestUtils.class, dependsOnMethods = {"methodNumTest"})
	public void methodNamingTest(Class<?> listenerClass) {
		//Should follow naming convention
		assertEquals(listenerClass.getDeclaredMethods()[0].getName(), "on" + TestUtils.getRootName(listenerClass));
		System.out.println("Success: Method naming is good in class " + listenerClass);
	}

	@Test(dataProvider = "getListeners", dataProviderClass = TestUtils.class, dependsOnMethods = {"methodNumTest"})
	public void methodParamNumTest(Class<?> listenerClass) {
		//Should only have 1 parameter
		assertEquals(listenerClass.getDeclaredMethods()[0].getParameterTypes().length, 1, TestUtils.wrapClass(listenerClass, "More than one method parameter found"));
		System.out.println("Success: Method parameter number is good in class " + listenerClass);
	}

	@Test(dataProvider = "getListeners", dataProviderClass = TestUtils.class, dependsOnMethods = {"methodNumTest", "methodParamNumTest"})
	public void methodParamClassTest(Class<?> listenerClass) {
		Class<?> paramClass = listenerClass.getDeclaredMethods()[0].getParameterTypes()[0];
		//Should be using the correct class correctly
		String paramName = paramClass.getSimpleName();
		String listenerName = listenerClass.getSimpleName();
		//Strip out suffixes and make sure the root is equal
		assertEquals(paramName.split("Event")[0], listenerName.split("Listener")[0], "Method does not use the correct class");
		//Make sure the event actually is an event (Class.isAssignableFrom fails, so the manual approach is nessesary)
		assertEquals(paramClass.getSuperclass(),Event.class,"Method parameter isn't an event (Event: "+paramClass+")");
		
		System.out.println("Success: Method parameter class is good in class " + listenerClass);
	}
}
