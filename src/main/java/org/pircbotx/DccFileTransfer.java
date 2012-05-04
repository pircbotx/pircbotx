/**
 * Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.Getter;
import org.pircbotx.exception.DccException;
import org.pircbotx.hooks.events.FileTransferFinishedEvent;

/**
 * This class is used to administer a DCC file transfer. <b>Note:</b> Unlike 
 * PircBot, the methods here do <i>not</i> execute in different threads.
 *
 * @since   PircBot 1.2.0
 * @author  Origionally by:
 *          <a href="http://www.jibble.org/">Paul James Mutton</a> for <a href="http://www.jibble.org/pircbot.php">PircBot</a>
 *          <p>Forked and Maintained by in <a href="http://pircbotx.googlecode.com">PircBotX</a>:
 *          Leon Blakey <lord.quackstar at gmail.com>
 */
public class DccFileTransfer {
    /**
     * The default buffer size to use when sending and receiving files.
     */
    public static final int BUFFER_SIZE = 1024;
    protected PircBotX bot;
    protected DccManager manager;
    @Getter
    protected User user;
    protected String type;
    protected InetAddress address;
    protected int port;
    protected long size;
    protected boolean received;
    protected Socket socket = null;
    protected long progress = 0;
    protected File file = null;
    protected String filename;
    protected int timeout = 0;
    protected boolean incoming;
    protected long packetDelay = 0;
    protected long startTime = 0;
    public Closeable fileStream = null;

    /**
     * Constructor used for receiving files.
     */
    protected DccFileTransfer(PircBotX bot, DccManager manager, User user, String type, String filename, InetAddress address, int port, long size) {
	this.bot = bot;
	this.manager = manager;
	this.user = user;
	this.type = type;
	this.file = new File(filename);
	this.filename = filename;
	this.address = address;
	this.port = port;
	this.size = size;
	received = false;

	incoming = true;
    }

    /**
     * Constructor used for sending files.
     */
    protected DccFileTransfer(PircBotX bot, DccManager manager, File file, User user, int timeout) {
	this.bot = bot;
	this.manager = manager;
	this.user = user;
	this.file = file;
	this.filename = file.getName();
	size = file.length();
	this.timeout = timeout;
	received = true;

	incoming = false;
    }

    /**
     * Receives a DccFileTransfer and writes it to the specified file.
     * Resuming allows a partial download to be continue from the end of
     * the current file contents.
     *
     * @param file The file to write to.
     * @param resume True if you wish to try and resume the download instead
     *               of overwriting an existing file.
     *
     */
    public synchronized void receive(File file, boolean resume) {
	if (received)
	    throw new DccException("File has already been received, can't receive file again");
	received = true;
	this.file = file;
	progress = file.length();

	if (type.equals("SEND") && resume)
	    if (progress == 0)
		//File is empty, must be a new transfer
		doReceive(file, false);
	    else {
		//File has content, someone is attempting to send instead of resuming transfer. Attempt to resume
		bot.sendCTCPCommand(user.getNick(), "DCC RESUME file.ext " + port + " " + progress);
		manager.addAwaitingResume(this);
	    }
	else
	    //User must be resuming transfer
	    doReceive(file, resume);
    }

    /**
     * Receive the file
     */
    protected void doReceive(final File file, final boolean resume) {
	BufferedOutputStream foutput = null;
	Exception exception = null;
	try {
	    // Connect the socket and set a timeout.
	    socket = new Socket(address, port);
	    socket.setSoTimeout(30 * 1000);
	    startTime = System.currentTimeMillis();

	    // No longer possible to resume this transfer once it's underway.
	    manager.removeAwaitingResume(this);

	    BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
	    BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

	    // Following line fixed for jdk 1.1 compatibility.
	    foutput = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), resume));
	    fileStream = foutput;

	    byte[] inBuffer = new byte[BUFFER_SIZE];
	    byte[] outBuffer = new byte[4];
	    int bytesRead = 0;
	    while ((bytesRead = input.read(inBuffer, 0, inBuffer.length)) != -1) {
		foutput.write(inBuffer, 0, bytesRead);
		progress += bytesRead;
		// Send back an acknowledgement of how many bytes we have got so far.
		outBuffer[0] = (byte) ((progress >> 24) & 0xff);
		outBuffer[1] = (byte) ((progress >> 16) & 0xff);
		outBuffer[2] = (byte) ((progress >> 8) & 0xff);
		outBuffer[3] = (byte) ((progress >> 0) & 0xff);
		output.write(outBuffer);
		output.flush();
		delay();
	    }
	    foutput.flush();
	} catch (Exception e) {
	    exception = e;
	} finally {
	    try {
		foutput.close();
		socket.close();
	    } catch (Exception e) {
		//This might be important, but don't change any existing exception
		if (exception == null)
		    exception = e;
	    }
	}
	bot.getListenerManager().dispatchEvent(new FileTransferFinishedEvent(bot, DccFileTransfer.this, exception));
    }

    /**
     * Method to send the file
     */
    protected void doSend(final boolean allowResume) {
	BufferedInputStream finput = null;
	Exception exception = null;
	try {
	    ServerSocket ss = manager.createServerSocket();
	    ss.setSoTimeout(timeout);
	    port = ss.getLocalPort();
	    InetAddress inetAddress = bot.getDccInetAddress();
	    if (inetAddress == null)
		inetAddress = bot.getInetAddress();
	    String ipNum = DccManager.addressToInteger(inetAddress);

	    // Rename the filename so it has no whitespace in it when we send it
	    String safeFilename = file.getName().replace(' ', '_').trim();
	    safeFilename = safeFilename.replace('\t', '_');

	    if (allowResume)
		manager.addAwaitingResume(this);

	    // Send the message to the user, telling them where to connect to in order to get the file.
	    bot.sendCTCPCommand(user.getNick(), "DCC SEND " + safeFilename + " " + ipNum + " " + port + " " + file.length());

	    // The client may now connect to us and download the file.
	    socket = ss.accept();
	    socket.setSoTimeout(30000);
	    startTime = System.currentTimeMillis();

	    // No longer possible to resume this transfer once it's underway.
	    if (allowResume)
		manager.removeAwaitingResume(this);

	    // Might as well close the server socket now; it's finished with.
	    ss.close();

	    BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
	    BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
	    finput = new BufferedInputStream(new FileInputStream(file));
	    fileStream = finput;

	    // Check for resuming.
	    if (progress > 0) {
		long bytesSkipped = 0;
		while (bytesSkipped < progress)
		    bytesSkipped += finput.skip(progress - bytesSkipped);
	    }

	    byte[] outBuffer = new byte[BUFFER_SIZE];
	    byte[] inBuffer = new byte[4];
	    int bytesRead = 0;
	    while ((bytesRead = finput.read(outBuffer, 0, outBuffer.length)) != -1) {
		output.write(outBuffer, 0, bytesRead);
		output.flush();
		input.read(inBuffer, 0, inBuffer.length);
		progress += bytesRead;
		delay();
	    }
	} catch (Exception e) {
	    exception = e;
	} finally {
	    try {
		finput.close();
		socket.close();
	    } catch (Exception e) {
		//This might be important, but don't change any existing exception
		if (exception == null)
		    exception = e;
	    }
	}

	bot.getListenerManager().dispatchEvent(new FileTransferFinishedEvent(bot, DccFileTransfer.this, exception));
    }

    /**
     * Package mutator for setting the progress of the file transfer.
     */
    protected void setProgress(long progress) {
	this.progress = progress;
    }

    /**
     *  Delay between packets.
     */
    protected void delay() {
	if (packetDelay > 0)
	    try {
		Thread.sleep(packetDelay);
	    } catch (InterruptedException e) {
		throw new DccException("Sleep between packets interupted", e);
	    }
    }

    /**
     * Returns the suggested file to be used for this transfer.
     *
     * @return the suggested file to be used.
     *
     */
    public File getFile() {
	return file;
    }

    /**
     * Returns the port number to be used when making the connection.
     *
     * @return the port number.
     *
     */
    public int getPort() {
	return port;
    }

    /**
     * Returns true if the file transfer is incoming (somebody is sending
     * the file to us).
     *
     * @return true if the file transfer is incoming.
     *
     */
    public boolean isIncoming() {
	return incoming;
    }

    /**
     * Returns true if the file transfer is outgoing (we are sending the
     * file to someone).
     *
     * @return true if the file transfer is outgoing.
     *
     */
    public boolean isOutgoing() {
	return !isIncoming();
    }

    /**
     * Sets the delay time between sending or receiving each packet.
     * Default is 0.
     * This is useful for throttling the speed of file transfers to maintain
     * a good quality of service for other things on the machine or network.
     *
     * @param millis The number of milliseconds to wait between packets.
     *
     */
    public void setPacketDelay(long millis) {
	packetDelay = millis;
    }

    /**
     * returns the delay time between each packet that is send or received.
     *
     * @return the delay between each packet.
     *
     */
    public long getPacketDelay() {
	return packetDelay;
    }

    /**
     * Returns the size (in bytes) of the file being transfered.
     *
     * @return the size of the file. Returns -1 if the sender did not
     *         specify this value.
     */
    public long getSize() {
	return size;
    }

    /**
     * Returns the progress (in bytes) of the current file transfer.
     * When resuming, this represents the total number of bytes in the
     * file, which may be greater than the amount of bytes resumed in
     * just this transfer.
     *
     * @return the progress of the transfer.
     */
    public long getProgress() {
	return progress;
    }

    /**
     * Returns the progress of the file transfer as a percentage.
     * Note that this should never be negative, but could become
     * greater than 100% if you attempt to resume a larger file
     * onto a partially downloaded file that was smaller.
     *
     * @return the progress of the transfer as a percentage.
     */
    public double getProgressPercentage() {
	return 100 * (getProgress() / (double) getSize());
    }

    /**
     * Stops the DCC file transfer by closing the connection.
     */
    public void close() throws IOException {
	socket.close();
	fileStream.close();
    }

    /**
     * Returns the rate of data transfer in bytes per second.
     * This value is an estimate based on the number of bytes
     * transfered since the connection was established.
     *
     * @return data transfer rate in bytes per second.
     */
    public long getTransferRate() {
	long time = (System.currentTimeMillis() - startTime) / 1000;
	if (time <= 0)
	    return 0;
	return getProgress() / time;
    }

    /**
     * Returns the address of the sender
     *
     * @return the address of the sender
     */
    public InetAddress getNumericalAddress() {
	return address;
    }

    /**
     * Return the original file name. When receiving this is the filename that the
     * user sent it as, when sending this is the name of the file that is being
     * sent
     * @return Original file name
     */
    public String getFilename() {
	return filename;
    }
}
