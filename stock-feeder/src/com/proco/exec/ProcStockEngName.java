package com.proco.exec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import com.proco.datautil.StockNameStore;
import com.proco.datautil.StockNames;
import com.proco.datautil.SystemStatus;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class ProcStockEngName {
	public static ExecutorManager threadPool = null;
	static ConcurrentSkipListMap<Long,String[]> msgQueue0 = new ConcurrentSkipListMap<Long,String[]>();
	public static java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	static Runnable runner = null;
	static String date = "";
	
	static {
		runner = new Runnable() {	
			   @Override
			   public void run() {
				   threadProcess();
			   }
		};
	}
	
	
	public static void init(int size) {
		if(threadPool==null)
			threadPool = new ExecutorManager(size);
	}
	
	public static void init(ExecutorManager threadPool0) {
		if(threadPool==null)
			threadPool = threadPool0;
	}
	
	public static void proc(String[] msg, String date) {
		ProcStockEngName.date = date;
		msgQueue0.put(execCount.addAndGet(1), msg);
		threadPool.schedule(ProcStockEngName.runner, 3000, TimeUnit.MILLISECONDS, "");
	}
	
	
	public static void threadProcess() {
		
		if(msgQueue0.isEmpty()) return;
		boolean chk = false;
		do {
			
			SystemStatus ss = new SystemStatus("all",date,date);
			Map<String, String> sm = ss.getMap();
			if(!sm.isEmpty()) {
				String sns = sm.get("sns");
				if(sns!=null) {
					if(sns.equals("1")) {
						System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.SystemStatus : get StockNameStore ok. ");
						chk=true;
					}
				}
			}
			System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.SystemStatus : wait StockNameStore : "+date+" "+msgQueue0.size()+" "+sm.size());
			Utility.sleep(10000);
		} while(!chk);
		
		while(!msgQueue0.isEmpty()) {
			String[] js = msgQueue0.pollFirstEntry().getValue();
			if(js.length<4) continue;
			
			//String exchange,String symbol,String cname, String date
			String exchange = js[0]; 
			String symbol = js[1]; 
			String cname = js[2]; 
			String date = js[3]; 
			
			String ename = "";
			String ch = symbol;
			if(ch.endsWith(".tw")) ch = ch.substring(0,ch.length()-3);
			
			StockNameStore sns = new StockNameStore(date,ch);
			List<Map.Entry<String, String>> sList = sns.get();
			for(Map.Entry<String, String> hcol : sList){
				if(hcol.getKey().equals("en")){
					ename = (String)hcol.getValue();
					ename = ename.toUpperCase();
				}
			}
			
			if(ename.equals(""));
			else {
				StockNames.prepardStockNames(null,exchange, symbol, "", ename,  date);
			}
			
		}
		
	}
	
	
}
