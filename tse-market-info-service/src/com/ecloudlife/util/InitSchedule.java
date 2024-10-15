package com.ecloudlife.util;

import java.util.TimerTask;
import com.ecloudlife.cass.logicutil.StockCategory;
import com.ecloudlife.cass.logicutil.StockNewboard;
import com.ecloudlife.cass.logicutil.StockInfoManager;
import com.ecloudlife.cass.logicutil.ExLastTradeDate;
import com.ecloudlife.cass.logicutil.OddInfoManager;
import com.ecloudlife.cass.logicutil.OtOhlcManager;
import com.ecloudlife.cass.logicutil.StockStatisManager;
import com.ecloudlife.cass.logicutil.StocksTimelineManager;
import com.proco.cache.JedisManager;

public class InitSchedule extends TimerTask {
  public static String version = null;
  static {
	  version = "20240715_1";
  }


  public static String init(String from,boolean reset){
    //ExLastTradeDate.replayDate = "";
    //CassConnectionPool.init();
    JedisManager.clear();
    if(reset) {
    	StockCategory.cateHash.clear();
        StockCategory.symbolsRows.clear();
        StockCategory.detailRows.clear();
        OtOhlcManager.ohlcMap.clear();
    }

    //StockCategory.stnameHash.clear();
    //StockCategory.cateHash.clear();
    StockNewboard.cateHash.clear();
    StockInfoManager.execJspSet.clear();
    StockInfoManager.stockDelayHash.clear();
    StockInfoManager.stockHash.clear();
    StockInfoManager.resCacheObject.clear();
    StockInfoManager.resCacheTime.clear();
    StockStatisManager.statisHash.clear();

    OddInfoManager.execJspSet.clear();
    OddInfoManager.stockDelayHash.clear();
    OddInfoManager.stockHash.clear();
    OddInfoManager.resCacheObject.clear();
    OddInfoManager.resCacheTime.clear();
    ExLastTradeDate.tradeDateMap.clear();
    

    
    String rdate = ExLastTradeDate.getLastTradeDate(null, "tse");
    if(rdate!=null) StocksTimelineManager.getStocksTimeline(rdate, "10");
    ///StocksTimelineManager.clear();
    
    System.out.println(Utility.getDateTimeStr() + " InitSchedule init by "+from+" reset:"+reset);
    return rdate;

  }

  public void run() {
    init("Timer",false);
  }

  public static void main(String[] as){
    System.out.println(version);
  }

}
