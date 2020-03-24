package org.pircbotx.delay;

import static com.google.common.base.Preconditions.checkArgument;

public class StaticReadonlyDelay implements Delay {
	
	private long delay;
	
	public StaticReadonlyDelay( long delay) {
		checkArgument(delay >= 0, "Delay may not be negative");
		
		this.delay = delay;
	}
	

	@Override
	public long getDelay() {
		return delay;
	}
	
}
