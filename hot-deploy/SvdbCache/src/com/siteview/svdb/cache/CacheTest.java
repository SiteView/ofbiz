package com.siteview.svdb.cache;

import java.util.HashMap;

public class CacheTest {
	public static void main(String[] args) {
		// System.out.println(CacheManager.getSimpleFlag("alksd"));
		Cache c = new Cache();
		c.setKey("hhh");
		c.setValue("ssssss");
		CacheManager.putCache("abc", c);
//		CacheManager.putCache("def", new Cache());
//		CacheManager.putCache("ccc", new Cache());
		HashMap h = CacheManager.cacheMap;
		Cache ca = CacheManager.getCacheInfo("abc");
		System.out.println(ca.getValue().toString());
//		CacheManager.clearOnly("");
//		Cache c = new Cache();
//		for (int i = 0; i < 10; i++) {
//			CacheManager.putCache("" + i, c);
//		}
//		CacheManager.putCache("aaaaaaaa", c);
//		CacheManager.putCache("abchcy;alskd", c);
//		CacheManager.putCache("cccccccc", c);
//		CacheManager.putCache("abcoqiwhcy", c);
//		System.out.println("删除前的大小：" + CacheManager.getCacheSize());
//		CacheManager.getCacheAllkey();
//		CacheManager.clearAll("aaaa");
//		System.out.println("删除后的大小：" + CacheManager.getCacheSize());
//		CacheManager.getCacheAllkey();
	}
}
