
package org.pircbotx.hooks.types;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

/**
 *
 * @author Leon
 */
public interface GenericChannelEvent<T extends PircBotX> extends GenericEvent<T> {
	/**
	 * The source channel of this event. This may be null
	 * @return A channel or null
	 */
	public Channel getChannel();
}
