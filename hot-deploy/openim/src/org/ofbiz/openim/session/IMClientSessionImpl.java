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

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.OpenIMContainer;

import net.java.dev.openim.IMPresenceHolder;
import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.data.jabber.IMPresenceImpl;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

public class IMClientSessionImpl extends AbstractIMSession implements IMClientSession {

    public final static String module = IMClientSessionImpl.class.getName();

    private User user;
    private IMPresence presence;

    private IMPresenceHolder presenceHolder;
    
    public IMClientSessionImpl() {
        super.defaultEncoding = "UTF-8";
        presenceHolder = OpenIMContainer.presenceHolder;
        disposed = new Boolean(false);
        synchronized (lastSessionId)  {
            sessionId = lastSessionId.longValue();
            lastSessionId = new Long(sessionId + 1);
        }
    }

    @Override
    public void close() {
        Debug.logInfo("Closing session id " + getId(), module);
        synchronized (disposed) {
            try {
                // set disconnected to all roster friend
                if (user != null && getConnectionType() == IMSession.C2S_CONNECTION) {
                    IMPresence presence = presenceHolder.removePresence(user.getJIDAndRessource());
                    Debug.logInfo("Remove presence jid " + user.getJIDAndRessource(), module);

                    // emit unavailaible to all user
                    presence = new IMPresenceImpl();
                    presence.setFrom(user.getJIDAndRessource());
                    presence.setType(IMPresence.TYPE_UNAVAILABLE);
                    presence.setStatus("Disconnected");
                    List rosterList = user.getRosterItemList();
                    if (rosterList != null) {
                        for (int i = 0, l = rosterList.size(); i < l; i++) {
                            IMRosterItem item = (IMRosterItem) rosterList.get(i);
                            Debug.logInfo("Item " + item, module);
                            IMPresence localPresence = (IMPresence) presence.clone();
                            localPresence.setTo(item.getJID());
                            if (router != null) {
                                router.route(this, localPresence);
                            }
                        }
                    }
                }

                if (router != null) {
                    router.unregisterSession(this);
                }

            }
            catch (Exception e) {
                Debug.logWarning(e, "Session dispose failed (stage1): " + e.getMessage(), module);
            }

            try {
                writeOutputStream("</stream:stream>");

            } catch (Exception e) {
                Debug.logWarning("Session dispose failed (stage2): " + e.getMessage(), module);
            }

            try {
                Debug.logInfo("Session " + sessionId + " closed", module);

                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    outputStreamWriter.close();
                }
            } catch (Exception e) {
                Debug.logWarning(e, "Session dispose failed (stage3): " + e.getMessage(), module);
            }
            Debug.logWarning("Session " + sessionId + " disposed ", module);
        }

        disposed = new Boolean(true);

    }

    @Override
    public int getConnectionType() {
        return C2S_CONNECTION;
    }

    @Override
    public IMPresence getPresence() {
        return this.presence;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public void setPresence(IMPresence presence) {
        this.presence = presence;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
