package com.proco.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;
import org.aredis.cache.RedisCommandList;

import com.proco.util.Utility;


public class RedisManager {
	public static AbstractMultiAsyncRedisClient aredis0 = null;
	public static ExecutorService executor = null;
	public static String redis = "localhost:10001";
	public static String auth = null;
	public static String authPath = null;
	//public static BlockingQueue<Runnable> jobQueue = new LinkedTransferQueue<Runnable>();
	public static int a = 8;
	public static int b = 16;
	public static int c = 32;
	public static long restart = 0;
	
	/*
	static {
		DB db = DBMaker.newMemoryDirectDB().transactionDisable().closeOnJvmShutdown().make();	
		//DB db = MapDbUtil.getMapDB(Utility.getDateStr());		
		jobQueue = db.getQueue("RedisManager");
	}*/

	public static void initAuthPath(String path){
		File file = new File(path);
		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				for (String s; (s = br.readLine()) != null; ) {
					if (s.startsWith("requirepass")) {
						s = s.replaceFirst("requirepass", "");
						s = s.trim();
						auth = s;
						authPath=path;
						break;
					}
				}
				br.close();
				fr.close();
			} catch(Exception ex) {}
	    }
	}

	public static void init(){
		if(aredis0==null){
			executor = Executors.newFixedThreadPool(b);
	        //executor = new ThreadPoolExecutor(a, b, c, TimeUnit.SECONDS, jobQueue);
	        //executor.allowCoreThreadTimeOut(true);
	        /*
	        AsyncRedisFactory f = new AsyncRedisFactory(executor);
	        JavaHandler jj = new JavaHandler();
	        jj.setStringCompressionThreshold(10240);
	        f.setDataHandler(jj);
			aredis0 = f.getClient(redis);*/
			aredis0 = new MultiAsyncRedisClient(redis,auth, b, executor);

		}
		boolean p = true;
		while(!aredis0.checkClients(true)) {			
			Utility.sleep(5000);
			if(p) {
				System.out.println(Utility.getFullyDateTimeStr()+" check redis client error, wait to connect.");
				p = false;
			}
		}
		
		startCheckClients();
		
	}

	public static boolean checkClients(){
		boolean rt = false;
		if(aredis0==null) return rt;
		return aredis0.checkClients(false);
	}
	
	static boolean check_clients = false;
	public static void startCheckClients() {
		if(!check_clients) {
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					long s = size();
					if(s==0) {
						boolean ok = checkClients();
						if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" queue="+s+" check redis client."+ok);	
						if(!ok) RedisManager.reinit();
					} else {
						if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" queue="+s+" skip check redis client.");	
					
					}

				}
			};			
			Timer tr = new Timer();
			tr.schedule(tt, 30000L,30000L);
			check_clients = true;
		}
	}
	
	//public static void init(int a, int b , int c){
	public static void init(int b){
		//RedisManager.a = a;
		//RedisManager.b = b;
		RedisManager.b = b;
		if(aredis0==null){
			executor = Executors.newFixedThreadPool(b);
	        //executor = new ThreadPoolExecutor(a, b, c, TimeUnit.SECONDS, jobQueue);
	        //executor.allowCoreThreadTimeOut(true);
	        /*AsyncRedisFactory f = new AsyncRedisFactory(executor);
	        JavaHandler jj = new JavaHandler();
	        jj.setStringCompressionThreshold(10240);
	        f.setDataHandler(jj);
			aredis0 = f.getClient(redis);*/
			aredis0 = new MultiAsyncRedisClient(redis,auth, b, executor);

		}
		boolean p = true;
		while(!aredis0.checkClients(true)) {
			Utility.sleep(5000);
			if(p) {
				System.out.println(Utility.getFullyDateTimeStr()+" check redis client error, wait to connect.");
				p = false;
			}
		}
		startCheckClients();
	}
	
	
	public static void close(){
		executor.shutdown();
	}
	
	public synchronized static void reinit() {
		if((System.currentTimeMillis()-restart)<100000) return;
		restart = System.currentTimeMillis();
		if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" RedisManager.reinit(): Restart Redis Client.....");
		
		final ExecutorService executor0 = executor;
		final AbstractMultiAsyncRedisClient aredis01 = aredis0; //
		
		executor = Executors.newFixedThreadPool(b);
		aredis0 = new MultiAsyncRedisClient(redis,auth, b, executor);	
		
		Thread th = new Thread() {
			public void run() {
				if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" RedisManager.reinit(): Start New Redis Client.....OK");
				executor0.shutdown();
				int waitcount = 0;
				while(!executor0.isTerminated() || aredis01.size()>0) {
					Utility.sleep(1000);
					waitcount++; 
					if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" RedisManager.reinit(): Old Redis Client.....Waitting to Terminated="+executor0.isTerminated()+" c="+waitcount+" exec_jobs="+aredis01.size());
				}
				aredis01.close();
				if(RedisCommandList.StatusChange.get()) System.out.println(Utility.getFullyDateTimeStr()+" RedisManager.reinit(): Old Redis Client.....Shutdown OK.");				
			}
		};
		th.start();
		
	}
	
	public static long size() {
		if(aredis0==null)
			return 0;
		return aredis0.size();
	}

	public static int worker() {
		if(aredis0==null)
			return 0;
		return aredis0.worker();
	}
	
	public static long getCommandCount() {
		if(aredis0==null)
			return 0;
		return aredis0.getCommandCount();
	}
	
	public  static void main(String[] args){
		RedisManager.init(16);
		long ct = System.currentTimeMillis();
		{
		try {
			for(int i = 0 ; i < 100000 ; i++) {
				ArrayList<RedisCommandInfo>rinfos = new ArrayList<RedisCommandInfo>();
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxv1",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxv2",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxv3",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxv4",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxv5_"+i,String.valueOf(123), String.valueOf(1)));
				int c = rinfos.size();
				if(c==0) return;
				RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
				rinfos.toArray(rinfo);
				//System.out.println(rinfo[0].getParams()[0]);				
				//Object[] hkcvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.KEYS , "c_hk*HK1").get().getResult();
				//RedisManager.aredis0.submitCommand(RedisCommand.DEL, hkcvals).get().getResult();
				RedisManager.aredis0.submitCommands(rinfo);
				//Utility.sleep(1);
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		}
		while(RedisManager.size()>0) {
			//System.out.println("---------------Wait RedisManager.size()="+RedisManager.size());
			Utility.sleep(1);
		}
		System.out.println("---------------First RedisManager.size()="+RedisManager.size()+" ct="+(System.currentTimeMillis()-ct)+"ms");
		RedisManager.reinit();
		ct = System.currentTimeMillis();
		{
		try {
			for(int i = 0 ; i < 100000 ; i++) {
				ArrayList<RedisCommandInfo>rinfos = new ArrayList<RedisCommandInfo>();
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxz1",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxz2",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxz3",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxz4",String.valueOf(123), String.valueOf(1)));
				rinfos.add(new RedisCommandInfo(RedisCommand.HINCRBY, "aaaa_pxz5_"+i,String.valueOf(123), String.valueOf(1)));
				int c = rinfos.size();
				if(c==0) return;
				RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
				rinfos.toArray(rinfo);
				//System.out.println(rinfo[0].getParams()[0]);				
				//Object[] hkcvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.KEYS , "c_hk*HK1").get().getResult();
				//RedisManager.aredis0.submitCommand(RedisCommand.DEL, hkcvals).get().getResult();
				RedisManager.aredis0.submitCommands(rinfo);
				//Utility.sleep(1);
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		while(RedisManager.size()>0) {
			//System.out.println("---------------Wait RedisManager.size()="+RedisManager.size());
			Utility.sleep(1);
		}
		System.out.println("---------------END RedisManager.size()="+RedisManager.size()+" ct="+(System.currentTimeMillis()-ct)+"ms");
		System.exit(0);
	}
	

	
}
