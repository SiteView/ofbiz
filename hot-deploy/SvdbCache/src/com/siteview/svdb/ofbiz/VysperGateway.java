package com.siteview.svdb.ofbiz;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.apache.vysper.mina.TCPEndpoint;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.apache.vysper.xmpp.authorization.AccountManagement;
import org.apache.vysper.xmpp.modules.Module;
import org.apache.vysper.xmpp.modules.extension.xep0049_privatedata.PrivateDataModule;
import org.apache.vysper.xmpp.modules.extension.xep0050_adhoc_commands.AdhocCommandsModule;
import org.apache.vysper.xmpp.modules.extension.xep0054_vcardtemp.VcardTempModule;
import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationModule;
import org.apache.vysper.xmpp.modules.extension.xep0092_software_version.SoftwareVersionModule;
import org.apache.vysper.xmpp.modules.extension.xep0119_xmppping.XmppPingModule;
import org.apache.vysper.xmpp.modules.extension.xep0133_service_administration.ServiceAdministrationModule;
import org.apache.vysper.xmpp.modules.extension.xep0202_entity_time.EntityTimeModule;
import org.apache.vysper.xmpp.modules.roster.RosterException;
import org.apache.vysper.xmpp.modules.roster.RosterItem;
import org.apache.vysper.xmpp.modules.roster.SubscriptionType;
import org.apache.vysper.xmpp.modules.roster.persistence.RosterManager;
import org.apache.vysper.xmpp.server.XMPPServer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.StringUtil;

import com.siteview.ecc.util.ObjectTransformation;
import com.siteview.svdb.queue.EccLogQueue;

public class VysperGateway implements Container, MessageListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String configFileLocation = null;
	public static final String module = VysperGateway.class.getName();
	protected static List<Chat> loggerChat = FastList.newInstance();

	public static String XMPP_SERVER = "localhost";
	public static Boolean XMPP_DEBUG = false;
	public static Integer XMPP_SERVER_PORT = 5222;
	public static String XMPP_USERNAME;
	public static String XMPP_PASSWORD;
	public static String MONITOR_LOG_USERNAMES;
	public static String MONITOR_LOG_PASSWORDS;
	private static List<String> MONITOR_LOG_USERNAME_LIST;
	public static int delay = 0;//读取频率
	public static int buffer = 0;//缓冲池大小
	public static int readcount = 0;//批量读取数量

	public static XMPPConnection connection;
	public static Roster roster;
	public static InetAddress ipaddr = null;
	public EccLogQueue logQueue = new EccLogQueue();

	int indexLogger = 0;

	public void xmppLogin(String userName, String password)
			throws XMPPException {
		ConnectionConfiguration config = new ConnectionConfiguration(
				XMPP_SERVER, XMPP_SERVER_PORT, XMPP_SERVER);
		connection = new XMPPConnection(config);

		// TODO: show reconnect progress
		config.setReconnectionAllowed(true);

		int i = 0;
		while (!connection.isConnected()) {
			try {
				Thread.sleep(i * 2000);
				connection.connect();
			} catch (Exception ex) {
				i++;
				i = Math.min(i, 20);
				Debug.logInfo("Connect to XMPP server(" + XMPP_SERVER
						+ ") failed, wait for " + i + " seconds to retry.",
						module);
			}
		}

		roster = connection.getRoster();
		// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		connection.login(userName, password);
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus("Running ...");
		connection.sendPacket(presence);

		loggerChat.clear();
		if (MONITOR_LOG_USERNAME_LIST != null) {
			for (String username : MONITOR_LOG_USERNAME_LIST) {
				loggerChat.add(connection.getChatManager().createChat(
						username + "@" + XMPP_SERVER, this));
			}
		} else {
			Debug
					.logError(
							"No monitor loggers configured in ofbiz-containers.xml. Log data will NOT be inserted into database !",
							module);
		}
	}

	public void disconnect() {
		connection.disconnect();
	}

	@Override
	public void init(String[] args, String configFile)
			throws ContainerException {
		// TODO Auto-generated method stub
		this.configFileLocation = configFile;
		try {
			ipaddr = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean start() throws ContainerException {
		// TODO Auto-generated method stub
		final ContainerConfig.Container cfg = ContainerConfig.getContainer(
				"vyspergateway-container", this.configFileLocation);

		XMPP_SERVER = cfg.getProperty("xmpp").value;
		if (XMPP_SERVER == null)
			XMPP_SERVER = ipaddr.getHostName();
		readcount = Integer.parseInt(cfg.getProperty("monitor_logger_readcount").value);
		XMPP_DEBUG = Boolean.getBoolean(cfg.getProperty("xmpp_debug").value);
		String server_port = cfg.getProperty("xmpp_port").value;
		XMPP_SERVER_PORT = (server_port != null) ? Integer
				.parseInt(server_port) : 5222;

		XMPP_USERNAME = cfg.getProperty("xmpp_username").value;
		if (XMPP_USERNAME == null)
			XMPP_USERNAME = "datasender";

		XMPP_PASSWORD = cfg.getProperty("xmpp_password").value;
		if (XMPP_PASSWORD == null)
			XMPP_PASSWORD = "siteview";

		MONITOR_LOG_USERNAMES = cfg.getProperty("monitor_logger_usernames").value;
		MONITOR_LOG_PASSWORDS = cfg.getProperty("monitor_logger_passwords").value;
		MONITOR_LOG_USERNAME_LIST = StringUtil
				.split(MONITOR_LOG_USERNAMES, ",");

		// start the vysper xmpp server
		startVysper("component://SvdbCache/config/bogus_mina_tls.cert");
		try {
			// turn on the enhanced debugger
			XMPPConnection.DEBUG_ENABLED = XMPP_DEBUG;

			// start the main client to receive the data into xmpp
			xmppLogin(XMPP_USERNAME, XMPP_PASSWORD);

			// starting the multiple clients to log the data into entity
			final List<String> pwdList = StringUtil.split(
					MONITOR_LOG_PASSWORDS, ",");
			int i = 0;
			buffer = Integer
					.parseInt(cfg.getProperty("monitor_logger_buffer").value);
			delay = Integer
					.parseInt(cfg.getProperty("monitor_logger_delay").value);
			for (i = 0; i < MONITOR_LOG_USERNAME_LIST.size(); i++) {
				MonitorLoggers logger = new MonitorLoggers(XMPP_SERVER,
						XMPP_SERVER_PORT, MONITOR_LOG_USERNAME_LIST.get(i),
						pwdList.get(i), XMPP_USERNAME, buffer, delay);
				new Thread(logger).start();
			}
		} catch (XMPPException e1) {
			Debug.logError(e1, module);
		}

		Runnable ofbizNode = new Runnable() {
			public void run() {
				serve(cfg);
			}
		};
		new Thread(ofbizNode).start();
		return true;
	}

	public void serve(ContainerConfig.Container cfg) {
		int indexLogger = 0;
		while (true) {
			// reconnect if lost connection to the server
			while (!connection.isConnected()) {
				try {
					xmppLogin(XMPP_USERNAME, XMPP_PASSWORD);
				} catch (Exception ex) {
					Debug.logError(ex, module);
				}
			}

			indexLogger = (indexLogger == loggerChat.size()) ? 0 : indexLogger;
			try {
				Thread.sleep(delay);//发送时间间隔
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			Map<String, String> data = logQueue.getFirst();// 从缓存队列取出第一个元素
//			if (data != null) {
//				try {
//					loggerChat.get(indexLogger).sendMessage(
//							ObjectTransformation.OToS(data));
//				} catch (XMPPException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			//----------------------批量读取数据------------------------------
			List<Map<String, String>> batchMap = logQueue.getBatchMap(readcount);
			if (batchMap != null) {
//				Debug.logInfo("/*************读取数据频率: "+delay+" ms;批量读取出 "+batchMap.size()+" 条数据,此时队列中还剩下  "+EccLogQueue.listMap.size()+" 条数据等待读取*************/", module);
				try {
					loggerChat.get(indexLogger).sendMessage(
							ObjectTransformation.OToS(batchMap));
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			indexLogger++;

		}

	}

	private void startVysper(String fileName) {
		String domain = XMPP_SERVER;

		String addedModuleProperty = System.getProperty("vysper.add.module");
		List<Module> listOfModules = null;
		if (addedModuleProperty != null) {
			String[] moduleClassNames = addedModuleProperty.split(",");
			listOfModules = createModuleInstances(moduleClassNames);
		}

		// choose the storage you want to use
		// StorageProviderRegistry providerRegistry = new
		// JcrStorageProviderRegistry();
		StorageProviderRegistry providerRegistry = new MemoryStorageProviderRegistry();

		final Entity adminJID = EntityImpl.parseUnchecked("admin@" + domain);
		final Entity erlangNodeJID = EntityImpl.parseUnchecked(XMPP_USERNAME
				+ "@" + domain);
		final AccountManagement accountManagement = (AccountManagement) providerRegistry
				.retrieve(AccountManagement.class);
		final RosterManager rosterManager = (RosterManager) providerRegistry
				.retrieve(RosterManager.class);

		String initialPassword = System.getProperty(
				"vysper.admin.initial.password", "siteview");
		if (!accountManagement.verifyAccountExists(adminJID)) {
			try {
				accountManagement.addUser(adminJID, initialPassword);
				accountManagement.addUser(erlangNodeJID, initialPassword);

				for (String user : MONITOR_LOG_USERNAME_LIST) {
					final Entity userJID = EntityImpl.parseUnchecked(user + "@"
							+ domain);
					accountManagement.addUser(userJID, initialPassword);
					rosterManager.addContact(erlangNodeJID, new RosterItem(
							userJID, SubscriptionType.BOTH));
					rosterManager.addContact(userJID, new RosterItem(
							erlangNodeJID, SubscriptionType.BOTH));
				}

				// for (String user: DEBUG_USERNAME_LIST) {
				// accountManagement.addUser(EntityImpl.parseUnchecked(user+ "@"
				// + domain), initialPassword);
				// }

			} catch (AccountCreationException e) {
				e.printStackTrace();
			} catch (RosterException e) {
				e.printStackTrace();
			}
		}

		XMPPServer xmppserver = new XMPPServer(domain);
		xmppserver.addEndpoint(new TCPEndpoint());
		// server.addEndpoint(new StanzaSessionFactory());
		xmppserver.setStorageProviderRegistry(providerRegistry);

		try {
			xmppserver.setTLSCertificateInfo(FileUtil.getFile(fileName),
					"boguspw");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			xmppserver.start();
			System.out.println("vysper server is running...");
		} catch (Exception e) {
			e.printStackTrace();
		}

		xmppserver.addModule(new SoftwareVersionModule());
		xmppserver.addModule(new EntityTimeModule());
		xmppserver.addModule(new VcardTempModule());
		xmppserver.addModule(new XmppPingModule());
		xmppserver.addModule(new PrivateDataModule());
		xmppserver.addModule(new InBandRegistrationModule());
		xmppserver.addModule(new AdhocCommandsModule());
		final ServiceAdministrationModule serviceAdministrationModule = new ServiceAdministrationModule();
		// unless admin user account with a secure password is added, this will
		// be not become effective
		serviceAdministrationModule.setAddAdminJIDs(Arrays.asList(adminJID));

		xmppserver.addModule(serviceAdministrationModule);

		if (listOfModules != null) {
			for (Module module : listOfModules) {
				xmppserver.addModule(module);
			}
		}

	}

	private static List<Module> createModuleInstances(String[] moduleClassNames) {
		List<Module> modules = new ArrayList<Module>();

		for (String moduleClassName : moduleClassNames) {
			Class<Module> moduleClass;
			try {
				moduleClass = (Class<Module>) Class.forName(moduleClassName);
			} catch (ClassCastException e) {
				System.err.println("not a Vysper module class: "
						+ moduleClassName);
				continue;
			} catch (ClassNotFoundException e) {
				System.err.println("could not load module class "
						+ moduleClassName);
				continue;
			}
			try {
				Module module = moduleClass.newInstance();
				modules.add(module);
			} catch (Exception e) {
				System.err.println("failed to instantiate module class "
						+ moduleClassName);
				continue;
			}
		}
		return modules;
	}

	@Override
	public void stop() throws ContainerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processMessage(Chat chat, Message message) {
		// TODO Auto-generated method stub
		if (message.getType() == Message.Type.chat) {
			System.out.println(chat.getParticipant() + " says: "
					+ message.getBody());
			try {
				chat.sendMessage(message.getBody() + " echo");
			} catch (XMPPException ex) {
				Debug.logError(ex.toString(), module);
			}
		}
	}

}
