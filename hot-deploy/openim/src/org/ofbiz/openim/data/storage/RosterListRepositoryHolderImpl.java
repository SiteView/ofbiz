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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.openim.OpenIMContainer;

import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.data.storage.RosterListRepositoryHolder;

public class RosterListRepositoryHolderImpl implements RosterListRepositoryHolder {

    public final static String module = RosterListRepositoryHolderImpl.class.getName();

    private ServerParameters serverParameters = null;
    private Delegator delegator = null;
    
    public RosterListRepositoryHolderImpl(Delegator delegator) {
        this.serverParameters = OpenIMContainer.serverParameters;
        this.delegator = delegator;
    }

    @Override
    public List getRosterList(String username) {
        List rosterList = FastList.newInstance();
        try {
            GenericValue userLogin = null;
            List<GenericValue> userLogins = delegator.findList("UserLogin", null, null, null, null, false);
            for (GenericValue tempUserLogin : userLogins) {
                String userLoginId = tempUserLogin.getString("userLoginId");
                if (userLoginId.equalsIgnoreCase(username)) {
                    userLogin = tempUserLogin;
                }
            }
            
            if (UtilValidate.isNotEmpty(userLogin)) {
                GenericValue party = userLogin.getRelatedOne("Party");
                
                // add TO roster items
                List<GenericValue> toPartyRelationships = party.getRelated("ToPartyRelationship");
                for (GenericValue toPartyRelationship : toPartyRelationships) {
                    GenericValue fromParty = toPartyRelationship.getRelatedOne("FromParty");
                    List<GenericValue> fromUserLogins = fromParty.getRelated("UserLogin");
                    if (UtilValidate.isNotEmpty(fromUserLogins)) {
                        GenericValue fromUserLogin = EntityUtil.getFirst(fromUserLogins);
                        String fromUserLoginId = fromUserLogin.getString("userLoginId");
                        GenericValue partyNameView = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", fromParty.getString("partyId")), false);
                        String partyName = partyNameView.getString("firstName") + " " + partyNameView.getString("lastName");
                        IMRosterItem rosterItem = new IMRosterItem();
                        rosterItem.setJID(fromUserLoginId + "@" + serverParameters.getHostName());
                        rosterItem.setName(partyName);
                        rosterItem.setSubscription(IMRosterItem.SUBSCRIPTION_TO);
                        rosterList.add(rosterItem);
                    }
                }
                
                // add FROM roster items
                /*
                List<GenericValue> fromPartyRelationships = party.getRelated("FromPartyRelationship");
                for (GenericValue fromPartyRelationship : fromPartyRelationships) {
                    GenericValue toParty = fromPartyRelationship.getRelatedOne("ToParty");
                    List<GenericValue> toUserLogins = toParty.getRelated("UserLogin");
                    if (UtilValidate.isNotEmpty(toUserLogins)) {
                        GenericValue toUserLogin = EntityUtil.getFirst(toUserLogins);
                        String toUserLoginId = toUserLogin.getString("userLoginId");
                        GenericValue partyNameView = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", toParty.getString("partyId")), false);
                        String partyName = partyNameView.getString("firstName") + " " + partyNameView.getString("lastName");
                        IMRosterItem rosterItem = new IMRosterItem();
                        rosterItem.setJID(toUserLoginId + "@" + serverParameters.getHostName());
                        rosterItem.setName(partyName);
                        rosterItem.setSubscription(IMRosterItem.SUBSCRIPTION_FROM);
                        rosterList.add(rosterItem);
                    }
                }
                */
            } else {
                Debug.logWarning("Not found user: " + username, module);
            }
        } catch (Exception e) {
            Debug.logWarning(e, "User " + username + " roster list not found", module);
        }
        return rosterList;
    }

    @Override
    public void setRosterList(String username, List rosterList) {
        if (username != null && rosterList != null) {
            
        }
    }
}
