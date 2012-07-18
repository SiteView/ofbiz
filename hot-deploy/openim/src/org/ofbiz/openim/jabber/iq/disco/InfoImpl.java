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
package org.ofbiz.openim.jabber.iq.disco;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;

public class InfoImpl extends DefaultSessionProcessor {
    
    public final static String module = InfoImpl.class.getName();
    
    protected RoomRepositoryHolder roomRepositoryHolder = null;
    
    public InfoImpl() {
        roomRepositoryHolder = OpenIMContainer.roomRepositoryHolder;
    }

    @Override
    public void process(IMSession session, Object context) throws Exception {
        String iqId = ((IMIq)context).getId();
        String from = ((IMIq)context).getFrom();
        String to = ((IMIq)context).getTo();
        
        Room room = roomRepositoryHolder.getRoom(to);
        if (UtilValidate.isNotEmpty(room)) {
        
            String s = "<iq from='" + to + "' to='" + from + "' type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' >"
            + "<query xmlns='http://jabber.org/protocol/disco#info'>"
            + "<identity category='conference' name='" + room.getName() + "' type='text'/>"
            + "<feature var='http://jabber.org/protocol/muc'/>"
            + "</query>"
            + "</iq>";
            session.writeOutputStream(s);
        } else {
            String s = "<iq from='" + to + "' to='" + from + "' type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' >"
            + "<query xmlns='http://jabber.org/protocol/disco#info'>"
            + "<feature var='http://jabber.org/protocol/muc'/>"
            + "</query>"
            + "</iq>";
            session.writeOutputStream(s);
            Debug.logWarning("Not found chat room : " + to, module);
        }
    }
}
