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

package org.pircbotx;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TestUtils {
	public static String wrapClass(Class aClass, String message) {
		return message + " in class " + aClass.toString();
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
}
