
package org.pircbotx.dcc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import static org.pircbotx.DccFileTransfer.BUFFER_SIZE;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * Sends a file to a user
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class SendFileTransfer {
	protected final PircBotX bot;
	protected final User user;
	protected final String filename;
	protected Socket socket;
	protected long progress;
	
	public void sendFile(File source) throws IOException {
		// Rename the filename so it has no whitespace in it when we send it
		String safeFilename = source.getName().replace(' ', '_').trim();
		safeFilename = safeFilename.replace('\t', '_');
		
		//Make a server that the user can connect to
		ServerSocket server = bot.getDccManager().createServerSocket();
		String ipNum = org.pircbotx.DccManager.addressToInteger(server.getInetAddress());

		// Send the message to the user, telling them where to connect to in order to get the file.
		bot.sendCTCPCommand(user.getNick(), "DCC SEND " + safeFilename + " " + ipNum + " " + server.getLocalPort() + " " + source.length());

		// The client may now connect to us and download the file.
		socket = server.accept();
		socket.setSoTimeout(30000);

		// Might as well close the server socket now; it's finished with.
		server.close();

		@Cleanup BufferedOutputStream socketOutput = new BufferedOutputStream(socket.getOutputStream());
		@Cleanup BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
		@Cleanup BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(source));

		// Check for resuming.
		if (progress > 0) {
			long bytesSkipped = 0;
			while (bytesSkipped < progress)
				bytesSkipped += fileInput.skip(progress - bytesSkipped);
		}

		byte[] outBuffer = new byte[BUFFER_SIZE];
		byte[] inBuffer = new byte[4];
		int bytesRead = 0;
		while ((bytesRead = fileInput.read(outBuffer, 0, outBuffer.length)) != -1) {
			socketOutput.write(outBuffer, 0, bytesRead);
			socketOutput.flush();
			socketInput.read(inBuffer, 0, inBuffer.length);
			progress += bytesRead;
		}
	}
}
