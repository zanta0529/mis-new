package com.proco.datautil;

import java.util.NavigableMap;

public class StockDateList extends RedisModelBase {
	public String columnPath = "stockdatelist";

	public StockDateList (String date) {
		super(date, date);
		this.rowkeyString = date;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}
	
	public StockDateList(String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub	 
		this.rowkeyString = date;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		chk = true;
	}

	@Override
	public String getExchange(String rowkeyString) {
		// TODO Auto-generated method stub
		return "tse";
	}
}
