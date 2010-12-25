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
import org.pircbotx.TestUtils;
import org.pircbotx.hooks.helpers.ListenerAdapterInterface;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MassListenerTest {
	private final List<Class<?>> listenerClasses = new ArrayList(Arrays.asList(ListenerAdapterInterface.class.getInterfaces()));

	@Test
	public void methodNumTest() {
		for (Class<?> listenerClass : listenerClasses)
			//Should only have 1 method
			assertEquals(listenerClass.getDeclaredMethods().length, 1, TestUtils.wrapClass(listenerClass, "More than one method found"));
	}

	@Test(dependsOnMethods={"methodNumTest"})
	public void methodNamingTest() {
		for (Class<?> listenerClass : listenerClasses)
			//Should follow naming convention
			assertEquals(listenerClass.getDeclaredMethods()[0].getName(), "on" + TestUtils.getRootName(listenerClass));

	}

	@Test(dependsOnMethods={"methodNumTest"})
	public void methodParamNumTest() {
		for (Class<?> listenerClass : listenerClasses)
			//Should only have 1 parameter
			assertEquals(listenerClass.getDeclaredMethods()[0].getParameterTypes().length, 1, TestUtils.wrapClass(listenerClass, "More than one method parameter found"));
	}

	@Test(dependsOnMethods={"methodNumTest","methodParamNumTest"})
	public void methodParamClassTest() {
		for (Class<?> listenerClass : listenerClasses) {
			//Should be using the correct class correctly
			String paramName = listenerClass.getDeclaredMethods()[0].getParameterTypes()[0].getSimpleName();
			String listenerName = listenerClass.getSimpleName();
			//Strip out suffixes and make sure the root is equal
			assertEquals(paramName.split("Event")[0], listenerName.split("Listener")[0], "Method does not use the correct class");
		}
	}
}
