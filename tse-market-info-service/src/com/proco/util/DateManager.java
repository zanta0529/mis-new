package com.proco.util;



public class DateManager {
	public static java.util.concurrent.ConcurrentHashMap<String, String> exDate = new java.util.concurrent.ConcurrentHashMap<String, String>();
	static {
		//exDate.put("tse", "20230921");
		//exDate.put("otc", "20230921");
	}
	
	public static String getExchangeDate(String ex) {
		String ed = exDate.get(ex);
		if(ed==null) ed = "";
		return ed;
	}
	
	public static void setExchangeDate(String ex,String date) {
		exDate.put(ex,date);
	   
	}
	
}