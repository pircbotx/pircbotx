package org.pircbotx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.exception.NotReadyException;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

public class ModeClass
{

    @Setter(AccessLevel.NONE)
    public String mode = "";
    public  PircBotX bot;

    @Getter(AccessLevel.NONE)
    public final Object modeChangeLock = new Object();
    /**
     * Gets the channel mode. If mode is simple (no arguments), this will return
     * immediately. If its not (mode with arguments, eg channel key), then asks
     * the server for the correct mode, waiting until it gets a response
     * <p>
     * <b>WARNING:</b> Because of the last checking, a threaded listener manager
     * like {@link ThreadedListenerManager} is required. Using a single threaded
     * listener manager like
     * {@link org.pircbotx.hooks.managers.GenericListenerManager} will mean this
     * method <i>never returns</i>!
     *
     * @return A known good mode, either immediately or soon.
     */
    public String getMode() throws NotReadyException {
        synchronized (modeChangeLock) {
            if (mode != null)
                return mode;
            else
                throw new NotReadyException("Mode not ready yet");
        }
    }

    public String getMode(String defaultValue)  {
        try {
            return getMode();
        } catch (NotReadyException e) {
            return defaultValue;
        }
    }

    public boolean containsMode(char modeLetter, boolean defaultValue) {
        try {
            final String mode = getMode();
            if (mode.isEmpty())
                return false;

            String modeLetters = StringUtils.split(mode, ' ')[0];
            return StringUtils.contains(modeLetters, modeLetter);
        } catch (NotReadyException e) {
            return defaultValue;
        }
    }


}
