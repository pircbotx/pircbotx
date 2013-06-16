
package org.pircbotx.hooks.types;

import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 *
 * @author Leon
 */
public interface GenericUserEvent<T extends PircBotX> extends GenericEvent<T> {
	/**
	 * The source user of the event
	 * @return A user
	 */
	public User getUser();
}
