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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class HookUtils {
	private static final List<Class<? extends Event>> allEvents = new ArrayList();

	static {
		//Add all Event classes from ListenerAdapter
		for (Method curMethod : ListenerAdapter.class.getDeclaredMethods())
			if (curMethod.getParameterTypes().length == 1) {
				Class parameter = curMethod.getParameterTypes()[0];
				if (!parameter.isAssignableFrom(Event.class) && parameter != Event.class)
					allEvents.add(parameter);
			}
	}

	/**
	 * @return the allEvents
	 */
	public static List<Class<? extends Event>> getAllEvents() {
		return allEvents;
	}
}
