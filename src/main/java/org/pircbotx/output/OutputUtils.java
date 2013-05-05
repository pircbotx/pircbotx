/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.output;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Leon
 */
public class OutputUtils {
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
