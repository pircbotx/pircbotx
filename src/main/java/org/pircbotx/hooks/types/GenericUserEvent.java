package org.pircbotx.hooks.types;

import org.pircbotx.User;

/**
 * Generic Event for user generated actions.
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericUserEvent {
	public User getUser();
}
