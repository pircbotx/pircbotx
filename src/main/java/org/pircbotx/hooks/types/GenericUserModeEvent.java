package org.pircbotx.hooks.types;

import org.pircbotx.Channel;
import org.pircbotx.hooks.events.OpEvent;

/**
 * Any user event that happens in a channel. Eg {@link OpEvent}
 * 
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface GenericUserModeEvent extends GenericUserEvent {
	/**
	 * The channel that the mode changed occured in
	 */
	public Channel getChannel();
}
