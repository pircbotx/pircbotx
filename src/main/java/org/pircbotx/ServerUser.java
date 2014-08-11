

package org.pircbotx;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircbotx.output.OutputUser;

/**
 * Represents any hostmask that may or may not be an actual user. 
 * @author Leon
 */
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId", "bot"})
@Data
public class ServerUser implements Comparable<User> {
	@NonNull
	private final PircBotX bot;
	private final UUID userId = UUID.randomUUID();
	/**
	 * Lazily created output since it might not ever be used
	 */
	@Getter(AccessLevel.NONE)
	protected final AtomicSafeInitializer<OutputUser> output = new AtomicSafeInitializer<OutputUser>() {
		@Override
		protected OutputUser initialize() {
			return bot.getConfiguration().getBotFactory().createOutputUser(bot, ServerUser.this);
		}
	};
	/**
	 * Hostmask of the user (The entire user!login@hostname).
	 */
	@NonNull
	private String hostmask;
	/**
	 * Current nick of the user (nick!login@hostname).
	 */
	private String nick;
	/**
	 * Login of the user (nick!login@hostname).
	 */
	private final String login;
	/**
	 * Hostname of the user (nick!login@hostname).
	 */
	private final String hostname;
	
	/**
	 * Send a line to the user.
	 * @return A {@link OutputUser} for this user
	 */
	public OutputUser send() {
		try {
			return output.get();
		} catch (ConcurrentException ex) {
			throw new RuntimeException("Could not generate OutputChannel for " + getNick(), ex);
		}
	}
	
	/**
	 * Compare {@link #getNick()} with {@link String#compareToIgnoreCase(java.lang.String) }.
	 * This is useful for sorting lists of User objects.
	 * @param other Other user to compare to
	 * @return the result of calling compareToIgnoreCase user nicks.
	 */
	@Override
	public int compareTo(User other) {
		return getNick().compareToIgnoreCase(other.getNick());
	}
}
