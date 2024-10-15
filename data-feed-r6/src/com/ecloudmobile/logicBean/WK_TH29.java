package com.ecloudmobile.logicBean;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.util.Hashtable;
import java.util.TreeMap;

public class WK_TH29 implements Runnable {
    PreparedFile pm;
    String base = "TH28"; //Update TH28 Data
    //int prefix = 120;
    int prefix = 140;
  public WK_TH29(PreparedFile pm){
    this.pm = pm;
  }

  public void run(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      if(size < prefix){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix, ConfigData.gwEncode).trim();
      //System.out.println("header: "+header);
      JSONArray ja = new JSONArray();
      TreeMap<String,String> dhash = new TreeMap<String,String>();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();
        String transno = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String excd = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String transcode = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String brkid = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String ivacno = Utility.readInputStreamToString(bai, 7, ConfigData.gwEncode).trim();
        String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String terminalid = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String sequenceno = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String buysell = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String odrtpe = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String txcd = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String qty = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        String txtime = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String bfqty = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        String price = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String sfbrkid = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String recno = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String amt = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String remainlimit = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String filler = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String filler2 = Utility.readInputStreamToString(bai, 20, ConfigData.gwEncode).trim();

        j1.put("stkno",stkno);
        j1.put("slblimit",Utility.parseInt(remainlimit));
        j1.put("txtime", Utility.getSQLTimeStr(txtime));
        dhash.put(stkno,j1.toString());
        count++;
        //ja.put(j1);
        //j.put(stkno,j1);
        System.out.println(Utility.cleanString(stkno+": "+txtime+" "+remainlimit));
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String key = ioFile.getName();
      String rtmessage = " Size : "+count+ " File Size:"+size ;
      System.out.println(rtmessage);

      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      boolean ok = sio.insertCF();
      pm.setSuccess(ok);
      if (ok) {
        pm.setReturnCode("0000");
        pm.setReturnMesaage(base+" Insert "+rtmessage);
      }
      else {
        pm.setReturnMesaage(base + " Insert Error.");
      }

      System.out.println(base+": "+ok);

    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Insert Error : "+ex.toString());
      ex.printStackTrace(System.out);
    }
  }


  public static void main(String[] args){
    try {
      int prefix = 140;
      java.io.File ioFile = new File("Data/HWH29U.dat");
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();

      if(size < prefix){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix, ConfigData.gwEncode).trim();
      //System.out.println("header: "+header);
      JSONArray ja = new JSONArray();
      Hashtable<String,String> dhash = new Hashtable<String,String>();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();
        String transno = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String excd = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String transcode = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String brkid = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String ivacno = Utility.readInputStreamToString(bai, 7, ConfigData.gwEncode).trim();
        String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String terminalid = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String sequenceno = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String buysell = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String odrtpe = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String txcd = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String qty = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        String txtime = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String bfqty = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        String price = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String sfbrkid = Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String recno = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String amt = Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String remainlimit = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String filler = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String filler2 = Utility.readInputStreamToString(bai, 20, ConfigData.gwEncode).trim();

        j1.put("stkno",stkno);
        j1.put("slblimit",Utility.parseInt(remainlimit));
        j1.put("txtime", Utility.getSQLTimeStr(txtime));
        dhash.put(stkno,j1.toString());
        count++;
        //ja.put(j1);
        //j.put(stkno,j1);
        System.out.println(Utility.cleanString(stkno+": "+txtime+" "+remainlimit));
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String key = ioFile.getName();
      String rtmessage = " Size : "+count+ " File Size:"+size ;
      System.out.println(Utility.cleanString(rtmessage));
    }
    catch (Exception ex) {
      ex.printStackTrace(System.out);
    }
  }

}
