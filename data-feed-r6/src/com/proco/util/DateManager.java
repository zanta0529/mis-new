package com.proco.util;

import java.util.concurrent.ExecutionException;

import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;

public class DateManager {
	public static java.util.concurrent.ConcurrentHashMap<String, String> exDate = new java.util.concurrent.ConcurrentHashMap<String, String>();
	static {
		//exDate.put("tse", "20230921");
		//exDate.put("otc", "20230921");
	}
	
	public static String getExchangeDate(String ex) {
		String ed = null; //exDate.get(ex);
		if(ed==null) {
		    try {
		    	String cvals = (String)RedisManager.aredis0.submitCommand(RedisCommand.GET , ex).get().getResult();
		    	int d = Utility.parseInt(cvals);
		    	if(d!=0) {
		    		ed = cvals;
		    		exDate.put(ex,ed);
		    	} else ed = "";
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ed;
	}
	
	public static void setExchangeDate(String ex,String date) {
	    RedisCommandInfo[] rinfo = new RedisCommandInfo[] {new RedisCommandInfo(RedisCommand.SET, ex, date)};
	    RedisManager.aredis0.submitCommands(rinfo);	
		
	    try {
	    	String cvals = (String)RedisManager.aredis0.submitCommand(RedisCommand.GET , ex).get().getResult();
	    	int d = Utility.parseInt(cvals);
	    	if(d!=0 && date.equals(cvals)) exDate.put(ex,date);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
	
}
