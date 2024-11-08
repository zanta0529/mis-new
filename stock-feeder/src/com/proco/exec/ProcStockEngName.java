package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	
	static AtomicBoolean runThread = new AtomicBoolean(false);
	static AtomicBoolean runCheck = new AtomicBoolean(false);
	public static void threadProcess(){
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
	
	public static void threadProcess1() {
		
		if(msgQueue0.isEmpty()) return;
		boolean chk = false;
		do {
			
			SystemStatus ss = new SystemStatus("all",date,date);
			Map<String, String> sm = ss.getMap();
			if(!sm.isEmpty()) {
				String sns = sm.get("sns");
				if(sns!=null) {
					if(sns.equals("1")) {
						System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.threadProcess : get StockNameStore ok. "+date+" "+msgQueue0.size()+" "+sm.size());
						chk=true;
					}
				}
			}
			if(!chk) {
				System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.threadProcess : wait StockNameStore : "+date+" "+msgQueue0.size()+" "+sm.size());
				Utility.sleep(10000);				
			}

		} while(!chk);
		
		ArrayList<String[]> failReTry = new ArrayList<String[]>();
		while(!msgQueue0.isEmpty()) {
			String[] js = msgQueue0.pollFirstEntry().getValue();
			if(js.length<4) continue;
			prepardEngName(failReTry , js,0);
		}
		System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.threadProcess : End prepardEngName : "+date+" trycount:"+failReTry.size());
		
		//Patch 索引因同步時序遺漏
		if(!failReTry.isEmpty()) { //查詢至剩餘量不變為止
			long ct = System.currentTimeMillis();
			int base = 0;
			int base0 = 0;
			int c = 0;
			do {
				Utility.sleep(20000);	
				c++;
				base = failReTry.size();
				ArrayList<String[]> failReTry2 = new ArrayList<String[]>();
				for(String[] js : failReTry) {
					prepardEngName(failReTry2 , js, c);
				}				
				base0 = failReTry2.size();
				failReTry.clear();
				failReTry = failReTry2;
				System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.threadProcess : Patch "+c+" prepardEngName : "+date+" trycount:"+base0+"-"+base);
				if(failReTry.isEmpty()) break; //無剩餘，跳出
			} while(base0!=base || (System.currentTimeMillis()-ct) < 300000L);		
			System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.threadProcess : Patch End prepardEngName : "+date+" trycount:"+base0);
			failReTry.clear();
		}
	}

	private static void prepardEngName(List<String[]> retry, String[] js, int step) {
		String exchange = js[0]; 
		String symbol = js[1]; 
		String cname = js[2]; 
		String date = js[3]; 
		
		String ename = "";
		String ch = symbol;
		if(ch.endsWith(".tw")) ch = ch.substring(0,ch.length()-3);

		StockNameStore sns = new StockNameStore(date,ch);
		Map<String, String> snmap = sns.getMap();
		String en0 = snmap.get("en");
		if(en0!=null) {
			ename = en0;
			String cn0 = snmap.get("cn");
			if(cn0==null) {
				cn0 = cname;
				TreeMap<String,String> snmap0 = new TreeMap<String,String>();
				snmap0.putAll(snmap);
				snmap0.put("cn", cn0);
				StockNameStore sns0 = new StockNameStore(ch,date,snmap0);
				boolean ok = sns0.insertCF();
				System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.prepardEngName : patch ZhName : "+step+" "+ok+" "+date+" "+ch+" "+snmap0.toString());
			}						
		} else {
			retry.add(js);
			//if(ch.length()==4)
			//	System.out.println(Utility.getSQLTimeStr() +" ProcStockEngName.prepardEngName : lost StockNameStore : "+step+" "+date+" "+ch+" c:"+cname+" e:"+ename+" ");
		}				

		if(ename.equals(""));
		else {
			StockNames.prepardStockNames(null,exchange, symbol, "", ename,  date);
		}
	}
	
	
}
