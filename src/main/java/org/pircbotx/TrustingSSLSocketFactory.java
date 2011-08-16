/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * An SSLSocketFactory implementation that treats all certificates as valid. Use with care.
 * 
 * @deprecated This class has been superseded by {@link UtilSSLSocketFactory}, which
 * provides much more flexibility and ease of use. This class is kept for backwards
 * compatability only.
 * @author Originally by: 
 *         <a href="http://www.brudvik.org/projects/development/pircbot-with-ssl/">Kjell Arne Brudvik from brudvik.org</a>
 *         <p>Implemented and Maintained in PircBotX by: 
 *         Leon Blakey <lord.quackstar at gmail.com>
 *         
 */
@Deprecated
public class TrustingSSLSocketFactory extends SSLSocketFactory {
	private SSLSocketFactory factory;
	private String[] ciphers;

	/**
	 * Create a new SSLSocketFactory factory that will create Sockets regardless of what certificate
	 * is used.
	 * @throws SSLException if it cannot initialize correctly.
	 */
	public TrustingSSLSocketFactory() throws SSLException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		try {
			SSLContext sslContext;
			sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(null, new TrustManager[]{new UtilSSLSocketFactory.TrustingX509TrustManager()}, null);
			factory = sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException nsae) {
			throw new SSLException("Unable to initialize the SSL context:  ", nsae);
		} catch (KeyManagementException kme) {
			throw new SSLException("Unable to register a trust manager:  ", kme);
		}
		ciphers = factory.getDefaultCipherSuites();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.ssl.SSLSocketFactory#getDefaultCipherSuites()
	 */
	@Override
	public String[] getDefaultCipherSuites() {
		return ciphers;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.ssl.SSLSocketFactory#getSupportedCipherSuites()
	 */
	@Override
	public String[] getSupportedCipherSuites() {
		return ciphers;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
	 */
	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return prepare((SSLSocket) factory.createSocket(host, port));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
	 */
	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return prepare((SSLSocket) factory.createSocket(host, port, localHost, localPort));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
	 */
	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException {
		return prepare((SSLSocket) factory.createSocket(address, port));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int, java.net.InetAddress,
	 *      int)
	 */
	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
			int localPort) throws IOException {
		return prepare((SSLSocket) factory.createSocket(address, port, localAddress, localPort));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.net.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int,
	 *      boolean)
	 */
	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose)
			throws IOException {
		return prepare((SSLSocket) factory.createSocket(s, host, port, autoClose));
	}

	/**
	 * Setup the socket with ciphers. Add code here to do things to all created Sockets.
	 * @param baseSocket
	 * @return &lt;code&gt;baseSocket&lt;/code&gt; all set up.
	 */
	private SSLSocket prepare(SSLSocket baseSocket) {
		baseSocket.setEnabledCipherSuites(ciphers);
		return baseSocket;
	}
}
