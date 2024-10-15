package com.proco.exec;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.proco.datautil.StockDelay;
import com.proco.datautil.StockStatis;
import com.proco.datautil.StockStatisInfo;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;


public class ProcF02 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF02> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF02>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	ConcurrentSkipListMap<Long,Map<String,String>> msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String>>();
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	Runnable runner = null;
	int hashcode;
	long lastTimeMillis = 0;
	
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
	
	public ProcF02(String ex, String baseKey,String date,String securityID) {
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.ex = ex;
		this.hashcode = (baseKey+"_ProcF02").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF02 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcF02 ProcF02Factory(String ex, String baseKey,String date,String securityID){
		ProcF02 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF02(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_stockstatisinfo_"+securityID;
		final ProcF02 ua = ProcF02Factory(ex,baseKey,date,securityID);
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
		}
		
		if(!snapshot.isEmpty()) {
			String time = snapshot.get("%");

	        //Time Patch	
	        if(time!=null) {
	        	if(time.equals("14:30:00") || time.equals("13:35:00") ){		

	        		String key = date+"_"+ex+"_4";
	        		StockDelay sa = new StockDelay(ex,date,key);	
	        		boolean ip_empty = sa.get().isEmpty();

	        		if(!ip_empty) {
	        			snapshot.put("%", "13:33:00");
	        		}
	        		else {	
	        			snapshot.put("%", "13:31:00");
	        		}
	        	}	
	        }
			
			
			if(time.endsWith(":00")||time.equals("09:00:15")){
				Set<String> names = snapshot.keySet();
				for(String name : names) {
					String val = snapshot.get(name);
					if(val!=null) infojs.put(name, val);
				}
				java.util.TreeMap<String,String> statisHash = new java.util.TreeMap<String, String> ();
				statisHash.put(time, infojs.toString());
				StockStatisInfo ss = new StockStatisInfo(ex,date,statisHash);
				ss.insertCF();
			}
			
        	long thisMillis = Utility.getSQLTimeFormatTransMillis(time);
        	if(thisMillis>=this.lastTimeMillis) lastTimeMillis = thisMillis;
        	else return;
			
			StockStatis ss = new StockStatis(ex,date,snapshot);
			ss.insertCF();
		}
		
	}
	
}