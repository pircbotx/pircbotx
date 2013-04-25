
package org.pircbotx.dcc;

import org.pircbotx.User;

/**
 *
 * @author Leon
 */
public interface FileTransfer {
	public User getUser();
	public String getFilename();
	public long getStartPosition();
	public long getBytesTransfered();
	public int getProgress();
	public DccState getState();
}
