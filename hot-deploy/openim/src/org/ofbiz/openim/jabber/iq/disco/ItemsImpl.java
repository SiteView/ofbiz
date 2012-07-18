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

import java.util.List;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;

public class ItemsImpl extends DefaultSessionProcessor {

    public final static String module = ItemsImpl.class.getName();
    
    protected RoomRepositoryHolder roomRepositoryHolder = null;
    
    public ItemsImpl() {
        roomRepositoryHolder = OpenIMContainer.roomRepositoryHolder;
    }
    
    @Override
    public void process(IMSession session, Object context) throws Exception {
        String iqId = ((IMIq)context).getId();
        String type = ((IMIq)context).getType();
        String from = ((IMIq)context).getFrom();
        String to = ((IMIq)context).getTo();
        
        super.process(session, context);
        
        if(IMIq.TYPE_GET.equals(type)) { // GET
            String s = "<iq from='" + to + "' to='" + from + "' type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "'>";
            s += "<query xmlns='http://jabber.org/protocol/disco#items'>";
            
            List<Room> rooms = roomRepositoryHolder.getRooms();
            for(Room room : rooms) {
                s += "<item jid='" + room.getJId() + "'";
                s += " name='" + room.getName() + "'/>";
            }
            
            s += "</query></iq>";
            
            session.writeOutputStream(s);
        }
    }
}
