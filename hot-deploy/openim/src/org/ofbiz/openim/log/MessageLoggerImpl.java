/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package org.ofbiz.openim.log;

import org.ofbiz.base.util.Debug;

import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.jabber.IMMessage;
import net.java.dev.openim.log.MessageLogger;

public class MessageLoggerImpl implements MessageLogger {
    
    public final static String module = MessageLoggerImpl.class.getName();

    public void log(Transitable message) {
        if (Debug.infoOn()) {
            if ( message instanceof IMMessage ) {
                IMMessage m = (IMMessage) message;
                Debug.logInfo(m.getFrom() + " " + m.getTo() + " " + m.toString().length(), module);
            }
        }
    }
}
