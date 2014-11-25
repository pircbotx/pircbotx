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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.events;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 *
 * @author Leon
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectFailedEvent extends Event {
	private final Exception lastConnectException;
	
	public ConnectFailedEvent(PircBotX bot, Exception lastConnectException) {
		super(bot);
		this.lastConnectException = lastConnectException;
	}
	
	@Override
	@Deprecated
	public void respond(@Nullable String response) {
		throw new UnsupportedOperationException("Attepting to respond to a disconnected server");
	}
}
