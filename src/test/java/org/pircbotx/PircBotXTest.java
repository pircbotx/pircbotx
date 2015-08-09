/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.NoInjection;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <leon.m.blakey at gmail.com>
 */
public class PircBotXTest {
	private static final Logger log = LoggerFactory.getLogger(PircBotXTest.class);

	@DataProvider
	public Object[][] genericReturnTestDataProvider() throws IOException {
		List<Object[]> result = Lists.newLinkedList();
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
