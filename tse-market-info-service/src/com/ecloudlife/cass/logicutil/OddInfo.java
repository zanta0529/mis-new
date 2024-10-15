package com.ecloudlife.cass.logicutil;

import com.ecloudlife.util.Utility;
import com.proco.datautil.OddDetail;
import com.proco.datautil.OddQuote;
import com.proco.datautil.StockTickerInfo;

import java.util.List;
import java.util.Map;
import java.util.Vector;


public class OddInfo  {
  //public long timeout = 900000;
  public long timeout = 120000;
  String key = "";
  long tlong = 0;
  long change = 0;
  long active = System.currentTimeMillis();
  List<Map.Entry<String,String>> oddDetail = new Vector<Map.Entry<String,String>>();
  List<Map.Entry<String,String>> oddQuote = new Vector<Map.Entry<String,String>>();
  boolean chk = false;

  public OddInfo(String key, List<Map.Entry<String, String>> oddDetail, List<Map.Entry<String, String>> oddQuote) {
    this.key = key;
    this.oddDetail = oddDetail;
    if (oddDetail.size() == 0)
      return;
    this.oddQuote = oddQuote;
    if (oddQuote.size() > 0) {
      for (int i = 0; i < oddQuote.size(); i++) {
    	Map.Entry<String, String> col = oddQuote.get(i);
        if (col.getKey().equals("tlong")) {
          tlong = Long.parseLong(col.getValue());
          change = tlong;
          break;
        }
      }
    }
    chk = true;
  }


  public OddInfo(String key){
    this.key = key;

    OddDetail sd = new OddDetail(this.key);
    oddDetail = sd.get();
    if(oddDetail.size()==0) return;
    OddQuote sq = new OddQuote(this.key);
    oddQuote =  sq.get();
    if(oddQuote.size()>0)
      this.tlong = sq.getTimestamp();
    chk = true;
  }

  public void updateQuote(List<Map.Entry<String,String>> oddQuote){
    this.oddQuote = oddQuote;
    for(int i = 0 ; i < oddQuote.size() ; i++){
    	Map.Entry<String,String> col = oddQuote.get(i);
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

  public List<Map.Entry<String,String>> getOddDetail() {
    return oddDetail;
  }

  public List<Map.Entry<String,String>> getOddQuote() {
    return oddQuote;
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
