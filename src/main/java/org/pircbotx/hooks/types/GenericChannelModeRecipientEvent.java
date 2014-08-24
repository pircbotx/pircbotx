/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pircbotx.hooks.types;

import org.pircbotx.PircBotX;

/**
 *
 * @author Leon
 */
public interface GenericChannelModeRecipientEvent<T extends PircBotX> extends GenericUserModeEvent<T>, GenericChannelModeEvent<T>{
	
}
