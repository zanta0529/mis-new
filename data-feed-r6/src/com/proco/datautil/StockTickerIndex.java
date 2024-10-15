package com.proco.datautil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;

import org.aredis.cache.RedisCommandInfo;
import org.json.JSONException;
import com.proco.util.Utility;



public class StockTickerIndex extends RedisModelBase {
	public String columnPath = "stocktickerindex";
	long timestamp = System.currentTimeMillis();
	String infoKey = "";
	
	public StockTickerIndex(String ex, String date, String rowkeyString) {
		super(date, rowkeyString);
		// TODO Auto-generated constructor stub
		 this.rowkeyString = rowkeyString;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}
	
	public StockTickerIndex(String ex, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
	    String productid = dataHash.get("@");
	    if(productid==null) return;
	    dataHash.put("ch",productid);

	    dataHash.put("d",date);
	    String time = dataHash.get("%");
	    if(time==null) return;
	    dataHash.put("t",time);
	    String pid = dataHash.get("#");
	    if(pid==null) return;
	    dataHash.put("pid",pid);

	    String pt = "B";

	    String serial = "";
	    if(pid.indexOf("|") ==-1) return;
	    String[] exs = pid.split("\\|");

	    long ser = Utility.parseLong(exs[1]);
	    if(ser==0)
	      serial = Utility.intStr2String(exs[1],10) ;
	    else
	      serial = String.valueOf((10000000000L-ser));
	    if(pid.indexOf(".") ==-1) return;
	    dataHash.put("ex",ex);


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
	      return;
	    }
	    timestamp = dateObj.getTime();
	    this.dataHash.put("tlong", String.valueOf(timestamp));

	    this.infoKey = (new StringBuffer(productid).append("_").append(ex).
	                     append("_").append(date).append("_").append(pt).append(
	    "_").append(serial)).toString();

	    this.rowkeyString = (new StringBuffer(ex).append("_").append(productid).append("_").append(date)).toString();
	    chk = true;		
	    this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
	    this.hashcode = (redisKeyString).hashCode();
	    if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}
	
	public String getInfoKey(){
		return this.infoKey;
	}
	
	public boolean insertIndex(List<RedisCommandInfo> ks){
		org.json.JSONObject js = new org.json.JSONObject();
		
		java.util.Set<String> keys = this.dataHash.keySet();
		for(String key : keys){
			String val = this.dataHash.get(key);
			if(val!=null) try {
				js.put(key, val);
			}
			catch (JSONException ex) {
			}
		}
		    /*
		    for(java.util.Map.Entry<String,String> ent : this.dataHash.entrySet()){
		      try {
		        js.put(ent.getKey(), ent.getValue());
		      }
		      catch (JSONException ex) {
		      }
		    }*/
		this.dataHash = new java.util.concurrent.ConcurrentSkipListMap<>();
		this.dataHash.put(this.infoKey,js.toString());
		return this.insertCF(ks);
	}	
	
}