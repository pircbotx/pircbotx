/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
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
package org.pircbotx;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility for doing various useful things to an SSL socket factory.
 * <p>
 * Most methods follow the builder pattern, meaning you can declare and setup
 * this Socket Factory in one line
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Slf4j
public class UtilSSLSocketFactory extends SSLSocketFactory {
	protected SSLSocketFactory wrappedFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	@Getter
	protected boolean trustingAllCertificates = false;
	@Getter
	protected boolean diffieHellmanDisabled = false;
	protected boolean wrappedFactoryChanged = false;

	/**
	 * By default, trust ALL certificates. <b>This is <i>very</i> insecure.</b>
	 * It also defeats one of the points of SSL: Making sure your connecting to
	 * the right server. Cannot be combined with {@link #disableDiffieHellman(javax.net.ssl.SSLSocketFactory)
	 * } since this overwrites the wrapped factory and will throw an exception!
	 *
	 * @return The current UtilSSLSocketFactory instance
	 */
	public UtilSSLSocketFactory trustAllCertificates() {
		if (wrappedFactoryChanged)
			throw new RuntimeException("Cannot combine trustAllCertificates() and disableDiffieHellman(SSLSocketFactory)");
		if (trustingAllCertificates)
			//Already doing this, no need to do it again
			return this;
		trustingAllCertificates = true;
		try {
			TrustManager[] tm = new TrustManager[]{new TrustingX509TrustManager()};
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(new KeyManager[0], tm, new SecureRandom());
			wrappedFactory = context.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Can't recreate socket factory that trusts all certificates", e);
		}
		return this;
	}

	/**
	 * Disable the Diffie Hellman key exchange algorithm. This is useful to work
	 * around JDK bug #6521495 which throws an Exception when prime sizes are
	 * above 1024 bits.
	 * <p>
	 * Note that this requires that the server supports other key exchange
	 * algorithms. This socket factory (nor any other built in Socket Factory)
	 * cannot connect to a server that only supports Diffie Hellman key exchange
	 * with prime sizes larger than 1024 bits.
	 * <p>
	 * Also see PircBotX Issue #34
	 *
	 * @return The current UtilSSLSocketFactory instance
	 */
	public UtilSSLSocketFactory disableDiffieHellman() {
		diffieHellmanDisabled = true;
		return this;
	}

	/**
	 * Disable the Diffie Hellman key exchange algorithm using the provided SSL
	 * socket factory. Cannot be combined with {@link #disableDiffieHellman(javax.net.ssl.SSLSocketFactory)
	 * } since this overwrites the wrapped factory and will throw an exception!
	 *
	 * @see #disableDiffieHellman()
	 * @param sourceSocketFactory
	 * @return The current UtilSSLSocketFactory instance
	 */
	public UtilSSLSocketFactory disableDiffieHellman(SSLSocketFactory sourceSocketFactory) {
		if (trustingAllCertificates)
			throw new RuntimeException("Cannot combine trustAllCertificates() and disableDiffieHellman(SSLSocketFactory)");
		wrappedFactory = sourceSocketFactory;
		wrappedFactoryChanged = true;
		return disableDiffieHellman();
	}

	protected SSLSocket prepare(Socket socket) {
		SSLSocket sslSocket = (SSLSocket) socket;
		// Source: https://stackoverflow.com/questions/6851461/why-does-ssl-handshake-give-could-not-generate-dh-keypair-exception/6862383#6862383
		if (diffieHellmanDisabled) {
			List<String> limited = new LinkedList<String>();
			for (String suite : sslSocket.getEnabledCipherSuites())
				if (!suite.contains("_DHE_"))
					limited.add(suite);
			sslSocket.setEnabledCipherSuites(limited.toArray(new String[limited.size()]));
		}
		return sslSocket;
	}

	@Override
	public SSLSocket createSocket(String host, int port) throws IOException, UnknownHostException {
		return prepare(wrappedFactory.createSocket(host, port));
	}

	@Override
	public SSLSocket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		return prepare(wrappedFactory.createSocket(host, port, localHost, localPort));
	}

	@Override
	public SSLSocket createSocket(InetAddress address, int port) throws IOException {
		return prepare(wrappedFactory.createSocket(address, port));
	}

	@Override
	public SSLSocket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return prepare(wrappedFactory.createSocket(address, port, localAddress, localPort));
	}

	@Override
	public SSLSocket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return prepare(wrappedFactory.createSocket(s, host, port, autoClose));
	}

	//@Override
	public SSLSocket createSocket(Socket socket, InputStream in, boolean bln) throws IOException {
		//This method is new in JDK 8, however PircBotX must compile on 7 and below
		try {
			return prepare((Socket) getClass().getMethod("createSocket", Socket.class, InputStream.class, boolean.class)
					.invoke(this, socket, in, bln));
		} catch (Exception e) {
			throw new RuntimeException("Failed to create socket", e);
		}
	}

	@Override
	public SSLSocket createSocket() throws IOException {
		return prepare(wrappedFactory.createSocket());
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return wrappedFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return wrappedFactory.getSupportedCipherSuites();
	}

	/**
	 * X509TrustManager that trusts all certificates. <b>This is very
	 * insecure</b>
	 *
	 * Source: http://www.howardism.org/Technical/Java/SelfSignedCerts.html
	 */
	public static class TrustingX509TrustManager implements X509TrustManager {
		/**
		 * Doesn't throw an exception, so this is how it approves a certificate.
		 *
		 * @see
		 * javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
		 * String)
		 *
		 */
		public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
		}

		/**
		 * Doesn't throw an exception, so this is how it approves a certificate.
		 *
		 * @see
		 * javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
		 * String)
		 *
		 */
		public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 *
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
}
