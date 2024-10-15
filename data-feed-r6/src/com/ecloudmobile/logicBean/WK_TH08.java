package com.ecloudmobile.logicBean;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;

import java.io.*;
import java.util.*;
import org.json.JSONObject;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;

public class WK_TH08 implements Runnable {
    PreparedFile pm;
    String base = "TH08";
    int prefix = 176;
  public WK_TH08(PreparedFile pm){
    this.pm = pm;
  }

  public void run(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      //Check Delete
      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");
          Utility.closeInputStream(bai);
          Utility.closeInputStream(fis);
    	  return;
      }
      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base);
      Map.Entry<String, String> sList = qsio.get(ioNames[1]);
      //ioName = Utility.filiterString(ioName,".dat");

      if(sList==null){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
      }
      if(ioNames[2].endsWith(".dat")) ioNames[2]=ioNames[2].substring(0,ioNames[2].length()-4);


      if(size <= prefix){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        pm.setSuccess(true);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix, ConfigData.gwEncode).trim();
      //System.out.println("header: "+header);
      JSONObject j1 = new JSONObject();
      int count = 0;
      do{
        count++;

        String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String time = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String tradetype = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String matchqty = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String matchfare = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String totcreditqty = Utility.readInputStreamToString(bai, 10, ConfigData.gwEncode).trim();
        String creditcnt = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String creditfare1 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String creditqty1 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String creditfare2 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String creditqty2 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String creditfare3 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String creditqty3 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String creditfare4 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String creditqty4 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String creditfare5 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String creditqty5 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String totdebitqty = Utility.readInputStreamToString(bai, 10, ConfigData.gwEncode).trim();
        String debitcnt = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String debitfare1 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String debitqty1 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String debitfare2 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String debitqty2 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String debitfare3 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String debitqty3 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String debitfare4 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String debitqty4 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String debitfare5 = Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String debitqty5 = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String date = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String recno = Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String returnopt = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String filler = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();

        j1.put("stkno",stkno);
        j1.put("time",time);
        j1.put("tradetype",tradetype);
        j1.put("matchqty",matchqty);
        j1.put("matchfare",matchfare);
        j1.put("totcreditqty",totcreditqty);
        j1.put("creditcnt",creditcnt);
        j1.put("creditfare1",creditfare1);
        j1.put("creditqty1",creditqty1);
        j1.put("creditfare2",creditfare2);
        j1.put("creditqty2",creditqty2);
        j1.put("creditfare3",creditfare3);
        j1.put("creditqty3",creditqty3);
        j1.put("creditfare4",creditfare4);
        j1.put("creditqty4",creditqty4);
        j1.put("creditfare5",creditfare5);
        j1.put("creditqty5",creditqty5);
        j1.put("totdebitqty",totdebitqty);
        j1.put("debitcnt",debitcnt);
        j1.put("debitfare1",debitfare1);
        j1.put("debitqty1",debitqty1);
        j1.put("debitfare2",debitfare2);
        j1.put("debitqty2",debitqty2);
        j1.put("debitfare3",debitfare3);
        j1.put("debitqty3",debitqty3);
        j1.put("debitfare4",debitfare4);
        j1.put("debitqty4",debitqty4);
        j1.put("debitfare5",debitfare5);
        j1.put("debitqty5",debitqty5);
        j1.put("date",date);
        j1.put("recno",recno);
        j1.put("returnopt",returnopt);
        j1.put("filler",filler);
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);




      String rtmessage = ioNames[1]+" Size : "+count+ " File Size:"+size ;
      System.out.println(Utility.cleanString(rtmessage));

      String key = j1.getString("stkno") + "_" + ioNames[2];
      System.out.println("key : "+Utility.cleanString(key));

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      dhash.put(key,j1.toString());
      dhash.put(ioNames[1],key);
      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      boolean ok = sio.insertCF();
      pm.setSuccess(ok);
      if(ok){
        pm.setReturnCode("0000");
        pm.setReturnMesaage(base+" Insert "+rtmessage);
      } else {
        pm.setReturnMesaage(base+" Insert Error.");
      }
      System.out.println(base+": "+ok);

    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Insert Error : "+ex.toString());
      ex.printStackTrace(System.out);
    }
  }
}
