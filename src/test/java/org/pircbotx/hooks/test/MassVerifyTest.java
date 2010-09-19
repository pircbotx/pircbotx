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

package org.pircbotx.hooks.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
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
		for (Class<?> curClazz : MetaSimpleListenerInterface.class.getInterfaces())
			try {
				String parentClazz = curClazz.getEnclosingClass().getName();
				ClassLoader cl = getClass().getClassLoader();
				allParents.add(curClazz.getEnclosingClass());
				allSimpleListeners.add(cl.loadClass(parentClazz + "$SimpleListener"));
				allListeners.add(cl.loadClass(parentClazz + "$Listener"));
				allEvents.add(cl.loadClass(parentClazz + "$Event"));
			} catch (Throwable e) {
				throw new ClassFailureException(e, curClazz);
			}
		System.out.println("Success: Loaded all appropiate hook classes");
	}

	@Test
	public void verifyNestedClasses() throws Exception {
		for (Class<?> clazz : allParents)
			try {
				assertEquals(clazz.getDeclaredClasses().length, 3);
			} catch (Throwable e) {
				throw new ClassFailureException(e, clazz);
			}
		System.out.println("Success: Hooks only contain 3 classes");
	}

	@Test
	public void verifyListenerMethod() throws Exception {
		for (Class<?> clazz : allListeners)
			try {
				//Is there only one method?
				Method[] allMethods = clazz.getDeclaredMethods();
				assertEquals(allMethods.length, 1);

				Method eventMethod = allMethods[0];

				//Does that method follow naming convention?
				assertEquals(eventMethod.getName(), "on" + allParents.get(allListeners.indexOf(clazz)).getSimpleName());

				//Is there only one parameter?
				Class<?>[] parameters = eventMethod.getParameterTypes();
				assertEquals(parameters.length, 1);

				//Is that parameter the Event?
				assertEquals(parameters[0], allEvents.get(allListeners.indexOf(clazz)));
			} catch (Throwable e) {
				throw new ClassFailureException(e, clazz);
			}
		System.out.println("Success: Listeners' method is good");
	}

	@Test
	public void verifySimpleListenerMethod() throws Exception {
		for (Class<?> clazz : allSimpleListeners)
			try {
				//Is there only one method?
				Method[] allMethods = clazz.getDeclaredMethods();
				assertEquals(allMethods.length, 1);

				Method eventMethod = allMethods[0];

				//Does that method follow naming convention?
				assertEquals(eventMethod.getName(), "on" + allParents.get(allSimpleListeners.indexOf(clazz)).getSimpleName());
			} catch (Throwable e) {
				throw new ClassFailureException(e, clazz);
			}
		System.out.println("Success: SimpleListeners' method is good");
	}

	@Test
	public void verifyEvent() throws Exception {
		for (Class<?> clazz : allEvents)
			try {
				//Is there only one constructor?
				Constructor[] constructors = clazz.getDeclaredConstructors();
				assertEquals(constructors.length, 1);

				Constructor constructor = constructors[0];

				//Are the number of fields and constructor parameters equal?
				//(subtract one field to account for timestamp)
				assertEquals(constructor.getParameterTypes().length, clazz.getDeclaredFields().length - 1);
			} catch (Throwable e) {
				throw new ClassFailureException(e, clazz);
			}
		System.out.println("Success: Event classes is good");
	}

	@Test(dependsOnMethods = {"verifySimpleListenerMethod", "verifyEvent"})
	public void verifySimpleListenerToEvent() throws Exception {
		for (Class<?> clazz : allSimpleListeners)
			try {
				Method eventMethod = clazz.getDeclaredMethods()[0];
				Constructor constr = allEvents.get(allSimpleListeners.indexOf(clazz)).getConstructors()[0];

				//While we can't get parameter names, we can check if the param classes are in order
				Class[] methodParams = eventMethod.getParameterTypes();
				Class[] constrParams = constr.getParameterTypes();
				assertEquals(methodParams.length, constrParams.length);
				for (int i = 0; i < methodParams.length; i++)
					assertEquals(methodParams[i], constrParams[i]);
			} catch (Throwable e) {
				throw new ClassFailureException(e, clazz);
			}
		System.out.println("Success: SimpleListener and Event constructor match");
	}

	public class ClassFailureException extends Exception {
		public ClassFailureException(Throwable e, Class clazz) {
			this(e, clazz, "");
		}

		public ClassFailureException(Throwable e, Class clazz, String extra) {
			super("Failed on class " + clazz.getName() + " " + extra, e);
		}
	}
}
