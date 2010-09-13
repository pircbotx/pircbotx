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

package org.pircbotx.impl;

import java.lang.reflect.Field;
import org.pircbotx.hooks.Action;

/**
 *
 * @author Owner
 */
public class Sandbox {
	public static void main(String[] args) {
		Class<?> listener = Action.Listener.class;
		Class<?> event = Action.Event.class;

		for(Class<?> clazz : Action.class.getDeclaredClasses()) {
			System.out.println("Field: "+clazz);
		}
	}
}
