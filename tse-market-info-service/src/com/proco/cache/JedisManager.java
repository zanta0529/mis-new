package com.proco.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

public class JedisManager {

	static JedisMultiClient jm;
	
	public static void init(String ip, String port,String passwd) {
		if (ip == null && port == null) {
			return;
		}
		if(jm==null) jm = new JedisMultiClient(ip,port,passwd);	
	}

	public static void init(String hosts, String passwd) {
		String port = "6379";
		String ip = "127.0.0.1";
		String[] hostSet = hosts.split(":");
		if(hostSet.length==1) {
			ip = hosts;
		}
		if(hostSet.length==2) {
			ip = hostSet[0];
			port = hostSet[1];
		}
		if (ip == null && port == null) {
			return;
		}
		if(jm==null) jm = new JedisMultiClient(ip,port,passwd);	
	}
	
	public static void initPreMarketDate() {
		jm.initPreMarketDate();

	}


	public static void close() {
		jm.close();
	}

	public static boolean isConnected() {
		return jm.isConnected();
	}
	
	public static long getCheckTime() {
		return jm.getCheckTime();
	}

	public static List<Tuple> getSortedSetByKey(String key) {
		return jm.getSortedSetByKey(key);
	}

	public static List<Tuple> getSortedSetByScore(String key, long score , int size) {
		return jm.getSortedSetByScore(key, score,size);
	}
	
	
	public static Map<String, String> getDataRowMapByKey(String key) {
		return jm.getDataRowMapByKey(key);
	}
	
	public static String getDateByMarket(String market) {	
		return jm.getDateByMarket(market);
	}

	/**
	 * 同步获取Jedis实例
	 * 
	 * @return Jedis
	 */
	public static synchronized Jedis getJedis() {
		return jm.getJedis();
	}

	public static String getCurrentCacheDate(String market) {
		return jm.marketToDateMap.get(market);
	}

	public static boolean verifyDateAfterClear(String market) {
		return jm.verifyDateAfterClear(market);
	}

	public static List<String> verifyDateAfterResetRedis() {
		return jm.verifyDateAfterResetRedis();
	}

	public static void clear() {
		jm.clear();
	}

	/**
	 * 
	 * 释放jedis资源
	 * 
	 * @param jedis
	 * 
	 */

	public static void returnResource(final Jedis jedis) {
		jm.returnResource(jedis);
	}

	
	public static void main(String[] args) {
		JedisManager.init("127.0.0.1", "10001",null);

		
		//List<Tuple> xxx = JedisManager.getSortedSetByScore("xxxx", 0, 1); 
		//System.out.println("--->"+xxx.toString());
		//JedisManager.close();
	}
	
}

