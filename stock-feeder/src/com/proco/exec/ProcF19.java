package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.datautil.StockDetail;
import com.proco.datautil.StockNames;
import com.proco.datautil.StockQuote;
import com.proco.datautil.StockTickerIndex;
import com.proco.datautil.StockTickerInfo;
import com.proco.util.ExecutorManager;

public class ProcF19 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF19> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF19>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	ConcurrentSkipListMap<Long,Map<String,String>> msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String>>();
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	Runnable runner = null;
	int hashcode;
	
	org.json.JSONObject infojs = new org.json.JSONObject();
	public java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	
	
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public ProcF19(String ex, String baseKey,String date,String securityID) {
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.ex = ex;
		this.hashcode = (baseKey+"_ProcF19").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF19 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcF19 ProcF19Factory(String ex, String baseKey,String date,String securityID){
		ProcF19 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF19(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_systemstatus_"+securityID;
		final ProcF19 ua = ProcF19Factory(ex,baseKey,date,securityID);
		ua.msgQueue0.put(ua.execCount.addAndGet(1), msg);
		threadPool.submit(ua.runner,baseKey);
	}
	
	long lastRunThread = System.currentTimeMillis();
	boolean send = false;
	AtomicBoolean runThread = new AtomicBoolean(false);
	AtomicBoolean runCheck = new AtomicBoolean(false);
	public void threadProcess(){
		if(!runThread.getAndSet(true)) {
			try {
				threadProcess1();
				while(runCheck.getAndSet(false)) threadProcess1();
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			} finally {
				runThread.set(false);
				//runThread = false;
			}
		} else runCheck.set(true);
	}	
	
	public void threadProcess1(){
		
		
		List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
		java.util.TreeMap<String,String> snapshot = new java.util.TreeMap<String, String>();
		while(!msgQueue0.isEmpty()) {
			Map<String,String> js = msgQueue0.pollFirstEntry().getValue();
			js.put("^", date);

			String securityID = js.get("@");
			String baseKey = ex+"_"+date+"_"+securityID;
			
			ProcF01 f01 = ProcF01.uaHash.get(baseKey);
			if(f01==null) return;
			
		    String st = js.remove("st");
		    String rt = js.remove("rt");

		    f01.infoHash2.remove("st");
		    f01.infoHash2.remove("rt");
		    boolean inst = false;
		    if(st!=null) {
		    	js.put("st", st);
		    	f01.infoHash2.put("st", st);
		    	inst = true;
		    }
		    if(rt!=null) {
		    	js.put("rt", rt);
		    	f01.infoHash2.put("rt", rt);
		    	inst = true;
		    }
		    
		    System.out.println("--19--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+inst+" "+js.toString());
			if(!snapshot.isEmpty()) {
				StockDetail sp = new StockDetail(date,snapshot,false);
				sp.insertCF(ks);
			}
		}
		
		
		
	
		StockNames.exec(hashcode,ks);
	}
	
}