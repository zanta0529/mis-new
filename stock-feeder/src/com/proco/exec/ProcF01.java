package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.datautil.StockDelay;
import com.proco.datautil.StockDetail;
import com.proco.datautil.StockNames;
import com.proco.datautil.StockNewboard;
import com.proco.util.DateManager;
import com.proco.util.ExecutorManager;

public class ProcF01 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF01> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF01>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	public static java.util.concurrent.ConcurrentSkipListSet<String> countQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	ConcurrentSkipListMap<Long,Map<String,String>> msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String>>();
	
	public static java.util.concurrent.atomic.AtomicLong tse1Count = new java.util.concurrent.atomic.AtomicLong(0);
	public static java.util.concurrent.atomic.AtomicLong otc1Count = new java.util.concurrent.atomic.AtomicLong(0);
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	String rowkeyString;
	Runnable runner = null;
	int hashcode;
	public java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	public ConcurrentSkipListMap<String,String> infoHash2 = new ConcurrentSkipListMap<String, String>();
	
	public static java.util.concurrent.atomic.AtomicBoolean flushall = new java.util.concurrent.atomic.AtomicBoolean(false);
	
	
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public ProcF01(String ex, String baseKey,String date,String securityID) {
		this.ex = ex;
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.rowkeyString = ex+"_"+securityID+"_"+date; //tse_1102.tw_20230911
		this.hashcode = (baseKey+"_ProcF01").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF01 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public synchronized static ProcF01 ProcF01Factory(String ex,String baseKey,String date,String securityID){
		ProcF01 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF01(ex,baseKey,date,securityID);
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
		
        if(securityID.equals("t00.tw")||securityID.equals("o00.tw")) { //Date Setting & print detail
        	DateManager.execFlushall(ex, date, baseKey, msg);
        	/*
        	String d0 = DateManager.getExchangeDate(ex);
        	boolean flush = false;
        	if(d0.equals("")) {
            	if(!flushall.getAndSet(true)) {
            		RedisManager.aredis0.submitCommand(RedisCommand.FLUSHALL);
            		uaMsgQueue.clear();
            		flush = true;
            	}
        		DateManager.setExchangeDate(ex, date);
        		System.out.println(Utility.getFullyDateTimeStr()+" Set First DateManager ->"+date+" "+uaMsgQueue.contains(baseKey)+" "+baseKey+" flush:"+flush+" "+msg.toString());
        	} else if(!d0.equals(date)) {
            	if(!flushall.getAndSet(true)) {
            		RedisManager.aredis0.submitCommand(RedisCommand.FLUSHALL);
            		uaMsgQueue.clear();
            		flush = true;
            	}
        		DateManager.setExchangeDate(ex, date);
        		System.out.println(Utility.getFullyDateTimeStr()+" Set Flush DateManager ->"+d0+" to "+date+" "+uaMsgQueue.contains(baseKey)+" "+baseKey+" flush:"+flush+" "+msg.toString());
        	} else System.out.println(Utility.getFullyDateTimeStr()+" Reload DateManager ->"+d0+" to "+date+" "+uaMsgQueue.contains(baseKey)+" "+baseKey+" flush:"+flush+" "+msg.toString());
        	if(flush) StockNameStore.insertEtfIndexDetail(date,""); */
        }
		final ProcF01 ua = ProcF01Factory(ex,baseKey,date,securityID);
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
        	System.out.println("--F1--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+inst+" "+infoHash2.toString());
        }
		
		if(inst && !infoHash2.isEmpty()) {
			List<RedisCommandInfo> rinfos = new ArrayList<RedisCommandInfo>();
			
			StockDetail sd = new StockDetail(date,infoHash2,false);
			sd.insertCF(rinfos);
			
			String packageId = infoHash2.get("#");
			if(packageId!=null) {
				if(!countQueue.contains(this.baseKey)) {
					countQueue.add(this.baseKey);
					if(packageId.startsWith("1.tse"))tse1Count.addAndGet(1);
					if(packageId.startsWith("1.otc"))otc1Count.addAndGet(1);	
				}
			}
			
			String bp = (String)infoHash2.get("bp"); //創新版
			if(bp==null) bp="0"; //舊版本支持
			
			String i = (String)infoHash2.get("i");
			String it = (String)infoHash2.get("it");
			String ip = (String)infoHash2.get("ip");
			String p = (String)infoHash2.get("p");
			String io = (String)infoHash2.get("io");
			
			TreeMap<String, String> keyHash = new TreeMap<String,String>();
			keyHash.put(this.securityID,sd.getRowkeyString());
			if (i != null) {
				String key = (new StringBuffer(ex).append("_i_").append(i).
						append("_").append(infoHash2.get("^"))).toString();
				ProcStockCategory.proc(key, date, keyHash);
				//StockCategory sci = new StockCategory(date,key,keyHash);
	            //sci.insertCF(rinfos);
			}
			if (it != null) {
				String key = (new StringBuffer(ex).append("_it_").append(it).
						append("_").append(infoHash2.get("^"))).toString();
				ProcStockCategory.proc(key, date, keyHash);
				//StockCategory scit = new StockCategory(date,key,keyHash);
	            //scit.insertCF(rinfos);
			}
			if (ip != null) {
				String key = (new StringBuffer(ex).append("_ip_").append(ip).
						append("_").append(infoHash2.get("^"))).toString();
				ProcStockCategory.proc(key, date, keyHash);
				//StockCategory scip = new StockCategory(date,key,keyHash);
	            //scip.insertCF(rinfos);
				
	              //Build StockDelay
	              if(!ip.equals("0")){
	                //sd.rowkeyString
	            	  String sde_key =  sd.getEx()+"_"+ip;
	            	  TreeMap<String, String> sdeMap = new TreeMap<String,String>();
	            	  sdeMap.put(sd.rowkeyString,ip);
	            	  StockDelay sde = new StockDelay(ex,date,sde_key,sdeMap);
	            	  sde.insertCF(rinfos);
	            }			
			}
			if (io != null) {	
				String key = (new StringBuffer(ex).append("_io_").append(io).
						append("_").append(infoHash2.get("^"))).toString();
				ProcStockCategory.proc(key, date, keyHash);
				//StockCategory scio = new StockCategory(date,key,keyHash);
	            //scio.insertCF(rinfos);
			}
			if (p != null) {
				String key = (new StringBuffer(ex).append("_p_").append(p).
						append("_").append(infoHash2.get("^"))).toString();
				ProcStockCategory.proc(key, date, keyHash);
				//StockCategory scp = new StockCategory(date,key,keyHash);
	            //scp.insertCF(rinfos);
			}

			//New Board
			if (!bp.equals("0")) {
				String key = (new StringBuffer(ex).append("_bp_").append(bp).
						append("_").append(infoHash2.get("^"))).toString();
				StockNewboard scp = new StockNewboard(date,key,keyHash);
	            scp.insertCF(rinfos);
			}
			///prepardStockNames
			StockNames.prepardStockNames(rinfos, sd.getEx(),infoHash2.get("ch"),infoHash2.get("n"),infoHash2.get("^"));
			
			StockNames.exec(hashcode,rinfos);
		}
		
	}
	
}
