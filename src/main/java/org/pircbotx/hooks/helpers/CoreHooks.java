/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx.hooks.helpers;

import java.util.Date;
import org.pircbotx.hooks.Finger;
import org.pircbotx.hooks.Ping;
import org.pircbotx.hooks.ServerPing;
import org.pircbotx.hooks.Time;
import org.pircbotx.hooks.Version;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class CoreHooks implements Finger.Listener, Ping.Listener, ServerPing.Listener, Time.Listener, Version.Listener {

	public void onFinger(Finger.Event event) {
		event.getBot().sendCTCPResponse(event, "FINGER " + event.getBot().getFinger());
	}

	public void onPing(Ping.Event event) {
		event.getBot().sendCTCPResponse(event, "PING " + event.getPingValue());
	}

	public void onServerPing(ServerPing.Event event) {
		event.getBot().sendRawLine("PONG " + event.getResponse());
	}

	public void onTime(Time.Event event) {
		event.getBot().sendCTCPResponse(event, "TIME " + new Date().toString());
	}

	public void onVersion(Version.Event event) {
		event.getBot().sendCTCPResponse(event, "VERSION " + event.getBot().getVersion());
	}
}
