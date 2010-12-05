/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of pircbotx.
 *
 * pircbotx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pircbotx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pircbotx.hooks.helpers.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.hooks.Voice;
import org.pircbotx.hooks.helpers.MetaListener;
import org.pircbotx.hooks.helpers.MetaListenerInterface;
import org.pircbotx.hooks.helpers.MetaSimpleListener;
import org.pircbotx.hooks.helpers.MetaSimpleListenerInterface;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MetaTest {
	/**
	 * Make sure only defining interface methods and nothing else
	 */
	@Test
	public void methodTest() throws Exception {
		methodCheck(MetaListener.class, MetaListenerInterface.class);
		methodCheck(MetaSimpleListener.class, MetaSimpleListenerInterface.class);
	}

	protected void methodCheck(final Class testClazz, final Class metaClazz) throws Exception {
		//Get number of implmented interfaces
		int interfaces = metaClazz.getInterfaces().length;
		int methods = testClazz.getDeclaredMethods().length;
		try {
			assertEquals(interfaces, methods);
		} catch (Throwable e) {
			//Collect diff information
			ArrayList<String> allInterfaces = new ArrayList() {
				{
					for (Class clazz : metaClazz.getInterfaces())
						add(clazz.getMethods()[0].getName());
				}
			};
			ArrayList<String> allMethods = new ArrayList() {
				{
					for (Method curMethods : testClazz.getDeclaredMethods())
						add(curMethods.getName());
				}
			};
			System.out.println("All Interfaces: " + StringUtils.join(allInterfaces, ", "));
			System.out.println("All Methods   : " + StringUtils.join(allMethods, ", "));
			allMethods.removeAll(allInterfaces);
			System.out.println("Method length: " + allMethods.size());

			String leftOver = StringUtils.join(allMethods.toArray(), ", ");
			throw new Exception("Extra method(s) declared in " + testClazz + " that isn't implmented by " + metaClazz + ": " + leftOver, e);
		}
		System.out.println("Success: " + metaClazz + " implments all methods in " + testClazz);
	}

	/**
	 *  Makes sure methods don't throw any exceptions
	 */
	@Test
	public void methodThrowTest() throws Exception {
		methodThrowCheck(MetaSimpleListener.class);
		methodThrowCheck(MetaListener.class);
	}

	/**
	 * Actual code to check that abstract class methods don't throw any exceptions
	 */
	protected void methodThrowCheck(Class testClazz) throws Exception {
		Object testInst = testClazz.newInstance();
		for (Method curMethod : testClazz.getDeclaredMethods()) {
			//Generate appropiate number of nulls for method parameters
			List paramList = new ArrayList();
			for (Class curClazz : curMethod.getParameterTypes())
				if (curClazz == Integer.TYPE)
					paramList.add(0);
				else if (curClazz == Boolean.TYPE)
					paramList.add(false);
				else
					paramList.add(null);

			//See if it throws any checked exceptions
			assertEquals(curMethod.getExceptionTypes().length, 0, "Method " + curMethod + "Cannot throw any checked exceptions");

			//Execute and throw any exceptions upstream
			try {
				curMethod.invoke(testInst, paramList.toArray());
			} catch (Throwable e) {
				throw new Exception("Method " + curMethod + " cannot throw any exceptions", e);
			}

		}
		System.out.println("Success: " + testClazz + " methods don't throw any exceptions");
	}
}
