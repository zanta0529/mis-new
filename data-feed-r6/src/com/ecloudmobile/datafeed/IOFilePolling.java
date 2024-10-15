package com.ecloudmobile.datafeed;

import com.ecloudlife.util.Utility;
import com.proco.datautil.StockNameStore;
import com.proco.util.DateManager;

import java.util.Vector;
import java.io.*;
import com.ecloudlife.util.ConfigData;


public class IOFilePolling {
  static long delay = 10000;
  public static String currentDate = ""; 
  public static String fixedDate = "";
  
  public static void main(String[] args) {
    System.out.println("====Stock R6 Info Gateway 1.05 ========20240420_TC1");
    //args = new String[]{"20231210"};
    if(args.length==1) fixedDate = args[0];
    
    new ConfigData();
    Vector<String[]> codeList = new Vector<String[]>();
    delay = ConfigData.delay;
    try {
      String ls_str;
      java.io.RandomAccessFile raf = new RandomAccessFile("PreparedFile.conf", "r");
      while ( (ls_str = raf.readLine()) != null) {
        if (ls_str.trim().length() == 0) {
          continue;
        }
        String[] trs = ls_str.split("=");
        if(trs.length==2){
          codeList.add(trs);
        }
      }
    }
    catch (Exception ex) {
    }

    if(codeList.size()==0){
      System.out.println("PreparedFile.conf not config");
      return;
    }

    //if(true) return;
    while(true){
      String cdate = DateManager.getExchangeDate("tse");
      if(cdate == null || cdate.equals("")) {
    	  System.out.println(Utility.getSQLDateTimeStr()+"\tProcess File Count : "+ IOFileProcessLog.getIOFileProcessLogSize()+" cdate:"+cdate +" wait date match.");
    	  Utility.sleep(delay);
    	  continue;
      }
      
      if(!fixedDate.equals("") && cdate.equals(fixedDate)) currentDate = fixedDate;
      else if(cdate.equals(Utility.getDateStr())){
        currentDate = Utility.getDateStr();
        //PreparedFile.fileHash = new java.util.Hashtable<String,Long>();
      } else {
    	  System.out.println(Utility.cleanString(Utility.getSQLDateTimeStr()+"\tProcess File Count : "+ IOFileProcessLog.getIOFileProcessLogSize()+" currentDate:"+currentDate+" cdate:"+cdate+" fixedDate:"+fixedDate +" wait date match."));
    	  Utility.sleep(delay);
    	  continue;
      }

      try {
        //ReloadProcess.reload(currentDate);
      }
      catch (Exception ex1) {
      }

      System.out.println(Utility.cleanString(Utility.getSQLDateTimeStr()+"\tProcess File Count : "+ IOFileProcessLog.getIOFileProcessLogSize()+" cdate:"+cdate+" currentDate:"+currentDate));
      for(int i = 0 ; i < codeList.size() ; i++){
        //System.out.println(codeList.get(i)[0]+" "+codeList.get(i)[1]);
        PreparedFile.main0(codeList.get(i));
        if (StockNameStore.checkLog(ConfigData.mstkPath, ConfigData.mstkLog)) {
          System.out.println(Utility.getSQLDateTimeStr() +
                             " Starting Insert Stock Eng. Data.");
          Thread mstk = new Thread() {
            public void run() {
              StockNameStore.insertMSTK(ConfigData.mstkPath,
                                        ConfigData.mstkEncode);
            }
          };
          mstk.start();
        }
        if(StockNameStore.checkLog(ConfigData.warrPath,ConfigData.warrLog)){
          System.out.println(Utility.getSQLDateTimeStr()+" Starting Insert WarrantRef Data.");
          Thread warr = new Thread(){
            public void run(){
              StockNameStore.insertWarrantRef(ConfigData.warrPath,
                                        ConfigData.mstkEncode);
            }
          };
          warr.start();
        }
      }

      Utility.sleep(delay);
    }
  }
}
