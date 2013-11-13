/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx.cap;

import com.google.common.collect.ImmutableList;
import javax.net.ssl.SSLSocketFactory;
import lombok.Getter;
import lombok.ToString;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.CAPException;

/**
 * CAP STARTTLS support <b>*MUST BE LAST CAP HANDLER*</b>. Due to how STARTTLS
 * works and how PircBotX is designed this must be the last CAP handler, otherwise
 * you will receive an "SSL peer shutdown incorrectly" exception
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@ToString
public class TLSCapHandler extends EnableCapHandler {
	@Getter
	protected SSLSocketFactory sslSocketFactory;

	public TLSCapHandler() {
		super("tls");
		this.sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	}

	public TLSCapHandler(SSLSocketFactory sslSocketFactory, boolean ignoreFail) {
		super("tls", ignoreFail);
		this.sslSocketFactory = sslSocketFactory;
	}

	@Override
	public boolean handleACK(PircBotX bot, ImmutableList<String> capabilities) throws CAPException {
		if (capabilities.contains("tls"))
			bot.sendRaw().rawLineNow("STARTTLS");
		//Not finished, still need to wait for 670 line
		return false;
	}

	@Override
	public boolean handleUnknown(PircBotX bot, String rawLine) {
		//Finished if we have successfully upgraded the socket
		return rawLine.contains(" 670 ");
	}
}
