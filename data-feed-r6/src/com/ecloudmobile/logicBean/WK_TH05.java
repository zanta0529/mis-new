package com.ecloudmobile.logicBean;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;


public class WK_TH05 implements Runnable {
    PreparedFile pm;
    String base = "TH05";
    //int prefix = 29;
    //int prefix = 32;
    int prefix = 46;

  public WK_TH05(PreparedFile pm){
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
      JSONObject j = new JSONObject();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();
        String stkno= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String stkname = Utility.readInputStreamToString(bai, 16, ConfigData.gwEncode).trim();

        String market= Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String lastprice= Utility.readInputStreamToString(bai, 9, ConfigData.gwEncode).trim();
        Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        Utility.readInputStreamToString(bai, 13, ConfigData.gwEncode).trim();
        j1.put("stkno",stkno);
        j1.put("stkname",stkname);
        j1.put("market",market);
        j1.put("lastprice",lastprice);
        ja.put(j1);
        j.put(stkno,j1);
        count++;
        //j.put(stkno,j1);
        //System.out.println(stkno+": "+stkname+" "+market+" "+lastprice);
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");    	  
        return;
      }

      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate, base);
      Map.Entry<String, String> sList = qsio.get(ioNames[1]);
      //ioName = Utility.filiterString(ioName,".dat");
      String rtmessage = ioNames[1]+" Size : "+count+ " File Size:"+size ;
      System.out.println(Utility.cleanString(rtmessage));
      if(sList==null){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
      }

      String key = ioNames[1];

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      JSONObject jt = new JSONObject();
      jt.put("msgArray",ja);
      jt.put("msgObject",j);
      dhash.put(key,jt.toString());
      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      //sio.delete(CassConnectionPool.getKeyspace(),null);
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
