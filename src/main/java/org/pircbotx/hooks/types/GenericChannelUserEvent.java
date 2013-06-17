package org.pircbotx.hooks.types;

import org.pircbotx.PircBotX;

/**
 *
 * @author Leon
 */
public interface GenericChannelUserEvent<T extends PircBotX> extends GenericUserEvent<T>, GenericChannelEvent<T> {
}
