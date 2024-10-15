package com.proco.datautil;

import java.util.NavigableMap;

public class StockTradeMinute extends RedisModelBase {
	public String columnPath = "stocktrademinute";
	
	public StockTradeMinute(String date,String a) {
		super(date, a);
		this.rowkeyString = a;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}

	public StockTradeMinute(String key, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = key;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);	
	}

}
