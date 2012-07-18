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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.storage.RoomRepositoryHolder;

import net.java.dev.openim.IMRouter;
import net.java.dev.openim.S2SConnectorManager;
import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.Deferrable;
import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.DeferrableListRepositoryHolder;
import net.java.dev.openim.log.MessageLogger;
import net.java.dev.openim.log.MessageRecorder;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.tools.JIDParser;

public class IMRouterImpl implements IMRouter {

    public final static String module = IMRouterImpl.class.getName();

    private ServerParameters serverParameters;
    private SessionsManager sessionsManager;
    private DeferrableListRepositoryHolder deferrableListHolder;
    private AccountRepositoryHolder accountHolder;
    private RoomRepositoryHolder roomRepositoryHolder;
    private S2SConnectorManager s2sConnectorManager;
    private MessageLogger messageLogger;
    private MessageRecorder messageRecorder;

    private int deliveryRetryDelay;
    private int deliveryMaxRetry;
    private long deliveryMessageQueueTimeout;

    private Map<String,IMSession> sessionMap;
    private Map<String,RemoteDeliveryThreadPerHost> remoteDeliveryThreadMap;
    
    public IMRouterImpl(ServerParameters serverParameters, SessionsManager sessionsManager, DeferrableListRepositoryHolder deferrableListHolder
                        , AccountRepositoryHolder accountHolder, RoomRepositoryHolder roomRepositoryHolder, S2SConnectorManager s2sConnectorManager, MessageLogger messageLogger, MessageRecorder messageRecorder
                        , int deliveryRetryDelay, int deliveryMaxRetry, long deliveryMessageQueueTimeout) {
        this.serverParameters = serverParameters;
        this.sessionsManager = sessionsManager;
        this.deferrableListHolder = deferrableListHolder;
        this.accountHolder = accountHolder;
        this.roomRepositoryHolder = roomRepositoryHolder;
        this.s2sConnectorManager = s2sConnectorManager;
        this.messageLogger = messageLogger;
        this.messageRecorder = messageRecorder;
        
        this.deliveryRetryDelay = deliveryRetryDelay;
        this.deliveryMaxRetry = deliveryMaxRetry;
        this.deliveryMessageQueueTimeout = deliveryMessageQueueTimeout;
        
        sessionMap = FastMap.newInstance();
        remoteDeliveryThreadMap = FastMap.newInstance();
    }

    private void enqueueRemoteDelivery(Transitable transitable, IMSession session) {
        TransitableAndSession tas = new TransitableAndSession(transitable, session);

        final String hostname = tas.getHostname();
        synchronized (remoteDeliveryThreadMap) {
            RemoteDeliveryThreadPerHost remoteDeliveryThread = (RemoteDeliveryThreadPerHost) remoteDeliveryThreadMap.get(hostname);
            if (remoteDeliveryThread == null) {
                // should get from a pool (to implem later)
                if (hostname == null) {
                    Debug.logWarning("Absurd hostname for Transitable " + transitable, module);
                }

                remoteDeliveryThread = new RemoteDeliveryThreadPerHost(hostname);
                remoteDeliveryThread.enqueue(tas);

                remoteDeliveryThread.start();
                remoteDeliveryThreadMap.put(hostname, remoteDeliveryThread);
            } else {
                remoteDeliveryThread.enqueue(tas);
            }
        }
    }
    
    private IMClientSession getRegisteredSession(final String name) {
        IMClientSession session = (IMClientSession) sessionMap.get(name);
        Debug.logInfo("getting session for " + name + " having map key " + sessionMap.keySet(), module);
        if (session == null) {
            String username = name;
            if (name.indexOf('/') > 0) {
                // we have a ressource => get the login
                username = JIDParser.getName(name);
            }

            //TODO: check if correct (was name)
            List list = getAllRegisteredSession(username);
            for (int i = 0, l = list.size(); i < l; i++) {
                IMClientSession s = (IMClientSession) list.get(i);
                if (session == null || (getPriorityNumber(s) > getPriorityNumber(session))) {
                    session = s;
                    Debug.logInfo("Select session " + s, module);
                }
            }
        }
        return session;
    }

    private final int getPriorityNumber(IMClientSession session) {
        int priorityNumber = 0;
        if (session.getPresence() != null) {
            String priorityStr = session.getPresence().getPriority();

            if (priorityStr != null) {
                try {
                    priorityNumber = Integer.parseInt(priorityStr);
                } catch (Exception e) {
                    Debug.logError(e, e.getMessage(), module);
                }
            }
        }
        return priorityNumber;
    }
    
    @Override
    public List getAllRegisteredSession(String name) {
        List<IMSession> sessions = new ArrayList<IMSession>(1);
        Iterator<String> keyIter = sessionMap.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            Debug.logInfo("Check if " + name + " could match " + key, module); 
            if (key.startsWith(name)) {
                sessions.add(sessionMap.get(key));
            }
        }
        return sessions;
    }

    @Override
    public S2SConnectorManager getS2SConnectorManager() {
        return s2sConnectorManager;
    }

    @Override
    public void registerSession(IMClientSession session) {
        final User user = session.getUser();

        if (session.getConnectionType() == IMSession.C2S_CONNECTION && user != null) {
            Debug.logInfo("Session map before register : " + sessionMap, module);
            Debug.logInfo("Register session user: " + user.getNameAndRessource() + " session id " + session.getId(), module);
            try {
                IMSession prevSession = (IMSession) sessionMap.get(user.getNameAndRessource());
                if (prevSession != null) {
                    Debug.logInfo("Allready register session: " + prevSession.getId(), module);
                    sessionsManager.release(prevSession);
                }
            } catch (Exception e) {
                Debug.logError(e, e.getMessage(), module);
            }
            
            synchronized (sessionMap) {
                sessionMap.put(user.getNameAndRessource(), session);
            }

            try {
                deliverQueueMessage(session, user.getName());
            } catch (Exception e) {
                Debug.logWarning(e, "Failed to deliver queue message " + e.getMessage(), module);
            }
        }
    }

    @Override
    public void releaseSessions() {
        Debug.logInfo("Releasing sessions ", module);
        synchronized (sessionMap) {
            Iterator it = sessionMap.values().iterator();
            while (it.hasNext()) {
                IMSession sess = (IMSession) it.next();
                sessionsManager.release(sess);
            }
        }
    }

    @Override
    public void route(IMSession currentSession, Transitable transit) throws IOException {
        final String to = transit.getTo();
        //final String from = transit.getFrom();
        final String toHostname = JIDParser.getHostname(to);

        if (serverParameters.getHostNameList().contains(toHostname)) { // local delivery

            IMClientSession session = getRegisteredSession(JIDParser.getNameAndRessource(to));

            if (session == null) {
                if (transit instanceof Deferrable) {

                    String username = JIDParser.getName(to);
                    Account account = accountHolder.getAccount(username);
                    Room room = roomRepositoryHolder.getRoom(to);
                    boolean isRoom = UtilValidate.isNotEmpty(room);
                    if (account == null) {
                        Debug.logInfo(to + " unknown user. Transit value was: " + transit, module);
                        String from = transit.getFrom();
                        transit.setError("Not Found");
                        transit.setErrorCode(404);
                        transit.setFrom(to);
                        transit.setTo(from);
                        transit.setType(Transitable.TYPE_ERROR);

                        messageLogger.log(transit);
                        currentSession.writeOutputStream(transit.toString());
                        messageLogger.log(transit);
                        messageRecorder.record(transit);

                    } else if (!isRoom) {
                        Debug.logInfo(to + " is not connected for getting message, should store for offline dispatch. Transit value was: " + transit, module);
                        List<Transitable> list = deferrableListHolder.getDeferrableList(username);
                        if (list == null) {
                            list = new ArrayList<Transitable>();
                        }
                        list.add(transit);
                        deferrableListHolder.setDeferrableList(username, list);
                    }
                }
            } else {
                transit.setTo(session.getUser().getJIDAndRessource());
                session.writeOutputStream(transit.toString());
                messageLogger.log(transit);
                messageRecorder.record(transit);
            }
        } else { // remote delivery
            Debug.logInfo("Remote delivery to " + transit.getTo(), module);
            enqueueRemoteDelivery(transit, currentSession);
            Debug.logInfo("Enqueued to " + transit.getTo(), module);
            //new Thread(new AsyncDeliverer(transit, toHostname, currentSession)).start();
        }
    }

    @Override
    public void setS2SConnectorManager(S2SConnectorManager s2sConnectorManager) {
        this.s2sConnectorManager = s2sConnectorManager;
    }

    @Override
    public void unregisterSession(IMClientSession session) {
        if (session instanceof IMClientSession) {
            User user = ((IMClientSession) session).getUser();
            if (user != null) {
                Debug.logInfo("Unregister register session user: " + user.getJIDAndRessource() + " session id " + session.getId(), module);
                synchronized (sessionMap) {                    
                    sessionMap.remove(user.getNameAndRessource());
                }
            }
        }
    }
    
    public void deliverQueueMessage(IMSession currentSession, String username) throws IOException {
        final List list = deferrableListHolder.getDeferrableList(username);
        if (list != null) {
            for (int i = 0, l = list.size(); i < l; i++) {
                route(currentSession, (Transitable) list.get(i));
            }
        }
        // empty list
        deferrableListHolder.setDeferrableList(username, new ArrayList());
    }

    public class TransitableAndSession {
        private Transitable transitable;

        private IMSession session;

        public TransitableAndSession(Transitable transitable, IMSession session) {
            this.transitable = transitable;
            this.session = session;
        }

        public Transitable getTransitable() {
            return transitable;
        }

        public IMSession getSession() {
            return session;
        }

        public String getHostname() {
            return JIDParser.getHostname(transitable.getTo());
        }
    }
    
    public class RemoteDeliveryThreadPerHost extends Thread {
        
        private LinkedBlockingQueue<TransitableAndSession> perHostRemoteDeliveryQueue;
        private IMSession remoteSession = null;
        private String hostname;
        private String currentStatus;
    
        public RemoteDeliveryThreadPerHost(String hostname) {
            this.hostname = hostname;
            perHostRemoteDeliveryQueue = new LinkedBlockingQueue<TransitableAndSession>();
            currentStatus = "";
        }
    
        public void enqueue(TransitableAndSession tas) {
            Debug.logInfo("Adding tas for " + hostname + " this thread (" + this + ") isAlive: " + isAlive() + " current status: " + currentStatus, module);
            perHostRemoteDeliveryQueue.add(tas);
        }
    
        public void run() {
            currentStatus = "Started";
            Debug.logInfo("Starting thread " + this, module);
            while (true) {
                TransitableAndSession tas = null;
                try {
                    tas = (TransitableAndSession) perHostRemoteDeliveryQueue.poll(120, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Debug.logError(e, e.getMessage(), module);
                }
                Debug.logInfo("Remove tas for " + hostname, module);
    
                if (tas != null) {
                    deliver(tas);
                    Debug.logInfo("Delivered tas for " + hostname, module);
                } else {
                    synchronized (remoteDeliveryThreadMap) {
                        if (perHostRemoteDeliveryQueue.isEmpty()) {
                            Debug.logInfo("Removing thread (" + this + "/" + hostname + ") from list", module);
    
                            //RemoteDeliveryThreadPerHost remoteDeliveryThread = (RemoteDeliveryThreadPerHost) 
                            remoteDeliveryThreadMap.remove(hostname);
                            // should get back to pool (to implem later)
                            break;
                        }
                    }
                }
            }
    
            // cleanup
            sessionsManager.release(remoteSession);
            remoteSession = null;
    
            currentStatus = "Ended";
            Debug.logInfo("Ending thread " + this, module);
        }
    
        private void deliver(TransitableAndSession tas) {
            Transitable transitable = tas.getTransitable();
            try  {
                boolean failedToDeliver = true;
                for (int retry = 0; retry < deliveryMaxRetry; retry++) {
                    try {
                        Debug.logInfo("Trying to send (" + transitable + ") to hostname " + hostname + " step " + retry, module);
                        if (remoteSession == null || remoteSession.isClosed()) {
                            remoteSession = s2sConnectorManager.getRemoteSessionWaitForValidation(hostname, deliveryMessageQueueTimeout);
                        }
                        
                        remoteSession.writeOutputStream(transitable.toString());
                        messageLogger.log(transitable);
                        messageRecorder.record(transitable);
                        Debug.logInfo("Sent (" + transitable + ") to hostname " + hostname + " step " + retry, module);
                        failedToDeliver = false;
                        break;
                    } catch (java.net.SocketException e) {
                        sessionsManager.release(remoteSession);
                        remoteSession = null;
                        temporise(e);
                    } catch (java.io.IOException e) {
                        sessionsManager.release(remoteSession);
                        remoteSession = null;
                        temporise(e);
                    } catch (Exception e) {
                        sessionsManager.release(remoteSession);
                        remoteSession = null;
                        Debug.logWarning(e, "Remote send failed " + e.getMessage(), module);
                        break;
                    }
                }
    
                if (failedToDeliver) {
                    String to = transitable.getTo();
                    Debug.logInfo("Failed to sent (from " + transitable.getFrom() + ") to hostname " + hostname, module);
                    String from = transitable.getFrom();
                    transitable.setError("Delivery failed");
                    transitable.setErrorCode(500);
                    transitable.setFrom(to);
                    transitable.setTo(from);
                    transitable.setType(Transitable.TYPE_ERROR);
    
                    try {
                        tas.getSession().writeOutputStream(transitable.toString());
                        messageLogger.log(transitable);
                        messageRecorder.record(transitable);
                    } catch (IOException e) {
                        Debug.logWarning(e, "Error delivery failed " + e.getMessage(), module);
                    }
                }
            } catch (Exception e) {
                Debug.logWarning(e, e.getMessage(), module);
            }
        }
    
        private final void temporise(Exception e) {
            Debug.logWarning("Remote send failed (retying in " + deliveryRetryDelay + "ms) " + e.getMessage(), module);
            sessionsManager.release(remoteSession);
            remoteSession = null;
    
            try {
                sleep(deliveryRetryDelay);
            } catch (InterruptedException ie) {
                Debug.logWarning(ie, ie.getMessage(), module);
            }
        }
    }
}
