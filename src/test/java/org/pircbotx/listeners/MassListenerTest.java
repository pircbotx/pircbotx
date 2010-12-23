package org.pircbotx.listeners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pircbotx.hooks.helpers.ListenerAdapterInterface;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
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
			assertEquals(methods.length, 1, wrapClass(listenerClass, "More than one method found"));

			Method method = methods[0];
			Class<?>[] params = method.getParameterTypes();

			//Should follow naming convention
			assertEquals(method.getName(), "on" + listenerClass.getSimpleName());

			//Should only have 1 parameter
			assertEquals(params.length, 1, wrapClass(listenerClass, "More than one method parameter found"));

			//Should be using the correct class correctly
			String paramName = params.getClass().getSimpleName();
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
			assertEquals(params.length, 1, wrapClass(listenerClass, "More than one method parameter found"));

			//Should be using the correct class correctly
			String paramName = params.getClass().getSimpleName();
			String listenerName = listenerClass.getSimpleName();
			//Strip out suffixes and make sure the root is equal
			assertEquals(paramName.split("Event")[0], listenerName.split("Listener")[0], "Method does not use the correct class");
		}
	}

	protected String wrapClass(Class aClass, String message) {
		return message + " in class " + aClass.toString();
	}
}
