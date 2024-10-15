package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;
import com.proco.datautil.StockNames;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcStockName {
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcStockName> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcStockName>();
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
		
		long a = Utility.ckeckLong(baseKey);
		if(a==-1) { //非数字要 3byte
			byte[] check = baseKey.getBytes();
			if(check.length<3) return;			
		} else { //非数字要 2byte
			byte[] check = baseKey.getBytes();
			if(check.length<2) return;		
		}
		
		final ProcStockName ua = ProcStockNameFactory(baseKey,date);
		ua.msgQueue0.put(execCount.addAndGet(1), msg);
		if(uaMsgQueue.contains(baseKey)) return;
		uaMsgQueue.add(baseKey);
		threadPool.schedule(ua.runner, 3000, TimeUnit.MILLISECONDS, baseKey);
	}
	

	public static ProcStockName ProcStockNameFactory(String baseKey,String date){
		ProcStockName ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcStockName(baseKey,date);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}	
	
	public ProcStockName(String baseKey,String date) {
		this.baseKey = baseKey;
		this.date = date;
		this.hashcode = (baseKey+"ProcStockName").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcStockName ua0 = this;
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
			if(js!=null) {
		        for (Map.Entry<? extends String, ? extends String> e : js.entrySet()) {
		        	String k = e.getKey();
		        	String v = e.getValue();
		        	if(k!=null&&v!=null)
		        		infoHash2.put(k, v);
		        }
				//infoHash2.putAll(js);
				c++;				
			}
			if(c == 150) {
				c=0;
				// StockNames(String key, String date, NavigableMap<String, String> dataHash) 
				StockNames scit = new StockNames(this.baseKey,this.date,infoHash2);
		        scit.insertCF(rinfos);
		        //System.out.println(Utility.getFullyDateTimeStr()+" ProcStockName_0 ->"+baseKey+" to "+infoHash2.size());
		        infoHash2 = new ConcurrentSkipListMap<String, String>();
			}
			
		}
		if(infoHash2.isEmpty()) return;
		
		StockNames scit = new StockNames(this.baseKey,this.date,infoHash2);
        scit.insertCF(rinfos);
        //System.out.println(Utility.getFullyDateTimeStr()+" ProcStockName_1 ->"+baseKey+" to "+infoHash2.size());
        while(RedisManager.size()>1000) Utility.sleep(100);
        StockNames.exec(hashcode,rinfos);
        
	}
	
}