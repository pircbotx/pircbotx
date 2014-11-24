/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks.events;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

/**
 * A command sent to the IRC server from PircBotX
 * @author Leon Blakey
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutputEvent extends Event {
	private final String rawLine;
	/**
	 * Raw line split into its individual parts
	 * @see org.pircbotx.Utils#tokenizeLine(java.lang.String)
	 */
	private final List<String> lineParsed;

	public OutputEvent(PircBotX bot, String rawLine, List<String> lineParsed) {
		super(bot);
		this.rawLine = rawLine;
		this.lineParsed = lineParsed;
	}

	/**
	 * @param response
	 * @deprecated Cannot respond to output
	 */
	@Override
	@Deprecated
	public void respond(String response) {
		throw new UnsupportedOperationException("Not supported");
	}
}
