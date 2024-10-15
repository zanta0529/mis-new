package com.proco.exec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aredis.cache.RedisCommandInfo;

import com.proco.datautil.StockDateList;
import com.proco.datautil.StockDelay;
import com.proco.datautil.StockDetail;
import com.proco.datautil.StockNames;
import com.proco.datautil.StockTimeLine;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcL20 {
	
	public static ExecutorManager threadPool = null;
	public static java.util.concurrent.ConcurrentHashMap<String, ProcL20> uaHash = new java.util.concurrent.ConcurrentHashMap<String, ProcL20>();
	public static java.util.concurrent.ConcurrentSkipListSet<String> uaMsgQueue = new java.util.concurrent.ConcurrentSkipListSet<String>();	
	public static java.util.concurrent.atomic.AtomicLong exec0 = new java.util.concurrent.atomic.AtomicLong(0);
	public static java.util.concurrent.atomic.AtomicLong exec1 = new java.util.concurrent.atomic.AtomicLong(0);
	
	String ex;
	String securityID;
	String baseKey;
	String date;
	String lastTradeVolume = "";
	long score = 9999999999999L;
	Runnable runner = null;
	int hashcode;
	ConcurrentSkipListMap<Long,Map<String,String> > msgQueue0 = new ConcurrentSkipListMap<Long,Map<String,String> >();
	
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
	
	public ProcL20(String ex,String baseKey,String date,String securityID) {
		this.ex = ex;
		this.securityID = securityID;
		this.baseKey = baseKey;
		this.date = date;
		this.hashcode = (baseKey+"_ProcF20").hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		final ProcL20 ua0 = this;
		this.runner = new Runnable() {	
			   @Override
			   public void run() {
				   ua0.threadProcess();
			   }
		}; 	
	}	

	public static ProcL20 ProcL20Factory(String ex,String baseKey,String date,String securityID){
		ProcL20 ua = uaHash.get(baseKey);
		if(ua==null){ 
			ua = new ProcL20(ex,baseKey,date,securityID);
			uaHash.put(baseKey, ua);
		}
		return ua;
	}
	
	public static void proc(String ex, String date, Map<String,String> msg) {
		String securityID = msg.get("@");
		String baseKey = ex+"_"+date+"_"+securityID;
		
		ProcL01 l01 = ProcL01.uaHash.get(baseKey);
		if(l01==null) return;
		
		final ProcL20 ua = ProcL20Factory(ex,baseKey,date,securityID);
		ua.msgQueue0.put(ua.execCount.addAndGet(1), msg);
		
		if(uaMsgQueue.contains(baseKey)) return;
		uaMsgQueue.add(baseKey);
		threadPool.schedule(ua.runner, 10000, TimeUnit.MILLISECONDS, baseKey);
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
			ProcL01 p01 = ProcL01.uaHash.get(this.baseKey);
			if(p01!=null) infoHash2.putAll(p01.infoHash2);
		}
		
        List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
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
	        }			
			
			ip0 = sf.get("ip");
			nVolumn = sf.get("v");
			if(nVolumn==null) nVolumn = "";
			nLast = sf.get("z");
			nTime = sf.get("%");
			
			if(nLast!=null) if(!nLast.equals("") && !nLast.equals("0")) {
				if(!sf.containsKey("h") && !sf.containsKey("l")) {
					if(securityID.equals("t00.tw") || securityID.equals("o00.tw")){
						String tt2 = sf.get("%");
						this.setOHCLx00(tt2, nLast, pLast);
					} else this.setOHCL(nLast, pLast);	
				}	
			}
			

			
			
			pLast = nLast;
			
			infoHash2.putAll(sf);
			insert = true;
		}
		
		if(insert) {
	        final StockTimeLine sq = new StockTimeLine(ex,date,infoHash2);
	        //if ( (sf.get("v") != null || sf.get("z") != null)) 
	        {
	        	//if (!nVolumn.equals(lastTradeVolume)/* && !nVolumn.equals("")*/) 
	        	{
	        		lastTradeVolume = nVolumn;
	        		String tk0 = infoHash2.get("tk1");
	        		if (tk0 != null)
	        			infoHash2.put("tk0", tk0);
	        		//this.infoHash.put("tk1", sq.getRowkeyString());
	        		infoHash2.put("tk1", sq.getInfoKey());
	        		
	        		if(nTime.startsWith("13:3"))
	        		if(sq.getScore() >= this.score) {
	        			long nscore = this.score -1;
	        			sq.setScore(nscore);
	        		}
	        		
	        		if(sq.getScore() < this.score) {
		                boolean rt = sq.insertIndex(ks);
		                if(rt) {
		                	this.score = sq.getScore();	
		                	exec0.addAndGet(1);
		                }
		                else System.out.println(Utility.getFullyDateTimeStr()+ " ZADD Inset Error:"+baseKey+" r:"+sq.getRowkeyString()+" "+sq.getScore()+" "+infoHash2.toString());
	        		}
	        		/* 暫時不更新 StockDateList
	        		String indexQuote = sq.getIndexQuote();
	        		if(!indexQuote.equals("")) {
	                    NavigableMap<String, String> sdeMap = new java.util.concurrent.ConcurrentSkipListMap<>(); 
	                    sdeMap.put(this.securityID,indexQuote);
	                    StockDateList sdl = new StockDateList(date,sdeMap);
	                    sdl.insertCF(ks);
	        		}*/
	        	}

	        }			
		}
		
		
		String pVolumn = infoHash2.get("v");
		String pTime = infoHash2.get("%");
        
		if(this.securityID.equals("2330.tw")||this.securityID.equals("00888.tw")) {
        	//System.out.println("-20--->"+uaMsgQueue.contains(baseKey)+" "+baseKey+" "+insert+" "+infoHash2.toString());
        }
		
        boolean updateDetail = false;
        if(ip0 != null && ip1 != null)
          updateDetail = !ip0.equals(ip1);    
        
		if(updateDetail){
            StockDetail sd = new StockDetail(date,infoHash2,false);

           // System.out.println(Utility.getSQLDateTimeStr()+" insert : "+key+" "+infoHash2.get("ch"));

            //Build StockDelay
            if(!ip0.equals("0")){
              //sd.rowkeyString
              String sde_key = date+"_"+ex+"_"+ip0;
              NavigableMap<String, String> sdeMap = new java.util.concurrent.ConcurrentSkipListMap<>(); 
              sdeMap.put(sd.rowkeyString,ip0);
              StockDelay sde = new StockDelay(ex,date,sde_key,sdeMap);
              sde.insertCF(ks);
            }			
		}
		
		exec1.addAndGet(1);
		StockNames.exec(hashcode,ks);
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
	
	public void setOHCLx00(String timeStr, String value, String oz) {
		timeStr = timeStr.replaceAll(":","");
		int time = Utility.parseInt(timeStr); //90005

		if(oz==null || oz.equals("0") || oz.equals("0.0000")){ //open
			if(time<=90005) this.infoHash2.put("o", value);
			this.infoHash2.put("h", value);
			this.infoHash2.put("l", value);
		} else {
			BigDecimal current = new BigDecimal(value);
			if(time<=90005) this.infoHash2.put("o",value);
			if(this.infoHash2.get("h")==null) this.infoHash2.put("h",value);
			if(this.infoHash2.get("l")==null) this.infoHash2.put("l",value);
			if(time<=90005) this.infoHash2.put("o",value);
			if(this.infoHash2.get("h").equals("-")) this.infoHash2.put("h",value);
			if(this.infoHash2.get("l").equals("-")) this.infoHash2.put("l",value);
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