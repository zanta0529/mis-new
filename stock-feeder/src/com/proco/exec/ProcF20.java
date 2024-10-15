package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.datautil.StockCategory;
import com.proco.datautil.StockDelay;
import com.proco.datautil.StockDetail;
import com.proco.datautil.StockNames;
import com.proco.datautil.StockQuote;
import com.proco.datautil.StockTickerIndex;
import com.proco.datautil.StockTickerInfo;
import com.proco.datautil.StockTickerTime;
import com.proco.io.ConfigData;
import com.proco.logicutil.OtOhlcManager;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcF20 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcF20> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcF20>();
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
	
	public ProcF20(String ex,String baseKey,String date,String securityID) {
		this.ex = ex;
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.hashcode = (baseKey+"_ProcF20").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcF20 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcF20 ProcF20Factory(String ex,String baseKey,String date,String securityID){
		ProcF20 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcF20(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_"+securityID;
		final ProcF20 ua = ProcF20Factory(ex,baseKey,date,securityID);
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
			ProcF01 p01 = ProcF01.uaHash.get(this.baseKey);
			if(p01!=null) infoHash2.putAll(p01.infoHash2);
			else {
				String rowkeyString = ex+"_"+securityID+"_"+date;
				StockDetail sd = new StockDetail(date,rowkeyString);
				Map<String,String> sdMap = sd.getMap();
				if(!sdMap.isEmpty()) {
					sdMap.remove("key");
					infoHash2.putAll(sdMap);
				}
				StockQuote sq = new StockQuote(date,rowkeyString);
				Map<String,String> sqMap = sq.getMap();
				if(!sqMap.isEmpty()) {
					sqMap.remove("key");
					infoHash2.putAll(sqMap);
				}
				
		        if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
		        	System.out.println("--F1R-20->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" sd:"+!sdMap.isEmpty()+" sq:"+!sqMap.isEmpty()+" "+infoHash2.toString());
		        }
			}
		}
		
		Map<String,RedisCommandInfo> ksm = new ConcurrentSkipListMap<String,RedisCommandInfo>();
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
	        boolean updateDetail = false;
			sf = msgQueue0.pollFirstEntry().getValue();
	        //Time Patch	
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
			
			ip0 = sf.get("ip");
			nVolumn = sf.get("v");
			if(nVolumn==null) nVolumn = "";
			nLast = sf.get("z");
			nTime = sf.get("%");
			
	        final StockTickerIndex sq = new StockTickerIndex(ex,date,infoHash2);
	        final StockTickerInfo sqinfo = new StockTickerInfo(ex,date,infoHash2);
	        if ( (sf.get("v") != null || sf.get("z") != null)) {
	        	if (!nVolumn.equals(lastTradeVolume)/* && !nVolumn.equals("")*/) {
	        		lastTradeVolume = nVolumn;
	        		String tk0 = infoHash2.get("tk1");
	        		if (tk0 != null)
	        			infoHash2.put("tk0", tk0);

	        		infoHash2.put("tk1", sq.getInfoKey());
	                //sq.insertIndex(ks);
	                sqinfo.insertCF(ksm);
	                if(ConfigData.saveTickTime==1){
	                	StockTickerTime stt = new StockTickerTime(ex,date,infoHash2);
	                	stt.insertTime(ksm);
	                }
	        	}
	            //分線 OHLC
	            if(it!=null && it.equals("t")){
	            	ohlcMgr.proc(threadPool,hashcode,infoHash2);
	            }
	        } else {
	            //rewrite tickid
	        	String io = infoHash2.get("io");
	            if(io!=null){
	            	if (io.startsWith("W"))
	            	{
	            		String tk0 = infoHash2.get("tk1");
	                if (tk0 != null)
	                	infoHash2.put("tk0", tk0);
	            	}
	            }
	        }
	        
			pLast = nLast;
			
			infoHash2.putAll(sf);
			//sf.clear();
			insert = true;
			
	        if(ip0 != null && ip1 != null)
	            updateDetail = !ip0.equals(ip1); 
	        
			if(updateDetail){
	            StockDetail sd = new StockDetail(date,infoHash2,false);
	            //System.out.println("StockDetail "+ infoHash2.get("ch")+" "+ infoHash2.get("#")+" "+ infoHash2.get("d")+" "+ infoHash2.get("t")+" "+sd.chk);
	            boolean ok = sd.insertCF(ksm);
	            
	            List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
	            
	            NavigableMap<String, String> keyHash = new java.util.concurrent.ConcurrentSkipListMap<>();            
	            keyHash.put(infoHash2.get("ch"),sd.getRowkeyString());
	            String key = (new StringBuffer(sd.getEx()).append("_ip_").append(ip1).
	                          append("_").append(infoHash2.get("^"))).toString();
	            StockCategory scip = new StockCategory(date,key);
	            scip.delete(infoHash2.get("ch"));
	            //System.out.println(Utility.getSQLDateTimeStr()+" delete : "+key+" "+infoHash2.get("ch"));

	            key = (new StringBuffer(sd.getEx()).append("_ip_").append(ip0).
	                   append("_").append(infoHash2.get("^"))).toString();
	            
	            scip = new StockCategory(date,key,keyHash);
	            scip.insertCF(ks);
	            // System.out.println(Utility.getSQLDateTimeStr()+" insert : "+key+" "+infoHash2.get("ch"));

	            //Build StockDelay
	            if(!ip0.equals("0")){
	              //sd.rowkeyString
	              String sde_key = date+"_"+sd.getEx()+"_"+ip0;
	              NavigableMap<String, String> sdeMap = new java.util.concurrent.ConcurrentSkipListMap<>(); 
	              sdeMap.put(sd.rowkeyString,ip0);
	              StockDelay sde = new StockDelay(ex,date,sde_key,sdeMap);
	              sde.insertCF(ks);
	            }	
				if(ex.equals("otc")) ProcStockSnapshot.exec(0,ks);
				else ProcStockSnapshot.exec(1,ks);
			}			
	        
	        
		}
		
		String pVolumn = infoHash2.get("v");
		String pTime = infoHash2.get("%");

		if(pTime==null) pTime = "";
		if(nTime==null) nTime = "";
		if(pVolumn==null) pVolumn = "";
		if(nVolumn==null) nVolumn = "";
		if(nLast==null) nLast = "";
			

      

        /* /Time Patch	
        String time = infoHash2.get("%");
        if(time!=null) {
        	if(time.equals("14:30:00") || time.equals("13:35:00") ){		

        		String key = date+"_"+ex+"_4";
        		StockDelay sa = new StockDelay(ex,date,key);	
        		boolean ip_empty = sa.get().isEmpty();

        		if(!ip_empty) {
        			infoHash2.put("bt", "13:33:00");
        			infoHash2.put("%", "13:33:00");
        		}
        		else {	
        			infoHash2.put("bt", "13:31:00");
        			infoHash2.put("%", "13:31:00");
        		}
        	} else infoHash2.put("bt", time);
        }  */          
        
		if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
        	//System.out.println("-20--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+insert+" "+infoHash2.toString());
        }
		
		if(insert && !infoHash2.isEmpty()) {
			StockQuote sp = new StockQuote(date,infoHash2,false);
			sp.insertCF(ksm);
		}
				
		ProcStockSnapshot.exec(hashcode,ksm);
	}
	
}
