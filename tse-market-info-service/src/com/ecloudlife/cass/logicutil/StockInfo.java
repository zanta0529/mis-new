package com.ecloudlife.cass.logicutil;

import java.util.*;

import com.proco.datautil.StockDetail;
import com.proco.datautil.StockQuote;
import com.proco.datautil.StockTickerInfo;
import com.ecloudlife.util.Utility;


public class StockInfo {
  //public long timeout = 900000;
  public long timeout = 120000;
  String key = "";
  long tlong = 0;
  long change = 0;
  long active = System.currentTimeMillis();
  List<Map.Entry<String,String>> stockDetail = new Vector<Map.Entry<String,String>>();
  List<Map.Entry<String,String>> stockQuote = new Vector<Map.Entry<String,String>>();
  boolean chk = false;

  public StockInfo(String key, List<Map.Entry<String, String>> stockDetail,
      List<Map.Entry<String, String>> stockQuote) {
    this.key = key;
    this.stockDetail = stockDetail;
    if (stockDetail.size() == 0)
      return;
    this.stockQuote = stockQuote;
    if (stockQuote.size() > 0) {
      for (int i = 0; i < stockQuote.size(); i++) {
    	  Map.Entry<String, String> col = stockQuote.get(i);
    	  if (col.getKey().equals("tlong")) {
          tlong = Long.parseLong(col.getValue());
          change = tlong;
          break;
        }
      }
    }
    chk = true;
  }


  public StockInfo(String key){
    this.key = key;
    StockDetail sd = new StockDetail(this.key);
    stockDetail = sd.get();
    if(stockDetail.isEmpty()) return;
    StockQuote sq = new StockQuote(this.key);
    stockQuote =  sq.get();
    if(!stockQuote.isEmpty())
      this.tlong = sq.getTimestamp();
    chk = true;
  }

  public void updateQuote(List<Map.Entry<String,String>> stockQuote){
    this.stockQuote = stockQuote;
    for(int i = 0 ; i < stockQuote.size() ; i++){
    	Map.Entry<String,String> col = stockQuote.get(i);
    	if(col.getKey().equals("tlong")){
        long tlong2 = Long.parseLong(col.getValue());
        if(tlong2>tlong){
          tlong = tlong2;
          change = System.currentTimeMillis();
        }
        break;
      }
    }
  }

  public void touch(){
    this.active = System.currentTimeMillis();
  }

  public void getStockInfo(){
    this.active = System.currentTimeMillis();
  }

  public boolean chkTimeStamp(long ct){
    return this.change > ct;
  }

  public boolean isTimeOut() {
    long cc = System.currentTimeMillis() - this.active;
    return timeout < cc;
  }

  public List<Map.Entry<String,String>> getStockDetail() {
    return stockDetail;
  }

  public List<Map.Entry<String,String>> getStockQuote() {
    return stockQuote;
  }

  public long getChange() {
    return change;
  }

  public String getKey() {
    return key;
  }
  
  public static String[] getTrade(String[] keyArray){
	  if(keyArray.length!=2) return new String[0];
	   String vol = "";
	   String time = "";
	   StockTickerInfo sq0 = new StockTickerInfo(keyArray[0]);
	   StockTickerInfo sq1 = new StockTickerInfo(keyArray[1]);
	   
	   Map<String,String> sq00 = sq0.getMap();
	   Map<String,String> sq01 = sq1.getMap();

	   String vol0 = sq00.get("v");
	   String time0 = sq00.get("t");
	   String vol1 = sq01.get("v");
	   String time1 = sq01.get("t");
	   
	   if(sq00.isEmpty() && sq01.isEmpty()) return new String[0];
	   if(!sq00.isEmpty() && sq01.isEmpty()) return new String[] {time0,vol0};
	   if(sq00.isEmpty() && !sq01.isEmpty()) return new String[] {time1,vol1};
	   
	   vol = String.valueOf(Utility.parseInt(vol1) - Utility.parseInt(vol0));
	   time = time1;
	   return new String[]{time,vol};
  
  }



}
