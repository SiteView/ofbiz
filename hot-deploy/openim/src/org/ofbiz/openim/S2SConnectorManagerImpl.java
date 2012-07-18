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

import net.java.dev.openim.IMConnectionHandler;
import net.java.dev.openim.S2SConnectorManager;
import net.java.dev.openim.session.IMServerSession;

public class S2SConnectorManagerImpl implements S2SConnectorManager {

    public final static String module = S2SConnectorManagerImpl.class.getName();

    @Override
    public IMServerSession getCurrentRemoteSession(String arg0)
            throws Exception {
        return null;
    }

    @Override
    public IMServerSession getRemoteSessionWaitForValidation(String arg0,
            long arg1) throws Exception {
        return null;
    }

    @Override
    public void setConnectionHandler(IMConnectionHandler connectionHandler) {
    }

    @Override
    public void verifyRemoteHost(String arg0, String arg1, String arg2,
            IMServerSession arg3) throws Exception {
    }

}
