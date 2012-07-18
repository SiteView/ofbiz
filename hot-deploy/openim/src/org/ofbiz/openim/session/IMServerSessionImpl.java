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

import net.java.dev.openim.session.IMServerSession;

public class IMServerSessionImpl extends AbstractIMSession implements IMServerSession {

    public final static String module = IMServerSessionImpl.class.getName();

    @Override
    public void close() {
    }

    @Override
    public int getConnectionType() {
        return 0;
    }

    @Override
    public boolean getDialbackValid() {
        return false;
    }

    @Override
    public String getDialbackValue() {
        return null;
    }

    @Override
    public String getRemoteHostname() {
        return null;
    }

    @Override
    public IMServerSession getTwinSession() {
        return null;
    }

    @Override
    public void setDialbackValid(boolean arg0) {
    }

    @Override
    public void setDialbackValue(String arg0) {
    }

    @Override
    public void setRemoteHostname(String arg0) {
    }

    @Override
    public void setTwinSession(IMServerSession arg0) {
    }

}
