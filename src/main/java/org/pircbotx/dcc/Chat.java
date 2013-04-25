package org.pircbotx.dcc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import org.pircbotx.User;

/**
 *
 * @author Leon
 */
public interface Chat {
	public User getUser();

	public Socket getSocket();

	public BufferedReader getBufferedReader();

	public BufferedWriter getBufferedWriter();
}
