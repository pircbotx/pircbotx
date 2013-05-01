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
	public void sendCAPREQ(String... capability) {
		sendRaw.rawLine("CAP REQ :" + Utils.join(Arrays.asList(capability), " "));
	}
}
