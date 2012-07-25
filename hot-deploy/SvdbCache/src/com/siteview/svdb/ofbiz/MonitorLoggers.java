/*
interface to entity.
receive the monitor log from erlangnode,  reduce the large number insert operation into database
 */

package com.siteview.svdb.ofbiz;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.siteview.ecc.util.ObjectTransformation;

public class MonitorLoggers implements Runnable {
	protected String configFileLocation = null;
	public static final String module = MonitorLoggers.class.getName();
	private static Delegator delegator = DelegatorFactory
			.getDelegator("default");
	private static LocalDispatcher dispatcher = GenericDispatcher
			.getLocalDispatcher(delegator.getDelegatorName(), delegator);

	protected String from, userName, pwd;
	protected String server;
	protected int bufferSize, delay, port;

	XMPPConnection connection;
	Presence presence;

	public MonitorLoggers(String server, Integer port, String userName,
			String pwd, String from, int bufferSize, int delay)
			throws XMPPException {
		this.from = from;
		this.userName = userName;
		this.pwd = pwd;
		this.server = server;
		this.port = port;
		this.bufferSize = bufferSize;
		this.delay = delay;
		ConnectionConfiguration config = new ConnectionConfiguration(server,
				port, server);
		connection = new XMPPConnection(config);
		// create the LocalDispatcher

		connection.connect();
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		Debug.logInfo("Monitor logger's userName/pwd=" + userName + "@"
				+ server + "/" + pwd, module);
		connection.login(userName, pwd);

		// Create a new presence. Pass in false to indicate we're unavailable.
		presence = new Presence(Presence.Type.available);
		presence.setStatus("logging data ...");
		connection.sendPacket(presence);
	}

	@Override
	public void run() {
		// Accept only messages from erlangnode
		PacketFilter filter = new AndFilter(
				new PacketTypeFilter(Message.class), new FromContainsFilter(
						from + "@" + server));

		// Collect these messages
		PacketCollector collector = connection.createPacketCollector(filter);
		List<Object> monitorList = FastList.newInstance();
		int i = 0;
		Packet packet;
		Boolean commit = false;
		while (true) {

			// try to reconnect if lost connection
			while (!connection.isConnected()) {
				try {
					Thread.sleep(1000);
					connection.connect();
					// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
					Debug.logInfo("Monitor logger's userName/pwd=" + userName
							+ "@" + server, module);
					connection.login(userName, pwd);

					// Create a new presence. Pass in false to indicate we're
					// unavailable.
					presence = new Presence(Presence.Type.available);
					presence.setStatus("logging data ...");
					connection.sendPacket(presence);
				} catch (Exception ex) {
					Debug.logInfo("Connect to XMPP server(" + server
							+ ") failed, wait for 1 seconds to retry.", module);
				}
			}

			packet = collector.nextResult(10);// wait for the next result,
												// blocking here or time out

			if (packet != null) {// data arrived
				// Process message
				if (packet instanceof Message) {
					Message msg = (Message) packet;
					Map<String, String> data = (Map<String, String>) ObjectTransformation
							.SToO(msg.getBody());// 取出接收到的数据
					Map<String, Object> context = FastMap.newInstance();
					context.put("operationId", data.get("MonitorID"));
					context.put("MonitorName", "");
					context.put("category", data.get("MonitorStatus"));
					context.put("description", data.get("MonitorDescription"));
					context.put("measurement", data.get("MonitorStatus") + " "
							+data.get("MonitorDescription"));
					try {
						dispatcher.runSync("InsertMonitorLogService", context);
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
					commit = (i == bufferSize);
				}
			} else
				// time out, no data for lst delayed seconds
				commit = (monitorList.size() > 0);

			// write to entity when timeout or reach buffer size
			if (commit && (monitorList != null)) {
				// dispatcher.runSync(serviceName,UtilMisc.toMap("monitorList",
				// monitorList));
				monitorList.clear();
				i = 0;
				commit = false;

			}
		}
	}

}