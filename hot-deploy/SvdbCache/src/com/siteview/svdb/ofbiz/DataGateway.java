package com.siteview.svdb.ofbiz;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

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
import org.ofbiz.base.util.StringUtil;

import com.siteview.ecc.util.ObjectTransformation;
import com.siteview.svdb.queue.EccLogQueue;

public class DataGateway implements Container, MessageListener, Serializable {

	public static final String module = DataGateway.class.getName();
	private static final long serialVersionUID = 1L;

	protected String configFileLocation = null;
	public static XMPPConnection connection;
	public static Roster roster;
	public static InetAddress ipaddr = null;

	public static String XMPP_SERVER = "localhost";
	public static Boolean XMPP_DEBUG = false;
	public static Integer XMPP_SERVER_PORT = 5222;
	public static String XMPP_USERNAME;
	public static String XMPP_PASSWORD;
	public static String MONITOR_LOG_USERNAMES;
	public static String MONITOR_LOG_PASSWORDS;
	public static int delay = 0;
	public static int buffer = 0;

	private static List<String> MONITOR_LOG_USERNAME_LIST;

	protected static List<Chat> loggerChat = FastList.newInstance();
	protected static List<Chat> debugChat = FastList.newInstance();
	public EccLogQueue logQueue = new EccLogQueue();

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

	@Override
	public boolean start() throws ContainerException {
		// TODO Auto-generated method stub
		final ContainerConfig.Container cfg = ContainerConfig.getContainer(
				"datagateway-container", this.configFileLocation);
		XMPP_SERVER = cfg.getProperty("xmpp").value;
		if (XMPP_SERVER == null)
			XMPP_SERVER = ipaddr.getHostName();
		XMPP_DEBUG = Boolean.getBoolean(cfg.getProperty("xmpp_debug").value);
		String server_port = cfg.getProperty("xmpp_port").value;
		XMPP_SERVER_PORT = (server_port != null) ? Integer
				.parseInt(server_port) : 5222;

		XMPP_USERNAME = cfg.getProperty("xmpp_username").value;
		if (XMPP_USERNAME == null)
			XMPP_USERNAME = "DemoChatUser1";

		XMPP_PASSWORD = cfg.getProperty("xmpp_password").value;
		if (XMPP_PASSWORD == null)
			XMPP_PASSWORD = "ofbiz";
		MONITOR_LOG_USERNAMES = cfg.getProperty("monitor_logger_usernames").value;
		MONITOR_LOG_PASSWORDS = cfg.getProperty("monitor_logger_passwords").value;
		MONITOR_LOG_USERNAME_LIST = StringUtil
				.split(MONITOR_LOG_USERNAMES, ",");
		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = XMPP_DEBUG;
		// start the main client to receive the data into xmpp
		try {
			xmppLogin(XMPP_USERNAME, XMPP_PASSWORD);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// starting the multiple clients to log the data into entity
		final List<String> pwdList = StringUtil.split(MONITOR_LOG_PASSWORDS,
				",");
		int i = 0;
		buffer = Integer
				.parseInt(cfg.getProperty("monitor_logger_buffer").value);
		delay = Integer.parseInt(cfg.getProperty("monitor_logger_delay").value);
		for (i = 0; i < MONITOR_LOG_USERNAME_LIST.size(); i++) {
			MonitorLoggers logger;
			try {
				logger = new MonitorLoggers(XMPP_SERVER, XMPP_SERVER_PORT,
						MONITOR_LOG_USERNAME_LIST.get(i), pwdList.get(i),
						XMPP_USERNAME, buffer, delay);
				new Thread(logger).start();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Runnable ofbizNode = new Runnable() {
			public void run() {
				serve(cfg);
			}
		};
		new Thread(ofbizNode).start();
		return false;
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
			Debug.logInfo("/*************读取数据频率: "+delay+" ms;从队列中读取一条消息,此时队列中还剩下  "+EccLogQueue.listMap.size()+" 条消息等待读取*************/", module);
			Map<String, String> data = logQueue.getFirst();// 从缓存队列取出第一个元素
			if (data != null) {
				try {
					loggerChat.get(indexLogger).sendMessage(
							ObjectTransformation.OToS(data));
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			indexLogger++;

		}

	}

	@Override
	public void stop() throws ContainerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processMessage(Chat chat, Message message) {
		// TODO Auto-generated method stub
		System.out.println(chat.getParticipant() + " says: "
				+ message.getBody());
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
