package com.proco.exec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;
import com.proco.datautil.OddDetail;
import com.proco.datautil.OddQuote;
import com.proco.datautil.StockDelay;
import com.proco.logicutil.OtOhlcManager;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcF23 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF23> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF23>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	ConcurrentSkipListMap<Long,Map<String,String> > msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String> >();
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	String lastTradeVolume = "";
	
	Runnable runner = null;
	int hashcode;
	long lastTimeMillis = 0;
	
	public java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	public ConcurrentSkipListMap<String,String> infoHash2 = new ConcurrentSkipListMap<String, String>();
	final OtOhlcManager ohlcMgr = new OtOhlcManager();
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public ProcF23(String ex,String baseKey,String date,String securityID) {
		this.ex = ex;
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.hashcode = (baseKey+"_ProcF23").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF23 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcF23 ProcF23Factory(String ex,String baseKey,String date,String securityID){
		ProcF23 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF23(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_"+securityID;
		final ProcF23 ua = ProcF23Factory(ex,baseKey,date,securityID);
		ua.msgQueue0.put(ua.execCount.addAndGet(1), msg);
		
		if(uaMsgQueue.contains(baseKey)) return;
		uaMsgQueue.add(baseKey);
		threadPool.schedule(ua.runner, 2000, TimeUnit.MILLISECONDS, baseKey);

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
		        
		uaMsgQueue.remove(baseKey);
		if(infoHash2.isEmpty()) {
			ProcF22 p22 = ProcF22.uaHash.get(this.baseKey);
			if(p22!=null) infoHash2.putAll(p22.infoHash2);
			else {
				String rowkeyString = ex+"_"+securityID+"_"+date;
				OddDetail od = new OddDetail(date,rowkeyString);
				Map<String,String> odMap = od.getMap();
				if(!odMap.isEmpty()) {
					odMap.remove("key");
					infoHash2.putAll(odMap);
				}
				OddQuote oq = new OddQuote(date,rowkeyString);
				Map<String,String> oqMap = oq.getMap();
				if(!oqMap.isEmpty()) {
					oqMap.remove("key");
					infoHash2.putAll(oqMap);
				}				
		        if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
		        	System.out.println("--F22R-23->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" od:"+!odMap.isEmpty()+" oq:"+!oqMap.isEmpty()+" "+infoHash2.toString());
		        }
			}			
		}
		
		String ip0 = "";
		String nVolumn = "";
		String nTime = "";
		String nLast = "";
		String pLast = infoHash2.get("z");
		String it = infoHash2.get("it");
		
		String ip1 = infoHash2.get("ip");
		boolean insert = false;
		Map<String,String> sf = null;
		while(!msgQueue0.isEmpty()) {
			sf = msgQueue0.pollFirstEntry().getValue();
			setCurrentOddInfo(sf);
			
			ip0 = sf.get("ip");
			nVolumn = sf.get("v");
			if(nVolumn==null) nVolumn = "";
			nLast = sf.get("z");
			nTime = sf.get("%");
			
	        String time = sf.get("%");
	        if(time!=null) {
	        	if(time.equals("14:30:00") || time.equals("13:35:00") ){		

	        		String key = date+"_"+ex+"_4";
	        		StockDelay sa = new StockDelay(ex,date,key);	
	        		boolean ip_empty = sa.get().isEmpty();

	        		if(!ip_empty) {
	        			sf.put("bt", "13:33:00");
	        			sf.put("%", "13:33:00");
	        		}
	        		else {	
	        			sf.put("bt", "13:31:00");
	        			sf.put("%", "13:31:00");
	        		}
	        	} else sf.put("bt", time);
	        	
	        	long thisMillis = Utility.getSQLTimeFormatTransMillis(time);
	        	if(thisMillis>=this.lastTimeMillis) lastTimeMillis = thisMillis;
	        	else continue;
	        }
			
			pLast = nLast;
			
			infoHash2.putAll(sf);
			//sf.clear();
			insert = true;
		}
		
		String pVolumn = infoHash2.get("v");
		String pTime = infoHash2.get("%");

		if(pTime==null) pTime = "";
		if(nTime==null) nTime = "";
		if(pVolumn==null) pVolumn = "";
		if(nVolumn==null) nVolumn = "";
		if(nLast==null) nLast = "";
			
        boolean updateDetail = false;
        if(ip0 != null && ip1 != null)
        	updateDetail = !ip0.equals(ip1);

       
        
		if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
        	//System.out.println("-23--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+insert+" "+infoHash2.toString());
        }
		
		if(insert && !infoHash2.isEmpty()) {
			Map<String,RedisCommandInfo> ks = new ConcurrentSkipListMap<String,RedisCommandInfo>();
			OddQuote sp = new OddQuote(date,infoHash2,false);
			sp.insertCF(ks);
			//OddQuote.exec(hashcode,ks);
			if(ex.equals("otc")) ProcStockSnapshot.exec(4,ks);
			else ProcStockSnapshot.exec(5,ks);
		}
	}
	
	public void setCurrentOddInfo(Map<String,String> sf) {
		String oz = "";
		String ns = "";
		String nz = "";

		String z = sf.get("z");
		if(z!=null) {
			 oz = (String)this.infoHash2.get("z");
			 nz = z;
			 this.infoHash2.put("z", z);
		}
		
		String s = sf.get("s");
		if(s!=null) {
			 ns = s;
				this.infoHash2.put("s", s);
		}		
	    if(ns!=null && nz!=null && !ns.equals("") && !ns.equals("0") && !nz.equals("")){
	    	this.setOHCL(nz, oz);
	    }		
		
	}

	public void setOHCL(String value, String oz) {
		if(oz==null || oz.equals("0") || oz.equals("0.0000")){ //open
			this.infoHash2.put("o", value);
			this.infoHash2.put("h", value);
			this.infoHash2.put("l", value);
		} else {
			BigDecimal current = new BigDecimal(value);
			if(this.infoHash2.get("o")==null) this.infoHash2.put("o",value);
			if(this.infoHash2.get("h")==null) this.infoHash2.put("h",value);
			if(this.infoHash2.get("l")==null) this.infoHash2.put("l",value);
			if(this.infoHash2.get("o").equals("-")) this.infoHash2.put("o",value);
			if(this.infoHash2.get("h").equals("-")) this.infoHash2.put("h",value);
			if(this.infoHash2.get("l").equals("-")) this.infoHash2.put("l",value);

			//try{BigDecimal high1 = new BigDecimal(this.infoHash.get("h"));} catch(Exception ex) {  System.out.println(this.infoHash.get("@")+"------>"+this.infoHash.get("h")+" ----> "+value+" : "+oz);}
			BigDecimal high = new BigDecimal(this.infoHash2.get("h"));
			BigDecimal low = new BigDecimal(this.infoHash2.get("l"));
			if(high.compareTo(current)==-1){
				this.infoHash2.put("h", value);
			}
			if(low.compareTo(current)==1){
				this.infoHash2.put("l", value);
			}
		}
	}	
}