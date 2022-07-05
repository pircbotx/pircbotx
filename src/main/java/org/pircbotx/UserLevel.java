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
package org.pircbotx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enum to represent the possible IRC levels a user can have.
 */
@RequiredArgsConstructor
public enum UserLevel {
	VOICE("+"), HALFOP("%"), OP("@"),SUPEROP("&"), OWNER("~");
	
	@Getter
	private final String symbol;
	
	/**
	 * Combines all UserLevel's symbols into a String
	 * @return 
	 */
	public static String getSymbols() {
		StringBuilder symbols = new StringBuilder();
		for(UserLevel curLevel : values())
			symbols.append(curLevel.getSymbol());
		return symbols.toString();
	}
	
	/**
	 * Searches for UserLevel that uses the given symbol
	 * @param symbol
	 * @return Matching UserLevel or null if not found
	 */
	public static UserLevel fromSymbol(char symbol) {
		for(UserLevel curLevel : values())
			if(curLevel.getSymbol().contains(String.valueOf(symbol)))
				return curLevel;
		return null;
	}
}
