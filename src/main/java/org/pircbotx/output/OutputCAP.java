/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.output;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.pircbotx.Utils;

/**
 *
 * @author Leon
 */
@RequiredArgsConstructor
public class OutputCAP {
	protected final OutputRaw sendRaw;

	public void getSupported() {
		sendRaw.rawLine("CAP LS");
	}

	public void getEnabled() {
		sendRaw.rawLine("CAP LIST");
	}

	public void request(String... capability) {
		sendRaw.rawLine("CAP REQ :" + Utils.join(Arrays.asList(capability), " "));
	}
	
	public void clear() {
		sendRaw.rawLine("CAP CLEAR");
	}
	
	public void end() {
		sendRaw.rawLine("CAP END");
	}
}
