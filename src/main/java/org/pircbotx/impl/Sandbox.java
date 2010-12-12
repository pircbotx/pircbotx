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

import java.lang.reflect.Constructor;
import lombok.Data;
import org.pircbotx.hooks.Action;

/**
 *
 * @author Owner
 */
public class Sandbox {
	public interface Event {
		
	}
	
	@Data
	public class Action implements Event {
		public final String message;
		public final String action;
	}
	
	public interface Listener<T extends Event> {
		public void onEvent(T event);
	}
	
	public class someListener implements Listener<Action> {

		public void onEvent(Action event) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
		
	}
	
}
