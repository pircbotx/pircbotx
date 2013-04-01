package org.pircbotx.dcc;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.DccException;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class DccManager2 {
	protected final PircBotX bot;
	protected List<SendFileTransfer> sendFileTransfers = Collections.synchronizedList(new ArrayList<SendFileTransfer>());
	protected List<ReceiveFileTransfer> recieveFileTransfers = Collections.synchronizedList(new ArrayList<ReceiveFileTransfer>());
	protected List<ReceiveFileTransfer> chats = Collections.synchronizedList(new ArrayList<ReceiveFileTransfer>());

	public boolean processDcc(User user, String line) {
		return true;
	}
	
	protected ServerSocket createServerSocket() throws IOException, DccException {
		ServerSocket ss = null;
		List<Integer> ports = bot.getDccPorts();
		if (ports.isEmpty())
			// Use any free port.
			ss = new ServerSocket(0);
		else {
			for (int currentPort : ports)
				try {
					ss = new ServerSocket(currentPort);
					// Found a port number we could use.
					break;
				} catch (Exception e) {
					// Do nothing; go round and try another port.
				}
			if (ss == null)
				// No ports could be used.
				throw new DccException("All ports returned by getDccPorts() " + ports.toString() + "are in use.");
		}
		return ss;
	}
	
	public void close() {
		
	}

	public static String addressToInteger(InetAddress address) {
		return new BigInteger(1, address.getAddress()).toString();
	}

	public static InetAddress integerToAddress(String rawInteger) {
		//Convert the rawInteger into something usable
		BigInteger bigIp = new BigInteger(rawInteger);
		byte[] addressBytes = bigIp.toByteArray();

		//If there aren't enough bytes, pad with 0 byte
		if (addressBytes.length == 5)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 5);
		else if (addressBytes.length < 4) {
			byte[] newAddressBytes = new byte[4];
			newAddressBytes[3] = addressBytes[0];
			newAddressBytes[2] = (addressBytes.length > 1) ? addressBytes[1] : (byte) 0;
			newAddressBytes[1] = (addressBytes.length > 2) ? addressBytes[2] : (byte) 0;
			newAddressBytes[0] = (addressBytes.length > 3) ? addressBytes[3] : (byte) 0;
			addressBytes = newAddressBytes;
		} else if (addressBytes.length == 17)
			//Has signum, strip it
			addressBytes = Arrays.copyOfRange(addressBytes, 1, 17);
		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException ex) {
			throw new RuntimeException("Can't get InetAdrress version of int IP address " + rawInteger + " (bytes: " + Arrays.toString(addressBytes) + ")", ex);
		}
	}
}
