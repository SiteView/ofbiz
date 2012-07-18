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
package org.ofbiz.openim;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;

import org.ofbiz.base.util.Debug;
import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.IMConnectionHandler;
import net.java.dev.openim.IMRouter;
import net.java.dev.openim.S2SConnectorManager;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.session.SessionsManager;

public class IMConnectionHandlerImpl extends DefaultSessionProcessor implements IMConnectionHandler {
    
    public final static String module = IMConnectionHandlerImpl.class.getName();

    private SessionsManager sessionsManager;
    private IMRouter router;
    private S2SConnectorManager s2sConnectorManager;
    private ServerParameters serverParameters;
    
    public IMConnectionHandlerImpl(ServerParameters serverParameters, SessionsManager sessionsManager, IMRouter router, S2SConnectorManager s2sConnectorManager) {
        this.serverParameters = serverParameters;
        this.s2sConnectorManager = s2sConnectorManager;
        this.router = router;
        this.sessionsManager = sessionsManager;
        this.s2sConnectorManager.setConnectionHandler(this);
        this.router.setS2SConnectorManager(s2sConnectorManager);
    }

    @Override
    public void handleConnection(Socket socket) throws IOException,
            ProtocolException {
        Debug.logInfo("Connection from :" + socket.getRemoteSocketAddress(), module);

        IMSession session = null;
        try {
            if (socket.getLocalPort() == serverParameters.getLocalClientPort()
                || socket.getLocalPort() == serverParameters.getLocalSSLClientPort()) {
                session = sessionsManager.getNewClientSession();
            } else {
                session = sessionsManager.getNewServerSession();
            }

            session.setRouter(router);

            Debug.logInfo("######## [" + serverParameters.getHostName() + "] New session instance: " + session.getId(), module);
            session.setup(socket);
            //session.setHostname(serverParameters.getHostName());

            //socket.setKeepAlive(true);

            final XmlPullParser xpp = session.getXmlPullParser();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.START_DOCUMENT) {
                eventType = xpp.getEventType();
            }

            String s = "<?xml version='1.0' encoding='" + session.getEncoding() + "' ?>";
            session.writeOutputStream(s);

            process(session);
        } catch (java.net.SocketException e) {
            Debug.logWarning(e.getMessage(), module);
        } catch (java.io.EOFException e) {
            Debug.logInfo(e.getMessage(), module);
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new IOException(e.getMessage());
        } finally {
            try {
                if (session != null) {
                    if (sessionsManager != null) {
                        Debug.logInfo("Release session " + session.getId(), module);
                        sessionsManager.release(session);
                    }
                } if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {
                Debug.logError(e, module);
                throw new java.io.IOException(e.getMessage());
            }
        }
        Debug.logInfo("Disconnected session " + session.getId(), module);
    }

    public void dispose() {
        Debug.logInfo("Disposing Router", module);
        router.releaseSessions();
        sessionsManager.releaseSessions();
    }
}
