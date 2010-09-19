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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.helpers.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.pircbotx.hooks.Voice;
import org.pircbotx.hooks.helpers.MetaListenerInterface;
import org.pircbotx.hooks.helpers.MetaSimpleListenerInterface;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class MetaListenerTest {
	/**
	 * Makes sure meta interfaces implment all the hooks
	 * @throws Exception
	 */
	@Test
	public void interfaceImplmentTest() throws Exception {
		String sep = System.getProperty("file.separator");
		Class testClazz = Voice.class;

		//Since dumping path in getResources() fails, extract path from root
		Enumeration<URL> rootResources = testClazz.getClassLoader().getResources("");
		assertNotNull(rootResources, "Voice class resources are null");
		assertEquals(rootResources.hasMoreElements(), true, "No voice class resources");
		String root = new File(rootResources.nextElement().getFile()).getParent();

		//Get root directory as a file
		File hookDir = new File(root + sep + "classes" + sep + (Voice.class.getPackage().getName().replace(".", sep)) + sep);
		assertNotNull(hookDir, "Fetched Hook directory is null");
		assertTrue(hookDir.exists(), "Hook directory doesn't exist");
		assertTrue(hookDir.isDirectory(), "Hook directory isn't a directory");

		//Get all classes
		List<String> clazzes = new ArrayList();
		assertTrue(hookDir.listFiles().length > 0, "Hook directory is empty");
		for (File clazzFile : hookDir.listFiles())
			//If its not a file or .java or its a subclass, ignore
			if (clazzFile.isFile() && clazzFile.getName().contains("class") && !clazzFile.getName().contains("$"))
				clazzes.add(clazzFile.getName().split("\\.")[0]);

		subtract(MetaSimpleListenerInterface.class, clazzes);
		subtract(MetaListenerInterface.class, clazzes);

		System.out.println("Success: Meta Interfaces implment all hooks");
	}

	/**
	 * Subtract Interfaces from class list
	 * @param testClazz
	 * @param testClasses
	 */
	protected void subtract(Class testClazz, List<String> testClasses) {
		List<String> clazzesCopy = new ArrayList(testClasses);
		for (Class curClazz : testClazz.getInterfaces()) {
			assertNotNull(curClazz.getEnclosingClass(), curClazz + " is not a sub class");
			clazzesCopy.remove(curClazz.getEnclosingClass().getSimpleName());
		}
		String leftOver = "";
		for (String leftOverClazz : clazzesCopy)
			leftOver += leftOverClazz;
		assertEquals(clazzesCopy.size(), 0, testClazz.getSimpleName() + " does not implment " + leftOver);
	}
}
