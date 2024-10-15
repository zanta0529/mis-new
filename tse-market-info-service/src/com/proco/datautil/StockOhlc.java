package com.proco.datautil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.NavigableMap;

import com.proco.util.Utility;



public class StockOhlc extends RedisModelBase {
	public String columnPath = "stockohlc";
	BigDecimal open = new BigDecimal(0);
	BigDecimal high = new BigDecimal(0);
	BigDecimal low = new BigDecimal(0);
	BigDecimal current = new BigDecimal(0);
	BigDecimal volume = new BigDecimal( 0);
	String time = "";
	String date = "";
	String ex = "";
	String ch = "";
	int replay = 0; //replay flag.
	long tlong = 0;
	
	public StockOhlc(String ex ,String date, String rowkeyString) {
		super(date, "");		
		this.ex = ex;
		this.date = date;
		this.rowkeyString = rowkeyString;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		chk = true;
	}

	public StockOhlc(String ex , String ch, String date, String time) {
		super(date, "");		
		this.ex = ex;
		this.ch = ch;
		this.date = date;
		this.time = time;
		this.rowkeyString = (new StringBuffer(ex).append("_").append(ch).append("_").append(date).append("_").append(time)).toString();
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		chk = true;
	}
	
	public StockOhlc(String ex , String ch, String date, String time, BigDecimal last, BigDecimal volume, BigDecimal pvolume) {
		super(date,"");
		this.open = new BigDecimal(last.toString());
		this.high = new BigDecimal(last.toString());
		this.low = new BigDecimal(last.toString());
		this.current = new BigDecimal(last.toString());
		this.volume = new BigDecimal(volume.toString());
		this.time = time;
		this.date = date;

		StringBuffer active = new StringBuffer(date).append(time);
		while (active.length() < 13)
			active.append('0');
		if (active.length() > 13)
			active = new StringBuffer(active.substring(0, 13));
		java.util.Date dateObj = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
			dateObj = sdf.parse(active.toString());
		}
		catch (ParseException ex1) {
			return;
		}
		this.tlong = dateObj.getTime();
		  
		this.dataHash = new java.util.concurrent.ConcurrentSkipListMap<>();
		dataHash.put("ex",ex);
		dataHash.put("ch",ch);
		dataHash.put("d",date);
		dataHash.put("t",time);
		dataHash.put("o",this.open.toString());
		dataHash.put("h",this.high.toString());
		dataHash.put("l",this.low.toString());
		dataHash.put("z",this.current.toString());
		dataHash.put("v",this.volume.toString());
		dataHash.put("tlong",String.valueOf(tlong));
		dataHash.put("s",pvolume.toString());
		this.rowkeyString = (new StringBuffer(ex).append("_").append(ch).append("_").append(date).append("_").append(time)).toString();
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		chk = true;
	}
	  
	public StockOhlc(String ex , String ch, String date, String time, BigDecimal last, BigDecimal volume) {
		super(date,"");
		this.open = new BigDecimal(last.toString());
		this.high = new BigDecimal(last.toString());
		this.low = new BigDecimal(last.toString());
		this.current = new BigDecimal(last.toString());
		this.volume = new BigDecimal(volume.toString());
		this.time = time;
		this.date = date;


		String active = date+time;
		while (active.length() < 13)
			active += "0";
		if (active.length() > 13)
			active = active.substring(0, 13);
		java.util.Date dateObj = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
			dateObj = sdf.parse(active);
		} catch (ParseException ex1) {
			return;
		}
		tlong = dateObj.getTime();

		this.dataHash = new java.util.concurrent.ConcurrentSkipListMap<>();
		dataHash.put("ex",ex);
		dataHash.put("ch",ch);
		dataHash.put("d",date);
		dataHash.put("t",time);
		dataHash.put("o",this.open.toString());
		dataHash.put("h",this.high.toString());
		dataHash.put("l",this.low.toString());
		dataHash.put("z",this.current.toString());
		dataHash.put("v",this.volume.toString());
		dataHash.put("tlong",String.valueOf(tlong));
		this.rowkeyString = (new StringBuffer(ex).append("_").append(ch).append("_").append(date).append("_").append(time)).toString();
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		chk = true;
	}
	  
	public static StockOhlc getStockOhlc(String ex, String ch,String date, String time) {
		StockOhlc aa = new StockOhlc(ex, ch, date, time);
		aa.get();
		if(aa.dataHash.size()<5) return null;
		String o = (String)aa.dataHash.get("o");
		String h = (String)aa.dataHash.get("h");
		String l = (String)aa.dataHash.get("l");
		String z = (String)aa.dataHash.get("z");
		String v = (String)aa.dataHash.get("v");
		if (o == null || h == null || l == null || z == null || v == null)
			return null;
		aa.ex = ex;
		aa.ch = ch;
		aa.open = new BigDecimal(o);
		aa.high = new BigDecimal(h);
		aa.low = new BigDecimal(l);
		aa.current = new BigDecimal(z);
		aa.volume = new BigDecimal(v);
		return aa;
	}	
	  
	public void setTrade(BigDecimal last,BigDecimal volume){
		current = new BigDecimal(last.toString());
		this.dataHash.put("z",last.toString());
		this.volume = new BigDecimal(volume.toString());
		this.dataHash.put("v",volume.toString());
		if(high.compareTo(current)==-1){
			high = new BigDecimal(last.toString());
			this.dataHash.put("h",last.toString());
		}
		if(low.compareTo(current)==1){
			low = new BigDecimal(last.toString());
			this.dataHash.put("l",last.toString());
		}
		//System.out.println(date+" "+time+" : "+this.rowkeyString+" "+ this.dataHash.get("o")+" "+ this.dataHash.get("h")+" "+ this.dataHash.get("l")+" "+ this.dataHash.get("z"));
	}
	  
	  
	public BigDecimal getHigh() {
		return high;
	}	

	public BigDecimal getCurrent() {
		return current;
	}	
	
	public BigDecimal getLow() {
		return low;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public String getTime() {
		return time;
	}

	public long getTlong() {
		return tlong;
	}


	public BigDecimal getVolume() {
		return volume;
	}

	public String getDate() {
		return date;
	}
	
	public int getReplay() {
		return replay;
	}

	public void setReplay(int replay) {
		this.replay = replay;
	}

	@Override
	public String getExchange(String rowkeyString) {
		String[] q = rowkeyString.split("_");
		// TODO Auto-generated method stub
		return q[0];
	}	
	
}