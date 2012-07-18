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
import net.java.dev.openim.jabber.iq.roster.Group;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.openim.DefaultSessionProcessor;

public class GroupImpl extends DefaultSessionProcessor implements Group {

    public final static String module = GroupImpl.class.getName();

    @Override
    public void process(IMSession session, Object context) throws Exception {
        if (context instanceof IMRosterItem) {
            ((IMRosterItem) context).setGroup(session.getXmlPullParser().getText().trim());
        } else if (context instanceof IMIq) {
        
        }
    }
}
