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
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author lordquackstar
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public class UtilSSLSocketFactory extends SSLSocketFactory {
	@Delegate(excludes = SSLSocketFactoryDelegateExclude.class)
	protected SSLSocketFactory wrappedFactory;
	protected boolean trustingAllCertificates = false;
	protected boolean diffieHellmanDisabled = false;

	public UtilSSLSocketFactory() {
	}

	public UtilSSLSocketFactory trustAllCertificates() {
		if (trustingAllCertificates)
			//Already doing this, no need to do it again
			return this;
		trustingAllCertificates = true;
		try {
			TrustManager[] tm = new TrustManager[]{new NaiveTrustManager()};
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(new KeyManager[0], tm, new SecureRandom());
			wrappedFactory = (SSLSocketFactory) context.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Can't recreate socket factory that trusts all certificates", e);
		}
		return this;
	}

	public UtilSSLSocketFactory disableDiffieHellman() {
		diffieHellmanDisabled = true;
		return this;
	}

	protected Socket prepare(Socket socket) {
		SSLSocket sslSocket = (SSLSocket) socket;
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
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return prepare(wrappedFactory.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return prepare(wrappedFactory.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException {
		return prepare(wrappedFactory.createSocket(address, port));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return prepare(wrappedFactory.createSocket(address, port, localAddress, localPort));
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return prepare(wrappedFactory.createSocket(s, host, port, autoClose));
	}

	public static class NaiveTrustManager implements X509TrustManager {
		/**
		 * Doesn't throw an exception, so this is how it approves a certificate.
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
		 **/
		public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
		}

		/**
		 * Doesn't throw an exception, so this is how it approves a certificate.
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
		 **/
		public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 **/
		public X509Certificate[] getAcceptedIssuers() {
			return null;  // I've seen someone return new X509Certificate[ 0 ]; 
		}
	}

	protected interface SSLSocketFactoryDelegateExclude {
		Socket createSocket(String host, int port) throws IOException, UnknownHostException;

		Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException;

		Socket createSocket(InetAddress address, int port) throws IOException;

		Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException;

		Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException;
	}
}
