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
package org.pircbotx.hooks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pircbotx.PircBotX;
import org.pircbotx.TestUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 *
 */
public class ListenerAdapterTest {
	private static final Logger log = LoggerFactory.getLogger(ListenerAdapterTest.class);	
	protected PircBotX bot;

	@BeforeMethod
	public void setUp() {
		bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.buildConfiguration());
	}

	/**
	 * Makes sure adapter uses all events
	 *
	 * @throws Exception
	 */
	@Test(dataProviderClass = TestUtils.class, dataProvider = "eventAllDataProvider", description = "Verify ListenerAdapter has methods for all events")
	public void eventImplementTest(Class eventClass) throws NoSuchMethodException {
		//Just try to load it. If the method doesn't exist then it throws a NoSuchMethodException
		String eventName = eventClass.getSimpleName();
		assertTrue(StringUtils.endsWith(eventName, "Event"), "Unknown event class " + eventClass);
		String methodName = "on" + StringUtils.removeEnd(StringUtils.capitalize(eventName), "Event");
		ListenerAdapter.class.getDeclaredMethod(methodName, eventClass);
	}

	@Test(dependsOnMethods = "eventImplementTest",
			dataProviderClass = TestUtils.class, dataProvider = "eventAllDataProvider", description = "Verify all methods in ListenerAdapter throw an exception")
	public void throwsExceptionTest(Class eventClass) throws NoSuchMethodException {
		String methodName = "on" + StringUtils.removeEnd(StringUtils.capitalize(eventClass.getSimpleName()), "Event");
		Method eventMethod = ListenerAdapter.class.getDeclaredMethod(methodName, eventClass);
		Class<?>[] exceptions = eventMethod.getExceptionTypes();
		assertEquals(exceptions.length, 1, "Method " + eventMethod + " in ListenerManager doesn't throw an exception or thows too many");
		assertEquals(exceptions[0], Exception.class, "Method " + eventMethod + " in ListenerManager doesn't throw the right exception");
	}

	@Test(description = "Do an actual test with a sample ListenerAdapter")
	public void usabilityTest() throws Exception {
		final MutableBoolean onMessageCalled = new MutableBoolean(false);
		final MutableBoolean onGenericMessageCalled = new MutableBoolean(false);
		ListenerAdapter testListener = new ListenerAdapter() {
			@Override
			public void onMessage(MessageEvent event) throws Exception {
				onMessageCalled.setValue(true);
			}

			@Override
			public void onGenericMessage(GenericMessageEvent event) throws Exception {
				onGenericMessageCalled.setValue(true);
			}
		};

		testListener.onEvent(mock(MessageEvent.class));
		assertTrue(onMessageCalled.isTrue(), "onMessage wasn't called on MessageEvent");
		assertTrue(onGenericMessageCalled.isTrue(), "onGenericMessage wasn't called on MessageEvent");
	}

	@Test(description = "Test with an unknown Event to make sure it doesn't throw an exception")
	public void unknownEventTest() throws Exception {
		Event customEvent = new Event(bot) {
			@Override
			public void respond(String response) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
		ListenerAdapter customListener = new ListenerAdapter() {
		};

		customListener.onEvent(customEvent);
	}

	@DataProvider
	@SuppressWarnings("unchecked")
	public static Object[][] onEventTestDataProvider() {
		//Map events to methods
		Map<Class<? extends Event>, Set<Method>> eventToMethod = new HashMap<>();
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			//Filter out methods by basic criteria
			if (curMethod.getName().equals("onEvent") || curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curClass = curMethod.getParameterTypes()[0];
			//Filter out methods that don't have the right param or are already added
			if (curClass.isAssignableFrom(Event.class) || curClass.isInterface()
					|| (eventToMethod.containsKey(curClass) && eventToMethod.get(curClass).contains(curMethod)))
				continue;
			Set<Method> methods = new HashSet<>();
			methods.add(curMethod);
			eventToMethod.put((Class<? extends Event>) curClass, methods);
		}

		//Now that we have all the events, start mapping interfaces
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			//Make sure this is an event method
			if (curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curEventClass = curMethod.getParameterTypes()[0];
			
			//Don't really have a concept of events extending eachother yet
			Class superClass = curEventClass.getSuperclass();
			if(!curEventClass.isInterface() && curEventClass != Event.class && superClass != Event.class) {
				eventToMethod.get(curEventClass)
						.add(eventToMethod
								.get(superClass)
								.iterator()
								.next());
				continue;
			}
			
			if (!curEventClass.isInterface() || !GenericEvent.class.isAssignableFrom(curEventClass))
				continue;
			//Add this interface method to all events that implement it
			for (Class curEvent : eventToMethod.keySet())
				if (curEventClass.isAssignableFrom(curEvent) && !eventToMethod.get(curEvent).contains(curMethod))
					eventToMethod.get(curEvent).add(curMethod);
		}

		//Build object array that TestNG understands
		Object[][] params = new Object[eventToMethod.size()][];
		int paramsCounter = 0;
		for (Map.Entry<Class<? extends Event>, Set<Method>> curEntry : eventToMethod.entrySet())
			params[paramsCounter++] = new Object[]{curEntry.getKey(), curEntry.getValue()};
		return params;
	}

	@Test(dataProvider = "onEventTestDataProvider", description = "Tests onEvent's completness")
	public void onEventTest(Class<? extends Event> eventClass, Set<Method> methodsToCall) throws Exception {
		//Init, using mocks to store method calls
		final Set<Method> calledMethods = new HashSet<>();
		ListenerAdapter mockListener = mock(ListenerAdapter.class, new Answer() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				calledMethods.add(invocation.getMethod());
				return null;
			}
		});
		doCallRealMethod().when(mockListener).onEvent(any(Event.class));

		//Execute onEvent
		Event eventObject = mock(eventClass);
		mockListener.onEvent(eventObject);

		//Make sure our methods were called
		assertEquals(methodsToCall, calledMethods, "Event " + eventClass + " doesn't call expected methods:" + SystemUtils.LINE_SEPARATOR
				+ "Expected: " + SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(methodsToCall, SystemUtils.LINE_SEPARATOR)
				+ SystemUtils.LINE_SEPARATOR
				+ "Called: " + SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(calledMethods, SystemUtils.LINE_SEPARATOR));
	}
}
