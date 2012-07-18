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

import net.java.dev.openim.session.IMSession;

public interface Room {

    public List<IMSession> getAllRegisteredSession();
    
    public void registerSession(IMSession session);
    
    public void setName(String name);
    
    public String getName();
    
    public void setJId(String jId);
    
    public String getJId();
}
