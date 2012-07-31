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
 * @author zhongping.wang 监测器日志查询服�?
 * 
 */
public class LogServices {
	public static final String module = LogServices.class.getName();
	/**
	 * query monitor logs
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
		String monitorId = (String) context.get("operationId");
		int from = (Integer) context.get("from");
		int to = (Integer) context.get("to");
		String startTime = (String) context.get("starttime");
		String endTime = (String) context.get("endtime");
		Timestamp startdate = Timestamp.valueOf(startTime);
		Timestamp enddate = Timestamp.valueOf(endTime);
		List<EntityCondition> entityConditionList = FastList.newInstance();
		
//		if (monitorIds != null && !monitorIds.isEmpty()) {
//			String[] ids =monitorIds.split(",");
//			for(int idssize=0;idssize<ids.length;idssize++){
//				if(!(ids[idssize].equals(""))&&ids[idssize].length()>0)
//					entityConditionList.add(EntityCondition.makeCondition(
//						"operationId", EntityOperator.EQUALS, ids[idssize]));
//			}
//		}
		
	
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
		List<GenericValue> result = resultiterator.getPartialList(from, to
				- from);
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		String[] str = new String[20];//时间�?�?数组
		String[] str2 = new String[20];//ReturnName数组
		String[] descArray = new String[result.size()];
		String monitorName = "";
		int goodCount = 0;
		int errorCount = 0; 
		int warnCount = 0;
		String latestCreateTime = "";
		String latestDstr = "";
		String latestStatus = "";
		Debug.logInfo(">>>>监测器ID: "+monitorId+" 查询�?"+result.size()+" 条数�?, module);
		// �?��解析封装数据
		for (int j = 0; j < result.size(); j++) {
			GenericValue genericValue = result.get(j);
			String category = genericValue.get("category").toString();
			if (category.equals("ok")) {
				goodCount++;
			}else if(category.equals("ERROR")){
				errorCount++;
			}else if(category.equals("WARNING")){
				warnCount++;
			}
			String valueStr = genericValue.get("description").toString();
			String reportDataValue = valueStr.substring(0,
					valueStr.length() - 1);
			monitorName = genericValue.get("name").toString();
			String logTime = genericValue.get("logTime").toString();
			logTime = logTime.substring(0,logTime.lastIndexOf("."));
			if (latestCreateTime.equals("")) {
				latestCreateTime = logTime;
				latestStatus = category;
				latestDstr = reportDataValue;
			}
			descArray[j] = logTime+"&"+reportDataValue+"#"+category;
			String[] dataValue = reportDataValue.split(",");
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
				double max = ArrayTool.getDoubleArrayMax(dArray);//计算�?���?
				double average = ArrayTool.getDoubleArrayAvg(dArray);//计算平均�?
				double min = ArrayTool.getDoubleArrayMin(dArray);//计算�?���?
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
				endMap.put("errorCondition", "?");
				endMap.put("errorPercent", ArrayTool.percent(errorCount, result.size()));//错误百分�?
				endMap.put("latestCreateTime", latestCreateTime);
				endMap.put("latestDstr", latestDstr);
				endMap.put("latestStatus", latestStatus);
				endMap.put("okPercent", ArrayTool.percent(goodCount, result.size()));//正确百分�?
				endMap.put("warnPercent", ArrayTool.percent(warnCount, result.size()));//危险百分�?
				fmap.put(monitorId, endMap);
				if (result.size()==0) {
					fmap.clear();
					Map<String, String> nodataMap = new FastMap<String, String>();
					nodataMap.put("return", "true");
					fmap.put("return", nodataMap);
				}
				response.put("logvalues", fmap);
		return response;
	}
}
