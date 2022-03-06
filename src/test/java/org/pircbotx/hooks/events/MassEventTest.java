/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks.events;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import org.pircbotx.hooks.Event;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 */
public class MassEventTest {
	@Test(dataProvider = "eventObjectDataProvider", dataProviderClass = TestUtils.class, description = "Verify event has a single constructor")
	public void singleConstructorTest(Class<?> event) {
		assertEquals(event.getDeclaredConstructors().length, 1, wrapClass(event, "No constructor or extra constructor found"));
	}

	@Test(dependsOnMethods = "singleConstructorTest",
			dataProvider = "eventObjectDataProvider", dataProviderClass = TestUtils.class, description = "Verify event has a single constructor")
	public void firstConstructorArgBotTest(Class<?> event) {
		Constructor constructor = event.getDeclaredConstructors()[0];
		assertEquals(constructor.getParameterTypes()[0], PircBotX.class, wrapClass(event, "First parameter of constructor isn't of PircBotX type"));
	}

	@Test(dependsOnMethods = "singleConstructorTest",
			dataProvider = "eventObjectDataProvider", dataProviderClass = TestUtils.class, description = "Verify number of class fields and number of constructor params match")
	public void constructorParamToFieldTest(Class<?> event) {
		if (event == WhoisEvent.class) {
			//This uses a builder
			return;
		}

		//Manually count fields to exclude synthetic ones
		int fieldCount = 0;
		for (Field curField : event.getDeclaredFields())
			if (TestUtils.isRealMember(curField))
				fieldCount++;
		
		//Don't really have a concept of events extending eachother yet
		Class parentclass = event.getSuperclass();
		if (parentclass != Event.class) {
			for (Field curField : parentclass.getDeclaredFields())
				if (TestUtils.isRealMember(curField))
					fieldCount++;
		}
		
		Constructor constructor = event.getDeclaredConstructors()[0];
		//(subtract one parameter to account for bot)
		assertEquals(constructor.getParameterTypes().length - 1, fieldCount, wrapClass(event, "Number of constructor parameters don't match number of fields"
				+ SystemUtils.LINE_SEPARATOR + "Constructor params [" + StringUtils.join(constructor.getParameterTypes(), ", ") + "]"));
	}

	public static String wrapClass(Class aClass, String message) {
		return message + " in " + aClass.toString();
	}
}
