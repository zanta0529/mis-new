package com.proco.datautil;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import com.proco.util.Utility;


public class StockNames extends RedisModelBase {
	public String columnPath = "stocknames";
	
	public StockNames(String date,String a) {
		super(date, a);
		this.rowkeyString = a;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}

	public StockNames(String key, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		this.rowkeyString = key;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		chk = true;
	}
		
	@Override
	public String getExchange(String rowkeyString) {
		return "";
	}		
	
	public static void main(String[] args) {
	}	
}
