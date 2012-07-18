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
package org.ofbiz.openim.data.jabber;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.OpenIMContainer;

import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.RosterListRepositoryHolder;

public class UserImpl implements User {

    public final static String module = UserImpl.class.getName();

    private AccountRepositoryHolder accountHolder;
    private RosterListRepositoryHolder rosterListHolder;

    private String name;
    private String hostname;
    private String password;
    private String digest;
    private String resource;
    
    public UserImpl() {
        accountHolder = OpenIMContainer.accountRepositoryHolder;
        rosterListHolder = OpenIMContainer.rosterListRepositoryHolder;
    }

    @Override
    public void authenticate(String sessionId) throws Exception {
        Debug.logInfo("Authenticating " + getJID() + " digest " + digest, module);

        Account account = accountHolder.getAccount(name);
        if (account == null) {
            throw new Exception("Unknow JID " + getJIDAndRessource());
        }
        
        // no password assuming digest
        if (password == null) {
            account.authenticate(Account.AUTH_TYPE_DIGEST, digest, sessionId);
        } else { // password available: plain authentification
            account.authenticate(Account.AUTH_TYPE_PLAIN, password, sessionId);
        }
    }

    @Override
    public String getDigest() {
        return digest;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getJID() {
        String jID = name;
        if (hostname != null) {
            jID += "@" + hostname;
        }
        return jID;
    }

    @Override
    public String getJIDAndRessource() {
        return getJID() + "/" + resource;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameAndRessource() {
        return name + "/" + resource;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public List getRosterItemList() {
        return rosterListHolder.getRosterList(name);
    }

    @Override
    public boolean isAuthenticationTypeSupported(int type) {
        Account account = accountHolder.getAccount(name);
        boolean isAuthentication = false;
        if (account == null) {
            Debug.logWarning("Account " + name + " does not exist", module);
        } else {
            isAuthentication = account.isAuthenticationTypeSupported(type);
        }
        return isAuthentication;
    }

    @Override
    public void setDigest(String digest) {
        this.digest = digest;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public void setRosterItemList(List rosterlist) {
        rosterListHolder.setRosterList(name, rosterlist);
    }
    
    @Override
    public String toString() {
        return getJIDAndRessource();
    }
}
