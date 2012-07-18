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
package org.ofbiz.openim.jabber.iq.browse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;
import org.ofbiz.openim.OpenIMContainer;

import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.jabber.iq.browse.Query;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

public class QueryImpl extends DefaultSessionProcessor implements Query {

    public final static String module = QueryImpl.class.getName();

    private ServerParameters serverParameters;
    
    public QueryImpl() {
        serverParameters = OpenIMContainer.serverParameters;
    }

    public void process(final IMSession session, final Object context) throws Exception{
        
        IMClientSession clientSession = (IMClientSession)session;
        String type = ((IMIq)context).getType();

        
        if(IMIq.TYPE_GET.equals(type)) { // GET
            get(clientSession, context);
        } else if(IMIq.TYPE_SET.equals(type)) { // SET
            set(clientSession, context);
        }
    }
    
    
    private void get(final IMClientSession session, Object context) throws Exception {
        
        String iqId = ((IMIq)context).getId();
        
        String s = "<iq type='result'";
        s += " from='" + serverParameters.getHostName() + "'";
        s += " to='" + session.getUser().getJIDAndRessource() + "'";
        s += " id='" + iqId + "'";
        s += ">";
        s += "<service jid='" + serverParameters.getHostName() + "' name='OpenIM Server' type='jabber' xmlns='jabber:iq:browse'>"; 
        s += "<item category='service' jid='" + serverParameters.getHostName() + "' name='OpenIM User Directory' type='jud'>";
        s += "<ns>jabber:iq:register</ns>";
        s += "</item>";
        s += "</service></iq>";

        session.writeOutputStream(s);
    }
    
    private void set(IMClientSession session, Object context) throws Exception {
        Debug.logWarning("Skipping jabber:iq:browse:query set", module);  
    }
}
