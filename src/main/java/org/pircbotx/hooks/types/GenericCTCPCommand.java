
package org.pircbotx.hooks.types;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 *
 * @author lordquackstar
 */
public interface GenericCTCPCommand<T extends PircBotX> {
	public User getUser();
	
	public Channel getChannel();
}
