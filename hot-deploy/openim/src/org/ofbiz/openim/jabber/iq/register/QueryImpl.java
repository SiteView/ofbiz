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
package org.ofbiz.openim.jabber.iq.register;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.AccountImpl;

import net.java.dev.openim.IMRouter;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.UsersManager;
import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.jabber.iq.register.Query;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

public class QueryImpl extends DefaultSessionProcessor implements Query {

    public final static String module = QueryImpl.class.getName();
    
    private ServerParameters    serverParameters;
    private UsersManager usersManager;
    private AccountRepositoryHolder accountRepository;
    
    public QueryImpl() {
        serverParameters = OpenIMContainer.serverParameters;
        usersManager = OpenIMContainer.usersManager;
        accountRepository = OpenIMContainer.accountRepositoryHolder;
    }

    public void process(final IMSession session, final Object context) throws Exception {
        IMClientSession clientSession = (IMClientSession) session;
        
        User currentUser = clientSession.getUser();
        User user = usersManager.getNewUser();
        clientSession.setUser(user);
        
        Map<Integer,Boolean> contextMap = new HashMap<Integer,Boolean>();
        contextMap.put(CTX_SHOULD_REMOVE, Boolean.FALSE);
        super.process(session, contextMap);

        String iqId = ((IMIq)context).getId();
        String type = ((IMIq)context).getType();
        
        if(IMIq.TYPE_GET.equals(type)) { // GET
            String s = "<iq type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' from='" + serverParameters.getHostName() + "'>"
             + "<query xmlns='jabber:iq:register'>"
             + "<instructions>Choose a username and password to register with this service.</instructions>"
             + "<password/><username/>"
             + "</query></iq>";
            session.writeOutputStream(s);
        } else if(IMIq.TYPE_SET.equals(type)) { // SET
            
            Boolean shouldRemove = (Boolean)contextMap.get(CTX_SHOULD_REMOVE);
            if(shouldRemove.booleanValue()){
                accountRepository.removeAccount(currentUser.getName());
                String s = "<iq type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' />";
                session.writeOutputStream(s);
                clientSession.setUser(null);
            } else { // no remove
                Account existingAccount = accountRepository.getAccount(user.getName());
                if(existingAccount == null) {
                    setAccount(user);

                    IMRouter router = session.getRouter();
                    router.registerSession(clientSession);

                    String s = "<iq type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' />";
                    session.writeOutputStream(s);
                } else if (currentUser != null) { // account already exists and we are logged
                    String s = null;
                    //if(currentUser.getName().equals(user.getName())) {
                    if (UtilValidate.isNotEmpty(user.getName())) {
                        setAccount(user);
                        s = "<iq type='" + IMIq.TYPE_RESULT + "' id='" + iqId + "' />";
                    } else {
                        clientSession.setUser(currentUser);
                        s = "<iq type='" + IMIq.TYPE_ERROR + "' id='" + iqId + "' />";
                    }
                    session.writeOutputStream(s);
                } else{ // abnormal sitatuation sending error
                    String s = "<iq type='" + IMIq.TYPE_ERROR  +"' id='" + iqId + "' />";
                    session.writeOutputStream(s);
                }
            }
        }
    }
    
    private void setAccount(User user) throws Exception{
        Account account = new AccountImpl();
        account.setName(user.getName());
        account.setPassword(user.getPassword());
        accountRepository.setAccount(account);
    }
}
