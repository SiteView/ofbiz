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
package org.ofbiz.openim.jabber.iq.muc;

import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.tools.JIDParser;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;

public class XImpl extends DefaultSessionProcessor {
    
    public final static String module = XImpl.class.getName();
    
    private RoomRepositoryHolder roomRepositoryHolder;
    
    public XImpl() {
        roomRepositoryHolder = OpenIMContainer.roomRepositoryHolder;
    }
    
    @Override
    public void process(IMSession session, Object context) throws Exception {
        String from = ((IMPresence) context).getFrom();
        String to = ((IMPresence) context).getTo();
        super.process(session, context);
        
        // register the session to the room
        Room room = roomRepositoryHolder.getRoom(JIDParser.getJID(to));
        if (UtilValidate.isNotEmpty(room)) {
            room.registerSession(session);
            
            // send presence back to the session
            String s = "<presence from='" + to + "' to='" + from + "'>"
            + "<x xmlns='http://jabber.org/protocol/muc#user'>"
            + "<item affiliation='owner' role='moderator'/>"
            + "<status code='110'/>"
            + "<status code='201'/>"
            + "</x>"
            + "</presence>";
            session.writeOutputStream(s);
        } else {
            Debug.logWarning("Not found room: " + JIDParser.getJID(to), module);
        }
    }
}
