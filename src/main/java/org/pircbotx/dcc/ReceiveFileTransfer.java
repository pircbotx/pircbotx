
package org.pircbotx.dcc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static org.pircbotx.DccFileTransfer.BUFFER_SIZE;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * Handle everything related to receiving a file from an IRC user
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
public class ReceiveFileTransfer {
	protected final PircBotX bot;
	protected final User user;
	protected final String filename;
	protected final Socket socket;
	protected final boolean resume;
	protected final long startPos;
	@Getter
	protected long bytesReceived;
	
	public void receiveFile(File destination) throws IOException {
		@Cleanup BufferedInputStream socketInput = new BufferedInputStream(socket.getInputStream());
		@Cleanup BufferedOutputStream socketOutput = new BufferedOutputStream(socket.getOutputStream());
		@Cleanup BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(destination.getCanonicalPath()));

		//Recieve file
		byte[] inBuffer = new byte[BUFFER_SIZE];
		byte[] outBuffer = new byte[4];
		int bytesRead = 0;
		while ((bytesRead = socketInput.read(inBuffer, 0, inBuffer.length)) != -1) {
			fileOutput.write(inBuffer, 0, bytesRead);
			bytesReceived += bytesRead;
			//Send back an acknowledgement of how many bytes we have got so far.
			//TODO: What does this actually do?
			outBuffer[0] = (byte) ((bytesReceived >> 24) & 0xff);
			outBuffer[1] = (byte) ((bytesReceived >> 16) & 0xff);
			outBuffer[2] = (byte) ((bytesReceived >> 8) & 0xff);
			//TODO: Why does netbeans say this does nothing?
			outBuffer[3] = (byte) ((bytesReceived >> 0) & 0xff);
			socketOutput.write(outBuffer);
			socketOutput.flush();
		}
		
		//Finished recieving file
		fileOutput.flush();
	}
}
