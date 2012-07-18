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

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.jabber.Streams;
import net.java.dev.openim.session.IMServerSession;
import net.java.dev.openim.session.IMSession;

public class StreamsImpl extends DefaultSessionProcessor implements Streams {

    public final static String module = StreamsImpl.class.getName();
    
    protected String namespace;

    @Override
    public void process(IMSession session, Object context) throws Exception {
        final XmlPullParser xpp = session.getXmlPullParser();
        namespace = xpp.getNamespace(null);
        processAttribute(session, context);
        if (session instanceof IMServerSession) {
            Debug.logInfo("Start stream " + ((IMServerSession) session).getRemoteHostname() + " id " + session.getId(), module);
        }
        super.process(session, context);
        if (session instanceof IMServerSession) {
            Debug.logInfo("Stop stream " + ((IMServerSession) session).getRemoteHostname() + " id " + session.getId(), module);
        }
    }
    
    public void processAttribute(final IMSession session, final Object context) throws Exception {
    
        final XmlPullParser xpp = session.getXmlPullParser();
        String to = xpp.getAttributeValue("", "to");
        String from = xpp.getAttributeValue("", "from");
    
        if (from == null || from.length() == 0) {
            Debug.logInfo("from attribut not specified in stream declaration", module);
        } else {
            if (session instanceof IMServerSession) {
                ((IMServerSession) session).setRemoteHostname(from);
            }
        }
    
        if (session.getConnectionType() == IMSession.S2S_L2R_CONNECTION) {
            Debug.logInfo("Local to Remote connection " + to, module);
        } else {
            String s = "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' " + "id='" + session.getId()
                + "' ";
            if (session.getConnectionType() == IMSession.C2S_CONNECTION) {
                s += "xmlns='jabber:client' ";
            } else if (session.getConnectionType() == IMSession.S2S_R2L_CONNECTION) {
                s += "xmlns='jabber:server' xmlns:db='jabber:server:dialback' ";
            }
            s += "from='" + OpenIMContainer.serverParameters.getHostName() + "'>";
            session.writeOutputStream(s);
        }
    }

    @Override
    public String getNamespace() {
        return namespace;
    }
}
