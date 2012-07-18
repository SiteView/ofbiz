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
package org.ofbiz.openim.jabber;

import java.io.EOFException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;

import net.java.dev.openim.session.IMSession;

public class ErrorImpl extends DefaultSessionProcessor implements net.java.dev.openim.jabber.Error {

    public final static String module = ErrorImpl.class.getName();

    @Override
    public void process(IMSession session, Object context) throws Exception {
        String msg = session.getXmlPullParser().getText().trim();
        Debug.logWarning(session.getId() + " / " + msg, module);
        throw new EOFException(msg);
    }
}
