package org.pircbotx.hooks.types;

import org.pircbotx.Channel;

/**
 * Generic Channel Mode change event
 * 
 * @author Leon Blakey <lord.quackstar@gmail.com>
 */
public interface GenericChannelModeEvent extends GenericUserEvent {
	/**
	 * The channel that the mode changed occured in
	 */
	public Channel getChannel();
}
