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
package org.ofbiz.openim.jabber.client;

import java.util.List;

import net.java.dev.openim.IMRouter;
import net.java.dev.openim.data.jabber.IMMessage;
import net.java.dev.openim.jabber.client.Message;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.tools.JIDParser;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;
import org.xmlpull.v1.XmlPullParser;

public class MessageImpl extends DefaultSessionProcessor implements Message {
    
    public final static String module = MessageImpl.class.getName();
    
    protected RoomRepositoryHolder roomRepositoryHolder;
    
    public MessageImpl() {
        roomRepositoryHolder = OpenIMContainer.roomRepositoryHolder;
    }
    
    public void process(final IMSession session, final Object context) throws Exception {
        XmlPullParser xpp = session.getXmlPullParser();
        String type = xpp.getAttributeValue("", "type");
        String to = xpp.getAttributeValue("", "to");
        String from = xpp.getAttributeValue("", "from");

        IMMessage message = new IMMessage();

        if (session instanceof IMClientSession) {
            if (from == null || from.length() == 0) {
                from = ((IMClientSession) session).getUser().getJIDAndRessource();
            }
            // Handle ping your self
            if (to == null || to.length() == 0) {
                to = ((IMClientSession) session).getUser().getJIDAndRessource();
            }
        }
        
        super.process(session, message);
        
        if (IMMessage.TYPE_CHAT.equals(type)) { // Chat
            message.setType(IMMessage.TYPE_CHAT);
            message.setFrom(from);
            message.setTo(to);
            IMRouter router = session.getRouter();
            router.route(session, message);
        
        } else if ("groupchat".equals(type)) { // Group Chat
            if (UtilValidate.isNotEmpty(message.getBody())) {
                // send to all room's occupants
                message.setType("groupchat");
                Room room = roomRepositoryHolder.getRoom(to);
                List<IMSession> registeredSessions = room.getAllRegisteredSession();
                for (IMSession registeredSession : registeredSessions) {
                    from = ((IMClientSession) registeredSession).getUser().getJIDAndRessource();
                    String s = "<message from='" + to + "' to='" + from + "' type='groupchat'>"
                    + "<body>" + message.getBody() + "</body>"
                    + "<x xmlns='http://jabber.org/protocol/muc#user'/>"
                    + "</message>";
                    registeredSession.writeOutputStream(s);
                    
                    // router cannot handle room
                    /*
                    message.setFrom(to);
                    message.setTo(from);
                    IMRouter router = registeredSession.getRouter();
                    router.route(registeredSession, message);
                    */
                }
            }
        }
    }
}
