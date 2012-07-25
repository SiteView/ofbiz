package com.siteview.svdb.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * 缓存WebSersvice接收的数据
 * @author zhongping.wang
 *
 */
public class EccLogQueue {

	public static ArrayList<Map<String, String>> listMap;

	public EccLogQueue() {
		listMap = new ArrayList<Map<String, String>>();
	}
	//获取队列第一个元素
	public Map<String, String> getFirst() {
		if (!listMap.isEmpty()) {
			Map<String, String> firstMap = listMap.get(0);
			listMap.remove(0);
			return firstMap;
		}
		return null;
	}
	//在队列末尾加入元素
	public void insertLast(Map<String, String> map) {
		listMap.add(map);
		System.out.println(listMap.size());
	}

	public static void main(String[] args) {
		EccLogQueue seq = new EccLogQueue();
		Map<String, String>  m1 = new HashMap<String, String>(); 
		m1.put("1key", "1Val");
		Map<String, String>  m2 = new HashMap<String, String>(); 
		m2.put("2key", "2Val");
		Map<String, String>  m3 = new HashMap<String, String>(); 
		m3.put("3key", "3Val");
		seq.insertLast(m1);
		seq.insertLast(m2);
		seq.insertLast(m3);
		System.out.println(seq.getFirst());
		System.out.println(seq.getFirst());
		System.out.println(seq.getFirst());
	}
}
