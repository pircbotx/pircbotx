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
		sendRaw.rawLineNow("CAP LS");
	}

	public void getEnabled() {
		sendRaw.rawLineNow("CAP LIST");
	}

	public void request(String... capability) {
		sendRaw.rawLineNow("CAP REQ :" + Utils.join(Arrays.asList(capability), " "));
	}
	
	public void clear() {
		sendRaw.rawLineNow("CAP CLEAR");
	}
	
	public void end() {
		sendRaw.rawLineNow("CAP END");
	}
}
