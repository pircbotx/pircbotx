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
package org.pircbotx.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.pircbotx.PircBotX;

/**
 * Helpful server for replaying a raw log to the bot. 
 * <p>
 * <b>NOTE:</b> In order to avoid write exceptions in the client you must override 
 * {@link PircBotX#sendRawLine(java.lang.String) } to simply print the output 
 * instead of sending it to this server!
 * <code>
 * PircBotX bot = new PircBotX() {
 *    @Override
 *    public void sendRawLine(String line) {
 *       System.out.println(">>>" + line);
 *    }
 * };
 * </code>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class ReplayServer {
	public static void main(String[] args) throws Exception {
		//Make sure the user specified a file
		if (args.length != 1 || args[0].trim().length() == 0) {
			System.out.println("Usage: org.pircbotx.impl.ReplayServer [log]");
			System.exit(5);
		}

		//Make sure the file exists
		File file = new File(args[0].trim());
		if (!file.exists()) {
			System.out.println("Error: File " + args[0] + " does not exist");
			System.exit(6);
		}

		//Wait
		System.out.println("*** Waiting for a client to connect");
		ServerSocket server = new ServerSocket(6667);
		Socket client = server.accept();
		System.out.println("*** Client connected");

		//Open up the streams
		BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		BufferedReader fileStream = new BufferedReader(new FileReader(file));

		//Wait till we get the NICK and USER lines so were not racing the bot
		boolean nickGood = false;
		boolean userGood = false;
		String line;
		while ((line = input.readLine()) != null) {
			System.out.println("<<<" + line);
			if (line.startsWith("USER"))
				userGood = true;
			else if (line.startsWith("NICK"))
				nickGood = true;
			if (nickGood && userGood)
				break;
		}

		if (!nickGood || !userGood)
			throw new RuntimeException("Premature end while waiting for USER and NICK lines");

		System.out.println("*** Replaying file");

		//Replay
		while ((line = fileStream.readLine()) != null) {
			if (input.ready())
				System.out.println("<<<" + input.readLine());
			output.write(line + "\r\n");
			output.flush();
			System.out.println(">>>" + line);
		}
		
		//Done, clear input from the client so we don't cause a recv error
		while(input.ready())
			input.readLine();

		//Close
		System.out.println("*** Done replaying file, closing");
		input.close();
		output.close();
		fileStream.close();
		client.close();
		server.close();
	}
}
