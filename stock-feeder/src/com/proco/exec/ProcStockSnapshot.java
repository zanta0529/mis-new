package com.proco.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.aredis.cache.RedisCommandInfo;
import com.proco.cache.RedisManager;
import com.proco.io.InfoDaemon;
import com.proco.util.Utility;

public class ProcStockSnapshot {

	static java.util.concurrent.ConcurrentSkipListMap<Integer, java.util.concurrent.ConcurrentSkipListMap<String,RedisCommandInfo>> queueMap = new java.util.concurrent.ConcurrentSkipListMap<Integer, java.util.concurrent.ConcurrentSkipListMap<String,RedisCommandInfo>>();
	static java.util.concurrent.ConcurrentSkipListMap<Integer, java.util.concurrent.ConcurrentSkipListMap<Long,List<RedisCommandInfo>>> queueList = new java.util.concurrent.ConcurrentSkipListMap<Integer, java.util.concurrent.ConcurrentSkipListMap<Long,List<RedisCommandInfo>>>();

	public static java.util.concurrent.atomic.AtomicBoolean executing = new java.util.concurrent.atomic.AtomicBoolean(false);
	
	public static java.util.concurrent.atomic.AtomicLong listCount = new java.util.concurrent.atomic.AtomicLong(0);
	public static java.util.concurrent.atomic.AtomicLong execCount = new java.util.concurrent.atomic.AtomicLong(0);
	static boolean check_clients = false;
	public static void init() {
		if(!check_clients) {
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(!executing.getAndSet(true)) {
						try {
							exec0();										
						} finally {
							executing.set(false);
						}
					}	
				}
			};			
			Timer tr = new Timer();
			tr.schedule(tt, 1000L,1000L);
			check_clients = true;
		}
	}
	
	public static void exec(int code0,Map<String,RedisCommandInfo> rinfos) {
		java.util.concurrent.ConcurrentSkipListMap<String,RedisCommandInfo> codeQueue = null;
		int code = code0%InfoDaemon.maxActive;
		synchronized(queueMap) {
			codeQueue = queueMap.get(code);
			if(codeQueue==null) codeQueue = new  java.util.concurrent.ConcurrentSkipListMap<String,RedisCommandInfo>();
			queueMap.put(code, codeQueue);
		}
		codeQueue.putAll(rinfos);
		
		return;
	}
	
	public static void exec(int code0,List<RedisCommandInfo> rinfos) {
		int code = code0%InfoDaemon.maxActive;
		java.util.concurrent.ConcurrentSkipListMap<Long,List<RedisCommandInfo>> codeList = null;
		synchronized(queueList) {
			codeList = queueList.get(code);
			if(codeList==null) codeList = new java.util.concurrent.ConcurrentSkipListMap<Long,List<RedisCommandInfo>>();
			queueList.put(code, codeList);
		}
		codeList.put(listCount.getAndAdd(1), rinfos);
		
		return;
	}
	
	public static void exec0() {
		if(RedisManager.size()>5000) return;
		
		java.util.concurrent.atomic.AtomicLong execCount0 = new java.util.concurrent.atomic.AtomicLong(0);
		Set<Integer> codes = queueMap.keySet();
		for(Integer code : codes) {
			java.util.concurrent.ConcurrentSkipListMap<String,RedisCommandInfo> qinfos = queueMap.get(code);
			if(qinfos==null) continue;

			
			List<RedisCommandInfo> rinfos = new ArrayList<RedisCommandInfo>();
			while(!qinfos.isEmpty()) {
				Map.Entry<String,RedisCommandInfo> ent = qinfos.pollFirstEntry();
				execCount0.addAndGet(1);				
				if(ent!=null)
				rinfos.add(ent.getValue());
			}
			
			int c = rinfos.size();
			if(c!=0) {
				RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
				rinfos.toArray(rinfo);
				while(RedisManager.size()>1000) Utility.sleep(100);	
				RedisManager.aredis0.submitCommands(code,rinfo);	
			}
		}
		
		codes = queueList.keySet();
		for(Integer code : codes) {
			java.util.concurrent.ConcurrentSkipListMap<Long,List<RedisCommandInfo>> codeList = queueList.get(code);
			if(codeList==null) continue;
			List<RedisCommandInfo> rinfos = new ArrayList<RedisCommandInfo>();
			
			while(!codeList.isEmpty()) {
				Map.Entry<Long,List<RedisCommandInfo>> ent = codeList.pollFirstEntry();
				execCount0.addAndGet(1);				
				if(ent!=null)
				rinfos.addAll(ent.getValue());
			}
			
			int c = rinfos.size();
			if(c!=0) {
				RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
				rinfos.toArray(rinfo);
				while(RedisManager.size()>1000) Utility.sleep(100);	
				RedisManager.aredis0.submitCommands(code,rinfo);	
			}
		}
		
		ProcStockSnapshot.execCount = execCount0;
	}
	
	
}
