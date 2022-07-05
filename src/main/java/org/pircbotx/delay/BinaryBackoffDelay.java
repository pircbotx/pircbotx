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

/*
 * Implements a binary exponential backoff
 * 
 * Targeted use is reconnectDelay 
 */
public class BinaryBackoffDelay implements Delay {
	
	long currentDelay;
	long initialDelay;
	long maxDelay;

	public BinaryBackoffDelay(long initialDelay, long maxDelay) {
		checkArgument(maxDelay >= initialDelay, "initialDelay may not be larger than maxdelay");
		setInitialDelay(initialDelay);
		setMaxDelay(maxDelay);
		
		currentDelay = initialDelay;
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
		currentDelay = Math.min(currentDelay*2, maxDelay);
		
		return currentDelay;
	}

}
