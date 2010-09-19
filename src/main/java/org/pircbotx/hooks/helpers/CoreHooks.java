/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx.hooks.helpers;

import org.pircbotx.hooks.Finger;
import org.pircbotx.hooks.Finger.Event;
import org.pircbotx.hooks.Ping;
import org.pircbotx.hooks.ServerPing;
import org.pircbotx.hooks.Time;
import org.pircbotx.hooks.Version;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks implements Finger.Listener, Ping.Listener, ServerPing.Listener, Time.Listener, Version.Listener {

	public void onFinger(Event event) {
		getBot().sendRawLine("NOTICE " + sourceNick + " :\u0001FINGER " + getBot().getFinger() + "\u0001");
	}

	public void onPing(Ping.Event event) {
		getBot().sendRawLine("NOTICE " + sourceNick + " :\u0001PING " + pingValue + "\u0001");
	}

	public void onServerPing(ServerPing.Event event) {
		getBot().sendRawLine("PONG " + response);
	}

	public void onTime(Time.Event event) {
		getBot().sendRawLine("NOTICE " + sourceNick + " :\u0001TIME " + new Date().toString() + "\u0001");
	}

	public void onVersion(Version.Event event) {
		getBot().sendRawLine("NOTICE " + sourceNick + " :\u0001VERSION " + getBot().getVersion() + "\u0001");
	}

}
