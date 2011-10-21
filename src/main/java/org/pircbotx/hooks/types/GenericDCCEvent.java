/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.hooks.types;

import org.pircbotx.PircBotX;

/**
 * Any event dealing with DCC. This includes chat and file transfers. This is
 * more of a marker event then anything else as DCC events don't have much
 * in common
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericDCCEvent<T extends PircBotX> extends GenericEvent<T> {
	
}
