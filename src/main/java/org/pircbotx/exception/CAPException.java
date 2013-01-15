package org.pircbotx.exception;

/**
 * General Exception for problems during CAP negotiation
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CAPException extends RuntimeException {
	public CAPException(String message) {
		super(message);
	}

	public CAPException(String message, Throwable cause) {
		super(message, cause);
	}
}
