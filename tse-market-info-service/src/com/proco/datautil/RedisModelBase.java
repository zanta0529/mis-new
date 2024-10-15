package com.proco.datautil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.proco.cache.JedisManager;
import com.proco.util.DateManager;
import com.proco.util.Utility;

import redis.clients.jedis.resps.Tuple;

public abstract class RedisModelBase {

	public String rowkeyString;
	public String redisKeyString;
	public String date ;
	public String columnPath;
	public java.util.NavigableMap<String, String> dataHash;
	public boolean chk = false;
	public int hashcode = 0;
	public abstract String getExchange(String rowkeyString);
	long queryTime = -1;
	
	public long score = -1;
	String indexQuote = "";
	String infoKey = "";
	
	public RedisModelBase(String rowkeyString) {
		this.date = DateManager.getExchangeDate(getExchange(rowkeyString));
		this.dataHash = new java.util.concurrent.ConcurrentSkipListMap<>();
		this.rowkeyString = rowkeyString;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
	}
	
	  
	public RedisModelBase(String date, java.util.NavigableMap<String,String> dataHash) {
		this.redisKeyString = columnPath+"_"+date+"_";
		this.date = date;
		this.dataHash = dataHash;
	}

	public RedisModelBase(String date, String rowkeyString) {
		this.date = date;
		this.dataHash = new java.util.concurrent.ConcurrentSkipListMap<>();
		this.rowkeyString = rowkeyString;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
	}
	
	public List<Map.Entry<String, String>> get() {
		long ct = System.currentTimeMillis();
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			
			Map<String, String> datas = JedisManager.getDataRowMapByKey(this.redisKeyString);
			if(datas!=null) if(!datas.isEmpty()) {
				this.dataHash.putAll(datas);
				this.dataHash.put("key", this.rowkeyString);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, String>> ret = new java.util.ArrayList<>(this.dataHash.entrySet());
		queryTime = System.currentTimeMillis() - ct;
		return ret;
	}
	
	public Map<String, String> getMap() {
		long ct = System.currentTimeMillis();
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			
			Map<String, String> datas = JedisManager.getDataRowMapByKey(this.redisKeyString);
			if(datas!=null) if(!datas.isEmpty()) {
				this.dataHash.putAll(datas);
				this.dataHash.put("key", this.rowkeyString);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryTime = System.currentTimeMillis() - ct;
		return this.dataHash;
	}

	
	public boolean isChk() {
		return chk;
	}
	  
	public String getRowkeyString() {
		return rowkeyString;
	}
	
	public Long getQueryTime() {
		return queryTime;
	}

	public String getIndexQuote(){
		return this.indexQuote;
	}
	
	public Long getIndexScore(){
		return this.score;
	}
	
	
	public String getZrange() {
		List<Tuple> cvals;
		try {
			cvals = JedisManager.getSortedSetByKey(this.redisKeyString); //(Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZRANGE , this.redisKeyString,"0","0","WITHSCORES").get().getResult();
			if(cvals==null) return null;
			if(cvals.isEmpty()) return null;
			this.indexQuote = (String) cvals.get(0).getElement();
			this.score = (long)cvals.get(0).getScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.indexQuote;
	}	
	
	public String getZrange(long index) {
		List<Tuple> cvals;
		try {
			cvals = JedisManager.getSortedSetByScore(this.redisKeyString, index, 1); //(Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZREMRANGEBYSCORE , this.redisKeyString,String.valueOf(index),"9999999999999","LIMIT","0","1","WITHSCORES").get().getResult();
			
			//System.out.println("getZrange:"+this.redisKeyString+" "+index);
			
			if(cvals==null) return null;
			if(cvals.isEmpty()) return null;
			this.indexQuote = (String) cvals.get(0).getElement();
			this.score = (long)cvals.get(0).getScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.indexQuote;
	}	
	
	public java.util.NavigableMap<Long, String> getZrange(long index,int size) {
		java.util.NavigableMap<Long, String> indexMap = new java.util.TreeMap<>();
		List<Tuple> cvals;
		try {
			cvals = JedisManager.getSortedSetByScore(this.redisKeyString, index, size); //(Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZREMRANGEBYSCORE , this.redisKeyString,String.valueOf(index),"9999999999999","LIMIT","0","1","WITHSCORES").get().getResult();
			if(cvals==null) return null;
			if(cvals.isEmpty()) return null;
			this.indexQuote = (String) cvals.get(0).getElement();
			this.score = (long)cvals.get(0).getScore();
			
			for(Tuple val : cvals) {
				indexMap.put((long)val.getScore(), val.getElement());
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexMap;
	}	
	
}
