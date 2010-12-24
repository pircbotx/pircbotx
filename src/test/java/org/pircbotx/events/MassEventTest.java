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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import static org.testng.Assert.*;
import org.pircbotx.hooks.helpers.ListenerAdapterInterface;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Test()
public class MassEventTest {
	List<Class<?>> eventClasses = new ArrayList();

	@BeforeClass
	public void setup() {
		for (Class listenerClass : ListenerAdapterInterface.class.getInterfaces())
			//Add listener parameter method to the list
			eventClasses.add(listenerClass.getDeclaredMethods()[0].getParameterTypes()[0]);
	}

	public void constructorTest() {
		for (Class<?> clazz : eventClasses) {
			//Is there only one constructor?
			Constructor[] constructors = clazz.getDeclaredConstructors();
			assertEquals(constructors.length, 1, TestUtils.wrapClass(clazz, "No constructor or extra constructor found"));

			Constructor constructor = constructors[0];

			//Is the first parameter a bot refrence?
			Class[] constParams = constructor.getParameterTypes();
			assertEquals(constParams[0], PircBotX.class, TestUtils.wrapClass(clazz, "First parameter of constructor isn't of PircBotX type"));

			//Are the number of fields and constructor parameters equal?
			//(subtract one parameter to account for bot)
			assertEquals(constParams.length - 1, clazz.getDeclaredFields().length, TestUtils.wrapClass(clazz, "Number of constructor parameters don't match number of fields"));
		}
		System.out.println("Success: Event constructor is good");
	}
	
	public void annotationTest() {
		for(Class<?> clazz : eventClasses) {
			//Make sure @Data and @EqualsAndHashCode are present
			assertNotNull(clazz.getAnnotation(Data.class));
			assertNotNull(clazz.getAnnotation(EqualsAndHashCode.class));
		}
	}
}
