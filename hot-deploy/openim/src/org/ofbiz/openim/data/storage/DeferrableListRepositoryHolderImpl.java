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

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;

import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.storage.DeferrableListRepositoryHolder;

public class DeferrableListRepositoryHolderImpl implements DeferrableListRepositoryHolder {

    public final static String module = DeferrableListRepositoryHolderImpl.class.getName();

    @Override
    public List getDeferrableList(String username) {
        List<Transitable> deferrableList = FastList.newInstance();
        try {
            //deferrableList = (List) repository.get( username );
        } catch (Exception e) {
            Debug.logWarning("User " + username + " message list not found", module);
        }
        return deferrableList;
    }

    @Override
    public void setDeferrableList(String username, List deferrableList) {
        if (username != null && deferrableList != null) {
            //repository.put( username, deferrableList );
        }
    }

}
