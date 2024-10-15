package com.ecloudlife.cass.logicutil;

import com.ecloudlife.util.UserSession;
import com.ecloudlife.util.Utility;
import com.proco.cache.JedisManager;
import com.proco.datautil.OddDetail;
import com.proco.datautil.OddQuote;

import java.util.concurrent.Executors;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

public class OddInfoManager {
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
  public static java.util.concurrent.ConcurrentHashMap<String,OddInfo> stockHash = new java.util.concurrent.ConcurrentHashMap<String,OddInfo>();

  static long updateDelayMillis = 600000; //10 mins.
  static long updateMillis = 0; //updateMillis active time

  public static void setUserDelayMillis(String code,long delay){
    stockDelayHash.put(code,new Long(delay));
  }

  public static void setShowChart(boolean show){
    OddInfoManager.showChart = show;
  }

  public static long getUserDelayMillis(String code){
    Long rt = OddInfoManager.stockDelayHash.get(code);
    if(rt==null) return 0;
    return rt.longValue() ;
  }

  public static boolean isShowChart(){
    return OddInfoManager.showChart ;
  }

  public static List<OddInfo> getOddInfoList(List<String> keys,String sessionKey, UserSession us, long wait , long userDelay){
    updatDelayMillisTimes();
    long ct = System.currentTimeMillis();
    boolean go = false;
    boolean sync = false;
    List<String> qkeys = new Vector<String>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      OddInfo sinfo = stockHash.get(key);
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
        OddInfo sinfo = stockHash.get(key);
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

    List<OddInfo> infos = new Vector<OddInfo>();
    for(int i = 0 ; i < keys.size() ; i++){
      String key = keys.get(i);
      OddInfo sinfo = stockHash.get(key);
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
		  OddDetail sd = new OddDetail(key);
		  List<Map.Entry<String,String>> cols = sd.get();
		  OddInfo sinfo = stockHash.get(key);
		  if(sinfo!=null) continue;
		  
		  OddQuote sq = new OddQuote(key);
		  List<Map.Entry<String, String>> schs = sq.get();
		  sinfo = new OddInfo(key,cols,schs);
		  stockHash.put(key,sinfo);
	  }
  }

  static long show = 0 ;
  static void updateOddInfo() {
    int updated_cnt = 0;
    String lastSymbol = "";
    try {
      List<String> keysInQry =new Vector<String>();
      List<String> keys = new Vector<String> (stockHash.keySet()); //new Vector<String>();
      for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        OddInfo sinfo = stockHash.get(key);
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
		  
		  OddInfo sinfo = stockHash.get(key);
		  if(sinfo==null) continue;
		  
		  OddQuote sq = new OddQuote(key);
		  List<Map.Entry<String, String>> schs = sq.get();
		  updated_cnt++;
		  sinfo.updateQuote(schs);
		  q0 += sq.getQueryTime();
		  lastSymbol = key;
	  }
	  queryMillisTime = q0;
    }
    catch (Exception ex) {
    	OddInfoManager.stockHash.clear();
    }
    if(System.currentTimeMillis() - show > 3000 ){
      System.out.println(Utility.getFullyDateTimeStr()+" OddInfoManager.updateOddInfo : "+updated_cnt+" "+lastSymbol+" queryMillisTime:"+queryMillisTime+" JedisManager.getCheckTime:"+JedisManager.getCheckTime()+" JedisManager.isConnected:"+JedisManager.isConnected());
      show = System.currentTimeMillis();
    }
  }  
  
  /*
  static long show = 0 ;
  static void updateOddInfo() {
    int updated_cnt = 0;
    String lastSymbol = "";
    try {
      List<String> keysInQry =new Vector<String>();
      List<String> keys = new Vector<String> (stockHash.keySet()); //new Vector<String>();
      for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        OddInfo sinfo = stockHash.get(key);
        if (sinfo == null) {
          stockHash.remove(key);
        }
        else if (sinfo.isTimeOut()) {
          //stockHash.remove(key);  不刪除了 //20200224
        } else keysInQry.add(key);
      }
      keys = keysInQry ; //new Vector<String> (stockHash.keySet());

      OddQuote sq = new OddQuote("");
      java.util.List<SuperRow<String, String, String, String>> itq = sq.
          getRowKeys(CassConnectionPool.getKeyspace(), keys);
      for (int inx = 0; inx < itq.size(); inx++) {
        // StockQuote
        SuperRow<String, String, String, String> srow = itq.get(inx);
        String key = srow.getKey();
        lastSymbol = key;
        OddInfo sinfo = stockHash.get(key);
        if (sinfo == null) {
          continue;
        }
        List<HColumn<String, String>> schs = new Vector<HColumn<String, String>> ();
        List scols = srow.getSuperSlice().getSuperColumns();
        for (int j = 0; j < scols.size(); j++) {
          HSuperColumn<String, String, String>
              shc = (HSuperColumn<String, String, String>) scols.get(j);
          schs.addAll(shc.getColumns());
        }
        updated_cnt++;
        sinfo.updateQuote(schs);
      }
      queryMicroTime = sq.getQueryTime();
    }
    catch (Exception ex) {
          OddInfoManager.stockHash.clear();
    }
    if(System.currentTimeMillis() - show > 3000 ){
      System.out.println(Utility.getSQLDateTimeStr()+" OddInfoManager.updateOddInfo : "+updated_cnt+" "+lastSymbol);
      show = System.currentTimeMillis();
    }
  }*/


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
          OddInfoManager.updateOddInfo();
          Utility.sleep(delayMillis);
        }
      }
    };
    th.start();
  }

}
