package com.siteview.svdb;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.ofbiz.entity.Delegator;
//import org.ofbiz.entity.DelegatorFactory;
//import org.ofbiz.service.GenericDispatcher;
//import org.ofbiz.service.LocalDispatcher;

import com.siteview.svdb.dao.DaoFactory;
import com.siteview.svdb.queue.EccLogQueue;

public class SvdbApiImpl implements SvdbApi {
	private static final Log log = LogFactory.getLog(SvdbApiImpl.class);
//	private static Delegator delegator = DelegatorFactory
//			.getDelegator("default");
//	private static LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(delegator.getDelegatorName(), delegator);

	@Override
	public void appendRecord(String id, String text) {
		try {
			String decodeId = URLDecoder.decode(id, "UTF-8");
			// System.out.println("decodeId : "+ decodeId);
			String decodeText = URLDecoder.decode(text, "UTF-8");
			// System.out.println("decodeText : "+ decodeText);
			Object url = ConfigReader.getConfig("CenterWebServiceUrl");
			// System.out.println("url : "+ url);
			if (url == null || "".equals(url)) {
				// log.info("CenterAdress 没有设置");
			} else if (url instanceof String) {
				if (((String) url).contains("localhost")
						|| ((String) url).contains("127.0.0.1")) {
					log.info("CenterAdress 不能指向本机");
				} else {
					try {
						boolean flag = DaoFactory.getTelebackupDataDao().query(
								decodeId);
						// System.out.println("flag : "+ flag);
						if (decodeId != null)
							decodeId = decodeId.trim();
						// System.out.println("falg.......>"+flag +
						// "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+decodeId);
						if (flag) {
							// if (proxy==null) proxy = new
							// SvdbApiImplProxy((String)url);
							// SvdbApiImplProxy proxy = new
							// SvdbApiImplProxy((String)url);
							// proxy.appendRecord(id, text);
							// if (service==null)
							com.siteview.cxf.client.SvdbApiImplService service = new com.siteview.cxf.client.SvdbApiImplService(
									new URL((String) ConfigReader
											.getConfig("CenterWebServiceUrl")));
							service.getSvdbApiImpl().appendRecord(id, text);
						}
					} catch (Exception e) {
						log.error(e);
					}
				}
			}
			//--------------------解析数据-----------------------------
			decodeText = decodeText.replaceAll("#", ",");
			decodeText = decodeText.replaceAll("\\s*", "");
			int indexofRecordState = decodeText.indexOf("RecordState");
			String monitorDesc = decodeText.substring(0,indexofRecordState);
			String monitorStatusValue = decodeText.substring(indexofRecordState+12, decodeText.length()-1);
//			log.info("received data>>> MonitorID:"+decodeId+" MonitorStatus:"+monitorStatusValue+" MonitorDescription:"+monitorDesc);
			//----------------------将数据缓存进入队�? 由Vpsper定时读取实现类是VysperGateway.java------------------
			Map<String, String>  dataMap = new HashMap<String, String>(); 
			dataMap.put("MonitorID", decodeId);
			dataMap.put("MonitorStatus", monitorStatusValue);
			dataMap.put("MonitorDescription", monitorDesc);
			EccLogQueue.listMap.add(dataMap);
			log.info("/*************加入�?��消息到队列中,此时队列中还�?"+EccLogQueue.listMap.size()+" 条消息等待读�?************/");
			
			//---------------------直接用ofbiz服务写入数据--------------------
			/** Map<String, Object> context = FastMap.newInstance();
			context.put("operationId", decodeId);
			context.put("MonitorName", "");
			context.put("category", monitorStatusValue);
			context.put("description", monitorDesc);
			context.put("measurement", monitorStatusValue+" "+monitorDesc);
			dispatcher.runSync("InsertMonitorLogService",context); **/
			//--------------------将数据写入MSSQL2005--------------------
			// ReportData data = new ReportData();
			// data.setId(decodeId);
			// data.setCreateTime(new Date());
			// data.setName("MonitorData");
			// for (String textKv : textArray){
			// if (textKv.trim().length() == 0 ){
			// continue;
			// }
			// String[] kv = textKv.split("=");
			// if (kv.length != 2){
			// continue;
			// }
			// String skey = kv[0].trim();
			// String value = kv[1].trim();
			// skey = skey.replaceAll("#", "");
			// value = value.replaceAll("#", "");
			// data.setValue(kv[0].trim(), kv[1].trim());
			// }
			// System.out.println("data> decodeId :"+data.getId()+"data> monitorid :"+data.getMonitorid()+"data>name: "+data.getName()+"data > getValueKeys:"+data.getValueKeys()+"data> createtime :"+data.getCreateTime()+"######OVER###");
			// if (data.getValueKeys().size()>0)
			// DaoFactory.getReportDataDao().insert(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
