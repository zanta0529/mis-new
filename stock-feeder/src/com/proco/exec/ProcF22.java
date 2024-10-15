package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;
import com.proco.datautil.OddDetail;
import com.proco.util.ExecutorManager;


public class ProcF22 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF22> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF22>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	ConcurrentSkipListMap<Long,Map<String,String>> msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String>>();
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	String rowkeyString;
	Runnable runner = null;
	int hashcode;
	public java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	public ConcurrentSkipListMap<String,String> infoHash2 = new ConcurrentSkipListMap<String, String>();
	
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public ProcF22(String ex, String baseKey,String date,String securityID) {
		this.ex = ex;
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.rowkeyString = ex+"_"+securityID+"_"+date; //tse_1102.tw_20230911
		this.hashcode = (baseKey+"_ProcF22").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF22 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public synchronized static ProcF22 ProcF22Factory(String ex,String baseKey,String date,String securityID){
		ProcF22 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF22(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String name = msg.get("n");
		if(name==null) return;
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_"+securityID;

		if(uaMsgQueue.contains(baseKey)) return;
		else uaMsgQueue.add(baseKey);
		
		/*
        if(securityID.equals("t00.tw")||securityID.equals("o00.tw")) { //Date Setting & print detail
        	String d0 = DateManager.getExchangeDate(ex);
        	if(d0.equals("")) {
        		DateManager.setExchangeDate(ex, date);
        		System.out.println(Utility.getFullyDateTimeStr()+" Set DateManager ->"+date+" "+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+msg.toString());
        	}
        }*/
		final ProcF22 ua = ProcF22Factory(ex,baseKey,date,securityID);
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
		boolean inst = false;
		while(!msgQueue0.isEmpty()) {
			Map<String,String> js = msgQueue0.pollFirstEntry().getValue();
			infoHash2.putAll(js);
			//js.clear();
			inst = true;
		}
		
		
        if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
        	System.out.println("--F22--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+inst+" "+infoHash2.toString());
        }
		
		if(inst && !infoHash2.isEmpty()) {
			List<RedisCommandInfo> rinfos = new ArrayList<RedisCommandInfo>();
			OddDetail sd = new OddDetail(date,infoHash2,false);
			sd.insertCF(rinfos);					
			OddDetail.exec(hashcode,rinfos);
		}
		
	}
	
}