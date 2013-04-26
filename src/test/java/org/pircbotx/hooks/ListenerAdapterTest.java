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
package org.pircbotx.hooks;

import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import java.lang.reflect.Method;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.types.GenericEvent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ListenerAdapterTest {
	protected PircBotX bot;

	@BeforeMethod
	public void setUp() {
		bot = new PircBotX(new Configuration.Builder()
				.setListenerManager(new GenericListenerManager())
				.buildConfiguration());
	}

	@DataProvider
	public Object[][] eventDataProvider(Method testMethod) throws IOException {
		System.out.println("Ran eventDataProvider");
		ClassPath classPath = ClassPath.from(getClass().getClassLoader());
		Iterable<ClassPath.ClassInfo> allClasses = Iterables.concat(
				classPath.getTopLevelClasses(VoiceEvent.class.getPackage().getName()),
				classPath.getTopLevelClasses(GenericEvent.class.getPackage().getName()));
		List<Object[]> argumentBuilder = new ArrayList();
		for (ClassPath.ClassInfo curClassInfo : allClasses) {
			Class loadedClass = curClassInfo.load();
			if (GenericEvent.class.isAssignableFrom(loadedClass) && !loadedClass.equals(GenericEvent.class))
				argumentBuilder.add(new Object[]{loadedClass});
		}
		return argumentBuilder.toArray(new Object[0][]);
	}

	/**
	 * Makes sure adapter uses all events
	 * @throws Exception
	 */
	@Test(dataProvider = "eventDataProvider", description = "Verify ListenerAdapter has methods for all events")
	public void eventImplementTest(Class eventClass) throws NoSuchMethodException {
		//Just try to load it. If the method doesn't exist then it throws a NoSuchMethodException
		String eventName = eventClass.getSimpleName();
		assertTrue(StringUtils.endsWith(eventName, "Event"), "Unknown event class " + eventClass);
		String methodName = "on" + StringUtils.removeEnd(StringUtils.capitalize(eventName), "Event");
		ListenerAdapter.class.getDeclaredMethod(methodName, eventClass);
	}

	@Test(dependsOnMethods = "eventImplementTest", dataProvider = "eventDataProvider", description = "Verify all methods in ListenerAdapter throw an exception")
	public void throwsExceptionTest(Class eventClass) throws NoSuchMethodException {
		String methodName = "on" + StringUtils.removeEnd(StringUtils.capitalize(eventClass.getSimpleName()), "Event");
		Method eventMethod = ListenerAdapter.class.getDeclaredMethod(methodName, eventClass);
		Class<?>[] exceptions = eventMethod.getExceptionTypes();
		assertEquals(exceptions.length, 1, "Method " + eventMethod + " in ListenerManager doesn't throw an exception or thows too many");
		assertEquals(exceptions[0], Exception.class, "Method " + eventMethod + " in ListenerManager doesn't throw the right exception");
	}

	@Test(description = "Do an actual test with a sample ListenerAdapter")
	public void usabilityTest() throws Exception {
		TestListenerAdapter listener = new TestListenerAdapter();

		//Test if onMessage got called
		listener.onEvent(new MessageEvent(bot, null, null, null));
		assertTrue(listener.isCalled(), "onMessage wasn't called on MessageEvent");

		//Test if onGenericChannelMode (interface) got called
		listener.setCalled(false);
		listener.onEvent(new MessageEvent(bot, null, null, null));
		assertTrue(listener.isCalled(), "onMessage wasn't called on MessageEvent");
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
	public static Object[][] onEventTestDataProvider() {
		//Map events to methods
		Map<Class<? extends Event>, Set<Method>> eventToMethod = new HashMap();
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			//Filter out methods by basic criteria
			if (curMethod.getName().equals("onEvent") || curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curClass = curMethod.getParameterTypes()[0];
			//Filter out methods that don't have the right param or are already added
			if (curClass.isAssignableFrom(Event.class) || curClass.isInterface()
					|| (eventToMethod.containsKey(curClass) && eventToMethod.get(curClass).contains(curMethod)))
				continue;
			Set methods = new HashSet();
			methods.add(curMethod);
			eventToMethod.put((Class<? extends Event>) curClass, methods);
		}

		//Now that we have all the events, start mapping interfaces
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			//Make sure this is an event method
			if (curMethod.getParameterTypes().length != 1 || curMethod.isSynthetic())
				continue;
			Class<?> curClass = curMethod.getParameterTypes()[0];
			if (!curClass.isInterface() || !GenericEvent.class.isAssignableFrom(curClass))
				continue;
			//Add this interface method to all events that implement it
			for (Class curEvent : eventToMethod.keySet())
				if (curClass.isAssignableFrom(curEvent) && !eventToMethod.get(curEvent).contains(curMethod))
					eventToMethod.get(curEvent).add(curMethod);
		}

		//Build object array that TestNG understands
		Object[][] params = new Object[eventToMethod.size()][];
		int paramsCounter = 0;
		for (Map.Entry<Class<? extends Event>, Set<Method>> curEntry : eventToMethod.entrySet())
			params[paramsCounter++] = new Object[]{curEntry.getKey(), curEntry.getValue()};
		return params;
	}

	@Test(dependsOnMethods = {"eventImplementTest"}, dataProvider = "onEventTestDataProvider",
			description = "Tests onEvent's completness")
	public void onEventTest(Class<? extends Event> eventClass, Set<Method> methodsToCall) throws Exception {
		//Init, using mocks to store method calls
		final Set<Method> calledMethods = new HashSet();
		ListenerAdapter mockListener = mock(ListenerAdapter.class, new Answer() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				calledMethods.add(invocation.getMethod());
				return null;
			}
		});
		doCallRealMethod().when(mockListener).onEvent(any(Event.class));

		//Call the constructor in the Event, trying to give as many default values as possible
		Constructor[] eventConstructors = eventClass.getConstructors();
		assertEquals(eventConstructors.length, 1, "Unexpected number of event constructors in " + eventClass);
		Constructor eventConstructor = eventClass.getConstructors()[0];
		Class[] eventConstructorParamTypes = eventConstructor.getParameterTypes();
		Object[] eventConstructorParams = new Object[eventConstructorParamTypes.length];
		for (int i = 0; i < eventConstructorParams.length; i++)
			if (eventConstructorParamTypes[i] == int.class || eventConstructorParamTypes[i] == long.class)
				eventConstructorParams[i] = 0;
			else if (eventConstructorParamTypes[i] == boolean.class)
				eventConstructorParams[i] = false;
		eventConstructorParams[0] = bot;
		Event eventObject = (Event) eventConstructor.newInstance(eventConstructorParams);

		//Execute onEvent
		mockListener.onEvent(eventObject);

		//Make sure our methods were called
		assertEquals(methodsToCall, calledMethods, "Event " + eventClass + " doesn't call expected methods:" + SystemUtils.LINE_SEPARATOR
				+ "Expected: " + SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(methodsToCall, SystemUtils.LINE_SEPARATOR)
				+ SystemUtils.LINE_SEPARATOR
				+ "Called: " + SystemUtils.LINE_SEPARATOR
				+ StringUtils.join(calledMethods, SystemUtils.LINE_SEPARATOR));
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	protected static class TestListenerAdapter extends ListenerAdapter {
		protected boolean called = false;

		@Override
		public void onGenericMessage(GenericMessageEvent event) throws Exception {
			called = true;
		}

		@Override
		public void onMessage(MessageEvent event) throws Exception {
			called = true;
		}
	}
}
