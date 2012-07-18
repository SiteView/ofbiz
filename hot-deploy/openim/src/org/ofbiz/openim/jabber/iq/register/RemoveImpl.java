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

import java.util.Map;

import org.ofbiz.openim.DefaultSessionProcessor;

import net.java.dev.openim.jabber.iq.register.Query;
import net.java.dev.openim.jabber.iq.register.Remove;
import net.java.dev.openim.session.IMSession;

public class RemoveImpl extends DefaultSessionProcessor implements Remove {
    
    public final static String module = RemoveImpl.class.getName();
    
    public void processText(final IMSession session, final Object context) throws Exception {
        ((Map<Integer,Boolean>) context).put(Query.CTX_SHOULD_REMOVE, Boolean.FALSE);
    }
}
