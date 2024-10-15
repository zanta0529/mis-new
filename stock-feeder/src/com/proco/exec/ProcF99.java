package com.proco.exec;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.proco.datautil.SystemStatus;
import com.proco.util.ExecutorManager;

public class ProcF99 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF99> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF99>();
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
	
	public ProcF99(String ex, String baseKey,String date,String securityID) {
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.ex = ex;
		this.hashcode = (baseKey+"_ProcF99").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF99 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcF99 ProcF99Factory(String ex, String baseKey,String date,String securityID){
		ProcF99 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF99(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_systemstatus_"+securityID;
		final ProcF99 ua = ProcF99Factory(ex,baseKey,date,securityID);
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
		java.util.TreeMap<String,String> snapshot = new java.util.TreeMap<String, String>();
		while(!msgQueue0.isEmpty()) {
			Map<String,String> js = msgQueue0.pollFirstEntry().getValue();
			snapshot.putAll(js);
			if(ex.equals("tse")) snapshot.put("c", String.valueOf(ProcF01.tse1Count.get()));
			if(ex.equals("otc")) snapshot.put("c", String.valueOf(ProcF01.otc1Count.get()));
		}
		
		if(!snapshot.isEmpty()) {
			
			SystemStatus ss = new SystemStatus(ex,date,snapshot);
			ss.insertCF();
		}
		
	}
	
}