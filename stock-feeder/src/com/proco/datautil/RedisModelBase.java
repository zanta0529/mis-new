package com.proco.datautil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;
import org.json.JSONException;

import com.proco.cache.RedisManager;
import com.proco.util.Utility;


public abstract class RedisModelBase {

	public String rowkeyString;
	public String redisKeyString;
	public String date ;
	public String columnPath;
	public java.util.NavigableMap<String, String> dataHash;
	public boolean chk = false;
	public int hashcode = 0;
	
	public long score = -1;
	String infoKey = "";
	String indexQuote = "";
	  
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

	public boolean insertSCF() {
		return insertCF();
	}
	
	public boolean insertCF() {
		boolean rt = false;	
		boolean first = true;
		java.util.ArrayList<String> quoteList = new java.util.ArrayList<String>();
		Set<String> keys = dataHash.keySet();
		for(String key: keys) {
			String val = dataHash.get(key);
			if(val!=null) {
				if(first) {
					quoteList.add(redisKeyString);
					first = false;
				}
				quoteList.add(key);
				quoteList.add(val);
			}
		}
		if(!quoteList.isEmpty()) {
			final String[] aab = new String[quoteList.size()];
			quoteList.toArray(aab);
		    RedisCommandInfo[] rinfo = new RedisCommandInfo[] {new RedisCommandInfo(RedisCommand.HMSET, aab)};
		    RedisManager.aredis0.submitCommands(this.hashcode, rinfo);	
		    rt = true;
		}
		return rt;
		  
	}
	
	public boolean insertSCF(List<RedisCommandInfo> rinfos) {
		return insertCF(rinfos);
	}
	
	public boolean insertCF(List<RedisCommandInfo> rinfos) {
		boolean rt = false;	
		boolean first = true;
		java.util.ArrayList<String> quoteList = new java.util.ArrayList<String>();
		Set<String> keys = dataHash.keySet();
		for(String key: keys) {
			String val = dataHash.get(key);
			if(val!=null) {
				if(first) {
					quoteList.add(redisKeyString);
					first = false;
				}
				quoteList.add(key);
				quoteList.add(val);
			}
		}
		if(!quoteList.isEmpty()) {
			final String[] aab = new String[quoteList.size()];
			quoteList.toArray(aab);
			rinfos.add(new RedisCommandInfo(RedisCommand.HMSET, aab));
		    rt = true;
		}
		return rt;
		  
	}	

	public boolean insertCF(Map<String,RedisCommandInfo> rinfom) {
		boolean rt = false;	
		boolean first = true;
		java.util.ArrayList<String> quoteList = new java.util.ArrayList<String>();
		Set<String> keys = dataHash.keySet();
		for(String key: keys) {
			String val = dataHash.get(key);
			if(val!=null) {
				if(first) {
					quoteList.add(redisKeyString);
					first = false;
				}
				quoteList.add(key);
				quoteList.add(val);
			}
		}
		if(!quoteList.isEmpty()) {
			final String[] aab = new String[quoteList.size()];
			quoteList.toArray(aab);
			rinfom.put(redisKeyString,new RedisCommandInfo(RedisCommand.HMSET, aab));
		    rt = true;
		}
		return rt;
	}
	
	/*
	public boolean insertCF(Map<Integer,List<RedisCommandInfo>> rinfom) {
		boolean rt = false;	
		boolean first = true;
		java.util.ArrayList<String> quoteList = new java.util.ArrayList<String>();
		Set<String> keys = dataHash.keySet();
		for(String key: keys) {
			String val = dataHash.get(key);
			if(val!=null) {
				if(first) {
					quoteList.add(redisKeyString);
					first = false;
				}
				quoteList.add(key);
				quoteList.add(val);
			}
		}
		if(!quoteList.isEmpty()) {
			final String[] aab = new String[quoteList.size()];
			quoteList.toArray(aab);
			int code = this.hashcode%RedisManager.worker();
			List<RedisCommandInfo> rinfos = rinfom.get(code);
			if(rinfos == null) {
				rinfos = new ArrayList<RedisCommandInfo>();
				rinfom.put(code, rinfos);
			}
			rinfos.add(new RedisCommandInfo(RedisCommand.HMSET, aab));
		    rt = true;
		}
		return rt;
		  
	}*/	
	
	public List<Map.Entry<String, String>> get() {
		Object[] cvals;
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.HGETALL , this.redisKeyString).get().getResult();
			String k = "";
			String v = "";
			int c = 0;
			for(Object val : cvals) {
				if(c%2==0) k = val.toString();
				else {
					v = val.toString();
					this.dataHash.put(k, v);
				}
				c++;
			}
			if(c>0) {
				//this.dataHash.put("rkey", this.redisKeyString);
				this.dataHash.put("key", this.rowkeyString);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, String>> ret = new java.util.ArrayList<>(this.dataHash.entrySet());
		return ret;
	}
	
	public Map<String, String> getMap() {
		Object[] cvals;
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.HGETALL , this.redisKeyString).get().getResult();
			String k = "";
			String v = "";
			int c = 0;
			for(Object val : cvals) {
				if(c%2==0) k = val.toString();
				else {
					v = val.toString();
					this.dataHash.put(k, v);
				}
				c++;
			}
			if(c>0) {
				//this.dataHash.put("rkey", this.redisKeyString);
				this.dataHash.put("key", this.rowkeyString);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.dataHash;
	}
	
	
	public void delete(String colKey){
	    RedisCommandInfo[] rinfo = new RedisCommandInfo[] {new RedisCommandInfo(RedisCommand.HDEL, this.redisKeyString , colKey)};
	    RedisManager.aredis0.submitCommands(this.hashcode, rinfo);	
	    this.dataHash.remove(colKey);
	}
	
	public void delete(){
	    RedisCommandInfo[] rinfo = new RedisCommandInfo[] {new RedisCommandInfo(RedisCommand.DEL, this.redisKeyString)};
	    RedisManager.aredis0.submitCommands(this.hashcode, rinfo);	
	    this.dataHash.clear();
	}
	
	public boolean insertIndex(List<RedisCommandInfo> ks){
		if(score < 0) return false;
		
		org.json.JSONObject js = new org.json.JSONObject();
		java.util.Set<String> keys = this.dataHash.keySet();
		for(String key : keys){
			String val = this.dataHash.get(key);
			if(val!=null) try {
				js.put(key, val);
			}
			catch (JSONException ex) {
			}
		}
		indexQuote = js.toString();
		ks.add(new RedisCommandInfo(RedisCommand.ZADD, this.redisKeyString, String.valueOf(score), indexQuote));
		return true;
	}	
	
	public String getZrange() {
		Object[] cvals;
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZRANGE , this.redisKeyString,"0","0","WITHSCORES").get().getResult();
			if(cvals==null) return null;
			if(cvals.length!=2) return null;
			this.indexQuote = (String)cvals[0];
			this.score = Utility.parseLong((String)cvals[1]);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.indexQuote;
	}	
	
	public String getZrange(long index) {
		Object[] cvals;
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			//zrangebyscore stocktimeline_20230912_tse_2330.tw_20230912 8305513858999 9999999999999 limit 0 1 WITHSCORES
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZREMRANGEBYSCORE , this.redisKeyString,String.valueOf(index),"9999999999999","LIMIT","0","1","WITHSCORES").get().getResult();
			if(cvals==null) return null;
			if(cvals.length!=2) return null;
			this.indexQuote = (String)cvals[0];
			this.score = Utility.parseLong((String)cvals[1]);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.indexQuote;
	}	
	
	public java.util.NavigableMap<Long, String> getZrange(long index,int size) {
		java.util.NavigableMap<Long, String> indexMap = new java.util.TreeMap<>();
		Object[] cvals;
		try {
			//if(this.redisKeyString.indexOf("t00.tw")!=-1)
			//	System.out.println("----->"+this.redisKeyString);
			//zrangebyscore stocktimeline_20230912_tse_2330.tw_20230912 8305513858999 9999999999999 limit 0 1 WITHSCORES
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.ZREMRANGEBYSCORE , this.redisKeyString,String.valueOf(index),"9999999999999","LIMIT","0",String.valueOf(size),"WITHSCORES").get().getResult();
			if(cvals==null) return null;
			if(cvals.length%2!=0) return null;			
			this.indexQuote = (String)cvals[0];
			this.score = Utility.parseLong((String)cvals[1]);
			
			cvals = (Object[]) RedisManager.aredis0.submitCommand(RedisCommand.HGETALL , this.redisKeyString).get().getResult();
			String k = "";
			String v = "";
			int c = 0;
			for(Object val : cvals) {
				if(c%2==0) k = val.toString();
				else {
					v = val.toString();
					indexMap.put(Utility.parseLong(v), k); //Zrange 第一欄位為內容，第二欄位為數據
				}
				c++;
			}			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexMap;
	}	
		
	public static void exec(int code,List<RedisCommandInfo> rinfos) {
		if(rinfos==null) return;
		if(rinfos.isEmpty()) return;
		int c = rinfos.size();
		if(c!=0) {
			RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
			rinfos.toArray(rinfo);
			RedisManager.aredis0.submitCommands(code,rinfo);	
		}
	}

	public static void exec(Map<Integer,List<RedisCommandInfo>> rinfom) {
		if(rinfom==null) return;
		if(rinfom.isEmpty()) return;
		
		Set<Integer> keylist = rinfom.keySet();
		for(Integer key : keylist) {
			List<RedisCommandInfo> rinfos = rinfom.get(key);
			if(rinfos==null) continue;
			int c = rinfos.size();
			if(c!=0) {
				RedisCommandInfo[] rinfo = new RedisCommandInfo[c];
				rinfos.toArray(rinfo);
				RedisManager.aredis0.submitCommands(key,rinfo);	
			}			
		}
	}	
	
	public String getInfoKey(){
		return this.infoKey;
	}
	
	public String getIndexQuote(){
		return this.indexQuote;
	}
	
	public boolean isChk() {
		return chk;
	}
	  
	public String getRowkeyString() {
		return rowkeyString;
	}
}
