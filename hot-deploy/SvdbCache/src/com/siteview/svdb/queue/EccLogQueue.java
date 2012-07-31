package com.siteview.svdb.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.siteview.svdb.SvdbApiImpl;

/**
 * 缓存WebSersvice接收的数�?
 * 
 * @author zhongping.wang
 * 
 */
public class EccLogQueue {
	private static final Log log = LogFactory.getLog(EccLogQueue.class);
	public static ArrayList<Map<String, String>> listMap;

	public EccLogQueue() {
		listMap = new ArrayList<Map<String, String>>();
	}

	// 获取队列第一个元�?
	public Map<String, String> getFirst() {
		if (!listMap.isEmpty()) {
			Map<String, String> firstMap = listMap.get(0);
			listMap.remove(0);
			return firstMap;
		}
		return null;
	}

	// 批量获取元素
	public List<Map<String, String>> getBatchMap(int mapCount) {
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

		if (!listMap.isEmpty()) {
			int listSize = listMap.size();
			if (listSize < mapCount) {
				log.info("队列中不�?"+mapCount+" 条数�?�?��取队列的�?��数据 "+listSize+" �?);
				mapCount = listSize;
			}
			for (int i = 0; i < mapCount; i++) {
				Map<String, String> indexMap = listMap.get(0);
				mapList.add(indexMap);
				listMap.remove(0);
			}
			return mapList;
		}
		return null;
	}

	// 在队列末尾加入元�?
	public void insertLast(Map<String, String> map) {
		listMap.add(map);
		System.out.println(listMap.size());
	}

	public static void main(String[] args) {
		EccLogQueue seq = new EccLogQueue();
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("1key", "1Val");
		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("2key", "2Val");
		Map<String, String> m3 = new HashMap<String, String>();
		m3.put("3key", "3Val");
		seq.insertLast(m1);
		seq.insertLast(m2);
		seq.insertLast(m3);
		System.out.println(seq.getFirst());
		System.out.println(seq.getFirst());
		System.out.println(seq.getFirst());
	}
}
