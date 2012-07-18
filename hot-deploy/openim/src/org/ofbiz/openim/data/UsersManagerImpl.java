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

import org.ofbiz.openim.data.jabber.UserImpl;

import net.java.dev.openim.data.UsersManager;
import net.java.dev.openim.data.jabber.User;

public class UsersManagerImpl implements UsersManager {
    
    public final static String module = UsersManagerImpl.class.getName();

    @Override
    public User getNewUser() throws Exception {
        return new UserImpl();
    }
}
