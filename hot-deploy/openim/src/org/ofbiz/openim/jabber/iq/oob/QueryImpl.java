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
package org.ofbiz.openim.jabber.iq.oob;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.jabber.iq.oob.Query;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.openim.DefaultSessionProcessor;

public class QueryImpl extends DefaultSessionProcessor implements Query {
    
    public final static String module = QueryImpl.class.getName();
    
    @Override
    public void process(IMSession session, Object context) throws Exception {
        String data = serialize(session.getXmlPullParser()).toString();

        IMIq iq = ((IMIq) context);
        iq.setFrom(((IMClientSession) session).getUser().getJIDAndRessource());
        iq.setStringData(data);
        session.getRouter().route(session, iq);
    }
}
