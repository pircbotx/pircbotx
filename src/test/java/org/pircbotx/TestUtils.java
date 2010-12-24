

package org.pircbotx;

import java.lang.reflect.Method;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TestUtils {
	public static String wrapClass(Class aClass, String message) {
		return message + " in class " + aClass.toString();
	}
}
