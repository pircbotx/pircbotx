/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.hooks;

import org.pircbotx.DccFileTransfer;
import org.pircbotx.hooks.helpers.BaseEvent;
import org.pircbotx.hooks.helpers.BaseListener;
import org.pircbotx.hooks.helpers.BaseSimpleListener;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class FileTransferFinished {
	public static interface SimpleListener extends BaseSimpleListener {
		public void onFileTransferFinished(DccFileTransfer transfer, Exception e);
	}

	public static interface Listener extends BaseListener {
		public void onFileTransferFinished(Event event);
	}

	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final DccFileTransfer transfer;
		protected final Exception e;

		public Event(long timestamp, DccFileTransfer transfer, Exception e) {
			this.timestamp = timestamp;
			this.transfer = transfer;
			this.e = e;
		}

		public Exception getE() {
			return e;
		}

		public DccFileTransfer getTransfer() {
			return transfer;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}
