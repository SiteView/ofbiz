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

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;

import net.java.dev.openim.SessionProcessor;

public class SessionProcessorFactory {

    public final static String module = SessionProcessorFactory.class.getName();
    
    private static Map<String, SessionProcessor> processorMap = FastMap.newInstance();
    
    public static SessionProcessor getSessionProcessor(String eventName) throws Exception {
        SessionProcessor processor = processorMap.get(eventName);
        if (UtilValidate.isEmpty(processor)) {
            String processorClassName = OpenIMContainer.getEventClassName(eventName);
            if (UtilValidate.isNotEmpty(processorClassName)) {
                processor = (SessionProcessor) Class.forName(processorClassName).newInstance();
                processorMap.put(eventName, processor);
            }
        }
        return processor;
    }
}
