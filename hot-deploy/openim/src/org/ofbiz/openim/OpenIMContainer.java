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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import net.java.dev.openim.IMConnectionHandler;
import net.java.dev.openim.IMPresenceHolder;
import net.java.dev.openim.IMPresenceHolderImpl;
import net.java.dev.openim.IMRouter;
import net.java.dev.openim.S2SConnectorManager;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.SubscriptionManager;
import net.java.dev.openim.data.UsersManager;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.DeferrableListRepositoryHolder;
import net.java.dev.openim.data.storage.PrivateRepositoryHolder;
import net.java.dev.openim.data.storage.RosterListRepositoryHolder;
import net.java.dev.openim.log.MessageLogger;
import net.java.dev.openim.log.MessageRecorder;
import net.java.dev.openim.session.SessionsManager;

import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.openim.data.UsersManagerImpl;
import org.ofbiz.openim.data.storage.AccountRepositoryHolderImpl;
import org.ofbiz.openim.data.storage.DeferrableListRepositoryHolderImpl;
import org.ofbiz.openim.data.storage.PrivateRepositoryHolderImpl;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;
import org.ofbiz.openim.data.storage.RoomRepositoryHolderImpl;
import org.ofbiz.openim.data.storage.RosterListRepositoryHolderImpl;
import org.ofbiz.openim.log.MessageLoggerImpl;
import org.ofbiz.openim.log.MessageRecorderImpl;
import org.ofbiz.openim.session.SessionsManagerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class OpenIMContainer implements Container {

    public final static String module = OpenIMContainer.class.getName();

    public static ServerParameters serverParameters = null;
    public static S2SConnectorManager s2sConnectorManager = null;
    public static DeferrableListRepositoryHolder deferrableListHolder = null;
    public static SessionsManager sessionsManager = null;
    public static IMRouter router = null;
    public static IMConnectionHandler connectionHandler = null;
    
    public static UsersManager usersManager = null;
    public static AccountRepositoryHolder accountRepositoryHolder = null;
    public static IMPresenceHolder presenceHolder = null;
    public static RosterListRepositoryHolder rosterListRepositoryHolder = null;
    public static RoomRepositoryHolder roomRepositoryHolder = null;
    
    public static SubscriptionManager subscriptionManager = null;
    public static PrivateRepositoryHolder privateRepository = null;
    
    public static Delegator delegator;
    
    protected String configFile;
    protected static Map<String, String> eventClassNameMap;
    
    
    private int localClientThreadPool = 20;
    private int localServerThreadPool = 10;
    private int localSSLClientThreadPool = 5;
    private int localSSLServerThreadPool = 1;

    private int deliveryRetryDelay = 500;
    private int deliveryMaxRetry = 3;
    private long deliveryMessageQueueTimeout = 3600000;;
    
    private int listenBacklog;
    private String bindAddress;
    
    protected void loadJabberEvents() throws ComponentException {
        File jabberEventsFile = FileUtil.getFile("component://openim/config/jabber-events.xml");
        Document jabberEventsDocument = null;
        try {
            jabberEventsDocument = UtilXml.readXmlDocument(jabberEventsFile.toURL(), true);
            for (Element jabberEventElement : UtilXml.childElementList(jabberEventsDocument.getDocumentElement())) {
                String eventName = jabberEventElement.getAttribute("name");
                String className = jabberEventElement.getAttribute("class");
                eventClassNameMap.put(eventName, className);
            }
        } catch (SAXException e) {
            throw new ComponentException("Error reading the jabber events config file: " + jabberEventsFile, e);
        } catch (ParserConfigurationException e) {
            throw new ComponentException("Error reading the jabber events config file: " + jabberEventsFile, e);
        } catch (IOException e) {
            throw new ComponentException("Error reading the jabber events config file: " + jabberEventsFile, e);
        }
    }
    
    @Override
    public void init(String[] args, String configFile)
            throws ContainerException {
        this.configFile = configFile;
        eventClassNameMap = FastMap.newInstance();
    }

    @Override
    public boolean start() throws ContainerException {
        try {
            // make sure the subclass sets the config name
            if (this.getContainerConfigName() == null) {
                throw new ContainerException("Unknown container config name");
            }
            // get the container config
            ContainerConfig.Container cc = ContainerConfig.getContainer(this.getContainerConfigName(), configFile);

            // get the delegator
            String delegatorName = ContainerConfig.getPropertyValue(cc, "delegator-name", "default");
            delegator = DelegatorFactory.getDelegator(delegatorName);
            
            this.localClientThreadPool = Integer.valueOf(ContainerConfig.getPropertyValue(cc, "localClientThreadPool", "20"));
            this.localServerThreadPool = Integer.valueOf(ContainerConfig.getPropertyValue(cc, "localServerThreadPool", "5"));
            this.localSSLClientThreadPool = Integer.valueOf(ContainerConfig.getPropertyValue(cc, "localSSLClientThreadPool", "10"));
            this.localSSLServerThreadPool = Integer.valueOf(ContainerConfig.getPropertyValue(cc, "localSSLServerThreadPool", "1"));
            
            // initial global objects
            serverParameters = new ServerParametersImpl(cc);
            s2sConnectorManager = new S2SConnectorManagerImpl();
            sessionsManager = new SessionsManagerImpl();
            deferrableListHolder = new DeferrableListRepositoryHolderImpl();
            accountRepositoryHolder = new AccountRepositoryHolderImpl(delegator);
            MessageLogger messageLogger = new MessageLoggerImpl();
            MessageRecorder messageRecorder = new MessageRecorderImpl();
            
            roomRepositoryHolder = new RoomRepositoryHolderImpl();
            router = new IMRouterImpl(serverParameters, sessionsManager, deferrableListHolder, accountRepositoryHolder, roomRepositoryHolder
                                      , s2sConnectorManager, messageLogger, messageRecorder, deliveryRetryDelay, deliveryMaxRetry, deliveryMessageQueueTimeout);
            connectionHandler = new IMConnectionHandlerImpl(serverParameters, sessionsManager, router, s2sConnectorManager);
            
            usersManager = new UsersManagerImpl();
            presenceHolder = new IMPresenceHolderImpl();
            rosterListRepositoryHolder = new RosterListRepositoryHolderImpl(delegator);
            
            subscriptionManager = new SubscriptionManagerImpl(presenceHolder, rosterListRepositoryHolder, serverParameters);
            privateRepository = new PrivateRepositoryHolderImpl();
            
            // load jabber events
            loadJabberEvents();
            
            InetAddress bindTo = null;
            if (bindAddress != null && bindAddress.length() > 0) {
                bindTo = InetAddress.getByName(bindAddress);
            }

            ServerSocketFactory factory = ServerSocketFactory.getDefault();
            ServerSocket clientServerSocket = factory.createServerSocket(serverParameters.getLocalClientPort(), listenBacklog, bindTo);
            new ConnectionManager(clientServerSocket, localClientThreadPool).start();
            ServerSocket serverServerSocket = factory.createServerSocket(serverParameters.getLocalServerPort(), listenBacklog, bindTo);
            new ConnectionManager(serverServerSocket, localServerThreadPool).start();

            // java -Djavax.net.ssl.keyStore=mySrvKeystore -Djavax.net.ssl.keyStorePassword=123456 MyServer
            // keytool -keystore mySrvKeystore -keypasswd 123456 -genkey -keyalg RSA -alias mycert
            ServerSocketFactory sslfactory = SSLServerSocketFactory.getDefault();
            ServerSocket sslClientServerSocket = sslfactory.createServerSocket(serverParameters.getLocalSSLClientPort(), listenBacklog, bindTo);
            new ConnectionManager(sslClientServerSocket, localSSLClientThreadPool).start();

            ServerSocket sslServerServerSocket = sslfactory.createServerSocket(serverParameters.getLocalSSLServerPort(), listenBacklog, bindTo);
            new ConnectionManager(sslServerServerSocket, localSSLServerThreadPool).start();

            String msg = "Server '" + serverParameters.getHostName() + "' initialized on" + " server2server port "
                + serverParameters.getLocalServerPort() + " SSL-server2server port "
                + serverParameters.getLocalSSLServerPort() + " client2server port "
                + serverParameters.getLocalClientPort() + " SSL-client2server port "
                + serverParameters.getLocalSSLClientPort();
            Debug.logInfo(msg , module);
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new ContainerException(e);
        }
        return false;
    }

    @Override
    public void stop() throws ContainerException {
    }
    
    public static String getEventClassName(String eventName) {
        return eventClassNameMap.get(eventName);
    }
    
    public String getContainerConfigName() {
        return "openim-container";
    }

    public class ConnectionManager extends Thread {

        private ServerSocket serverSocket;
        private int poolSize;

        public ConnectionManager(ServerSocket serverSocket, int poolSize) {
            this.serverSocket = serverSocket;
            this.poolSize = poolSize;
        }

        public void run() {
            ExecutorService pool = Executors.newFixedThreadPool(poolSize);
            for (;;) {
                try {
                    pool.execute(new Handler(serverSocket.accept()));
                } catch (IOException ex) {}
            }
            //pool.shutdown();
        }
    }
    
    class Handler implements Runnable {
        private final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                connectionHandler.handleConnection(socket);
            }
            catch (Exception e) {
                Debug.logError(e, module);
            }
        }
    }
}
