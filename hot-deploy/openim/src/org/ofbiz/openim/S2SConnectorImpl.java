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

import net.java.dev.openim.IMConnectionHandler;
import net.java.dev.openim.IMRouter;
import net.java.dev.openim.S2SConnector;
import net.java.dev.openim.session.IMServerSession;
import net.java.dev.openim.session.SessionsManager;

public class S2SConnectorImpl implements S2SConnector {

    public final static String module = S2SConnectorImpl.class.getName();

    @Override
    public IMServerSession getSession() throws Exception {
        return null;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void run() {
    }

    @Override
    public void sendResult() throws IOException {
    }

    @Override
    public void sendVerify(String arg0, String arg1) throws IOException {
    }

    @Override
    public void setIMConnectionHandler(IMConnectionHandler arg0) {
    }

    @Override
    public void setRouter(IMRouter arg0) {
    }

    @Override
    public void setSessionsManager(SessionsManager arg0) {
    }

    @Override
    public void setToHostname(String arg0) {
    }

}
