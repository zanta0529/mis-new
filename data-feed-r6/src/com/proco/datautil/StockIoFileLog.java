package com.proco.datautil;

import java.util.NavigableMap;

public class StockIoFileLog extends RedisModelBase {
	public String columnPath = "stockiofilelog";

	
	public StockIoFileLog(String date, String rowkeyString) {
		super(date, rowkeyString);
		// TODO Auto-generated constructor stub
		 this.rowkeyString = rowkeyString;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);		
	}
	
	public StockIoFileLog(String date, String key, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = key;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}	
	
}