package org.pircbotx.cap;

import java.util.List;
import org.pircbotx.PircBotX;

/**
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public interface CapHandler {
	public void handleLS(PircBotX bot, List<String> capabilities) throws Exception;
	public void handleACK(PircBotX bot, List<String> capabilities) throws Exception;
	public void handleNAK(PircBotX bot, List<String> capabilities) throws Exception;
	public void handleUnknown(PircBotX bot, String rawLine) throws Exception;
	public boolean isDone();
}
