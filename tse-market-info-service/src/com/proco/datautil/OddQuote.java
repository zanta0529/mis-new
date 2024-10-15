package com.proco.datautil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NavigableMap;
import com.proco.util.Utility;

public class OddQuote extends RedisModelBase {

	String ex = "";
	long timestamp = System.currentTimeMillis();
	public String columnPath = "oddquote";
	
	public OddQuote(String rowkeyString) {
		super(rowkeyString);
		this.rowkeyString = rowkeyString;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;		
	}
	
	public OddQuote(String date, String rowkeyString) {
		super(date, rowkeyString);
		this.rowkeyString = rowkeyString;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;		
		// TODO Auto-generated constructor stub
	}	
	
	public OddQuote(String date, NavigableMap<String, String> dataHash, boolean replay) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		
	    String productid = dataHash.get("@");
	    if(productid==null) return;
	    dataHash.put("ch",productid);

	    dataHash.put("d",date);

	    String time = dataHash.get("%");
	    String mtime = dataHash.get("m%");
	    if(time==null) return;
	    dataHash.put("t",time);
	    if(mtime!=null){
	    dataHash.put("mt",mtime);
	    }

	    String pid = dataHash.get("#");
	    if(pid==null) return;
	    if(pid.indexOf("|") ==-1) return;
	    pid = pid.split("\\|")[0];
	    if(pid.indexOf(".") ==-1) return;
	    pid = pid.split("\\.")[1];
	    dataHash.put("ex",pid);

	    StringBuffer active = new StringBuffer(date).append(time);
	    while (active.length() < 16)
	      active.append('0');
	    if (active.length() > 16)
	      active = new StringBuffer(active.substring(0, 16));
	    java.util.Date dateObj = new Date();
	    try {
	      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
	      dateObj = sdf.parse(active.toString());
	    }
	    catch (ParseException ex1) {
	      return ;
	    }
	    timestamp = dateObj.getTime();
	    this.dataHash.put("tlong", String.valueOf(timestamp));
	    if(replay) timestamp = Utility.getUniTimeStamp(productid);

	    this.rowkeyString = (new StringBuffer(pid).append("_").append(productid).append("_").append(date)).toString();
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	    this.chk = true;
	}
	
	public void update(java.util.NavigableMap<String,String> dataHash, boolean replay){
		this.dataHash = dataHash;
		String productid = dataHash.get("@");
		if(productid==null) return;
		dataHash.put("ch",productid);

		String date = dataHash.get("^");
		if(date==null) return;
		dataHash.put("d",date);
		
		String time = dataHash.get("%");
		String mtime = dataHash.get("m%");
		if(time==null) return;
		dataHash.put("t",time);
		if(mtime!=null){
			dataHash.put("mt",mtime);
		}

		String pid = dataHash.get("#");
		if(pid==null) return;
		if(pid.indexOf("|") ==-1) return;
		pid = pid.split("\\|")[0];
		if(pid.indexOf(".") ==-1) return;
		pid = pid.split("\\.")[1];
		dataHash.put("ex",pid);

		StringBuffer active = new StringBuffer(date).append(time);
		while (active.length() < 16)
			active.append('0');
		if (active.length() > 16)
			active = new StringBuffer(active.substring(0, 16));
		java.util.Date dateObj = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			dateObj = sdf.parse(active.toString());
		}
		catch (ParseException ex1) {
			return ;
		}
		timestamp = dateObj.getTime();
		this.dataHash.put("tlong", String.valueOf(timestamp));
		if(replay) timestamp = Utility.getUniTimeStamp(productid);
		
	    this.rowkeyString = (new StringBuffer(pid).append("_").append(productid).append("_").append(date)).toString();
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		this.chk = true;	
	}

	public String getRowkeyString(){
		return this.rowkeyString;
	}	
	
	@Override
	public String getExchange(String rowkeyString) {
		String[] q = rowkeyString.split("_");
		// TODO Auto-generated method stub
		return q[0];
	}	
	
	public static void main(String[] args) {
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}