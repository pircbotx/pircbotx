/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.cap;

import com.google.common.collect.ImmutableList;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.CAPException;

/**
 * Generic CAP handler. Relevant handle methods are called when a CAP line is
 * received. Connecting is not considered finished until a method returns true
 */
public interface CapHandler {
	public boolean handleLS(PircBotX bot, ImmutableList<String> capabilities) throws CAPException;

	public boolean handleACK(PircBotX bot, ImmutableList<String> capabilities) throws CAPException;

	public boolean handleNAK(PircBotX bot, ImmutableList<String> capabilities) throws CAPException;

	public boolean handleUnknown(PircBotX bot, String rawLine) throws CAPException;
}
