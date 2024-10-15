package com.ecloudlife.cass.logicutil;
import java.text.*;
import java.util.*;

import com.ecloudlife.util.UserSession;
import com.ecloudlife.util.Utility;
import com.proco.datautil.StockStatis;



public class StockStatisManager {
  public long timeout = 900000;
  static long queryCycleMillis = 15000;
  static long delayMillis = 700;
  static long queryMicroTime = 0;

  public static java.util.concurrent.ConcurrentHashMap<String, StockStatisManager> statisHash = new java.util.concurrent.ConcurrentHashMap<String,StockStatisManager>();
  List<Map.Entry<String,String>> statisList = new  Vector<Map.Entry<String,String>>();

  boolean chk = false;
  String key = "";
  String date = "";
  long tlong = 0;
  long change = 0;
  long active = System.currentTimeMillis();
  public StockStatisManager(String key, List<Map.Entry<String,String>> statisList){
    this.key = key;
    if(key.indexOf("_")==-1) return;
    this.statisList = statisList;
    if(this.statisList.size()>0){
      date = key.split("_")[1];
      String time = "";
      for(int i = 0 ; i < this.statisList.size() ; i++){
    	  Map.Entry<String,String> col = this.statisList.get(i);
        if(col.getKey().equals("%")){
          time = col.getValue();
          break;
        }
      }
      java.util.Date dateObj = new Date();
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        dateObj = sdf.parse(date+time);
      }
      catch (ParseException ex1) {
      }
      tlong = dateObj.getTime();
      chk = true;
      //System.out.println("StockStatisManager(): "+ date+time+" "+tlong+"   "+System.currentTimeMillis());
    }

  }

  public void updateStatis(List<Map.Entry<String,String>> statisList){
    this.statisList = statisList;
     String time = "";
    for(int i = 0 ; i < this.statisList.size() ; i++){
      Map.Entry<String,String> col = this.statisList.get(i);
      if(col.getKey().equals("%")){
        time = col.getValue();
        java.util.Date dateObj = new Date();
        try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
          dateObj = sdf.parse(date+time);
        }
        catch (ParseException ex1) {
        }
        long tlong2 = dateObj.getTime();
        if(tlong2>tlong){
          tlong = tlong2;
          change = System.currentTimeMillis();
        }
        //System.out.println("updateStatis(): "+ date+time+" "+tlong2+"   "+tlong);

        break;
      }
    }
  }


  public boolean chkTimeStamp(long ct){
    return this.change > ct;
  }

  public boolean isTimeOut() {
    long cc = System.currentTimeMillis() - this.active;
    return timeout < cc;
  }

  public void touch(){
    this.active = System.currentTimeMillis();
  }

  public List<Map.Entry<String,String>> getStatisList() {
    return statisList;
  }

  public static List<Map.Entry<String,String>> getStatisListNow(String sessionKey) {

    List<Map.Entry<String,String>> statisList = new  Vector<Map.Entry<String,String>>();
    String[] plists = sessionKey.split("_",2); // ex_date    
    StockStatis sq = new StockStatis(plists[0],plists[1],sessionKey);
    statisList = sq.get();
    

    return statisList;
  }

  static void updateStockStatis(List<String> keys){
	  for(String key : keys) {
	       StockStatisManager ssm = statisHash.get(key);
	       if(ssm!=null) continue;
	       String[] plists = key.split("_",2); // ex_date    
	       StockStatis sq = new StockStatis(plists[0],plists[1],key);
	       List<Map.Entry<String,String>> schs = sq.get();
	       
	       ssm = new StockStatisManager(key,schs);
	       statisHash.put(key,ssm);
	       
	  }
  }

  //public static  StockStatisManager getStockStatisList(String sessionKey, UserSession us, long wait){
  //  return getStockStatisList( sessionKey,  us,  wait,StockInfoManager.userDelayMillis);
  //}

  public static  StockStatisManager getStockStatisList(String sessionKey, UserSession us, long wait, long userDelay){
    long ct = System.currentTimeMillis();
    boolean go = false;
    List<String> keys = new Vector<String>();
    keys.add(sessionKey);
    List<String> qkeys = new Vector<String>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      StockStatisManager ssm = statisHash.get(key);
      if(ssm==null){
        qkeys.add(key);
        go = true;
      } else {
        ssm.touch();
        statisHash.put(key,ssm);
        if(!go) go = ssm.chkTimeStamp(us.getLatestMillis(sessionKey));
      }
    }
    if(qkeys.size() > 0){ //add new product
      updateStockStatis(qkeys);
    }
    go = userDelay == 0 ;
    while (!go) {
      go = ( (System.currentTimeMillis() - ct) >= wait) &&
          ( (System.currentTimeMillis() - ct) >= userDelay);
      if(go) break;
      for(int i = 0 ; i < keys.size() ; i++){
        String key = keys.get(i);
        StockStatisManager ssm = statisHash.get(key);
        //User 換頁
        long userLastMillis = us.getLatestMillis(sessionKey);
        go = (userLastMillis==-1);
        if(go) break;

        //Tick Timmer Trigger check
        if(!go) go = ssm.chkTimeStamp(userLastMillis);
        //Delay Time
        if(userDelay!=0) go = (System.currentTimeMillis() - ct) >= userDelay ;

        if(go) break;
      }
      if(go) break;
      Utility.sleep(500);
    }

    List<StockStatisManager> infos = new Vector<StockStatisManager>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      StockStatisManager ssm = statisHash.get(key);
      infos.add(ssm);
    }
    if(infos.size()==0) return null;
    return infos.get(0);
  }

  static long show = 0 ;
  static void updateStockStatis() {
    try {
      List<String> keys = new Vector<String> (statisHash.keySet()); //new Vector<String>();
      long lastMillis = 0;
      for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        StockStatisManager ssm = statisHash.get(key);
        if (ssm == null) {
          statisHash.remove(key);
        }
        else if (ssm.isTimeOut()) {
          statisHash.remove(key);
        }
        else {
          lastMillis = ssm.tlong;
        }
      }
      //System.out.println("Go Update! "+(System.currentTimeMillis()-lastMillis)+" "+queryCycleMillis);
      if ( (System.currentTimeMillis() - lastMillis) < queryCycleMillis) {
        return;
      }
      //System.out.println("Go Update!");

      keys = new Vector<String> (statisHash.keySet());
      
	  for(String key : keys) {
	       StockStatisManager ssm = statisHash.get(key);
	       if(ssm==null) continue;
	       
	       String[] plists = key.split("_",2); // ex_date    
	       StockStatis sq = new StockStatis(plists[0],plists[1],key);
	       List<Map.Entry<String,String>> schs = sq.get();
	       ssm.updateStatis(schs);
	  }

    }
    catch (Exception ex) {
      StockStatisManager.statisHash.clear();
    }
    if(System.currentTimeMillis() - show > 30000 ){
      System.out.println(Utility.getSQLDateTimeStr()+" StockStatisManager.updateStockStatis.");
      show = System.currentTimeMillis();
    }

  }

  public static long getQueryMicroTime() {
    return queryMicroTime;
  }

  public long getChange() {
    return change;
  }

  public static int getProductSize() {
    return statisHash.size();
  }


  public static void main(String[] args) {

    Thread th = new Thread(){
      public void run(){
        while(true){
          StockStatisManager.updateStockStatis();
          Utility.sleep(delayMillis);
        }
      }
    };
    th.start();
  }
}
