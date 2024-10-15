package com.proco.util;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;
import com.proco.datautil.StockNameStore;
import com.proco.exec.ProcF01;
import com.proco.exec.ProcL01;

public class DateManager {
	public static java.util.concurrent.atomic.AtomicBoolean flushall = new java.util.concurrent.atomic.AtomicBoolean(false);
	public static java.util.concurrent.ConcurrentHashMap<String, String> exDate = new java.util.concurrent.ConcurrentHashMap<String, String>();
	static {
		//exDate.put("tse", "20230921");
		//exDate.put("otc", "20230921");
	}
	
	public static String getExchangeDate(String ex) {
		String ed = exDate.get(ex);
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
	
	public static void execFlushall(String ex, String date,String baseKey, Map<String,String> msg) {
    	String d0 = DateManager.getExchangeDate(ex);
    	boolean flush = false;
    	if(d0.equals("")) {
        	if(!flushall.getAndSet(true)) {
        		RedisManager.aredis0.submitCommand(RedisCommand.FLUSHALL);
        		ProcF01.uaMsgQueue.clear();
        		ProcL01.uaMsgQueue.clear();
        		exDate.clear();
        		flush = true;
        	}
    		DateManager.setExchangeDate(ex, date);
    		System.out.println(Utility.getFullyDateTimeStr()+" Set First DateManager ->"+date+" "+baseKey+" flush:"+flush+" "+msg.toString());
    	} else if(!d0.equals(date)) {
        	if(!flushall.getAndSet(true)) {
        		RedisManager.aredis0.submitCommand(RedisCommand.FLUSHALL);
        		ProcF01.uaMsgQueue.clear();
        		ProcL01.uaMsgQueue.clear();
        		exDate.clear();
        		flush = true;
        	}
    		DateManager.setExchangeDate(ex, date);
    		System.out.println(Utility.getFullyDateTimeStr()+" Set Flush DateManager ->"+d0+" to "+date+" "+baseKey+" flush:"+flush+" "+msg.toString());
    	} else System.out.println(Utility.getFullyDateTimeStr()+" Reload DateManager ->"+d0+" to "+date+" "+baseKey+" flush:"+flush+" "+msg.toString());
    	if(flush) StockNameStore.insertEtfIndexDetail(date,"");
	}
	
}
