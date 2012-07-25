package com.siteview.svdb.ofbiz;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * 
 * @author zhongping.wang
 */
public class MonitorLogServices {
	public static final String module = MonitorLogServices.class.getName();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	//
	// /**
	// * insert monitor logs
	// *
	// * @param dctx
	// * @param context
	// * @return
	// * @throws Exception
	// */
	// public static Map<String, Object> insertLogData(DispatchContext dctx,
	// Map<String, ?> context) throws Exception {
	// Map<String, Object> response = ServiceUtil.returnSuccess();
	//
	// Map<String, Map<String, String>> fmap = new FastMap<String, Map<String,
	// String>>();
	// Map<String, String> ndata = new FastMap<String, String>();
	// StringBuilder estr = new StringBuilder();
	// String monitorId = "1.27.1.1";
	//
	// // ndata.put("dowhat", "QueryReportData");
	// // ndata.put("id", monitorId);
	// // ndata.put("compress", "true");
	// // ndata.put("dstrNeed", "true");
	// // ndata.put("byCount", "50");
	// ndata.put("dowhat", "QueryInfo");
	// ndata.put("parentid", "sv_id,sv_monitortype,sv_name");
	// ndata.put("needkey", "monitor");
	// ndata.put("needtype", "default");
	//
	// boolean ret = Jsvapi.getInstance().GetUnivData(fmap, ndata, estr);
	// if (!ret)
	// throw new Exception(estr.toString());
	// if (fmap.containsKey("return"))
	// fmap.remove("return");
	// System.out.println("从Svdb加载数据完毕<<<<<<<<<<<<<");
	// // 解析dstr节点
	// String mkey = "(dstr)" + monitorId;
	// Map<String, String> descValueMap = fmap.get(mkey);
	// String monitorName = descValueMap.get("MonitorName");
	// Iterator it = descValueMap.entrySet().iterator();
	// String entityIndex = sdf.format(new Date());// table index
	// Delegator delegator = dctx.getDelegator();
	// while (it.hasNext()) {
	// Entry entry = (Entry) it.next();
	// if (!entry.getKey().toString().equals("MonitorName")) {
	// String logId = delegator.getNextSeqId("OperationAttributeLog"
	// + entityIndex);
	// Object descValue = entry.getValue();
	// String category = descValue.toString().substring(0,
	// descValue.toString().indexOf(" "));
	// category = category.replaceAll("\\s*", "");
	// String decription = descValue.toString();
	// Timestamp logTime = str2Timestamp(entry.getKey().toString());
	// Map<String, String> fields = UtilMisc.toMap("logId", logId,
	// "operationId", monitorId, "logTime",
	// logTime, "name", monitorName, "category", category,
	// "description", decription, "measurement", decription);
	//
	// GenericValue log = delegator.makeValue("OperationAttributeLog"
	// + entityIndex, fields);
	// delegator.create(log);
	// }
	//
	// }
	//
	// return response;
	// }

	// /**
	// * insert monitor logs
	// *
	// * @param dctx
	// * @param context
	// * @return
	// * @throws Exception
	// */
	// public static Map<String, Object> insertLogData(DispatchContext dctx,
	// Map<String, Object> context) throws Exception {
	// Map<String, Object> response = ServiceUtil.returnSuccess();
	// //insert master data
	// Delegator delegator = dctx.getDelegator();
	// String entityIndex = sdf.format(new Date());// table index
	// String logId =
	// delegator.getNextSeqId("OperationAttributeLog"+entityIndex);
	// String decription = context.get("description").toString();
	// Map<String, String> fields = UtilMisc.toMap("logId", logId,
	// "operationId",context.get("operationId"),
	// "logTime",UtilDateTime.nowTimestamp(),
	// "name",context.get("MonitorName").toString(),
	// "category",context.get("category"),
	// "description",decription,
	// "measurement",context.get("measurement"));
	//        
	// GenericValue log =
	// delegator.makeValue("OperationAttributeLog"+entityIndex, fields);
	// delegator.create(log);
	// if (response.get("responseMessage").toString().equals("success")) {
	// Debug.logInfo("/******插入一条日志数据到数据库表  OperationAttributeLog"+entityIndex+" 中操作成功,日志ID: "+logId+" ******/",
	// module);
	// }else{
	// Debug.logInfo("/******插入一条日志数据到数据库表  OperationAttributeLog"+entityIndex+" 中操作失败,日志ID: "+logId+" ******/",
	// module);
	// }
	// return response;
	// }
	/**
	 * insert monitor logs
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> insertLogData(DispatchContext dctx,
			Map<String, Object> contextmap) throws Exception {
		Map<String, Object> response = ServiceUtil.returnSuccess();
		// insert master data
		List<GenericValue> values = new ArrayList<GenericValue>();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> contextList = (List<Map<String, Object>>) contextmap.get("contextList");
		String entityIndex = sdf.format(new Date());// table index
		StringBuffer sbf = new StringBuffer();
		for (Map<String, Object> context : contextList) {
			String logId = delegator.getNextSeqId("OperationAttributeLog"
					+ entityIndex);
			sbf.append(logId+";");
			String decription = context.get("description").toString();
			Map<String, String> fields = UtilMisc.toMap("logId", logId,
					"operationId", context.get("operationId"), "logTime",
					UtilDateTime.nowTimestamp(), "name", context.get(
							"MonitorName").toString(), "category", context
							.get("category"), "description", decription,
					"measurement", context.get("measurement"));
			GenericValue log = delegator.makeValue("OperationAttributeLog"
					+ entityIndex, fields);
			values.add(log);
		}
		delegator.storeAll(values);
		 if (response.get("responseMessage").toString().equals("success")) {
		 Debug.logInfo("/******批量插入 "+contextList.size()+" 条日志数据到数据库表  OperationAttributeLog"+entityIndex+" 中操作成功,日志ID分别为: "+sbf.toString()+" ******/",
		 module);
		 }else{
		 Debug.logInfo("/******批量插入 "+contextList.size()+" 条日志数据到数据库表  OperationAttributeLog"+entityIndex+" 中操作失败,日志ID分别为: "+sbf.toString()+" ******/",
		 module);
		 }
		return response;
	}

	/**
	 * query monitor logs
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> queryLogData(DispatchContext dctx,
			Map<String, ?> context) throws Exception {
		Map<String, Object> response = ServiceUtil.returnSuccess();

		String tablename = (String) context.get("tablename");
		int from = (Integer) context.get("from");
		int to = (Integer) context.get("to");
		String startTime = (String) context.get("starttime");
		String endTime = (String) context.get("endtime");
		Timestamp startdate = Timestamp.valueOf(startTime);
		Timestamp enddate = Timestamp.valueOf(endTime);
		List<EntityCondition> entityConditionList = FastList.newInstance();
		entityConditionList.add(EntityCondition.makeCondition("logTime",
				EntityOperator.BETWEEN, UtilMisc.toList(startdate, enddate)));

		EntityCondition condition = EntityCondition.makeCondition(
				entityConditionList, EntityOperator.AND);

		DynamicViewEntity dve = new DynamicViewEntity();
		Collection<String> fieldsToSelect = FastList.newInstance();
		fieldsToSelect.add("operationId");
		fieldsToSelect.add("name");
		fieldsToSelect.add("logTime");
		fieldsToSelect.add("category");
		fieldsToSelect.add("description");
		dve.addMemberEntity("OperLog", tablename);
		dve.addAliasAll("OperLog", "");
		List<String> orderBy = UtilMisc.toList("logTime");
		Delegator delegator = dctx.getDelegator();
		EntityListIterator resultiterator = delegator
				.findListIteratorByCondition(dve, condition, null,
						fieldsToSelect, orderBy, null);
		List<GenericValue> result = resultiterator.getPartialList(from, to
				- from);

		FastList<String> datalist = FastList.newInstance();
		for (GenericValue genericValue : result) {
			String monitorid = genericValue.get("operationId").toString();
			String reportCreateTime = genericValue.get("logTime").toString();
			String reportDataValue = genericValue.get("description").toString();
			String monitorStatus = genericValue.get("category").toString();
			String monitorLog = monitorid + "#" + reportCreateTime + "#"
					+ reportDataValue + "#" + monitorStatus;
			datalist.add(monitorLog);
		}
		System.out.println("查询出< " + datalist.size() + " >条数据");
		response.put("queryMap", datalist);
		return response;
	}

	/**
	 * 获取返回数量
	 * 
	 * @param nodeid
	 * @return
	 */
	public int getReturnSize(String nodeid,
			Map<String, Map<String, String>> returnMap) {
		if (returnMap == null)
			return 0;
		int size = 0;

		Map<String, String> rdata = returnMap.get(nodeid);
		if (rdata != null) {
			for (String key : rdata.keySet()) {
				String value = rdata.get(key);
				if (value != null && value.equals("ReturnValue"))
					++size;
			}
		}

		return size;
	}

	/**
	 * 字符串转换时间戳
	 * 
	 * @param str
	 * @return
	 */
	public static Timestamp str2Timestamp(String str) {
		Date date = str2Date(str, DEFAULT_FORMAT);
		return new Timestamp(date.getTime());
	}

	/**
	 * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
	 * 
	 * @param str
	 *            字符串
	 * @param format
	 *            日期格式
	 * @return 日期
	 * @throws java.text.ParseException
	 */
	public static Date str2Date(String str, String format) {
		if (null == str || "".equals(str)) {
			return null;
		}
		// 如果没有指定字符串转换的格式，则用默认格式进行转换
		if (null == format || "".equals(format)) {
			format = DEFAULT_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
