package com.proco.datautil;

import java.util.NavigableMap;

public class StockNewboard extends RedisModelBase {
	public String columnPath = "stocknewboard";

	public StockNewboard(String key, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = key;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);		 
	}
}
