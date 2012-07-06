/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * Dispatched when a reconnect happens
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReconnectEvent<T extends PircBotX> extends Event<T>  {
	protected boolean success;
	protected Exception ex;
	/**
	 * Default constructor to setup object. Timestamp is automatically set
	 * to current time as reported by {@link System#currentTimeMillis() }
	 */
	public ReconnectEvent(T bot, boolean success, Exception ex) {
		super(bot);
		this.success = success;
		this.ex = ex;
	}

	/**
	 * Does NOT respond to the server! This will throw an {@link UnsupportedOperationException} 
	 * since we can't respond to a server we might not be connected to yet. 
	 * @param response The response to send 
	 */
	@Override
	public void respond(String response) {
		throw new UnsupportedOperationException("Attepting to respond to a reconnected server");
	}
}
