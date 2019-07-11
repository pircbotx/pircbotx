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
package org.pircbotx.dcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.pircbotx.Configuration;
import org.pircbotx.User;

/**
 * A DCC File Transfer initiated by the bot
 *
 * @author Leon Blakey
 */
public class SendFileTransfer extends FileTransfer {
	public SendFileTransfer(Configuration configuration, Socket socket, User user, File file, long startPosition) {
		super(configuration, socket, user, file, startPosition, file.length());
	}

	@Override
	protected void transferFile() throws IOException {
		try (FileInputStream inputStream = new FileInputStream(file);) {
			FileChannel inChannel = inputStream.getChannel();
			SocketChannel outChannel = socket.getChannel();
			// Windows optimized buffer size.  It seems to help
			// https://stackoverflow.com/questions/7379469/filechannel-transferto-for-large-file-in-windows/20916464
			long bufferSize = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = startPosition;
			while (position < size) {
				position += inChannel.transferTo(position, bufferSize, outChannel);
			}
		}
	}
}
