package com.ecloudmobile.logicBean;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class WK_TH28 implements Runnable {
    PreparedFile pm;
    String base = "TH28";
    int prefix = 200;

  public WK_TH28(PreparedFile pm){
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
      TreeMap<String,String> dhash = new TreeMap<String,String>();
      JSONArray ja = new JSONArray();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();

        String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String stkbalperior = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String stksell = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String stkbuy = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String stkpay = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String stkbaltoday = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String stklimit = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String slbbalperior = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String slbsell = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String slbreturn = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String slbchange = Utility.readInputStreamToString(bai, 15, ConfigData.gwEncode).trim();
        String slbbaltoday = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String slblimit = Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String markstk = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String markslb = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        String filler = Utility.readInputStreamToString(bai, 23, ConfigData.gwEncode).trim();

        j1.put("stkno",stkno);
        j1.put("slblimit",Utility.parseInt(slblimit));
        j1.put("txtime","");
        dhash.put(stkno,j1.toString());
        //ja.put(j1);
        //j.put(stkno,j1);
        System.out.println(Utility.cleanString(stkno+": "+stkbalperior+" "+slblimit));
        count ++;
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");    	  
        return;
      }
 
      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base);
      Map.Entry<String, String> ent = qsio.get(ioNames[1]);
      List<Map.Entry<String, String>> sList = new ArrayList<Map.Entry<String, String>>();
      
      String rtmessage = Utility.getSQLDateTimeStr()+"Now Size "+sList.size()+" "+ioNames[1]+" Size : "+dhash.size()+ " File Size:"+size ;
      System.out.println(Utility.cleanString(rtmessage));

      if(ent==null){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
        Utility.sleep(1000);
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        sList = qsio.get();
        System.out.println(Utility.getSQLDateTimeStr()+" - "+base+" Delete OK ? : "+sList.size()+" - "+dhash.size());
      }

      if(ioNames[2].endsWith(".dat")) ioNames[2]=ioNames[2].substring(0,ioNames[2].length()-4);
      dhash.put(ioNames[1],ioNames[2]);

      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      boolean ok = sio.insertCF();
      if(ok){
        Utility.sleep(1000);
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        sList = qsio.get();
        System.out.println(Utility.getSQLDateTimeStr()+" - "+base+" Sec. Run ? : "+sList.size()+" - "+dhash.size());
        if(sList.size()<dhash.size()){
          Utility.sleep(1000);

          System.out.println(Utility.getSQLDateTimeStr()+" - "+base+" Sec. Run......");
          sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
          ok = sio.insertCF();
          if(ok) System.out.println(Utility.getSQLDateTimeStr()+" - "+base+" Sec. Run......OK");
          Utility.sleep(1000);
          qsio = new StockIoFile(IOFilePolling.currentDate,base);
          sList = qsio.get();
        }
        System.out.println(Utility.getSQLDateTimeStr()+" - "+base+" Sec. Run Finally : "+sList.size()+" - "+dhash.size());
      }

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
}
