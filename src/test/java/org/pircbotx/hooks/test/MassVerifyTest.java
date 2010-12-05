/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx.hooks.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.helpers.MetaSimpleListenerInterface;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MassVerifyTest {
	public final List<Class<?>> allParents = new ArrayList();
	public final List<Class<?>> allSimpleListeners = new ArrayList();
	public final List<Class<?>> allListeners = new ArrayList();
	public final List<Class<?>> allEvents = new ArrayList();

	/**
	 * Adds all listeners to Lists in order. Also unintentionally verifies that each
	 * class has a SimpleListener, Listener, and Event
	 * @throws Exception
	 */
	@BeforeClass
	public void setUp() throws Exception {
		for (Class<?> curClazz : MetaSimpleListenerInterface.class.getInterfaces()) {
			String parentClazz = curClazz.getEnclosingClass().getName();
			ClassLoader cl = getClass().getClassLoader();
			allParents.add(curClazz.getEnclosingClass());
			allSimpleListeners.add(cl.loadClass(parentClazz + "$SimpleListener"));
			allListeners.add(cl.loadClass(parentClazz + "$Listener"));
			allEvents.add(cl.loadClass(parentClazz + "$Event"));
		}
		System.out.println("Success: Loaded all appropiate hook classes");
	}

	@Test
	public void verifyNestedClasses() throws Exception {
		for (Class<?> clazz : allParents)
			assertEquals(clazz.getDeclaredClasses().length, 3, genClass("Hook", clazz) + " contains an unessesary class, or is missing one");
		System.out.println("Success: Hooks only contain 3 classes");
	}

	@Test
	public void verifyListenerMethod() throws Exception {
		for (Class<?> clazz : allListeners) {
			//Is there only one method?
			Method[] allMethods = clazz.getDeclaredMethods();
			assertEquals(allMethods.length, 1, genClass("Listener", clazz) + " has an unessesary method or is missing one");

			Method eventMethod = allMethods[0];

			//Does that method follow naming convention?
			assertEquals(eventMethod.getName(), "on" + allParents.get(allListeners.indexOf(clazz)).getSimpleName(), genMethod(eventMethod, "Listener", clazz) + " doesn't follow naming convention: on[eventName]");

			//Is there only one parameter?
			Class<?>[] parameters = eventMethod.getParameterTypes();
			assertEquals(parameters.length, 1, genMethod(eventMethod, "Listener", clazz) + " doesn't have a parameter or has unessesary ones");

			//Is that parameter the Event?
			assertEquals(parameters[0], allEvents.get(allListeners.indexOf(clazz)), "Parameter of " + genMethod(eventMethod, "Listener", clazz) + " doesn't have the appropiate class type");
		}
		System.out.println("Success: Listeners' method is good");
	}

	@Test
	public void verifySimpleListenerMethod() throws Exception {
		for (Class<?> clazz : allSimpleListeners) {
			//Is there only one method?
			Method[] allMethods = clazz.getDeclaredMethods();
			assertEquals(allMethods.length, 1, genClass("SimpleListener", clazz) + " has an unessesary method or is missing one");

			Method eventMethod = allMethods[0];

			//Does that method follow naming convention?
			assertEquals(eventMethod.getName(), "on" + allParents.get(allSimpleListeners.indexOf(clazz)).getSimpleName(), genMethod(eventMethod, "SimpleListener", clazz) + " doesn't have a parameter or has unessesary ones");
		}
		System.out.println("Success: SimpleListeners' method is good");
	}

	@Test
	public void verifyEvent() throws Exception {
		for (Class<?> clazz : allEvents) {
			//Is there only one constructor?
			Constructor[] constructors = clazz.getDeclaredConstructors();
			assertEquals(constructors.length, 1, genClass("Event", clazz) + " doesn't have a constructor or has an extra one");

			Constructor constructor = constructors[0];

			//Is the first parameter a bot refrence?
			Class[] constParams = constructor.getParameterTypes();
			assertEquals(constParams[0],PircBotX.class,"First parameter in constructor of "+genClass("Event",clazz)+ " isn't of PircBotX type");

			//Are the number of fields and constructor parameters equal?
			//(subtract one parameter to account for bot)
			assertEquals(constParams.length -1, clazz.getDeclaredFields().length, "Number of Contructor paramenters in " + genClass("Event", clazz) + " don't match number of fields");
		}
		System.out.println("Success: Event classes is good");
	}

	@Test(dependsOnMethods = {"verifySimpleListenerMethod", "verifyEvent"})
	public void verifySimpleListenerToEvent() throws Exception {
		for (Class<?> clazz : allSimpleListeners) {
			Method eventMethod = clazz.getDeclaredMethods()[0];
			Constructor constr = allEvents.get(allSimpleListeners.indexOf(clazz)).getConstructors()[0];

			//While we can't get parameter names, we can check if the param classes are in order
			Class[] methodParams = eventMethod.getParameterTypes();
			//(remove first constructor parameter to account for Bot refrence)
			Class[] constrParams = (Class[])ArrayUtils.remove(constr.getParameterTypes(), 0);

			assertEquals(methodParams.length, constrParams.length, "Number of parameters in " + genClass("SimpleListener", clazz) + " don't match number of parameters in Event constructor");

			for (int i = 0; i < methodParams.length; i++)
				assertEquals(methodParams[i], constrParams[i], "Parameter type mismatch of " + genClass("SimpleListener", clazz) + " and Event constructor");

		}
		System.out.println("Success: SimpleListener and Event constructor match");
	}

	protected String genClass(String prefix, Class clazz) {
		if(clazz.getEnclosingClass() != null)
			clazz = clazz.getEnclosingClass();
		return prefix + " " + clazz.getSimpleName();
	}

	protected String genMethod(Method method, String prefix, Class clazz) {
		return "Method " + method.toString() + " of " + prefix + " " + clazz.getEnclosingClass().getSimpleName();
	}
}
