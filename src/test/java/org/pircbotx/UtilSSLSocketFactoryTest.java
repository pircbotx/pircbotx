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

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class UtilSSLSocketFactoryTest {
	/**
	 * Ghetto diff by converting all signatures to Strings and replacing
	 * SSLSocketFactory with UtilSSLSocketFactory
	 */
	@Test
	public void delegatesAllMethods() {
		Set<String> implMethodSignatures = new HashSet<String>();
		for (Method curMethod : findPublicMethods(UtilSSLSocketFactory.class.getMethods())) {
			implMethodSignatures.add(curMethod.toGenericString());
			log.debug("added " + curMethod.toGenericString());
		}

		log.debug("");

		int failCount = 0;
		for (Method curMethod : findPublicMethods(SSLSocketFactory.class.getMethods())) {
			String newMethodSignature = curMethod.toGenericString()
					.replace("javax.net.ssl.SSLSocketFactory", "org.pircbotx.UtilSSLSocketFactory")
					.replace("javax.net.SocketFactory", "org.pircbotx.UtilSSLSocketFactory")
					.replace(" java.net.Socket", " javax.net.ssl.SSLSocket")
					.replace("abstract ", "");
			if (!implMethodSignatures.contains(newMethodSignature)) {
				log.debug(curMethod.toGenericString());
				log.debug("(expected) " + newMethodSignature);
				failCount++;
			}
		}

		assertEquals(failCount, 0, "UtilSSLSocketFactory fails to implement methods");
	}

	private Set<Method> findPublicMethods(Method[] methods) {
		HashSet<Method> publicMethods = new HashSet<>();
		for (Method curMethod : methods) {
			if (!curMethod.isSynthetic()
					&& curMethod.getDeclaringClass() != Object.class
					&& !Modifier.isStatic(curMethod.getModifiers())
					&& Modifier.isPublic(curMethod.getModifiers())) {
				publicMethods.add(curMethod);
			}
		}
		return publicMethods;
	}

	@Test
	public void combineTestLegal() {
		new UtilSSLSocketFactory().disableDiffieHellman();
		new UtilSSLSocketFactory().trustAllCertificates();
		new UtilSSLSocketFactory().trustAllCertificates().disableDiffieHellman();
		new UtilSSLSocketFactory().disableDiffieHellman().trustAllCertificates();
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void combineTestIllegal1() {
		new UtilSSLSocketFactory().disableDiffieHellman((SSLSocketFactory) SSLSocketFactory.getDefault()).trustAllCertificates();
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void combineTestIllegal2() {
		new UtilSSLSocketFactory().trustAllCertificates().disableDiffieHellman((SSLSocketFactory) SSLSocketFactory.getDefault());
	}
}
