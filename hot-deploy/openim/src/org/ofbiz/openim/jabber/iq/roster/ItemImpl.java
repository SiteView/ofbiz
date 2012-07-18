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
package org.ofbiz.openim.jabber.iq.roster;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.jabber.iq.roster.Item;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.xmlpull.v1.XmlPullParser;

public class ItemImpl extends DefaultSessionProcessor implements Item {

    public final static String module = ItemImpl.class.getName();

    @Override
    public void process(IMSession session, Object context) throws Exception {
        XmlPullParser xpp = session.getXmlPullParser();
        if (context instanceof IMRosterItem) {
            IMRosterItem rosterItem = (IMRosterItem) context;
            rosterItem.setName( xpp.getAttributeValue( "", "name" ) );
            rosterItem.setJID( xpp.getAttributeValue( "", "jid" ) );
            rosterItem.setSubscription( xpp.getAttributeValue( "", "subscription" ) );
            rosterItem.setAsk( xpp.getAttributeValue( "", "ask" ) );
            Debug.logInfo("add roster item:" + rosterItem, module);
        } else if (context instanceof IMIq) {
        
        }
        super.process( session, context );
    }
}
