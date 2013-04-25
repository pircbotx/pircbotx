/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import com.google.common.base.Function;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author Leon
 */
public class ConfigurationTest {
	@DataProvider
	public Object[][] fieldNamesDataProvider() throws NoSuchMethodException {
		List<Object[]> params = new ArrayList();
		for (Field curField : Configuration.class.getDeclaredFields())
			if (!curField.isSynthetic()) {
				String prefix = (curField.getType().equals(boolean.class)) ? "is" : "get";
				String name = StringUtils.capitalize(curField.getName());
				params.add(new Object[]{Configuration.class, prefix + name});
				params.add(new Object[]{Configuration.Builder.class, prefix + name});
			}
		return params.toArray(new Object[0][]);
	}

	@Test(dataProvider = "fieldNamesDataProvider", description = "Make sure every getter in builder gets called when creating Configuration")
	public void createCopyingBuilderTest(Class testClass, String getterName) throws Exception {
		//Get the method that is going to be called
		final Method methodToCall = Configuration.Builder.class.getDeclaredMethod(getterName);

		//Trip if method gets called
		final MutableBoolean isMethodCalled = new MutableBoolean(false);
		Configuration.Builder configBuilder = mock(Configuration.Builder.class, withSettings()
				.spiedInstance(new Configuration.Builder())
				.defaultAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getMethod().equals(methodToCall))
					isMethodCalled.setValue(true);
				return invocation.callRealMethod();
			}
		}));

		//Call and test
		testClass.getConstructor(Configuration.Builder.class).newInstance(configBuilder);
		assertTrue(isMethodCalled.getValue(), "Getter " + getterName + " on Builder not called in constructor in class "
				+ testClass);
	}

	@Test
	public void containSameFieldsTest() {
		Function<Field, String> function = new Function<Field, String>() {
			public String apply(Field string) {
				return string.getName();
			}
		};
		Map<String, Field> builderFields = Maps.uniqueIndex(Arrays.asList(Configuration.class.getDeclaredFields()), function);
		System.out.println("Builder:   " + builderFields);
		Map<String, Field> configureFields = Maps.uniqueIndex(Arrays.asList(Configuration.Builder.class.getDeclaredFields()), function);
		System.out.println("Configure: " + configureFields);

		MapDifference<String, Field> fieldsDiff = Maps.difference(builderFields, configureFields);
		assertTrue(fieldsDiff.entriesOnlyOnLeft().isEmpty(), "Configuration has some fields that Builder doesn't: "
				+ fieldsDiff.entriesOnlyOnLeft());
		assertTrue(fieldsDiff.entriesOnlyOnRight().isEmpty(), "Builder has some fields that Configuration doesn't: "
				+ fieldsDiff.entriesOnlyOnRight());
	}
}
