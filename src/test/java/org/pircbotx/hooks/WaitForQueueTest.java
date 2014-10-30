/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pircbotx.hooks;

import java.util.Arrays;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.MotdEvent;

/**
 *
 * @author Leon
 */
public class WaitForQueueTest {
	private void syntaxCompileTest() throws InterruptedException {
		if(true)
			throw new RuntimeException("DO NOT CALL THIS");
		
		WaitForQueue queue = new WaitForQueue();
		
		//These should all be legal
		MessageEvent mevent = queue.waitFor(MessageEvent.class);
		
		Event event;
		event = queue.waitFor(MessageEvent.class);
		event = queue.waitFor(MessageEvent.class, ActionEvent.class);
		event = queue.waitFor(MessageEvent.class, ActionEvent.class, MotdEvent.class);
		
		event = queue.waitFor(Arrays.asList(MessageEvent.class, ActionEvent.class, MotdEvent.class));
		event = queue.waitFor(Arrays.asList(MessageEvent.class, ActionEvent.class));
		event = queue.waitFor(Arrays.asList(MessageEvent.class));
	}
}

