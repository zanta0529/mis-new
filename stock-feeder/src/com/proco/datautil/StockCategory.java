package com.proco.datautil;

import java.util.NavigableMap;

public class StockCategory extends RedisModelBase {
	public String columnPath = "stockcategory";

	
	public StockCategory (String date,String a) {
		super(date, a);
		this.rowkeyString = a;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}
	
	public StockCategory(String date, String key, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub	 
		this.rowkeyString = key;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		chk = true;
	}
}
