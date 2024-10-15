package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;
import com.proco.datautil.StockCategory;
import com.proco.datautil.StockNames;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcStockCategory {
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcStockCategory> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcStockCategory>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	
	public static java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	ConcurrentSkipListMap<Long,Map<String,String> > msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String> >();
	String baseKey = "";
	String date = "";
	Runnable runner = null;
	int hashcode;
	
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public static void proc(String baseKey, String date, Map<String,String> msg) {

		final ProcStockCategory ua = ProcStockCategoryFactory(baseKey,date);
		ua.msgQueue0.put(execCount.addAndGet(1), msg);
		if(uaMsgQueue.contains(baseKey)) return;
		uaMsgQueue.add(baseKey);
		threadPool.schedule(ua.runner, 3000, TimeUnit.MILLISECONDS, baseKey);
	}
	

	public static ProcStockCategory ProcStockCategoryFactory(String baseKey,String date){
		ProcStockCategory ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcStockCategory(baseKey,date);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}	
	
	public ProcStockCategory(String baseKey,String date) {
		this.baseKey = baseKey;
		this.date = date;
		this.hashcode = (baseKey+"ProcStockCategory").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcStockCategory ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
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
		List<RedisCommandInfo> rinfos = new ArrayList<RedisCommandInfo>();
		ConcurrentSkipListMap<String,String> infoHash2 = new ConcurrentSkipListMap<String, String>();
		int c = 0;
		while(!msgQueue0.isEmpty()) {
			Map<String,String> js = msgQueue0.pollFirstEntry().getValue();
			infoHash2.putAll(js);
			c++;
			if(c == 150) {
				c=0;
		        StockCategory scit = new StockCategory(this.date,this.baseKey,infoHash2);
		        scit.insertCF(rinfos);
		        //System.out.println(Utility.getFullyDateTimeStr()+" ProcStockCategory_0 ->"+baseKey+" to "+infoHash2.size());
		        infoHash2 = new ConcurrentSkipListMap<String, String>();
			}
			
		}
		if(infoHash2.isEmpty()) return;
		
        StockCategory scit = new StockCategory(this.date,this.baseKey,infoHash2);
        scit.insertCF(rinfos);
        //System.out.println(Utility.getFullyDateTimeStr()+" ProcStockCategory_1 ->"+baseKey+" to "+infoHash2.size());
        while(RedisManager.size()>1000) Utility.sleep(100);
        StockNames.exec(hashcode,rinfos);
        
	}
	
}
