/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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
package org.pircbotx.hooks.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when an Exception is encountered
 * 
 * @author Leon Blakey <leon.m.blakey at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionEvent extends Event {
	private final Exception exception;
	private final String message;

	public ExceptionEvent(PircBotX bot, @NonNull Exception exception, @NonNull String message) {
		super(bot);
		this.exception = exception;
		this.message = message;
	}

	@Override
	public void respond(String response) {
		throw new UnsupportedOperationException("Cannot respond to exception");
	}
}
