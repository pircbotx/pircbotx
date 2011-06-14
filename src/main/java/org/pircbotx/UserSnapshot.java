package org.pircbotx;

import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author lordquackstar
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserSnapshot extends User {
	protected Set<Channel> channels;
	protected Set<Channel> channelsOpIn;
	protected Set<Channel> channelsVoiceIn;
	protected Set<Channel> channelsOwnerIn;
	protected Set<Channel> channelsSuperOpIn;
	protected Set<Channel> channelsHalfOpIn;
	
	public UserSnapshot(User user) {
		super(user.getBot(), user.getNick());
		
		//Clone fields
		super.setAway(user.isAway());
		super.setHops(user.getHops());
		super.setHostmask(user.getHostmask());
		super.setIdentified(user.isIdentified());
		super.setIrcop(user.isIrcop());
		super.setLogin(user.getLogin());
		super.setRealName(user.getRealName());
		super.setServer(user.getServer());
		
		//Store channels
		channels = user.getChannels();
		channelsOpIn = user.getChannelsOpIn();
		channelsVoiceIn = user.getChannelsVoiceIn();
		channelsSuperOpIn = user.getChannelsSuperOpIn();
		channelsHalfOpIn.addAll(user.getChannelsHalfOpIn());
	}

	@Override
	public void parseStatus(Channel chan, String prefix) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setAway(boolean away) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setHops(int hops) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setHostmask(String hostmask) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setIdentified(boolean identified) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setIrcop(boolean ircop) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setLogin(String login) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setNick(String nick) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setRealName(String realName) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}

	@Override
	void setServer(String server) {
		throw new UnsupportedOperationException("Attempting to set field on user snapshot");
	}
	
}
