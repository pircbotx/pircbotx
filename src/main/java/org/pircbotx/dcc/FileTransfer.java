
package org.pircbotx.dcc;

import org.pircbotx.User;

/**
 *
 * @author Leon
 */
public interface FileTransfer {
	public User getUser();
	public String getFilename();
	public long getFilesize();
	public long getStartPosition();
	public long getBytesTransfered();
	public DccState getState();
}
