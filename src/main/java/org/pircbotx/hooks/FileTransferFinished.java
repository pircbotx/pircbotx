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
 * This method gets called when a DccFileTransfer has finished.
 * If there was a problem, the Exception will say what went wrong.
 * If the file was sent successfully, the Exception will be null.
 *  <p>
 * Both incoming and outgoing file transfers are passed to this method.
 * You can determine the type by calling the isIncoming or isOutgoing
 * methods on the DccFileTransfer object.
 * @author Leon Blakey <lord.quackstar at gmail.com>
 * @see DccFileTransfer
 */
public class FileTransferFinished {
	/**
	 * Simple listener that takes event parameters as parameters. See 
	 * {@link FileTransferFinished} for an explanation on use 
	 * @see FileTransferFinished 
	 */
	public static interface SimpleListener extends BaseSimpleListener {
		public void onFileTransferFinished(DccFileTransfer transfer, Exception e);
	}

	/**
	 * Listener that receives an event. See {@link FileTransferFinished} for an explanation 
	 * on use and {@link Event} for an explanation on the event. 
	 * @see FileTransferFinished 
	 * @see Event 
	 */
	public static interface Listener extends BaseListener {
		public void onFileTransferFinished(Event event);
	}

	/**
	 * Event that is passed to all listeners that contains all the given
	 * information. See {@link FileTransferFinished} for an explanation on when this is created
	 * <p>
	 * <b>Note:<b> This class and all its subclasses are immutable since
	 * data should not change after creation
	 * @see FileTransferFinished 
	 * @see Listener
	 */
	public static class Event implements BaseEvent {
		protected final long timestamp;
		protected final DccFileTransfer transfer;
		protected final Exception e;

		/**
		 * Default constructor to setup object. Timestamp is automatically set
		 * to current time as reported by {@link System#currentTimeMillis() }
		 * @param transfer The DccFileTransfer that has finished.
		 * @param e null if the file was transfered successfully, otherwise this
		 *          will report what went wrong.
		 */
		public Event(DccFileTransfer transfer, Exception e) {
			this.timestamp = System.currentTimeMillis();
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
