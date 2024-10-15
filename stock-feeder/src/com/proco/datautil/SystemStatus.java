package com.proco.datautil;

import java.util.NavigableMap;

public class SystemStatus extends RedisModelBase {
	public String columnPath = "systemstatus"; //"stockstatis";

	
	public SystemStatus(String ex, String date, String rowkeyString) {
		super(date, rowkeyString);
		// TODO Auto-generated constructor stub
		 this.rowkeyString = ex+"_"+date;
		 this.redisKeyString = columnPath+"_"+date+"_"+this.rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);	
		 //System.out.println("---->"+redisKeyString);
	}
	
	public SystemStatus(String ex, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = ex+"_"+date;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}	
	
}