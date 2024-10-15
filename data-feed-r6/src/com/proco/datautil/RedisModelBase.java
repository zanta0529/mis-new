package com.proco.datautil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;

import com.ecloudlife.util.Utility;
import com.proco.cache.RedisManager;


public abstract class RedisModelBase {

	public String rowkeyString;
	public String redisKeyString;
	public String date ;
	public String columnPath;
	public java.util.NavigableMap<String, String> dataHash;
	public boolean chk = false;
	public int hashcode = 0;
	  
	  
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
	
	public List<Map.Entry<String, String>> get() {
		Object[] cvals;
		try {
			if(this.redisKeyString.indexOf("t00.tw")!=-1)
				System.out.println("----->"+this.redisKeyString);
			Object c0 = RedisManager.aredis0.submitCommand(RedisCommand.HGETALL , this.redisKeyString).get().getResult();
			if(c0==null) {
				System.out.println(Utility.getFullyDateTimeStr()+" get----->"+this.redisKeyString+" msg:"+null);
				return new java.util.ArrayList<>(this.dataHash.entrySet());				
			} else
			if(c0 instanceof String) {
				System.out.println(Utility.getFullyDateTimeStr()+" get----->"+this.redisKeyString+" msg:"+c0.toString());
				return new java.util.ArrayList<>(this.dataHash.entrySet());
			}
			cvals = (Object[]) c0;
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
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, String>> ret = new java.util.ArrayList<>(this.dataHash.entrySet());
		return ret;
	}
	
	public Map.Entry<String, String> get(String subKey) {
		Object cvals;
		try {
			if(this.redisKeyString.indexOf("t00.tw")!=-1)
				System.out.println("----->"+this.redisKeyString);
			cvals = RedisManager.aredis0.submitCommand(RedisCommand.HGET , this.redisKeyString , subKey).get().getResult();
			if(cvals==null) return null;

			this.dataHash.put(subKey, cvals.toString());
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, String>> ret = new java.util.ArrayList<>(this.dataHash.entrySet());
		if(ret.isEmpty()) return null;
		else return ret.get(0);
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
	
	public boolean isChk() {
		return chk;
	}
	  
	public String getRowkeyString() {
		return rowkeyString;
	}
}
