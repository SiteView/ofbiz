package com.siteview.svdb.ofbiz;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import com.siteview.ecc.util.ArrayTool;

/**
 * @author zhongping.wang 监测器日志查询服务
 * 
 */
public class LogServices {
	public static final String module = LogServices.class.getName();
	/**
	 * 单监测器报表查询
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/**public static Map<String, Object> queryReportData(DispatchContext dctx,
			Map<String, ?> context) throws Exception {
		Map<String, Object> response = ServiceUtil.returnSuccess();
		String tablename = (String) context.get("tablename");
		String monitorId = (String) context.get("operationId");
		if (monitorId.contains(",")) {
			monitorId = monitorId.replaceAll(",", "");
		}
		int from = (Integer) context.get("from");
		int to = (Integer) context.get("to");
		String startTime = (String) context.get("starttime");
		String endTime = (String) context.get("endtime");
		Timestamp startdate = Timestamp.valueOf(startTime);
		Timestamp enddate = Timestamp.valueOf(endTime);
		List<EntityCondition> entityConditionList = FastList.newInstance();
		
		entityConditionList.add(EntityCondition.makeCondition(
				"operationId", EntityOperator.EQUALS, monitorId));
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
		List<String> orderBy = UtilMisc.toList("logTime DESC");
		Delegator delegator = dctx.getDelegator();
		EntityListIterator resultiterator = delegator
				.findListIteratorByCondition(dve, condition, null,
						fieldsToSelect, orderBy, null);
		List<GenericValue> result = resultiterator.getPartialList(from, to);
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		String[] str = new String[20];//时间戳=值 数组
		String[] str2 = new String[20];//ReturnName数组
		String[] descArray = new String[result.size()];
		String monitorName = "";
		int goodCount = 0;
		int errorCount = 0; 
		int warnCount = 0;
		String latestCreateTime = "";
		String latestDstr = "";
		String latestStatus = "";
		Debug.logInfo(">>>>监测器ID: "+monitorId+" 查询出 "+result.size()+" 条数据", module);
		// 开始解析封装数据
		for (int j = 0; j < result.size(); j++) {
			GenericValue genericValue = result.get(j);
			String category = genericValue.get("category").toString();
			if (category.equals("ok")) {
				goodCount++;
			}else if(category.equals("ERROR")||category.equals("BAD")){
				errorCount++;
			}else if(category.equals("WARNING")){
				warnCount++;
			}
				String reportDataValue = genericValue.get("description").toString();
//				String reportDataValue = valueStr.substring(0,
//						valueStr.length() - 1);
				monitorName = genericValue.get("name").toString();
				String logTime = genericValue.get("logTime").toString();
				logTime = logTime.substring(0,logTime.lastIndexOf("."));
				if (latestCreateTime.equals("")) {
					latestCreateTime = logTime;
					latestStatus = category;
					latestDstr = reportDataValue;
				}
				descArray[j] = logTime+"&"+reportDataValue+"#"+category;
				if (category.equals("BAD")) {
					continue;
				}
				String[] dataValue  = null;
				if (reportDataValue.contains(",")) {
					 dataValue = reportDataValue.split(",");
				}
				for (int g = 0; g < dataValue.length; g++) {
					String indexDataValue = dataValue[g];// 如：包成功率(%)=83.33
					String returnName = indexDataValue.substring(0, indexDataValue
							.indexOf("="));// 如：包成功率(%)
					String returnValue = indexDataValue.substring(indexDataValue
							.indexOf("=") + 1, indexDataValue.length());// 如：83.33
					String timeValueStr = logTime + "=" + returnValue + ",";
					if (str[g] != null) {
						str[g] = str[g] + timeValueStr;
					} else {
						str2[g] = returnName;
						str[g] = timeValueStr;
					}
				}
		}
		//封装里层Map
		for (int strSize = 0; strSize < str.length; strSize++) {
			if (str[strSize] != null) {
				String dataStrArrayStr = str[strSize];
				String[] dataStrArray = dataStrArrayStr.split(",");
				double[] dArray = new double[result.size()];
				String whenMax = "";
				String latest = "";
				for (int i = 0; i < dataStrArray.length; i++) {
					String dValue = dataStrArray[i];
					String val = dValue.substring(dValue.indexOf("=")+1,dValue.length()); 
					if (latest.equals("")) {
						latest = val;
					}
					dArray[i] = Double.parseDouble(val);
				}
				double max = ArrayTool.getDoubleArrayMax(dArray);//计算最大值
				double average = ArrayTool.getDoubleArrayAvg(dArray);//计算平均值
				double min = ArrayTool.getDoubleArrayMin(dArray);//计算最小值
				for (String indexstr : dataStrArray) {
					String val = indexstr.substring(indexstr.indexOf("=")+1,indexstr.length()); 
					if (whenMax.equals("")&&val.equals(String.valueOf(max)+"0")) {
						whenMax = indexstr.substring(0,indexstr.indexOf("=")); 
					}
				}
				//------------------------part one-----------------------------
				Map<String, String> returnMap = new FastMap<String, String>();
				String returnKey = "(Return_"+strSize+")"+monitorId;
				returnMap.put("MonitorName", monitorName);
				returnMap.put("ReturnName", str2[strSize]);
				returnMap.put("average", String.valueOf(average));
				returnMap.put("latest", latest);
				returnMap.put("max", String.valueOf(max));
				returnMap.put("min", String.valueOf(min));
				returnMap.put("detail", dataStrArrayStr);
				returnMap.put("sv_baseline", "1");
				returnMap.put("sv_drawimage", "1");
				returnMap.put("sv_drawmeasure", "1");
				returnMap.put("sv_drawtable", "1");
				returnMap.put("sv_primary", "1");
				returnMap.put("when_max", whenMax);
				fmap.put(returnKey, returnMap);
				
			} else {
				break;
			}
		}
				//------------------------part two-----------------------------
				Map<String, String> descMap = new FastMap<String, String>();
				String descKey = "(dstr)"+monitorId;
				for (String desc : descArray) {
					String logTime = desc.substring(0, desc.indexOf("&"));
					String descValue = desc.substring(desc.indexOf("&")+1, desc.indexOf("#"));
					String category = desc.substring(desc.indexOf("#")+1, desc.length());
					if (category.equals("ERROR")||category.equals("BAD")) {
						category="error";
					}else if(category.equals("WARNING")){
						category="warning";
					}else if(category.equals("OK")){
						category="ok";
					}
					descMap.put(logTime, category+" "+descValue);
				}
				descMap.put("MonitorName", monitorName);
				fmap.put(descKey, descMap);
				//------------------------part three-----------------------------
				Map<String, String> endMap = new FastMap<String, String>();
				for (int strSize = 0; strSize < str.length; strSize++){
					String threeKey = "(Return_"+strSize+")";
					if (str[strSize] != null) endMap.put(threeKey, "ReturnValue");
				}
				endMap.put("MonitorName", monitorName);
				String monitorCondition = getMonitorCondition(monitorId);
				endMap.put("errorCondition", monitorCondition);
				endMap.put("errorPercent", ArrayTool.percent(errorCount, result.size()));//错误百分比
				endMap.put("latestCreateTime", latestCreateTime);
				endMap.put("latestDstr", latestDstr);
				endMap.put("latestStatus", latestStatus);
				endMap.put("okPercent", ArrayTool.percent(goodCount, result.size()));//正确百分比
				endMap.put("warnPercent", ArrayTool.percent(warnCount, result.size()));//危险百分比
				fmap.put(monitorId, endMap);
				if (result.size()==0) {
					fmap.clear();
					Map<String, String> nodataMap = new FastMap<String, String>();
					nodataMap.put("return", "true");
					fmap.put("return", nodataMap);
				}
				response.put("logvalues", fmap);
		return response;
	} **/
	
	/**
	 * 多监测器报表查询
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> queryReportData(DispatchContext dctx,
			Map<String, ?> context) throws Exception {
		Map<String, Object> response = ServiceUtil.returnSuccess();
		String tablename = (String) context.get("tablename");
		String monitorIds = (String) context.get("operationId");
		int from = (Integer) context.get("from");
		int to = (Integer) context.get("to");
		String startTime = (String) context.get("starttime");
		String endTime = (String) context.get("endtime");
		Timestamp startdate = Timestamp.valueOf(startTime);
		Timestamp enddate = Timestamp.valueOf(endTime);
		String[] Ids =  monitorIds.split(",");
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		for (String monitorId : Ids) {
			List<EntityCondition> entityConditionList = FastList.newInstance();
			entityConditionList.add(EntityCondition.makeCondition(
					"operationId", EntityOperator.EQUALS, monitorId));
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
			List<String> orderBy = UtilMisc.toList("logTime DESC");
			Delegator delegator = dctx.getDelegator();
			EntityListIterator resultiterator = delegator
					.findListIteratorByCondition(dve, condition, null,
							fieldsToSelect, orderBy, null);
			List<GenericValue> result = resultiterator.getPartialList(from, to);
			
			String[] str = new String[20];//时间戳=值 数组
			String[] str2 = new String[20];//ReturnName数组
			String[] descArray = new String[result.size()];
			int goodCount = 0;
			int errorCount = 0; 
			int warnCount = 0;
			String latestCreateTime = "";
			String latestDstr = "";
			String latestStatus = "";
			Debug.logInfo(">>>>监测器ID: "+monitorId+" 查询出 "+result.size()+" 条数据", module);
			/*************在这用jsvapi查询导致冲突 暂时由ZK前台调用jsvapi查询*********************/
//			String monitorNameAndCondition = getMonitorNameAndCondition(monitorId);
			String monitorName = "";//monitorNameAndCondition.substring(0,monitorNameAndCondition.indexOf("#"));
//			String monitorCondition = ""; // monitorNameAndCondition.substring(monitorNameAndCondition.indexOf("#")+1,monitorNameAndCondition.length());
			// 开始解析封装数据
			for (int j = 0; j < result.size(); j++) {
				GenericValue genericValue = result.get(j);
				String category = genericValue.get("category").toString();
				monitorName = genericValue.get("name").toString();
				if (category.equals("ok")) {
					goodCount++;
				}else if(category.equals("error")||category.equals("bad")){
					errorCount++;
				}else if(category.equals("warning")){
					warnCount++;
				}
					String reportDataValue = genericValue.get("description").toString();
//					String reportDataValue = valueStr.substring(0,
//							valueStr.length() - 1);
					
					String logTime = genericValue.get("logTime").toString();
					logTime = logTime.substring(0,logTime.lastIndexOf("."));
					if (latestCreateTime.equals("")) {
						latestCreateTime = logTime;
						latestStatus = category;
						latestDstr = reportDataValue;
					}
					descArray[j] = logTime+"&"+reportDataValue+"#"+category;
					if (category.equals("BAD")) {
						continue;
					}
					String[] dataValue  = null;
					reportDataValue = reportDataValue.substring(0,reportDataValue.length()-1);
					
					if (reportDataValue.contains(",")) {
						 dataValue = reportDataValue.split(",");
					}
					if (dataValue!=null) {
						for (int g = 0; g < dataValue.length; g++) {
							String indexDataValue = dataValue[g];// 如：包成功率(%)=83.33
							String returnName = indexDataValue.substring(0, indexDataValue
									.indexOf("="));// 如：包成功率(%)
							String returnValue = indexDataValue.substring(indexDataValue
									.indexOf("=") + 1, indexDataValue.length());// 如：83.33
							String timeValueStr = logTime + "=" + returnValue + ",";
							if (str[g] != null) {
								str[g] = str[g] + timeValueStr;
							} else {
								str2[g] = returnName;
								str[g] = timeValueStr;
							}
						}
					}
			}
			//封装里层Map
			for (int strSize = 0; strSize < str.length; strSize++) {
				if (str[strSize] != null) {
					String dataStrArrayStr = str[strSize];
					String[] dataStrArray = dataStrArrayStr.split(",");
					double[] dArray = new double[result.size()];
					String whenMax = "";
					String latest = "";
					for (int i = 0; i < dataStrArray.length; i++) {
						String dValue = dataStrArray[i];
						String val = dValue.substring(dValue.indexOf("=")+1,dValue.length()); 
						if (latest.equals("")) {
							latest = val;
						}
						dArray[i] = Double.parseDouble(val);
					}
					double max = ArrayTool.getDoubleArrayMax(dArray);//计算最大值
					double average = ArrayTool.getDoubleArrayAvg(dArray);//计算平均值
					double min = ArrayTool.getDoubleArrayMin(dArray);//计算最小值
					for (String indexstr : dataStrArray) {
						String val = indexstr.substring(indexstr.indexOf("=")+1,indexstr.length()); 
						if (whenMax.equals("")&&val.equals(String.valueOf(max)+"0")) {
							whenMax = indexstr.substring(0,indexstr.indexOf("=")); 
						}
					}
					//------------------------part one-----------------------------
					Map<String, String> returnMap = new FastMap<String, String>();
					String returnKey = "(Return_"+strSize+")"+monitorId;
					returnMap.put("MonitorName", monitorName);
					returnMap.put("ReturnName", str2[strSize]);
					returnMap.put("average", String.valueOf(average));
					returnMap.put("latest", latest);
					returnMap.put("max", String.valueOf(max));
					returnMap.put("min", String.valueOf(min));
					returnMap.put("detail", dataStrArrayStr);
					returnMap.put("sv_baseline", "1");
					returnMap.put("sv_drawimage", "1");
					returnMap.put("sv_drawmeasure", "1");
					returnMap.put("sv_drawtable", "1");
					returnMap.put("sv_primary", "1");
					returnMap.put("when_max", whenMax);
					fmap.put(returnKey, returnMap);
					
				} else {
					break;
				}
			}
					//------------------------part two-----------------------------
					Map<String, String> descMap = new FastMap<String, String>();
					String descKey = "(dstr)"+monitorId;
					for (String desc : descArray) {
						String logTime = desc.substring(0, desc.indexOf("&"));
						String descValue = desc.substring(desc.indexOf("&")+1, desc.indexOf("#"));
						String category = desc.substring(desc.indexOf("#")+1, desc.length());
						if (category.equals("ERROR")||category.equals("BAD")) {
							category="error";
						}else if(category.equals("WARNING")){
							category="warning";
						}else if(category.equals("OK")){
							category="ok";
						}
						descMap.put(logTime, category+" "+descValue);
					}
					descMap.put("MonitorName", monitorName);
					fmap.put(descKey, descMap);
					//------------------------part three-----------------------------
					Map<String, String> endMap = new FastMap<String, String>();
					for (int strSize = 0; strSize < str.length; strSize++){
						String threeKey = "(Return_"+strSize+")";
						if (str[strSize] != null) endMap.put(threeKey, "ReturnValue");
					}
//					String monitorCondition = getMonitorNameAndCondition(monitorId);
					endMap.put("MonitorName", monitorName);
//					endMap.put("errorCondition", monitorCondition);
					endMap.put("errorPercent", ArrayTool.percent(errorCount, result.size()));//错误百分比
					endMap.put("latestCreateTime", latestCreateTime);
					endMap.put("latestDstr", latestDstr);
					endMap.put("latestStatus", latestStatus);
					endMap.put("okPercent", ArrayTool.percent(goodCount, result.size()));//正确百分比
					endMap.put("warnPercent", ArrayTool.percent(warnCount, result.size()));//危险百分比
					fmap.put(monitorId, endMap);
					if (result.size()==0) {
						fmap.clear();
						Map<String, String> nodataMap = new FastMap<String, String>();
						nodataMap.put("return", "true");
						fmap.put("return", nodataMap);
					}
		}
		
				response.put("logvalues", fmap);
		return response;
	}
	
	
	/**
	 * 获取监测器阀值
	 * @param monitorid
	 * @return
	 */
	/**
	public static String getMonitorNameAndCondition(String monitorid){
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		Map<String, String> ndata = new FastMap<String, String>();
		StringBuilder estr = new StringBuilder();
		
		ndata.put("dowhat", "QueryReportData");
		ndata.put("id", monitorid);
		ndata.put("compress","false");
		ndata.put("dstrNeed", "false");
		ndata.put("byCount", "2");
		
		Jsvapi.getInstance().GetUnivData(fmap, ndata, estr);
		String condition = "";
		String monitorName = "";
		try {
			Map<String, String> descMap = fmap.get(monitorid);
			condition = descMap.get("errorCondition");
			monitorName = descMap.get("MonitorName");
		} catch (Exception e) {
			// TODO: handle exception
			monitorName = "";
			condition = "";
		}
		return monitorName+"#"+condition;
	}
	public static void main(String[] args) {
		LogServices.getMonitorNameAndCondition("1.9.23.1");
	} **/
}
