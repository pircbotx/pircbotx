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
import org.pircbotx.hooks.Listener;

/**
 * Dispatched when a listener throws an Exception. Will not be dispatched again 
 * if a listener throws an exception while handling this event
 * 
 * @author Leon Blakey <leon.m.blakey at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListenerExceptionEvent extends ExceptionEvent {
	private final Listener listener;
	private final Event sourceEvent;
	
	public ListenerExceptionEvent(PircBotX bot, Exception exception, String message, @NonNull Listener listener, @NonNull Event sourceEvent) {
		super(bot, exception, message);
		this.listener = listener;
		this.sourceEvent = sourceEvent;
	}
}
