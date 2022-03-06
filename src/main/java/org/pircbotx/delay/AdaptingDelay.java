/*
 * Copyright (C) 2010-2022 The PircBotX Project Authors
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.delay;

import static com.google.common.base.Preconditions.checkArgument;

import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;

public class AdaptingDelay extends ListenerAdapter implements Delay {
	long currentDelay;
	long initialDelay;
	long maxDelay;
	
	long lastChange;

	public AdaptingDelay(long initialDelay, long maxDelay) {
		checkArgument(maxDelay >= initialDelay, "initialDelay may not be larger than maxdelay");
		setInitialDelay(initialDelay);
		setMaxDelay(maxDelay);
		
		currentDelay = initialDelay;
		
		lastChange = System.currentTimeMillis();
	}
	
	public void setInitialDelay(long initialDelay) {
		checkArgument(initialDelay > 0, "initialDelay must be larger than zero");
		this.initialDelay = initialDelay;
	}
	
	public void setMaxDelay(long maxDelay) {
		checkArgument(maxDelay >= 0, "maxDelay may not be negative");
		this.maxDelay = maxDelay;
	}
	
	public void reset() {
		currentDelay = initialDelay;
	}

	@Override
	public long getDelay() {
		if (currentDelay>initialDelay && lastChange + 10000 < System.currentTimeMillis() ) {
			currentDelay = Math.max(currentDelay/2, initialDelay);
			lastChange = System.currentTimeMillis();
		}
		
		return currentDelay;
	}

	@Override
	public void onServerResponse(ServerResponseEvent event) throws Exception {
		if (event.getCode() == ReplyConstants.ERR_TARGETTOOFAST) {
			currentDelay = Math.min(currentDelay*2, maxDelay);			
			lastChange = System.currentTimeMillis();
		}
	}
	
	
	
	
}
