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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.hooks.types.GenericEvent;
import org.testng.annotations.DataProvider;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TestUtils {
	@DataProvider
	public static Object[][] eventObjectDataProvider() throws IOException {
		return generateEventArguments(true, false);
	}
	
	@DataProvider
	public static Object[][] eventGenericDataProvider() throws IOException {
		return generateEventArguments(false, true);
	}
	
	@DataProvider
	public static Object[][] eventAllDataProvider() throws IOException {
		return generateEventArguments(true, true);
	}
	
	protected static Object[][] generateEventArguments(boolean includeEvents, boolean includeGeneric) throws IOException {
		ClassPath classPath = ClassPath.from(TestUtils.class.getClassLoader());
		ImmutableSet.Builder<ClassPath.ClassInfo> classesBuilder = ImmutableSet.builder();
		if(includeEvents)
			classesBuilder.addAll(classPath.getTopLevelClasses(VoiceEvent.class.getPackage().getName()));
		if(includeGeneric)
			classPath.getTopLevelClasses(GenericEvent.class.getPackage().getName());
		List<Object[]> argumentBuilder = new ArrayList();
		for (ClassPath.ClassInfo curClassInfo : classesBuilder.build()) {
			Class loadedClass = curClassInfo.load();
			if (GenericEvent.class.isAssignableFrom(loadedClass) && !loadedClass.equals(GenericEvent.class))
				argumentBuilder.add(new Object[]{loadedClass});
		}
		return argumentBuilder.toArray(new Object[0][]);
	}

	public static String getRootName(Class aClass) {
		String name = aClass.getSimpleName();

		if (StringUtils.endsWith(name, "Event"))
			return name.split("Event")[0];
		else if (StringUtils.endsWith(name, "Listener"))
			return name.split("Listener")[0];

		//Can't get anything, error out
		throw new IllegalArgumentException("Cannot get root of class name " + aClass.toString());
	}
	
	public static Configuration.Builder generateConfigurationBuilder() {
		return new Configuration.Builder()
				.setCapEnabled(true)
				.setServerHostname("example.com")
				.setListenerManager(new GenericListenerManager())
				.setName("PircBotXBot")
				.setMessageDelay(0);
	}
}
