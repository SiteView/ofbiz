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
package org.ofbiz.openim.data;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

import javolution.util.FastMap;

import net.java.dev.openim.session.IMSession;

public class RoomImpl implements Room {
    
    protected Map<Long, IMSession> sessions = null;
    protected String name = null;
    protected String jid = null;
    
    public RoomImpl() {
        sessions = FastMap.newInstance();
    }
    
    @Override
    public void setJId(String jid) {
        this.jid = jid;
    }
    
    @Override
    public String getJId() {
        return jid;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public List<IMSession> getAllRegisteredSession() {
        return UtilMisc.toList(sessions.values());
    }
    
    @Override
    public void registerSession(IMSession session) {
        sessions.put(session.getId(), session);
    }
}
