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
package org.ofbiz.openim.jabber.server;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.jabber.server.Iq;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.xmlpull.v1.XmlPullParser;

public class IqImpl extends DefaultSessionProcessor implements Iq {
    
    public final static String module = IqImpl.class.getName();
    
    public void process(final IMSession session, final Object context) throws Exception {
    
        XmlPullParser xpp = session.getXmlPullParser();
    
        for (int i = 0, l = xpp.getAttributeCount(); i < l; i++)
        {
            Debug.logInfo("Attribut ns: " + xpp.getAttributeNamespace(i) + " name: " + xpp.getAttributeName(i)
                                   + " value: " + xpp.getAttributeValue(i), module);
        }
    
        IMIq iq = new IMIq();
        iq.setId(xpp.getAttributeValue("", "id"));
        iq.setType(xpp.getAttributeValue("", "type"));
        iq.setTo(xpp.getAttributeValue("", "to"));
        iq.setFrom(xpp.getAttributeValue("", "from"));
        Debug.logInfo("Got IQ " + iq, module);
        super.process(session, iq);
    
    }
}
