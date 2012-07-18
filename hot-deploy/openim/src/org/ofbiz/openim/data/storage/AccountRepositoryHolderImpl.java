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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.openim.data.AccountImpl;

import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.AccountRepositoryHolderMBean;

public class AccountRepositoryHolderImpl implements AccountRepositoryHolder, AccountRepositoryHolderMBean {

    public final static String module = AccountRepositoryHolderImpl.class.getName();

    private Delegator delegator = null;
    private Map<String, Account> accountRepository;
    private boolean regexpSearch;
    
    public AccountRepositoryHolderImpl(Delegator delegator) {
        this.delegator = delegator;
        try {
            loadAccountRepository();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot load account repository", module);
        }
    }
    
    private void loadAccountRepository() throws GenericEntityException {
        accountRepository = FastMap.newInstance();
        // user logins
        List<GenericValue> persons = delegator.findList("Person", null, null, null, null, false);
        for (GenericValue person : persons) {
            List<GenericValue> userLogins = person.getRelated("UserLogin");
            GenericValue userLogin = EntityUtil.getFirst(userLogins);
            if (UtilValidate.isNotEmpty(userLogin)) {
                String userLoginId = userLogin.getString("userLoginId");
                String currentPassword = userLogin.getString("currentPassword");
                Account account = new AccountImpl();
                account.setName(userLoginId);
                account.setPassword(currentPassword);
                accountRepository.put(userLoginId, account);
            }
        }
        
        // rooms
        List<GenericValue> chatRooms = delegator.findByAnd("PartyRoleAndPartyDetail", UtilMisc.toMap("partyTypeId", "PARTY_GROUP", "roleTypeId", "CHAT_ROOM"));
        for (GenericValue chatRoom : chatRooms) {
            String partyId = chatRoom.getString("partyId");
            List<GenericValue> userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
            GenericValue userLogin = EntityUtil.getFirst(userLogins);
            if (UtilValidate.isNotEmpty(userLogin)) {
                String userLoginId = userLogin.getString("userLoginId");
                String currentPassword = userLogin.getString("currentPassword");
                Account account = new AccountImpl();
                account.setName(userLoginId);
                account.setPassword(currentPassword);
                accountRepository.put(userLoginId, account);
            }
        }
    }

    @Override
    public Account getAccount(String username) {
        Account account = null;
        try {
            if (username != null && username.length() > 0) {
                synchronized (accountRepository) {
                    if (accountRepository.containsKey(username)) {
                        account = (AccountImpl) accountRepository.get(username);
                    } else {
                        GenericValue userLogin = null;
                        List<GenericValue> userLogins = delegator.findList("UserLogin", null, null, null, null, false);
                        for (GenericValue tempUserLogin : userLogins) {
                            String userLoginId = tempUserLogin.getString("userLoginId");
                            if (userLoginId.equalsIgnoreCase(username)) {
                                userLogin = tempUserLogin;
                            }
                        }
                        if (UtilValidate.isNotEmpty(userLogin)) {
                            String userLoginId = userLogin.getString("userLoginId");
                            String currentPassword = userLogin.getString("currentPassword");
                            account = new AccountImpl();
                            account.setName(userLoginId);
                            account.setPassword(currentPassword);
                            accountRepository.put(username, account);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
            account = null;
        }

        if (account == null) {
            Debug.logWarning("User " + username + " not found", module);
        }
        return account;
    }

    @Override
    public List getAccountList(String searchPattern) {
        List<Account> list = new ArrayList<Account>();
        if (!regexpSearch) {
            searchPattern = searchPattern.replaceAll("\\*", ".*");
        }

        try {
            synchronized (accountRepository) {
                Iterator iter = accountRepository.values().iterator();
                while (iter.hasNext()) {
                    String name = iter.next().toString();
                    if (name.matches(searchPattern)) {
                        Account account = (Account) accountRepository.get(name);
                        list.add(account);
                    }
                }
            }
        } catch (Exception e) {
            Debug.logWarning(e, e.getMessage(), module);
        }
        return list;
    }

    @Override
    public Account removeAccount(String username) {
        Account account = null;
        synchronized (accountRepository) {
            account = (Account) accountRepository.remove(username);
            if (account != null) {
                // TODO save Map
            } else {
                Debug.logWarning("User " + username + " not found", module);
            }
        }
        return account;
    }

    @Override
    public void setAccount(Account userAccount) {
        AccountImpl account = new AccountImpl();

        account.setName(userAccount.getName());
        account.setPassword(userAccount.getPassword());

        Debug.logInfo("Setting account in repository " + account, module);
        synchronized (accountRepository) {
            accountRepository.put(account.getName(), account);
            // TODO save Map
        }
    }

    @Override
    public List getAccountList() {
        List<Account> list = new ArrayList<Account>();
        synchronized (accountRepository) {
            Iterator iter = accountRepository.values().iterator();
            while (iter.hasNext()) {
                Account o = (Account) iter.next();
                Debug.logInfo("Item " + o + " account " + getAccount(o.toString()), module);
                list.add(o);
            }
        }
        return list;
    }
    
    @Override
    public void setAccount(String accountStr) {
        try {
            Account account = new AccountImpl();
            int index = accountStr.indexOf('/');
            if (index < 0) {
                account.setName(accountStr);
                account.setPassword(accountStr);
            } else {
                account.setName(accountStr.substring(0, index));
                account.setPassword(accountStr.substring(index + 1));
            }
            setAccount(account);
        }
        catch (Exception e) {
            Debug.logWarning(e, e.getMessage(), module);
        }
    }
}
