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

import java.util.List;

import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

import net.java.dev.openim.ServerParameters;

public class ServerParametersImpl implements ServerParameters {

    public final static String module = ServerParametersImpl.class.getName();
    
    private ContainerConfig.Container container = null;
    
    public ServerParametersImpl(ContainerConfig.Container container) {
        this.container = container;
    }

    @Override
    public String getHostName() {
        return ContainerConfig.getPropertyValue(container, "hostnames", "localhost").split(",")[0];
    }

    @Override
    public List getHostNameList() {
        return UtilMisc.toListArray(ContainerConfig.getPropertyValue(container, "hostnames", "localhost").split(","));
    }

    @Override
    public int getLocalClientPort() {
        return Integer.valueOf(ContainerConfig.getPropertyValue(container, "localClientPort", "5222"));
    }

    @Override
    public int getLocalSSLClientPort() {
        return Integer.valueOf(ContainerConfig.getPropertyValue(container, "localSSLClientPort", "5223"));
    }

    @Override
    public int getLocalSSLServerPort() {
        return Integer.valueOf(ContainerConfig.getPropertyValue(container, "localSSLServerPort", "5269"));
    }

    @Override
    public int getLocalServerPort() {
        return Integer.valueOf(ContainerConfig.getPropertyValue(container, "localServerPort", "5270"));
    }

    @Override
    public int getRemoteServerPort() {
        return Integer.valueOf(ContainerConfig.getPropertyValue(container, "remoteServerPort", "5269"));
    }

}
