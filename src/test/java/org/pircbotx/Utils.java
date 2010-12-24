

package org.pircbotx;

import java.lang.reflect.Method;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class Utils {
	public static String genClass(String prefix, Class clazz) {
		if(clazz.getEnclosingClass() != null)
			clazz = clazz.getEnclosingClass();
		return prefix + " " + clazz.getSimpleName();
	}

	public static String genMethod(Method method, String prefix, Class clazz) {
		return "Method " + method.toString() + " of " + prefix + " " + clazz.getEnclosingClass().getSimpleName();
	}
}
