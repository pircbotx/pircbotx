package org.pircbotx.exception;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class UnknownHookException extends Exception {

	public UnknownHookException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownHookException(String message) {
		super(message);
	}
	
}
