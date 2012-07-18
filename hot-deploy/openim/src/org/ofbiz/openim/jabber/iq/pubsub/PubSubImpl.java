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
package org.ofbiz.openim.jabber.iq.pubsub;

import net.java.dev.openim.data.jabber.IMIq;
import net.java.dev.openim.session.IMSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.openim.DefaultSessionProcessor;

public class PubSubImpl extends DefaultSessionProcessor {

    public final static String module = PubSubImpl.class.getName();
    
    @Override
    public void process(IMSession session, Object context) throws Exception {
        String type = ((IMIq) context).getType();
        String to = ((IMIq) context).getTo();
        String id = ((IMIq) context).getId();
        super.process(session, context);
    }
}
