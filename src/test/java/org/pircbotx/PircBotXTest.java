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
package org.pircbotx;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.NoInjection;
import org.testng.annotations.Test;

import com.google.common.reflect.ClassPath;

/**
 *
 */
public class PircBotXTest {
	private static final Logger log = LoggerFactory.getLogger(PircBotXTest.class);

	@DataProvider
	public Object[][] genericReturnTestDataProvider() throws IOException {
		List<Object[]> result = new LinkedList<>();
		for (ClassPath.ClassInfo curClassInfo : ClassPath.from(PircBotXTest.class.getClassLoader()).getTopLevelClassesRecursive("org.pircbotx")) {
			Class<?> curClass = curClassInfo.load();
			try {
				//If this call doesn't throw an exception then there's a getBot
				Method method = curClass.getDeclaredMethod("getBot");

				result.add(new Object[]{method});
			} catch (NoSuchMethodException e) {
				//Ignore
			}
		}

		return result.toArray(new Object[result.size()][]);
	}

	/**
	 * Issue #261: All getBot methods should return <T extends PircBotX>
	 */
	@Test(dataProvider = "genericReturnTestDataProvider")
	public void genericReturnTest(@NoInjection Method testMethod) throws IOException {
		Type returnType = testMethod.getGenericReturnType();
		assertTrue(returnType instanceof TypeVariable, "Not a generic return "  + testMethod);
		
		TypeVariable returnVariable = (TypeVariable) returnType;
		assertEquals(returnVariable.getBounds().length, 1, "Too many parameters " + testMethod);
		assertEquals(returnVariable.getBounds()[0], PircBotX.class, "Not returning PircBotX " + testMethod);
	}
}
