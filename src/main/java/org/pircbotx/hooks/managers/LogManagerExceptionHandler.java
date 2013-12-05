package org.pircbotx.hooks.managers;

import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

/**
 * Write all exceptions to log
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Slf4j
public class LogManagerExceptionHandler implements ManagerExceptionHandler {
	public void onException(Listener listener, Event event, Throwable exception) {
		log.error("Exception encountered when executing event " + event + " on listener " + listener, exception);
	}
}
