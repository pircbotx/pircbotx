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

import java.lang.reflect.Method;
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
	public void methodCheck() {
		for (Class<?> listenerClass : listenerClasses) {
			Method[] methods = listenerClass.getDeclaredMethods();

			//Should only have 1 method
			assertEquals(methods.length, 1, TestUtils.wrapClass(listenerClass, "More than one method found"));

			Method method = methods[0];
			Class<?>[] params = method.getParameterTypes();

			//Should follow naming convention
			assertEquals(method.getName(), "on" + TestUtils.getRootName(listenerClass));

			//Should only have 1 parameter
			assertEquals(params.length, 1, TestUtils.wrapClass(listenerClass, "More than one method parameter found"));

			//Should be using the correct class correctly
			String paramName = params[0].getSimpleName();
			String listenerName = listenerClass.getSimpleName();
			//Strip out suffixes and make sure the root is equal
			assertEquals(paramName.split("Event")[0], listenerName.split("Listener")[0], "Method does not use the correct class");
		}
	}

	@Test
	public void parameterCheck() {
		//Get all the Listeners that are part of ListenerAdapterInterface
		for (Class<?> listenerClass : listenerClasses) {
			Method method = listenerClass.getMethods()[0];
			Class<?>[] params = method.getParameterTypes();

			//Should only have 1 parameter
			assertEquals(params.length, 1, TestUtils.wrapClass(listenerClass, "More than one method parameter found"));

			//Should be using the correct class correctly
			String paramName = params[0].getSimpleName();
			String listenerName = listenerClass.getSimpleName();
			//Strip out suffixes and make sure the root is equal
			assertEquals(paramName.split("Event")[0], listenerName.split("Listener")[0], "Method does not use the correct class");
		}
	}
}
