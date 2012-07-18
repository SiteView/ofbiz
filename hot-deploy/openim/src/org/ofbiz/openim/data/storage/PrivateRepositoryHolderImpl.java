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
package org.ofbiz.openim.data.storage;

import org.ofbiz.base.util.Debug;

import net.java.dev.openim.data.storage.PrivateRepositoryHolder;

public class PrivateRepositoryHolderImpl implements PrivateRepositoryHolder {

    public final static String module = PrivateRepositoryHolderImpl.class.getName();

    @Override
    public String getData(String username, String key) {
        String data = null;
        try {
            String repKey = username + "::" + key;
            //data = (String) repository.get(repKey);
            Debug.logInfo("Get key: " + repKey + " => " + data, module);
        } catch (Exception e) {
            Debug.logWarning("Username " + username + " dont have private for element " + key, module);
        }
        return data;
    }

    @Override
    public void setData(String username, String key, String data) {
        String repKey = username + "::" + key;
        Debug.logInfo("Put key: " + repKey + " => " + data, module);
        //repository.put(repKey, data);
    }
}
