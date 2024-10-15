package com.ecloudlife.cass.logicutil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.ecloudlife.util.InitSchedule;
import com.proco.util.DateManager;
import com.proco.util.Utility;

public class ExLastTradeDate extends TimerTask {

  public static String replayDate = "";
  public final static Long queryCacheTime = 120000L;
  public static java.util.concurrent.ConcurrentHashMap<String,Object[]> tradeDateMap = new java.util.concurrent.ConcurrentHashMap<String,Object[]>();

  public static String getLastTradeDate(Object ks,String ex){
    if(replayDate==null) replayDate = "";
    if(!replayDate.equals("")){
      return replayDate;
    }

    Object[] trades = tradeDateMap.get(ex);
    if(trades!=null){
      if(trades.length==2){
        Long lastTradeTime = (Long)trades[0];
        if((System.currentTimeMillis()-lastTradeTime)<queryCacheTime){
          return (String)trades[1];
        }
      }
    }

    String rt = "";
    String ch = "";
    if(ex.equals("otc")) ch = "o00.tw";
    if(ex.equals("tse")) ch = "t00.tw";
    if(ex.equals("taifex")) ch = "TXF00.tw";

    /*
    long tenDay = 1768000000L; //1000*3600*24*20;
    long ct = System.currentTimeMillis();
    String edt = Utility.getDateStr(ct);
    String sdt = Utility.getDateStr(ct-(tenDay));
    Hashtable<String,String> eq = new Hashtable<String,String>();
    eq.put("ex",ex);
    eq.put("ch",ch);
    Hashtable<String,String> gt = new Hashtable<String,String>();
    gt.put("d",sdt);
    Hashtable<String,String> lt = new Hashtable<String,String>();
    lt.put("d",edt);
    */
    //System.out.println(sdt);
    //System.out.println(edt);


    rt = DateManager.getExchangeDate(ex);


    if(trades!=null){ //check change Date
      if(trades.length==2){
        String rt0 = (String)trades[1];
        if(!rt.equals(rt0)){
          InitSchedule.init("ExLastTradeDate:"+rt0+"-->"+rt+" ",true);
          StockCategory.stnameHash.clear();
        }
      }
    }
    /*
    StockDetail sd = new StockDetail("");
    List<Row<String,String,String>> rows = sd.getIndex(ks,eq,null,gt,null,lt);
    for(int i = rows.size()-1 ; (i >= 0) && limit!=0  ; i--, limit--){
       Row<String,String,String> row = rows.get(i);
       rt = row.getColumnSlice().getColumnByName("d").getValue().toString();
    }*/
    tradeDateMap.put(ex,new Object[]{new Long(System.currentTimeMillis()),rt});
    return rt;
  }

  public void run() {
    ExLastTradeDate.replayDate = "";
    System.out.println(Utility.getDateTimeStr() + " Run");
  }

  public static long setReplayDate(String replayDate){
        ExLastTradeDate.replayDate = replayDate;
        long afterTenHr = System.currentTimeMillis() + (36000000);
        //ReInit Process
        try {
        java.util.Date date1 = new Date(afterTenHr);
        Timer timer1 = new Timer();
        ExLastTradeDate etfup = new ExLastTradeDate();
        timer1.schedule(etfup,date1);
        System.out.println("Resume System Time is '"+date1.toString()+"'");
        } catch (Exception ex){
        }
        return afterTenHr;
  }

}
