package com.proco.datautil;

import java.util.NavigableMap;

public class StockIoFile extends RedisModelBase {
	public String columnPath = "stockiofile";

	
	public StockIoFile(String date, String rowkeyString) {
		super(date, rowkeyString);
		// TODO Auto-generated constructor stub
		 this.rowkeyString = rowkeyString;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);		
	}
	
	public StockIoFile(String date, String key, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = key;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}

	@Override
	public String getExchange(String rowkeyString) {
		// TODO Auto-generated method stub
		return null;
	}
}
