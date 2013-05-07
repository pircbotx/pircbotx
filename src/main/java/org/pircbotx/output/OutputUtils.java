/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.output;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Leon
 */
public class OutputUtils {
	private OutputUtils() {
	}

	/**
	 * Internal utility method to init OutputRaw from PircBotX class, which is not
	 * in this package. Needed so {@link OutputRaw#init(java.net.Socket) } can stay
	 * protected
	 * @param outputRaw
	 * @param socket
	 * @throws IOException 
	 */
	public static void initOutputRaw(OutputRaw outputRaw, Socket socket) throws IOException {
		outputRaw.init(socket);
	}
}
