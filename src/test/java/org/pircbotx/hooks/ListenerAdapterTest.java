/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.hooks.events.VoiceEvent;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ListenerAdapterTest {
	/**
	 * Makes sure adapter uses all events
	 * @throws Exception
	 */
	@Test
	public void eventImplementTest() throws Exception {
		String sep = System.getProperty("file.separator");
		Class testClazz = VoiceEvent.class;

		//Since dumping path in getResources() fails, extract path from root
		Enumeration<URL> rootResources = testClazz.getClassLoader().getResources("");
		assertNotNull(rootResources, "Voice class resources are null");
		assertEquals(rootResources.hasMoreElements(), true, "No voice class resources");
		File root = new File(rootResources.nextElement().getFile().replace("%20", " ")).getParentFile();
		assertTrue(root.exists(), "Root dir (" + root + ") doesn't exist");

		//Get root directory as a file
		File hookDir = new File(root, sep + "classes" + sep + (testClazz.getPackage().getName().replace(".", sep)) + sep);
		assertNotNull(hookDir, "Fetched Event directory is null");
		assertTrue(hookDir.exists(), "Event directory doesn't exist");
		assertTrue(hookDir.isDirectory(), "Event directory isn't a directory");

		//Get all file names
		List<String> classFiles = new ArrayList();
		assertTrue(hookDir.listFiles().length > 0, "Event directory is empty");
		for (File clazzFile : hookDir.listFiles())
			//If its a directory, its not a .class, or its a subclass, ignore
			if (clazzFile.isFile() && clazzFile.getName().contains("class") && !clazzFile.getName().contains("$"))
				classFiles.add(clazzFile.getName().split("\\.")[0]);

		//Get all events ListenerAdapter uses
		List<String> classMethods = new ArrayList();
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods()) {
			assertEquals(curMethod.getParameterTypes().length, 1, "More than one parameter in method " + curMethod);
			classMethods.add(curMethod.getParameterTypes()[0].getSimpleName());
		}

		//Subtract the differences
		classFiles.removeAll(classMethods);
		String leftOver = StringUtils.join(classFiles.toArray(), ", ");
		assertEquals(classFiles.size(), 0, "ListenerAdapter does not implement " + leftOver);

		System.out.println("Success: Meta Interface implment all hooks");
	}
}
