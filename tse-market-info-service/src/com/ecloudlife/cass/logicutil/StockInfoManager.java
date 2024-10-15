package com.ecloudlife.cass.logicutil;
import java.util.*;

import com.ecloudlife.util.UserSession;
import com.ecloudlife.util.Utility;
import com.proco.cache.JedisManager;
import com.proco.datautil.StockDetail;
import com.proco.datautil.StockQuote;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class StockInfoManager {
  //static long userDelayMillis = 0;
  static ExecutorService executor_update = Executors.newFixedThreadPool(1);
  public static java.util.concurrent.ExecutorService jspUpdate = java.util.concurrent.Executors.newFixedThreadPool(8);
  public static java.util.concurrent.ConcurrentSkipListSet<String> execJspSet = new java.util.concurrent.ConcurrentSkipListSet<String>();

  static boolean showChart = false;
  static long delayMillis = 700;
  static long queryMillisTime = 0;

  public static java.util.concurrent.ConcurrentHashMap<String,Long> resCacheTime = new java.util.concurrent.ConcurrentHashMap<String,Long>();
  public static java.util.concurrent.ConcurrentHashMap<String,org.json.JSONObject> resCacheObject = new java.util.concurrent.ConcurrentHashMap<String,org.json.JSONObject>();
  public static java.util.concurrent.ConcurrentHashMap<String,Long> stockDelayHash = new java.util.concurrent.ConcurrentHashMap<String,Long>();
  public static java.util.concurrent.ConcurrentHashMap<String,StockInfo> stockHash = new java.util.concurrent.ConcurrentHashMap<String,StockInfo>();

  static long updateDelayMillis = 600000; //10 mins.
  static long updateMillis = 0; //updateMillis active time

  public static void setUserDelayMillis(String code,long delay){
    stockDelayHash.put(code,new Long(delay));
    //StockInfoManager.userDelayMillis = delay;
  }

  public static void setShowChart(boolean show){
    StockInfoManager.showChart = show;
  }

  public static long getUserDelayMillis(String code){
    Long rt = StockInfoManager.stockDelayHash.get(code);
    if(rt==null) return 0;
    return rt.longValue() ;
  }

  public static boolean isShowChart(){
    return StockInfoManager.showChart ;
  }

  //public static List<StockInfo> getStockInfoList(List<String> keys,String sessionKey, UserSession us, long wait){
  //  return getStockInfoList(keys, sessionKey, us, wait, StockInfoManager.userDelayMillis);
  //}

  public static List<StockInfo> getStockInfoList(List<String> keys,String sessionKey, UserSession us, long wait , long userDelay){
    updatDelayMillisTimes();
    long ct = System.currentTimeMillis();
    boolean go = false;
    boolean sync = false;
    List<String> qkeys = new Vector<String>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      StockInfo sinfo = stockHash.get(key);
      if(sinfo==null){
        qkeys.add(key);
        go = true;
        sync = true;
      } else {
        //----add by 20200224
        if(sinfo.isTimeOut()){
          qkeys.add(key);
          go = true;
        }
        //---
        sinfo.touch();
        stockHash.put(key,sinfo);
        if(!go) go = sinfo.chkTimeStamp(us.getLatestMillis(sessionKey));
      }
    }
    if(qkeys.size() > 0){ //add new product
      if(sync) updateProducts2(qkeys);
      else updateProducts(qkeys);
    }
    go = userDelay == 0 ;
    while (!go) {
      go = ( (System.currentTimeMillis() - ct) >= wait) &&
          ( (System.currentTimeMillis() - ct) >= userDelay);
      if (go) break;
      for(int i = 0 ; i < keys.size() ; i++){
        String key = keys.get(i);
        StockInfo sinfo = stockHash.get(key);
        //User 換頁
        long userLastMillis = us.getLatestMillis(sessionKey);
        go = (userLastMillis==-1);
        if(go) break;

        //Tick Timmer Trigger check
        if(!go) go = sinfo.chkTimeStamp(userLastMillis);

        //Delay Time
        if(userDelay!=0) go = (System.currentTimeMillis() - ct) >= userDelay ;
        if(go) break;
      }
      if(go) break;
      Utility.sleep(500);
    }

    List<StockInfo> infos = new Vector<StockInfo>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      StockInfo sinfo = stockHash.get(key);
      infos.add(sinfo);
    }
    return infos;
  }

  static java.util.concurrent.atomic.AtomicBoolean  millis = new java.util.concurrent.atomic.AtomicBoolean(false); //改 use atom
  static void updatDelayMillisTimes(){
    if(!millis.getAndSet(true)){
      Runnable th = new Runnable(){
        public void run(){
          updatDelayMillisTimes2();
          millis.set(false);
        }
      };
      executor_update.submit(th);
    }
  }

  static void updatDelayMillisTimes2(){
	  long ct =  System.currentTimeMillis();
	/*
    if(ct-updateMillis < updateDelayMillis) return;
    Keyspace ks = CassConnectionPool.getKeyspace();
    SystemAudit sa = new SystemAudit("UserDelayMillis");
    List<HColumn<String,String>> cols = sa.get(ks,"","",100);
    for(HColumn<String,String> col : cols){
      String name = col.getName();
      String val = col.getValue();
      //System.out.println("UserDelayMillis======="+name+" : "+val);
      if(name!=null && val!=null){
        if(stockDelayHash==null) stockDelayHash = new java.util.concurrent.ConcurrentHashMap<String,Long>();
        Long a = new Long(val);
        if(a!=null)
        stockDelayHash.put(name, a);
      }
    }*/
    updateMillis = ct;
  }

  /*
  static void updateProducts(List<String> keys){
    Keyspace ks = CassConnectionPool.getKeyspace();
    StockDetail sd = new StockDetail("");
    StockQuote sq = new StockQuote("");

    java.util.List<Row<String, String, String>> itd = sd.getRowKeys(ks,keys);
    java.util.List<SuperRow<String, String, String, String>> itq = sq.getRowKeys(ks,keys);
    for(int inx = 0 ; inx < itd.size() && inx < itq.size() ; inx++){
       Row<String, String, String> row = itd.get(inx);
       List<HColumn<String,String>> cols = row.getColumnSlice().getColumns();
       String key = row.getKey();
       StockInfo sinfo = stockHash.get(key);
       if(sinfo!=null) continue;

       // StockQuote
       SuperRow<String, String, String, String> srow = itq.get(inx);
       List<HColumn<String, String>> schs = new Vector<HColumn<String, String>> ();
       List scols = srow.getSuperSlice().getSuperColumns();
       for (int j = 0; j < scols.size(); j++) {
         HSuperColumn<String, String, String>
             shc = (HSuperColumn<String, String, String>) scols.get(j);
         schs.addAll(shc.getColumns());
       }
       sinfo = new StockInfo(key,cols,schs);
       stockHash.put(key,sinfo);
     }
  }*/

  static void updateProducts(final List<String> keys){
    Runnable th = new Runnable(){
      public void run(){
        updateProducts2(keys);
      }
    };
    executor_update.submit(th);
  }

  static void updateProducts2(List<String> keys){
	  for(String key : keys) {
		  StockDetail sd = new StockDetail(key);
		  List<Map.Entry<String,String>> cols = sd.get();
		  StockInfo sinfo = stockHash.get(key);
		  if(sinfo!=null) continue;
		  
		  StockQuote sq = new StockQuote(key);
		  List<Map.Entry<String, String>> schs = sq.get();
		  sinfo = new StockInfo(key,cols,schs);
		  stockHash.put(key,sinfo);
	  }
  }

  static long show = 0 ;
  static void updateStockInfo() {
    int updated_cnt = 0;
    String lastSymbol = "";
    try {
      List<String> keysInQry =new Vector<String>();
      List<String> keys = new Vector<String> (stockHash.keySet()); //new Vector<String>();
      for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        StockInfo sinfo = stockHash.get(key);
        if (sinfo == null) {
          stockHash.remove(key);
        }
        else if (sinfo.isTimeOut()) {
          //stockHash.remove(key);  不刪除了 //20200224
        } else keysInQry.add(key);
      }
      keys = keysInQry ; //new Vector<String> (stockHash.keySet());
      long q0 = 0;
	  for(String key : keys) {
		  StockInfo sinfo = stockHash.get(key);
		  if(sinfo==null) continue;
		  
		  StockQuote sq = new StockQuote(key);
		  List<Map.Entry<String, String>> schs = sq.get();
		  updated_cnt++;
		  sinfo.updateQuote(schs);
		  q0 += sq.getQueryTime();
		  lastSymbol = key;
	  }
	  queryMillisTime = q0;
    }
    catch (Exception ex) {
          StockInfoManager.stockHash.clear();
    }
    if(System.currentTimeMillis() - show > 3000 ){
      System.out.println(Utility.getFullyDateTimeStr()+" StockInfoManager.updateStockInfo : "+updated_cnt+" "+lastSymbol+" queryMillisTime:"+queryMillisTime+" JedisManager.getCheckTime:"+JedisManager.getCheckTime()+" JedisManager.isConnected:"+JedisManager.isConnected());
      show = System.currentTimeMillis();
    }
  }

  public static long getQueryMillisTime() {
    return queryMillisTime;
  }

  public static int getProductSize() {
    return stockHash.size();
  }

  public static void main(String[] args) {
	  Thread th = new Thread(){
		  public void run(){
			  while(true){
				  StockInfoManager.updateStockInfo();
				  Utility.sleep(delayMillis);	
			  }
		  }
	  };	
	  th.start();
  }

}
