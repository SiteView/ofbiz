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
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.openim.OpenIMContainer;
import org.ofbiz.openim.data.Room;
import org.ofbiz.openim.data.RoomImpl;

public class RoomRepositoryHolderImpl implements RoomRepositoryHolder {

    public final static String module = RoomRepositoryHolderImpl.class.getName();
    
    protected Map<String, Room> rooms = null;
    protected Delegator delegator = null;
    protected String hostName = null;
    
    public RoomRepositoryHolderImpl() {
        delegator = OpenIMContainer.delegator;
        this.hostName = OpenIMContainer.serverParameters.getHostName();
        rooms = FastMap.newInstance();
        loadRoom();
    }
    
    private void loadRoom() {
        try {
            List<GenericValue> chatRooms = delegator.findByAnd("PartyRoleAndPartyDetail", UtilMisc.toMap("partyTypeId", "PARTY_GROUP", "roleTypeId", "CHAT_ROOM"));
            for (GenericValue chatRoom : chatRooms) {
                String partyId = chatRoom.getString("partyId");
                List<GenericValue> userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
                if (UtilValidate.isNotEmpty(userLogins)) {
                    GenericValue userLogin = EntityUtil.getFirst(userLogins);
                    String userLoginId = userLogin.getString("userLoginId");
                    String jId = userLoginId.toLowerCase() + "@" + hostName;
                    Room room = new RoomImpl();
                    room.setJId(jId);
                    room.setName(chatRoom.getString("groupName"));
                    rooms.put(jId, room);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Cannot load room from a repository", module);
        }
    }
    
    @Override
    public List<Room> getRooms() {
        return UtilMisc.toList(rooms.values());
    }

    @Override
    public Room getRoom(String name) {
        return rooms.get(name);
    }
}
