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
package org.ofbiz.openim.session;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMServerSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.session.SessionsManager;

public class SessionsManagerImpl implements SessionsManager {

    public final static String module = SessionsManagerImpl.class.getName();

    private Map<Long, IMSession> activeSessions;
    
    public SessionsManagerImpl() {
        activeSessions = new HashMap<Long, IMSession>();
    }
    
    @Override
    public IMClientSession getNewClientSession() throws Exception {
        IMClientSession session = new IMClientSessionImpl();
        synchronized (activeSessions) {
            activeSessions.put(new Long(session.getId()), session);
        }
        return session;
    }

    @Override
    public IMServerSession getNewServerSession() throws Exception {
        return new IMServerSessionImpl();
    }

    @Override
    public void release(IMSession session) {
        if (session != null) {
            try {
                if (!session.isClosed()) {
                    session.close();
                } else {
                    Debug.logWarning("Session " + session.getId() + " already diposed", module);
                }
            } catch (Exception e) {
                Debug.logWarning(e, "Session " + session.getId() + " release failure " + e.getMessage(), module);
            }
            // Remove from sessionsMap
            synchronized (activeSessions) {
                activeSessions.remove(new Long(session.getId()));
            }
        }
    }

    @Override
    public void releaseSessions() {
        Debug.logInfo("Releasing sessions ", module);
        // Avoid concurrent mod
        Map<Long, IMSession> clonedSessions = new HashMap<Long, IMSession>(activeSessions);
        Iterator it = clonedSessions.values().iterator();
        while (it.hasNext()) {
            IMSession sess = (IMSession) it.next();
            release(sess);
        }
    }

}
